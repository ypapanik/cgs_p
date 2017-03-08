package gr.auth.csd.intelligence.mlclassification.labeledlda.models;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gr.auth.csd.intelligence.utils.Pair;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import gr.auth.csd.intelligence.mlclassification.labeledlda.CallableInferencer;
import gr.auth.csd.intelligence.mlclassification.labeledlda.LDADataset;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model implements Runnable {

    public LDADataset data; // link to a dataset
    public int M = 0; //dataset size (i.e., number of docs)
    public int V = 0; //vocabulary size
    public int K; //number of topics
    public double[] alpha;
    public double beta = 0.01; //LDA  hyperparameters
    protected int numSamples = 0; // number of samples taken
    Random rand = new Random();
    protected boolean inference = false;

    protected TIntIntHashMap[] z = null;
    protected TIntDoubleHashMap[] nw = null;       // number of instances of word/term j assigned to topic i, size K x V
    protected TIntDoubleHashMap[] nd = null;       // nd[i][j]: number of words in document i assigned to topic j, size M x K

    protected double[] nwsum = null;      // nwsum[j]: total number of words assigned to topic j, size K
    public TIntDoubleHashMap[] phi = null;   // phi: topic-word distributions, size K x V
    public double[][] theta = null; // theta: document - topic distributions, size M x K

    protected int niters;
    int nburnin = 50;
    final int samplingLag = 5;
    private int thread;
    String modelName;
    private int threads;
    private double betaSum;

    public Model() {
    }

    public Model(LDADataset data, int thread, double beta, boolean inf,
            String trainedModelName, int threads, int iters, int burnin) {
        this.data = data;
        K = data.getK();
        //+ allocate memory and assign values for variables		
        M = data.getDocs().size();
        V = data.getV();
        System.out.println("K " + K);
        System.out.println("V " + V);
        System.out.println("M " + M);
        if (beta >= 0) {
            this.beta = beta;
        }
        betaSum = V * beta;
        this.inference = inf;

        if (inference) {
            String trainedPhi = trainedModelName + ".phi";
            System.out.println(trainedPhi);
            phi = readPhi(trainedPhi);
            theta = new double[M][K];
        } else {
            nw = new TIntDoubleHashMap[K];
            for (int k = 0; k < K; k++) {
                nw[k] = new TIntDoubleHashMap();
            }
            nwsum = new double[K];
            phi = new TIntDoubleHashMap[K];
            for (int k = 0; k < K; k++) {
                phi[k] = new TIntDoubleHashMap();
            }
        }

        z = new TIntIntHashMap[M];
        nd = new TIntDoubleHashMap[M];
        for (int m = 0; m < M; m++) {
            nd[m] = new TIntDoubleHashMap();
            z[m] = new TIntIntHashMap();
        }

        this.niters = iters;
        this.nburnin = burnin;
        this.thread = thread;
        this.threads = threads;
        this.modelName = trainedModelName;
    }

    public int getM() {
        return M;
    }

    public int getK() {
        return K;
    }

    public void initAlpha() {
        alpha = new double[K];
        for (int k = 0; k < K; k++) {
                alpha[k] = 50.0/K;

        }
    }

    public void initialize() {
        for (int m = 0; m < M; m++) {
            TIntIterator it = data.getDocs().get(m).getWords().iterator();
            while (it.hasNext()) {
                int w = it.next();
                int topic;
                if (!inference) {
                    int randomIndex = rand.nextInt(data.getDocs().get(m).getLabels().length);
                    topic = data.getDocs().get(m).getLabels()[randomIndex] - 1;

                } else {
                    topic = rand.nextInt(K);
                }

                setZInitially(m, w, topic);
            }

            if (!inference) {
                for (int label : data.getDocs().get(m).getLabels()) {
                    it = data.getDocs().get(m).getWords().iterator();
                    while (it.hasNext()) {
                        int w = it.next();
                        if (!phi[label - 1].contains(w)) {
                            phi[label - 1].put(w, 0.0);
                        }
                    }
                }
            }
        }
    }

    public void update(int m) {
        double[] p = new double[K];
        TIntIterator it = data.getDocs().get(m).getWords().iterator();
        while (it.hasNext()) {
            int w = it.next();
            int topic = z[m].get(w);
            removeZi(m, w, topic);
            int[] labels = null;
            if (!inference) {
                labels = data.getDocs().get(m).getLabels();
            }
            int K_m = (labels == null) ? K : labels.length;
            for (int k = 0; k < K_m; k++) {
                topic = (labels == null) ? k : labels[k] - 1;
                double prob = probability(w, topic, m);

                p[k] = (k == 0) ? prob : p[k - 1] + prob;
            }

            double u = Math.random();
            for (topic = 0; topic < K_m; topic++) {
                if (p[topic] > u * p[K_m - 1]) {
                    break;
                }
            }

            if (topic == K_m) {
                topic = K_m - 1;
            }
            if (labels != null) {
                topic = labels[topic] - 1;
            }
            addZi(m, w, topic);
            z[m].put(w, topic);
        }
    }

    public void save(int twords) {
        if (!inference) {
            saveTwords("twords." + modelName, twords);
            savePhi(modelName);
        } else {
            saveTheta("theta." + modelName, theta);
        }
    }

    public static TIntDoubleHashMap[] readPhi(String fi) {
        TIntDoubleHashMap[] p = null;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fi))) {
            p = (TIntDoubleHashMap[]) input.readObject();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        System.out.println(fi + " loaded: K= " + p.length);
        return p;
    }

    public static double[][] readTheta(String th) {
        double[][] p = null;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(th))) {
            p = (double[][]) input.readObject();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        System.out.println(th + " loaded: K= " + p.length);
        return p;
    }

    @Override
    public String toString() {
        return "Model{" + "M=" + M + ", V=" + V + ", K=" + K + ", alpha=" + alpha + ", beta=" + beta + ", inference=" + inference + ", niters=" + niters + '}';
    }

    protected void saveTwords(String filename, int twords) {
        System.out.println("Saving..");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
            for (int k = 0; k < K; k++) {
                ArrayList<Pair> wordsProbsList = new ArrayList<>();
                TIntDoubleIterator it = phi[k].iterator();
                while (it.hasNext()) {
                    it.advance();
                    Pair pair = new Pair(it.key(), it.value(), false);
                    wordsProbsList.add(pair);
                }
                //print topic				
                writer.write("Label " + data.getLabel(k + 1) + ":\n");
                Collections.sort(wordsProbsList);
                int iterations = (twords > wordsProbsList.size()) ? wordsProbsList.size() : twords;
                for (int i = 0; i < iterations; i++) {
                    Integer index = (Integer) wordsProbsList.get(i).first;
                    writer.write("\t" + data.getWord(index) + "\t" + wordsProbsList.get(i).second + "\n");
                }
            }
        } catch (IOException e) {
        }
    }

    protected void savePhi(String modelName) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(modelName + ".phi"))) {
            output.writeObject(this.phi);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    protected void saveTheta(String string, double[][] theta) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(string))) {
            output.writeObject(theta);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public double[][] getTheta() {
        return theta;
    }

    public void setTheta(double[][] theta) {
        this.theta = theta;
    }

    public void setPhi(TIntDoubleHashMap[] phi) {
        this.phi = phi;
    }

    public TIntDoubleHashMap[] getPhi() {
        return phi;
    }

    public void updateParams(int totalSamples) {
        numSamples++;
        if (!inference) {
            this.computePhi(totalSamples);
        } else {
            this.computeTheta(totalSamples);
        }
    }

    protected TIntDoubleHashMap[] computePhi(int totalSamples) {
        for (int k = 0; k < K; k++) {
            TIntDoubleHashMap tempPhi = new TIntDoubleHashMap();
            TIntDoubleIterator iterator = nw[k].iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                int word = iterator.key();
                tempPhi.adjustOrPutValue(word, nw[k].get(word) + beta, nw[k].get(word) + beta);
            }
            tempPhi = Utils.normalize(tempPhi, 1.0);
            
            iterator = tempPhi.iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                int word = iterator.key();
                phi[k].adjustOrPutValue(word, tempPhi.get(word) , tempPhi.get(word));
            }
            if (numSamples == totalSamples) phi[k] = Utils.normalize(phi[k], 1.0);
        }
        return phi;
    }

    protected double[][] computeTheta(int totalSamples) {
        System.out.print("Updating parameters...");

        for (int m = 0; m < M; m++) {
            double tempTheta[] = new double[K];
            for (int k = 0; k < K; k++) {
                tempTheta[k] = nd[m].get(k) + alpha[k];
            }
            //normalize the sample
            tempTheta = Utils.normalize(tempTheta, 1.0);
            //if(m==0) System.out.println("theta:"+Arrays.toString(tempTheta));
            for (int k = 0; k < K; k++) {
                theta[m][k] += tempTheta[k];
            }
        }
        //average ove all samples
        if (numSamples == totalSamples) {
            for (int m = 0; m < M; m++) {
                for (int k = 0; k < K; k++) {
                    theta[m][k] /= numSamples;
                }
            }
        }
        return theta;
    }

    public float probability(int w, int k, int m) {
        if (!inference) {
            //return (float) ((nd[m].get(k) + 50.0 / data.getDocs().get(m).getLabels().length) * (nw[k].get(w) + beta) / (nwsum[k] + betaSum));
            double p = (float) ((nd[m].get(k) + alpha[k]) * (nw[k].get(w) + beta) / 
                    (nwsum[k] + nw[k].size() * beta));
            return (float) ((Double.isInfinite(p))?0:p);
        }
        return (float) ((nd[m].get(k) + alpha[k]) * phi[k].get(w));
    }

    public void setZInitially(int m, int word, int topic) {
        z[m].put(word, topic);
        nd[m].adjustOrPutValue(topic, 1, 1);
        if (!inference) {
            nw[topic].adjustOrPutValue(word, 1, 1);
            nwsum[topic]++; // total number of words assigned to topic j 
        }
    }

    public void removeZi(int m, int w, int topic) {
        nd[m].adjustValue(topic, -1);
        if (nd[m].get(topic) == 0) {
            nd[m].remove(topic);
        }
        if (!inference) {
            nwsum[topic] -= 1;
            nw[topic].adjustValue(w, -1);
            if(nw[topic].get(w)==0) nw[topic].remove(w);
        }
    }

    public void addZi(int m, int w, int topic) {
        nd[m].adjustOrPutValue(topic, 1, 1);
        if (!inference) {
            nw[topic].adjustOrPutValue(w, 1, 1);
            nwsum[topic] += 1;
        }
    }

    public TIntDoubleHashMap[] estimate(boolean save) {
        initAlpha();
        initialize();
        int totalSamples = (niters - nburnin) / samplingLag;
        System.out.println("Thread no" + thread + ": Sampling " + niters + " iterations");
        for (int i = 0; i <= niters; i++) {
            if (i % 50 == 0) {
                System.out.println(new Date() + " " + i);
            }
            for (int m = 0; m < M; m++) {
                update(m);
            }
            if (i > nburnin && i % samplingLag == 0) {
                System.out.println("Updating parameters...");
                updateParams(totalSamples);
            }
        }

        System.out.println("Thread no" + thread + " finished\n");
        //Un-comment only if we want one sample at the end of the chain
        //updateParams(totalSamples);
        if (save) {
            save(15);
        }
        return this.getPhi();
    }

    public double[][] inference() {
        initAlpha();
        initialize();
        int totalSamples = (niters - nburnin) / samplingLag;
        System.out.println("Sampling " + niters + " iterations for inference!");
        for (int i = 1; i <= niters; i++) {
            if (i % 10 == 0) {
                System.out.println(new Date() + " " + i);
            }
            exec();
            if (i > nburnin && i % samplingLag == 0) {
                updateParams(totalSamples);
            }
        }
        //Un-comment only if we want one sample at the end of the chain
        //updateParams(totalSamples);
        System.out.println("Gibbs sampling for inference completed!");
        return theta;
    }

    protected void exec() {

        ArrayList<CallableInferencer> calculators = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            calculators.add(new CallableInferencer(this, 0, (this.getM() - 1), threads, i));
        }
        try {
            pool.invokeAll(calculators);
        } catch (InterruptedException ex) {
        }
        pool.shutdown();

//        ForkJoinPool fjPool = new ForkJoinPool(threads);
//        fjPool.invoke(new ForkInferencer(newModel, 0, (newModel.M-1)));       
    }

    @Override
    public void run() {
        System.out.println("Thread " + thread);
        if (!inference) {
            estimate(false);
        } else {
            inference();
        }
    }

}
