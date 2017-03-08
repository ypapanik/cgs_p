package gr.auth.csd.intelligence.lda.models;

/*
 * Copyright (C) 2016 Yannis Papanikolaou <ypapanik@csd.auth.gr>
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
import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.lda.Evaluate;
import gr.auth.csd.intelligence.lda.LDADataset;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Arrays;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class InferenceModel extends Model {

    double[][] theta_p;

    public InferenceModel(LDADataset data, double a, boolean perp, int niters, 
            int nburnin, String modelName, int sl) {
        super(data, a, true, 0.01, perp, niters, nburnin, modelName, sl);
        phi = readPhi(modelName + ".phi");
        theta_p = new double[M][K];
    }

    public double[][] getTheta_p() {
        return theta_p;
    }

    @Override
    protected double[][] computeTheta(boolean finalSample) {

        for (int d = 0; d < M; d++) {
            double[] tempTheta = new double[K];
            for (int k = 0; k < K; k++) {
                tempTheta[k] = nd[d][k] + alpha[k];
            }
            //tempTheta = Utils.normalize(tempTheta, 1);
            for (int k = 0; k < K; k++) {
                theta[d][k] += tempTheta[k];
            }
        }
        if (finalSample) {

            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta[d][k] /= numSamples;
                }
                // normalize
                theta[d] = Utils.normalize(theta[d], 1);
            }
        }
        return theta;
    }
    
    protected double[][] computeTheta_p(boolean finalSample) {

        for (int d = 0; d < M; d++) {
            double[] tempTheta = new double[K];
            double[] p = new double[K];

            TIntArrayList words = data.getDocs().get(d).getWords();
            int ppxCounter = 0;
            for (int w = 0; w < words.size(); w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                int topic = z[d][w];
                nd[d][topic]--;
                for (int k = 0; k < K; k++) {
                    p[k] = (alpha[k] + nd[d][k]) * phi[k][word];
                }
                nd[d][topic]++;
                //average sampling probabilities
                p = Utils.normalize(p, 1);

                for (int k = 0; k < K; k++) {
                    tempTheta[k] += p[k];
                }
                if (perplexity && ppxCounter >= (words.size() / 2)) {
                    break;
                }
                ppxCounter++;
            }

            for (int k = 0; k < K; k++) {
                theta_p[d][k] += tempTheta[k];
            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                //if(d==0||d==1) System.out.println(Arrays.toString(theta_p[d]));
                for (int k = 0; k < K; k++) {
                    theta_p[d][k] /= numSamples;
                    theta_p[d][k] += alpha[k];
                }
                // normalize
                theta_p[d] = Utils.normalize(theta_p[d], 1);
            }
        }
        return theta_p;
    }

    @Override
    public void update(int d) {
        int documentLength = data.getDocs().get(d).getWords().size();
        int ppxCounter = 0;
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            int topic = z[d][w];
            nd[d][topic]--;
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

            z[d][w] = topic;
            nd[d][topic]++;
            if (perplexity && ppxCounter >= (documentLength / 2)) {
                break;
            }
            ppxCounter++;
        }
    }

    @Override
    public void updateParams(boolean average, boolean finalSample) {
        numSamples++;
        this.computeTheta(finalSample);
        this.computeTheta_p(finalSample);
    }

    @Override
    public double[][] inference() {
        //System.out.println(niters);
        for (int i = 1; i <= niters; i++) {
            //if(i%5==0) System.out.println(new Date()+" "+i);
            for (int d = 0; d < M; d++) {
                update(d);
            }
            if (i > nburnin && i % samplingLag == 0) {
                updateParams(true, false);
                //System.out.println("theta " + i + " " + Arrays.toString(theta[0]));
                //System.out.println("theta_p " + i + " " + Arrays.toString(theta_p[0]));
                if (perplexity) {
//            System.out.println(Evaluate.perplexity(data, this.getPhi(), theta)+" "
//                    + Evaluate.perplexity(data, this.getPhi(), theta_p));
                }

            }

        }
        updateParams(true, true);
        if (perplexity) {
//            System.out.println(Evaluate.perplexity(data, this.getPhi(), theta) + " "
//                    + Evaluate.perplexity(data, this.getPhi(), theta_p));
        }
        save(modelName, 1);
        return this.getTheta();
    }
}
