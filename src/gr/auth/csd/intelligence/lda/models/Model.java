package gr.auth.csd.intelligence.lda.models;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.lda.CallableInferencer;
import gr.auth.csd.intelligence.lda.Evaluate;
import gr.auth.csd.intelligence.utils.Pair;
import gr.auth.csd.intelligence.utils.Utils;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import gr.auth.csd.intelligence.lda.LDADataset;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Yannis Papanikolaou
 */
public class Model {

    protected double alpha[];
    protected double beta;
    protected double betaSum;
    protected int M;
    protected int K;
    protected int V;
    public LDADataset data;
    protected double nw[][];
    protected double nd[][];
    protected double nwsum[];
    protected double phi[][];
    protected double theta[][];
    protected int z[][];
    boolean inference = false;
    protected boolean perplexity = false;
    protected int niters;
    protected int nburnin = 100;
    protected final int samplingLag;
    protected String modelName = null;
    private int threads = 1;
    int numSamples = 0;

    public Model(LDADataset data, double a, boolean inf, double b, boolean perp,
            int niters, int nburnin, String modelName, int sl) {
        this.M = data.getDocs().size();
        this.K = data.getK();
        this.V = data.getV();
//        System.out.println("M " + M);
//        System.out.println("K " + K);
//        System.out.println("V " + V);
        alpha = new double[K];
        Arrays.fill(alpha, a);
        this.data = data;
        nd = new double[M][K];
        theta = new double[M][K];
        this.inference = inf;
        if (!inference) {
            this.beta = b;
            this.betaSum = beta * V;
            nw = new double[K][V];
            nwsum = new double[K];
            phi = new double[K][V];
        } else {
            this.perplexity = perp;
        }

        this.niters = niters;
        this.nburnin = nburnin;
        this.modelName = modelName;
        this.samplingLag = sl;
    }

    public double[][] getPhi() {
        return phi;
    }

    public double[][] getTheta() {
        return theta;
    }

    public int getM() {
        return M;
    }

    public void initialize() {
        Random r = new Random();
        z = new int[M][];
        for (int d = 0; d < M; d++) {
            int ppxCounter = 0;
            int documentLength = data.getDocs().get(d).getWords().size();
            z[d] = new int[documentLength];
            for (int w = 0; w < documentLength; w++) {
                //assign randomly a topic
                z[d][w] = r.nextInt(K);
                int word = data.getDocs().get(d).getWords().get(w);
                int k = z[d][w];
                nd[d][k]++;
                if (!inference) {
                    nw[k][word]++;
                    nwsum[k]++;
                } else if (perplexity && ppxCounter >= documentLength / 2) {
                    break;
                }
                ppxCounter++;
            }
        }
    }

    public void update(int d) {
        int documentLength = data.getDocs().get(d).getWords().size();
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            //System.out.println(d + " " + word);

            int topic = z[d][w];
            nw[topic][word]--;
            nd[d][topic]--;
            nwsum[topic]--;

            double probs[] = new double[K];
            for (int k = 0; k < K; k++) {
                double prob = (nw[k][word] + beta) * (nd[d][k] + alpha[k]) / (nwsum[k] + betaSum);
                probs[k] = (k == 0) ? prob : probs[k - 1] + prob;
            }

            double u = Math.random();
            for (topic = 0; topic < K; topic++) {
                if (probs[topic] > u * probs[K - 1]) {
                    break;
                }
            }
            if (topic == K) {
                topic = K - 1;
            }

            z[d][w] = topic;
            nw[topic][word]++;
            nd[d][topic]++;
            nwsum[topic]++;
        }
    }

    protected double[][] computePhi() {
        double[] tempPhi = new double[V];
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                tempPhi[w] = nw[k][w] + beta;
            }
            tempPhi = Utils.normalize(tempPhi, 1);

            for (int w = 0; w < V; w++) {
                phi[k][w] = tempPhi[w];
            }
        }
        return phi;
    }

    protected double[][] computeTheta(boolean finalSample) {

        double[] tempTheta = new double[K];
        for (int d = 0; d < M; d++) {
            for (int k = 0; k < K; k++) {
                tempTheta[k] = nd[d][k] + alpha[k];
            }
            tempTheta = Utils.normalize(tempTheta, 1);
            for (int k = 0; k < K; k++) {
                theta[d][k] = tempTheta[k];
            }
        }
        return theta;

    }

    public double logLikelihood(LDADataset data, double phi[][], double[][] theta) {
        double ll = 0;
        for (int d = 0; d < data.getDocs().size(); d++) {
            int documentLength = data.getDocs().get(d).getWords().size();
            for (int w = 0; w < documentLength; w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                double l = 0;
                for (int k = 0; k < data.getK(); k++) {
                    if (phi[k][word] != 0 && theta[d][k] != 0) {
                        l += phi[k][word] * theta[d][k];
                    }

                }
                ll += Math.log(l);
                //if(Double.isNaN(ll)) System.out.println(d+" "+word+" "+l+" "+ Math.log(l));
            }
        }
        //System.out.println(ll);
        return ll;
    }

    protected void savePhi(String modelName) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(modelName + ".phi"))) {
            output.writeObject(this.phi);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void save(String modelName, int twords) {
        bipartitionsWrite("bipartitions." + modelName, twords, theta);
        if (!inference) {
            saveTwords("twords." + modelName, twords);
            this.savePhi(modelName);
        }
        saveTheta("theta." + modelName, theta);
    }

    public static double[][] readPhi(String fi) {
        double[][] phi = null;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fi))) {
            phi = (double[][]) input.readObject();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        //System.out.println(fi + " loaded: K= " + phi.length);
        return phi;
    }

    public void updateParams(boolean average, boolean finalSample) {
        numSamples++;
        computePhi();
        computeTheta(finalSample);
    }

    protected void bipartitionsWrite(String bipartitionsFile, int twords, double[][] theta) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bipartitionsFile), "UTF-8"))) {
            for (int m = 0; m < M; m++) {
                writer.write(data.getDocs().get(m).getPmid() + ":\n");
                ArrayList<Pair> wordsProbsList = new ArrayList<>();
                for (int k = 0; k < K; k++) {
                    Pair pair = new Pair(k, theta[m][k], false);
                    wordsProbsList.add(pair);
                }
                Collections.sort(wordsProbsList);
                int iterations = (twords > wordsProbsList.size()) ? wordsProbsList.size() : twords;
                for (int i = 0; i < iterations; i++) {
                    Integer index = (Integer) wordsProbsList.get(i).first;
                    writer.write("\t" + index + "\t" + wordsProbsList.get(i).second + "\n");
                }
            }
        } catch (Exception e) {
        }
    }

    public void saveTwords(String filename, int twords) {
        System.out.println("Saving..");
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"))) {
            for (int k = 0; k < K; k++) {
                ArrayList<Pair> wordsProbsList = new ArrayList<>();
                for (int w = 0; w < V; w++) {
                    Pair pair = new Pair(w, phi[k][w], false);
                    wordsProbsList.add(pair);
                }
                //print topic				
                writer.write("Label no" + (k + 1) + ":\n");
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

    protected void saveTheta(String string, double[][] theta) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(string))) {
            output.writeObject(theta);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public double[][] estimate(boolean save) {
        initialize();
        //System.out.println("Sampling " + niters + " iterations");

        Date begin = new Date(System.currentTimeMillis());

        for (int i = 1; i <= niters; i++) {
            if (i % 50 == 0) {
                System.out.println(new Date() + " " + i);
            }

            for (int d = 0; d < M; d++) {
                update(d);
            }
            //updateParams(false, false);
            Date it = new Date(System.currentTimeMillis());
            //Uncomment to show loglikelihood
            //System.out.println(/*"log-likelihood:" +*/logLikelihood(data, phi, theta));

            //uncomment to use for n_dk vs sum(p_djk) plots
            if (i > nburnin) {
                System.out.println(i + " " + ndVsSumprobs(0, 0));
            }
        }
        //System.out.println(" finished");
        updateParams(false, true);
        if (save) {
            save(modelName, 10);
        }
        System.out.println("log-likelihood:" + logLikelihood(data, phi, theta));
        return this.getPhi();
    }

    public double[][] inference() {
        //System.out.println("Sampling " + niters + " iterations for inference!");
        for (int i = 1; i <= niters; i++) {
            //if(i%1==0) System.out.println(new Date()+" "+i);
            exec();
            if (i > nburnin && i % samplingLag == 0) {
                updateParams(true, false);
                if (perplexity) {
                    System.out.println("Perplexity = " + Evaluate.perplexity(data, this.getPhi(), this.getTheta()));
                }
            }
        }
        updateParams(true, true);
        if (perplexity) {
            System.out.println("Perplexity = " + Evaluate.perplexity(data, this.getPhi(), this.getTheta()));
        }
        System.out.println("Gibbs sampling for inference completed!");
        save(modelName, 1);
        return this.getTheta();
    }

    protected void exec() {

        ArrayList<CallableInferencer> calculators = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            calculators.add(new CallableInferencer(this, 0, M - 1, threads, i));
        }
        try {
            pool.invokeAll(calculators);
        } catch (InterruptedException ex) {
        }
        pool.shutdown();
    }

    private String ndVsSumprobs(int d, int topic) {
        double[] p = new double[K];
        double sump = 0;
        TIntArrayList words = data.getDocs().get(d).getWords();
        for (int w = 0; w < words.size(); w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            for (int k = 0; k < K; k++) {
                p[k] = (alpha[k] + nd[d][k]) * (nw[k][word] + beta)/(nwsum[k]+V*beta);
            }
            //average sampling probabilities
            p = Utils.normalize(p, 1);
            sump += p[topic];
        }
        for(int k=0; k < K; k++) {
            nd = Utils.normalize(nd[d], 1);
        }
    }

}
