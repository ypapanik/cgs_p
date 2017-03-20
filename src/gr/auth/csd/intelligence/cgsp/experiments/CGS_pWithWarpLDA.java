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

import gr.auth.csd.intelligence.cgsp.preprocessing.JSONtoWarpLDA;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    /**
     *
     */
    protected List<String> documents;

    /**
     *
     */
    protected int docs[][];

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
        documents = readFile(warpStatesFile);
        D = documents.size();
        V = readFile(vocabulary).size();
        System.out.println("K=" + K + " V=" + V + " D=" + D);
        nw = new double[K][V];
        nd = new double[D][K];
        nwsum = new double[K];
        z = new int[D][];
        docs = new int[D][];
    }

    /**
     *
     * @return
     */
    public int[][] getDocs() {
        return docs;
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
            for (int i = 1; i < tokens.length; i++) {
                String[] assignment = tokens[i].split(":");
                int wordType = Integer.parseInt(assignment[0]);
                int topic = Integer.parseInt(assignment[1]);
                nw[topic][wordType]++;
                nd[doc][topic]++;
                nwsum[topic]++;
                z[doc][i - 1] = topic;
                docs[doc][i - 1] = wordType;
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
        int K = 5000;//Integer.parseInt(args[2]);
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
        long thetaTime = System.currentTimeMillis();
        long c = thetaTime - calccountersTime;
        System.out.println("Calculating theta duration:" + c);
        double[][] theta_p = warp.computeTheta_p();
        long theta_pTime = System.currentTimeMillis();
        long d = theta_pTime - thetaTime;
        System.out.println("Calculating theta_p duration:" + d);
        double[][] phi = warp.computePhi();
        long phiTime = System.currentTimeMillis();
        long e = (phiTime - theta_pTime);
        System.out.println("Calculating phi duration:" + e);
        double[][] phi_p = warp.computePhi_p();
        long phi_pTime = System.currentTimeMillis();
        long f = phi_pTime - phiTime;
        System.out.println("Calculating phi_p duration:" + f);

//        System.out.println("phi + theta (Griffiths Steyvers estimator): " + warp.logLikelihood(phi, theta));
//        System.out.println("phi_p + theta: " + warp.logLikelihood(phi_p, theta));
//        System.out.println("phi + theta_p: " + warp.logLikelihood(phi, theta_p));
//        System.out.println("phi_p + theta_p: " + warp.logLikelihood(phi_p, theta_p));

        System.out.println((a+c+e)+" " + warp.logLikelihood(phi, theta));
        System.out.println((a+c+f)+" " + warp.logLikelihood(phi_p, theta));
        System.out.println((a+d+e)+ " " + warp.logLikelihood(phi, theta_p));
        System.out.println((a+d+f)+" " + warp.logLikelihood(phi_p, theta_p));

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
        double[][] theta = new double[D][K];
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
            for (int w = 0; w < docs[d].length; w++) {
                int word = docs[d][w];
                int topic = z[d][w];
                nd[d][topic]--;
                for (int k = 0; k < K; k++) {
                    p[k] = (alpha + nd[d][k]) * (nw[k][word] + beta) / (nwsum[k] + V * beta);
                }
                nd[d][topic]++;
                //average sampling probabilities
                p = Utils.normalize(p, 1);

                for (int k = 0; k < K; k++) {
                    theta_p[d][k] += p[k];
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
        double[][] phi = new double[K][V];
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
            for (int w = 0; w < docs[d].length; w++) {
                int word = docs[d][w];
                int topic = z[d][w];
                nd[d][topic]--;
                for (int k = 0; k < K; k++) {
                    p[k] = (nw[k][word] + beta) * (nd[d][k] + alpha) / (nwsum[k] + V * beta);
                }
                nd[d][topic]++;
                //average sampling probabilities
                p = Utils.normalize(p, 1);
                for (int k = 0; k < K; k++) {
                    phi_p[k][word] += p[k];
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

}
