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
import gr.auth.csd.intelligence.cgsp.lda.Evaluate;
import gr.auth.csd.intelligence.cgsp.lda.LDADataset;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.util.Arrays;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class InferenceExpModel extends InferenceModel {

    private double[][] theta24_1, theta24_10, theta24_100, theta24_1000;
    private double[][] theta23;

    /**
     *
     * @param data
     * @param a
     * @param perp
     * @param niters
     * @param nburnin
     * @param modelName
     * @param sl
     */
    public InferenceExpModel(LDADataset data, double a, boolean perp, int niters, int nburnin, String modelName, int sl) {
        super(data, a, perp, niters, nburnin, modelName, sl);
        phi = readPhi(modelName + ".phi");
        theta23 = new double[M][K];
        theta24_1 = new double[M][K];
        theta24_10 = new double[M][K];
        theta24_100 = new double[M][K];
        theta24_1000 = new double[M][K];
    }

    /**
     *
     * @return
     */
    public double[][] getTheta24_1() {
        return theta24_1;
    }

    /**
     *
     * @return
     */
    public double[][] getTheta24_10() {
        return theta24_10;
    }

    /**
     *
     * @return
     */
    public double[][] getTheta24_100() {
        return theta24_100;
    }

    /**
     *
     * @return
     */
    public double[][] getTheta24_1000() {
        return theta24_1000;
    }

    /**
     *
     * @return
     */
    public double[][] getTheta23() {
        return theta23;
    }

    /**
     *
     * @param finalSample
     * @return
     */
    protected double[][] computeTheta23(boolean finalSample) {

        for (int d = 0; d < M; d++) {

            TIntArrayList words = data.getDocs().get(d).getWords();
            int ppxCounter = 0;
            for (int w = 0; w < words.size(); w++) {
//                if(d==0) {
//                    System.out.println("theta23:"+data.getDocs().get(d).getWords().get(w));
//                    System.out.println(Arrays.toString(theta23[0])+Arrays.toString(nd[0]));
//                }
                int topic = z[d][w];
                theta23[d][topic]++;
                if (perplexity && ppxCounter >= (words.size() / 2)) {
                    break;
                }
                ppxCounter++;
            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta23[d][k] /= numSamples;
                    theta23[d][k] += alpha[k];
                }
                // normalize
                theta23[d] = Utils.normalize(theta23[d], 1);
            }
        }
        return theta23;
    }

    /**
     *
     * @param finalSample
     * @return
     */
    protected double[][] computeTheta24_1(boolean finalSample) {

        int L=1;
        for (int d = 0; d < M; d++) {

            TIntArrayList words = data.getDocs().get(d).getWords();
            int ppxCounter = 0;
            for (int w = 0; w < words.size(); w++) {
                int word = words.get(w);
                int topic = z[d][w];
                int oldTopic = topic;
                //theta24[d][topic]++;
                
                
                for (int l = 0; l < L; l++) {
                    nd[d][oldTopic]--;

                    double probs[] = new double[K];
                    for (int k = 0; k < K; k++) {
                        double prob = phi[k][word] * (nd[d][k] + alpha[k]);
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
                    theta24_1[d][topic]++;
                    nd[d][oldTopic]++; 
                }                
                
                if (perplexity && ppxCounter >= (words.size() / 2)) {
                    break;
                }
                ppxCounter++;
            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta24_1[d][k] /= (numSamples*L);
                    theta24_1[d][k] += alpha[k];
                }
                // normalize
                theta24_1[d] = Utils.normalize(theta24_1[d], 1);
            }
        }

        return theta24_1;
    }

    /**
     *
     * @param finalSample
     * @return
     */
    protected double[][] computeTheta24_10(boolean finalSample) {
        int L=10;
        for (int d = 0; d < M; d++) {

            TIntArrayList words = data.getDocs().get(d).getWords();
            int ppxCounter = 0;
            for (int w = 0; w < words.size(); w++) {
                int word = words.get(w);
                int topic = z[d][w];
                int oldTopic = topic;
                //theta24[d][topic]++;
                
                
                for (int l = 0; l < L; l++) {
                    nd[d][oldTopic]--;

                    double probs[] = new double[K];
                    for (int k = 0; k < K; k++) {
                        double prob = phi[k][word] * (nd[d][k] + alpha[k]);
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
                    theta24_10[d][topic]++;
                    nd[d][oldTopic]++; 
                }                
                
                if (perplexity && ppxCounter >= (words.size() / 2)) {
                    break;
                }
                ppxCounter++;
            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta24_10[d][k] /= (numSamples*L);
                    theta24_10[d][k] += alpha[k];
                }
                // normalize
                theta24_10[d] = Utils.normalize(theta24_10[d], 1);
            }
        }

        return theta24_10;
    }

    /**
     *
     * @param finalSample
     * @return
     */
    protected double[][] computeTheta24_100(boolean finalSample) {
        int L=100;
        for (int d = 0; d < M; d++) {

            TIntArrayList words = data.getDocs().get(d).getWords();
            int ppxCounter = 0;
            for (int w = 0; w < words.size(); w++) {
                int word = words.get(w);
                int topic = z[d][w];
                int oldTopic = topic;
                //theta24[d][topic]++;
                
                
                for (int l = 0; l < L; l++) {
                    nd[d][oldTopic]--;

                    double probs[] = new double[K];
                    for (int k = 0; k < K; k++) {
                        double prob = phi[k][word] * (nd[d][k] + alpha[k]);
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
                    theta24_100[d][topic]++;
                    nd[d][oldTopic]++; 
                }                
                
                if (perplexity && ppxCounter >= (words.size() / 2)) {
                    break;
                }
                ppxCounter++;
            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta24_100[d][k] /= (numSamples*L);
                    theta24_100[d][k] += alpha[k];
                }
                // normalize
                theta24_100[d] = Utils.normalize(theta24_100[d], 1);
            }
        }

        return theta24_100;
    }

    /**
     *
     * @param finalSample
     * @return
     */
    protected double[][] computeTheta24_1000(boolean finalSample) {
       int L=1000;
        for (int d = 0; d < M; d++) {

            TIntArrayList words = data.getDocs().get(d).getWords();
            int ppxCounter = 0;
            for (int w = 0; w < words.size(); w++) {
                int word = words.get(w);
                int topic = z[d][w];
                int oldTopic = topic;
                //theta24[d][topic]++;
                
                
                for (int l = 0; l < L; l++) {
                    nd[d][oldTopic]--;

                    double probs[] = new double[K];
                    for (int k = 0; k < K; k++) {
                        double prob = phi[k][word] * (nd[d][k] + alpha[k]);
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
                    theta24_1000[d][topic]++;
                    nd[d][oldTopic]++; 
                }                
                
                if (perplexity && ppxCounter >= (words.size() / 2)) {
                    break;
                }
                ppxCounter++;
            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta24_1000[d][k] /= (numSamples*L);
                    theta24_1000[d][k] += alpha[k];
                }
                // normalize
                theta24_1000[d] = Utils.normalize(theta24_1000[d], 1);
            }
        }

        return theta24_1000;
    }

    /**
     *
     * @param average
     * @param finalSample
     */
    @Override
    public void updateParams(boolean average, boolean finalSample) {
        super.updateParams(average, finalSample);
        //computeTheta23(finalSample);
        computeTheta24_1(finalSample);
        computeTheta24_10(finalSample);
        computeTheta24_100(finalSample);
        computeTheta24_1000(finalSample);
    }

    /**
     *
     * @return
     */
    @Override
    public double[][] inference() {
        System.out.println(niters);
        for (int i = 1; i <= niters; i++) {
            //if(i%5==0) System.out.println(new Date()+" "+i);
            for (int d = 0; d < M; d++) {
                update(d);
            }
            if (i > nburnin && i % samplingLag == 0) {
                updateParams(true, false);
                //System.out.println("theta " + i + " " + Arrays.toString(theta[0]));
                //System.out.println("theta_p " + i + " " + Arrays.toString(theta_p[0]));

            }

        }
        updateParams(true, true);
        if (perplexity) {
            System.out.println(Evaluate.perplexity(data, this.getPhi(), theta) + " "
                    + Evaluate.perplexity(data, this.getPhi(), theta_p) + " "
                   // + Evaluate.perplexity(data, this.getPhi(), theta23) + " "
                    + Evaluate.perplexity(data, this.getPhi(), theta24_1)+" "
            + Evaluate.perplexity(data, this.getPhi(), theta24_10)+" "
            + Evaluate.perplexity(data, this.getPhi(), theta24_100)+ " "
            + Evaluate.perplexity(data, this.getPhi(), theta24_1000));
        }
        save(modelName, 1);
        return this.getTheta();
    }
}
