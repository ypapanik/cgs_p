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
package gr.auth.csd.intelligence.cgsp.experiments;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gr.auth.csd.intelligence.cgsp.preprocessing.JSONtoWarpLDA;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Example to show how a trained WarpLDA model can be used to compute \theta_p
 * and \phi_p.
 *
 * * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class CGS_pWithWarpLDA {

    int D;
    int V;
    int K;
    double alpha;
    double beta;
    double nw[][];
    double nd[][];
    double nwsum[];
    int z[][];
    protected TIntIntHashMap documentWordFrequencies[];
    protected List<String> documents;
    protected int docs[][];
    private double[][] theta_p;
    private double[][] phi_p;
    private double betaSum;
    private double[][] theta;
    private double[][] phi;

    /**
     *
     */
    public CGS_pWithWarpLDA() {
    }

    /**
     *
     * @param K
     * @param alpha
     * @param beta
     * @param warpStatesFile
     * @param vocabulary
     */
    public CGS_pWithWarpLDA(int K, double alpha, double beta, String warpStatesFile, String vocabulary) {
        this.K = K;
        this.alpha = alpha;
        this.beta = beta;
        betaSum = beta * V;
        documents = readFile(warpStatesFile);
        D = documents.size();
        V = readFile(vocabulary).size();
        System.out.println("K=" + K + " V=" + V + " D=" + D);
        nw = new double[K][V];
        nd = new double[D][K];
        nwsum = new double[K];
        z = new int[D][];
        docs = new int[D][];
        documentWordFrequencies = new TIntIntHashMap[D];
    }

    /**
     *
     */
    protected void calculateCounters() {
        int doc = 0;
        for (String document : documents) {
            String[] tokens = document.split(" ");
            int docLength = Integer.parseInt(tokens[0]);
            z[doc] = new int[docLength];
            docs[doc] = new int[docLength];
            documentWordFrequencies[doc] = new TIntIntHashMap();
            for (int i = 1; i < tokens.length; i++) {
                String[] assignment = tokens[i].split(":");
                int wordType = Integer.parseInt(assignment[0]);
                int topic = Integer.parseInt(assignment[1]);
                nw[topic][wordType]++;
                nd[doc][topic]++;
                nwsum[topic]++;
                z[doc][i - 1] = topic;
                docs[doc][i - 1] = wordType;
                documentWordFrequencies[doc].adjustOrPutValue(wordType, 1, 1);
            }

//            System.out.println(document+" \n z:"+Arrays.toString(z[doc])+" \n docs:"+
//                    Arrays.toString(docs[doc])+" \n nd: "+Arrays.toString(nd[doc]));
            doc++;
        }
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String args[]) throws IOException, InterruptedException {

        String json = args[0];
        int iter = Integer.parseInt(args[1]);
        int K = 50;//Integer.parseInt(args[2]);
        double alpha = 0.1;//Double.parseDouble(args[3]);
        double beta = 0.01;//Double.parseDouble(args[4]);
        long startTime = System.currentTimeMillis();

        JSONtoWarpLDA json2warp = new JSONtoWarpLDA(json, "train");
        Process process = new ProcessBuilder("./warplda-master/release/src/format", "-input", "train", "-prefix", "train").inheritIO().start();
        process.waitFor();

        process = new ProcessBuilder("./warplda-master/release/src/warplda",
                "--prefix", "train", "--k", K + "", "--niter", iter + "", "-alpha", K * alpha + "").redirectOutput(new File("err.txt")).start();
        process.waitFor();
        String warpStatesFile = "train.z.estimate";
        String vocabulary = "train.vocab";
        long runningTime = System.currentTimeMillis();
        long a = runningTime - startTime;
        System.out.println("Running WarpLDA duration:" + a);
        CGS_pWithWarpLDA warp = new CGS_pWithWarpLDA(K, alpha, beta, warpStatesFile, vocabulary);
        warp.calculateCounters();
        long calccountersTime = System.currentTimeMillis();
        long b = calccountersTime - runningTime;
        System.out.println("Calculating counters duration:" + b);
        double[][] theta = warp.computeTheta();
        double[][] phi = warp.computePhi();
        long stdTime = System.currentTimeMillis();
        long c = stdTime - calccountersTime;
        System.out.println("Calculating theta and phi duration:" + c);
        warp.computeBothEstimators();
        long cgs_pTime = System.currentTimeMillis();
        long d = cgs_pTime - stdTime;
        System.out.println("Calculating theta_p and phi_p duration:" + d);

//        System.out.println("phi + theta (Griffiths Steyvers estimator): " + warp.logLikelihood(phi, theta));
//        System.out.println("phi_p + theta: " + warp.logLikelihood(phi_p, theta));
//        System.out.println("phi + theta_p: " + warp.logLikelihood(phi, theta_p));
//        System.out.println("phi_p + theta_p: " + warp.logLikelihood(phi_p, theta_p));
        System.out.print((a + b + c) + " " + warp.logLikelihood(phi, theta) + " ");
        System.out.println((a + b + d) + " " + warp.logLikelihood(warp.phi_p, warp.theta_p));

    }

    /**
     *
     * @param file
     * @return
     */
    public List<String> readFile(String file) {
        List<String> documents = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(file))) {

            documents = stream.collect(Collectors.toList());
        } catch (IOException ex) {
            Logger.getLogger(CGS_pWithWarpLDA.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documents;
    }

    /**
     *
     * @return
     */
    protected double[][] computeTheta() {
        theta = new double[D][K];
        for (int d = 0; d < D; d++) {
            for (int k = 0; k < K; k++) {
                theta[d][k] = nd[d][k] + alpha;
            }
            theta[d] = Utils.normalize(theta[d], 1);
        }
        return theta;
    }

    /**
     *
     * @return
     */
    protected double[][] computeTheta_p() {
        double[][] theta_p = new double[D][K];
        for (int d = 0; d < D; d++) {
            double[] p = new double[K];
            TIntIntIterator it = documentWordFrequencies[d].iterator();
            while (it.hasNext()) {
                it.advance();
                int word = it.key();
                for (int k = 0; k < K; k++) {
                    p[k] = (alpha + nd[d][k]) * (nw[k][word] + beta) / (nwsum[k] + V * beta);
                }
                //average sampling probabilities
                p = Utils.normalize(p, 1);

                for (int k = 0; k < K; k++) {
                    theta_p[d][k] += p[k] * it.value();
                }
            }

            for (int k = 0; k < K; k++) {
                theta_p[d][k] += alpha;
            }
            // normalize
            theta_p[d] = Utils.normalize(theta_p[d], 1);

        }
        return theta_p;
    }

    /**
     *
     * @return
     */
    protected double[][] computePhi() {
        phi = new double[K][V];
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                phi[k][w] = nw[k][w] + beta;
            }
            phi[k] = Utils.normalize(phi[k], 1.0);
        }
        return phi;
    }

    /**
     *
     * @return
     */
    protected double[][] computePhi_p() {
        double[][] phi_p = new double[K][V];
        for (int d = 0; d < D; d++) {
            double[] p = new double[K];
            TIntIntIterator it = documentWordFrequencies[d].iterator();
            while (it.hasNext()) {
                it.advance();
                int word = it.key();
                for (int k = 0; k < K; k++) {
                    p[k] = (nw[k][word] + beta) * (nd[d][k] + alpha) / (nwsum[k] + V * beta);
                }
                //average sampling probabilities
                p = Utils.normalize(p, 1);
                for (int k = 0; k < K; k++) {
                    phi_p[k][word] += p[k] * it.value();
                }
            }
        }
        for (int k = 0; k < K; k++) {
            //add beta hyperparameter
            for (int w = 0; w < V; w++) {
                phi_p[k][w] = phi_p[k][w] + beta;
            }
            //normalize
            phi_p[k] = Utils.normalize(phi_p[k], 1.0);
        }
        return phi_p;
    }

    protected void computeBothEstimators() {
        theta_p = new double[D][K];
        phi_p = new double[K][V];

        double[][] phi = new double[K][V];
        for (int k = 0; k < K; k++) {
            for (int w = 0; w < V; w++) {
                phi[k][w] = nw[k][w] + beta;
            }
            phi[k] = Utils.normalize(phi[k], 1.0);
        }

        ArrayList<CallablePcalculation> calculators = new ArrayList<>();
        int threads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            calculators.add(new CallablePcalculation(this, 0, D - 1, threads, i, phi));
        }
        try {
            pool.invokeAll(calculators);
        } catch (InterruptedException ex) {
        }
        pool.shutdown();

//        for (int d = 0; d < D; d++) {
//            processDoc(d);
//        }
        for (int k = 0; k < K; k++) {
            //add beta hyperparameter
            for (int w = 0; w < V; w++) {
                phi_p[k][w] = phi_p[k][w] + beta;
            }
            //normalize
            phi_p[k] = Utils.normalize(phi_p[k], 1.0);
        }

    }

    /**
     *
     * @param phi
     * @param theta
     * @return
     */
    public double logLikelihood(double phi[][], double[][] theta) {
        double ll = 0;
        for (int d = 0; d < docs.length; d++) {
            for (int w = 0; w < docs[d].length; w++) {
                int word = docs[d][w];
                double l = 0;
                for (int k = 0; k < K; k++) {
                    if (phi[k][word] != 0 && theta[d][k] != 0) {
                        l += phi[k][word] * theta[d][k];
                    }

                }
                ll += Math.log(l);
                //if(Double.isNaN(ll)) System.out.println(d+" "+word+" "+l+" "+ Math.log(l));
            }
        }
        return ll;
    }

    protected void processDoc(int d, double[][] phi) {
        //if(d%100==0) System.out.println(d+" "+new Date());
        double[] prob = new double[K];
        TIntIntIterator it = documentWordFrequencies[d].iterator();
        while (it.hasNext()) {
            it.advance();
            int word = it.key();
            for (int k = 0; k < K; k++) {
                prob[k] = (nd[d][k]+alpha) * (phi[k][word]) / (nwsum[k]);
//(alpha + nd[d][k]) * (nw[k][word] + beta) / (nwsum[k] + betaSum);
            }
            //average sampling probabilities
            prob = Utils.normalize(prob, 1);

            for (int k = 0; k < K; k++) {
                theta_p[d][k] += prob[k] * it.value();
                phi_p[k][word] += prob[k] * it.value();
            }
        }

        for (int k = 0; k < K; k++) {
            theta_p[d][k] += alpha;
        }
        // normalize
        theta_p[d] = Utils.normalize(theta_p[d], 1);
    }
}
