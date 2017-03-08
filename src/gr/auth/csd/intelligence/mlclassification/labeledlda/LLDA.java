package gr.auth.csd.intelligence.mlclassification.labeledlda;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.set.hash.TIntHashSet;
import gr.auth.csd.intelligence.LLDACmdOption;
import gr.auth.csd.intelligence.mlclassification.MLClassifier;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.CGS_pEstimationModel;
import gr.auth.csd.intelligence.mlclassification.svm.MetaModel;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.CVB0InferenceModel;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.CVB0Model;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.Model;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.PriorModel;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.CGS_pPriorModel;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Dictionary;
import gr.auth.csd.intelligence.preprocessing.Labels;
import gr.auth.csd.intelligence.preprocessing.Vectorize;
import gr.auth.csd.intelligence.utils.Utils;

public class LLDA extends MLClassifier {

    LDADataset data;
    ParallelMCMC pmc;
    String metalabelerFile;
    protected int M;
    protected String method;
    private TIntDoubleHashMap[] phi;
    private de.bwaldvogel.liblinear.Model metalabeler;
    final double beta;
    protected int iters;
    int burnin = 50;
    final String trainedModelName;
    private final boolean parallel;
    protected int chains = 1;
    private final String metaTrainLabels = "metaTrainLabels";
    private final String trainLibsvm = "train.Libsvm";

    public LLDA(LLDACmdOption option) {
        super(option.trainingFile, option.testFile, option.dictionary, option.labels, option.threads);
        this.metalabelerFile = option.metalabelerFile;
        this.method = option.method;
        this.parallel = option.parallel;
        this.beta = option.beta;
        this.burnin = option.nburnin;
        this.iters = option.niters;
        this.trainedModelName = option.trainingFile + ".model";
        this.chains = option.chains;
    }

    public LLDA(String metalabeler, String method, double beta, int iters, boolean parallel,
            String trainingFile, String test, Dictionary dic, Labels labels, int c, boolean inf, int t) {
        super(trainingFile, test, dic, labels, t);
        this.metalabelerFile = metalabeler;
        this.method = method;
        this.beta = beta;
        this.iters = iters;
        this.trainedModelName = trainingFile + ".model";
        this.parallel = parallel;
        this.chains = c;
    }

    public LLDA(String metalabeler, String method, double beta, int iters, boolean parallel,
            Dictionary dictionary, Labels labels, CorpusJSON trainingCorpus,
            CorpusJSON testCorpus, boolean inf, int c, String trainingFile, int t) {
        super(dictionary, labels, trainingCorpus, testCorpus, t);
        this.metalabelerFile = metalabeler;
        this.method = method;
        this.beta = beta;
        this.iters = iters;
        this.trainedModelName = trainingFile + ".model";
        this.parallel = parallel;
        this.chains = c;
    }

    public static void main(String args[]) {
        LLDACmdOption option = new LLDACmdOption(args);
        LLDA llda = new LLDA(option);
        llda.train();
        llda.predict(null);
    }

    @Override
    public void train() {
        data = new LDADataset(dictionary, globalLabels, false);
        data.create((CorpusJSON) corpus);
        this.M = data.getDocs().size();
        Model trnModel = null;
        if (parallel) {
            Model[] models = new Model[threads];
            for (int i = 0; i < threads; i++) {
                if ("cvb0".equals(method)) {
                    models[i] = new CVB0Model(data, i, beta, false, trainedModelName, threads, iters, burnin);
                }
                if ("cgs_p".equals(method)) {
                    models[i] = new CGS_pEstimationModel(data, i, beta, trainedModelName, threads, iters, burnin);

                } else {
                    models[i] = new Model(data, i, beta, false, trainedModelName, threads, iters, burnin);
                }
            }
            //parallel Estimation
            pmc = new ParallelEstimation(data, models, threads);
            phi = pmc.startThreads();
        } else {
            TIntDoubleHashMap[] phiSum = null;
            for (int i = 0; i < chains; i++) {

                if ("cvb0".equals(method)) {
                    trnModel = new CVB0Model(data, i, beta, false, trainedModelName, threads, iters, burnin);
                } else if ("cgs_p".equals(method)) {
                    trnModel = new CGS_pEstimationModel(data, i, beta, trainedModelName, threads, iters, burnin);
                } else {
                    trnModel = new Model(data, i, beta, false, trainedModelName, threads, iters, burnin);
                }

                trnModel.estimate(true);
                //sum phi's
                if (i == 0) {
                    phiSum = trnModel.getPhi();
                } else {
                    for (int k = 0; k < trnModel.getK(); k++) {
                        TIntDoubleIterator it = trnModel.getPhi()[k].iterator();
                        while (it.hasNext()) {
                            it.advance();
                            phiSum[k].adjustOrPutValue(it.key(), it.value(), it.value());
                        }
                    }
                }
            }
            //average phi
            for (int k = 0; k < data.getK(); k++) {
                TIntDoubleIterator it = phiSum[k].iterator();
                while (it.hasNext()) {
                    it.advance();
                    it.setValue(it.value() / chains);
                }
            }

            System.out.println("Serial estimation finished");
//System.out.println(phiSum[0]);
            Model m = trnModel;
            m.setPhi(phiSum);
            m.save(15);
            phi = m.getPhi();

        }
        initMetalabeler();
    }

    @Override
    public double[][] predictInternal(TIntHashSet mc) {
        data = new LDADataset(dictionary, globalLabels, true);
        data.create((CorpusJSON) corpus2);
        M = data.getDocs().size();
        double[][] thetaSum = new double[M][data.getK()];
        Model newModel = null;
        System.out.println("Serial Inference");
        for (int i = 0; i < chains; i++) {
            newModel = createModel();
            newModel.inference();
            for (int m = 0; m < newModel.M; m++) {
                //sum up probabilities from the different markov chains
                for (int k = 0; k < newModel.K; k++) {
                    thetaSum[m][k] += newModel.getTheta()[m][k];
                }
            }
//            for (int k = 0; k < newModel.K; k++) {
//                System.out.print(globalLabels.getLabel(k + 1) + "=" + thetaSum[0][k] + " ");
//            }
//            System.out.println();

            if (i < chains - 1) {
                newModel = null;
                System.gc();
            }
        }
        //normalize
        System.out.println("Serial inference finished. Averaging....");
        for (int doc = 0; doc < thetaSum.length; doc++) {
            thetaSum[doc] = Utils.normalize(thetaSum[doc], 1.0);
        }
        newModel.setTheta(thetaSum);
        newModel.save(15);
        predictions = thetaSum;
        return predictions;
    }

    @Override
    public void createBipartitions() {
        createBipartitionsFromRanking(metalabelerFile);
    }

    protected Model createModel() {
        Model newModel;
        switch (method) {
            case "cvb0":
                System.out.println("CVB0 Inference");
                newModel = new CVB0InferenceModel(data, trainedModelName, threads, iters, burnin);
                break;
            case "cgs_p":
                System.out.println("CGS_p Inference");
                newModel = new CGS_pPriorModel(data, trainedModelName, threads, iters, burnin);
                break;
            default:
                System.out.println("Standard Inference");
                newModel = new PriorModel(data, trainedModelName, threads, iters, burnin);
                break;
        }
        return newModel;
    }

    protected void initMetalabeler() {
        if (metalabelerFile != null) {
            System.out.println("Training metalabeler...");
            Vectorize vectorize = new Vectorize(dictionary, true, globalLabels);
            vectorize.vectorizeTrain(corpus, "train.Libsvm", "trainLabels", "metaTrainLabels", globalLabels);
            MetaModel ml = new MetaModel(numLabels, trainLibsvm, null, dictionary.getId().size());
            ml.train(metaTrainLabels);
            ml.saveModel(metalabelerFile);
            metalabeler = ml.getModel();
        }
    }

    public double[][] getPredictions() {
        return predictions;
    }

    public void setPredictions(double[][] predictions) {
        this.predictions = predictions;
    }
}
