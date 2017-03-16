/* 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.auth.csd.intelligence.cgsp.lda.models;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.cgsp.lda.CallableInferencer;
import gr.auth.csd.intelligence.cgsp.lda.Evaluate;
import gr.auth.csd.intelligence.cgsp.utils.Pair;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
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
import gr.auth.csd.intelligence.cgsp.lda.LDADataset;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou
 */
public class Model {

    /**
     *
     */
    protected double alpha[];

    /**
     *
     */
    protected double beta;

    /**
     *
     */
    protected double betaSum;

    /**
     *
     */
    protected int M;

    /**
     *
     */
    protected int K;

    /**
     *
     */
    protected int V;

    /**
     *
     */
    public LDADataset data;

    /**
     *
     */
    protected double nw[][];

    /**
     *
     */
    protected double nd[][];

    /**
     *
     */
    protected double nwsum[];

    /**
     *
     */
    protected double phi[][];

    /**
     *
     */
    protected double theta[][];

    /**
     *
     */
    protected int z[][];
    boolean inference = false;

    /**
     *
     */
    protected boolean perplexity = false;

    /**
     *
     */
    protected int niters;

    /**
     *
     */
    protected int nburnin = 100;

    /**
     *
     */
    protected final int samplingLag;

    /**
     *
     */
    protected String modelName = null;
    private int threads = 1;
    int numSamples = 0;

    /**
     *
     * @param data
     * @param a
     * @param inf
     * @param b
     * @param perp
     * @param niters
     * @param nburnin
     * @param modelName
     * @param sl
     */
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

    /**
     *
     * @return
     */
    public double[][] getPhi() {
        return phi;
    }

    /**
     *
     * @return
     */
    public double[][] getTheta() {
        return theta;
    }

    /**
     *
     * @return
     */
    public int getM() {
        return M;
    }

    /**
     *
     */
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

    /**
     *
     * @param d
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @param finalSample
     * @return
     */
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

    /**
     *
     * @param data
     * @param phi
     * @param theta
     * @return
     */
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

    /**
     *
     * @param modelName
     */
    protected void savePhi(String modelName) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(modelName + ".phi"))) {
            output.writeObject(this.phi);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     *
     * @param modelName
     * @param twords
     */
    public void save(String modelName, int twords) {
        bipartitionsWrite("bipartitions." + modelName, twords, theta);
        if (!inference) {
            saveTwords("twords." + modelName, twords);
            this.savePhi(modelName);
        }
        saveTheta("theta." + modelName, theta);
    }

    /**
     *
     * @param fi
     * @return
     */
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

    /**
     *
     * @param average
     * @param finalSample
     */
    public void updateParams(boolean average, boolean finalSample) {
        numSamples++;
        computePhi();
        computeTheta(finalSample);
    }

    /**
     *
     * @param bipartitionsFile
     * @param twords
     * @param theta
     */
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

    /**
     *
     * @param filename
     * @param twords
     */
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

    /**
     *
     * @param string
     * @param theta
     */
    protected void saveTheta(String string, double[][] theta) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(string))) {
            output.writeObject(theta);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     *
     * @param save
     * @return
     */
    public double[][] estimate(boolean save) {
        initialize();
        //System.out.println("Sampling " + niters + " iterations");
        for (int i = 1; i <= niters; i++) {
            if (i % 50 == 0) {
                //System.out.println(new Date() + " " + i);
            }

            for (int d = 0; d < M; d++) {
                update(d);
            }

            //Uncomment to show loglikelihood
            //System.out.println(/*"log-likelihood:" +*/logLikelihood(data, phi, theta));
            //uncomment to use for n_dk vs sum(p_djk) plots
//            if (i % 10 == 0) {
//                double sumdiffs = 0;
//                for (int d = 0; d < M; d++) {
//                    sumdiffs += ndVsSumprobs(i, d);
//                }
//                System.out.println(i + " " + sumdiffs + " " + sumdiffs / M);
//            }
        }

        //Uncomment for the phi_p approximation experiment
        for (int d = 1; d <= M; d++) {
        if (d % 100 == 0) 
        System.out.println(d + " " + this.testPhi_pApproximation(d));

        }
        updateParams(false, true);
        if (save) {
            save(modelName, 10);
        }
        System.out.println("log-likelihood:" + logLikelihood(data, phi, theta));
        return this.getPhi();
    }

    /**
     *
     * @return
     */
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

    /**
     *
     */
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

    private double ndVsSumprobs(int iteration, int d) {
        double[] p = new double[K];
        double sump[] = new double[K];
        double ndk[] = new double[K];
        TIntArrayList words = data.getDocs().get(d).getWords();
        for (int w = 0; w < words.size(); w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            for (int k = 0; k < K; k++) {
                p[k] = (alpha[k] + nd[d][k]) * (nw[k][word] + beta) / (nwsum[k] + V * beta);
            }
            //average sampling probabilities
            p = Utils.normalize(p, 1);
            for (int k = 0; k < K; k++) {
                sump[k] += p[k];
            }
        }
        ndk = Arrays.copyOf(nd[d], nd[d].length);
        sump = Utils.normalize(sump, 1);
        ndk = Utils.normalize(ndk, 1);
        StringBuilder sb = new StringBuilder();
        double diff = 0;
        for (int k = 0; k < K; k++) {
            diff += Math.abs(sump[k] - ndk[k]);
            if (d == 0 && (iteration == 50 || iteration % 100 == 0)) {
                sb.append(k).append(" ").append(Math.abs(sump[k] - ndk[k])).append("\n");
            }
        }

        if (d == 0 && (iteration == 50 || iteration % 100 == 0)) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(iteration + ".txt"))) {
                bw.append(sb);
                bw.flush();

            } catch (IOException ex) {
                Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return diff;
    }

    /**
     * This method performs the experiment of section 5.4, testing if the
     * approximation of eq.47 holds. The idea here is to explicitly compute the
     * left and right hands of the equation. The relevant expected values are
     * computed as E[X] = \sum (pi*xi)
     *
     */
    private double testPhi_pApproximation(int D) {
        double[][][] probability = new double[M][][];
        double[][][] phi_hops = new double[M][][];
        double nw[][] = new double[K][V];
        double nwsum[] = new double[K];
        double nd[][] = new double[D][K];

        for (int d = 0; d < D; d++) {
            int docLength = data.getDocs().get(d).getWords().size();
            for (int w = 0; w < docLength; w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                int k = z[d][w];
                nd[d][k]++;
                nw[k][word]++;
                nwsum[k]++;
            }
        }

        for (int d = 0; d < D; d++) {
            int docLength = data.getDocs().get(d).getWords().size();
            probability[d] = new double[docLength][K];
            phi_hops[d] = new double[docLength][K];
            for (int w = 0; w < docLength; w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                int topic = z[d][w];
                nw[topic][word]--;
                nd[d][topic]--;
                nwsum[topic]--;
                for (int k = 0; k < K; k++) {
                    double prob = (nw[k][word] + beta) * (nd[d][k] + alpha[k]) / (nwsum[k] + betaSum);
                    probability[d][w][k] = prob;
                }
                nw[topic][word]++;
                nd[d][topic]++;
                nwsum[topic]++;

                probability[d][w] = Utils.normalize(probability[d][w], 1.0);

                for (int k = 0; k < K; k++) {

                    //the next six lines perform the hop from topic to all possible k
                    nw[topic][word]--;
                    nwsum[topic]--;
                    nw[k][word]++;
                    nwsum[k]++;

                    phi_hops[d][w][k] = (nw[k][word] + beta) / (nwsum[k] + betaSum);

                    nw[topic][word]++;
                    nwsum[topic]++;
                    nw[k][word]--;
                    nwsum[k]--;
                }
                phi_hops[d][w] = Utils.normalize(phi_hops[d][w], 1.0);
            }
        }

        //Calculate the expected values
        double[][] Expleft = new double[K][V];
        double[][] Expright = new double[K][V];
        for (int d = 0; d < D; d++) {
            int docLength = data.getDocs().get(d).getWords().size();
            for (int w = 0; w < docLength; w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                for (int k = 0; k < K; k++) {

                    Expright[k][word] += probability[d][w][k];
                    Expleft[k][word] +=  phi_hops[d][w][k];
                }
            }
        }

        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                //quick hack when dealing with D<M
                if(Expright[k][w]!=0) 
                    Expright[k][w] = Expright[k][w] + beta;
            }
            //normalize
            Expright[k] = Utils.normalize(Expright[k], 1.0);
            Expleft[k] = Utils.normalize(Expleft[k], 1.0);
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        double diff = 0;
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                diff += Math.abs(Expright[k][w] - Expleft[k][w]);
                sb.append(Expleft[k][w]).append(" ");
                sb2.append(Expright[k][w]).append(" ");
//                if (k == 0&&(Expleft[k][w]!=0||Expright[k][w]!=0)) {
//                    System.out.println(w+" "+ Expleft[0][w]+" "+ Expright[0][w]);
//                }
            }
            sb.append("\n");
            sb2.append("\n");
        }
        //if (D == M) {
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter("picLeft.txt"))) {
//            bw.append(sb);
//            bw.flush();
//        } catch (IOException ex) {
//            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter("picRight.txt"))) {
//            bw.append(sb2);
//            bw.flush();
//        } catch (IOException ex) {
//            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // }
        return diff / V;
    }
}
