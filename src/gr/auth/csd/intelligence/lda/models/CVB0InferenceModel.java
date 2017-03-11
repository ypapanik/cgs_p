/* 
 * Copyright (C) 2017
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
package gr.auth.csd.intelligence.lda.models;

import gr.auth.csd.intelligence.utils.Utils;
import gr.auth.csd.intelligence.lda.LDADataset;
import java.util.Arrays;

/**
 *
 * @author Yannis Papanikolaou
 */
public class CVB0InferenceModel extends CVB0Model {

    public CVB0InferenceModel(LDADataset data, double a, boolean perp, int niters,
            int nburnin, String modelName, int sl) {
        super(data, a, true, 0.01, perp, niters, nburnin, modelName, sl);
        phi = readPhi(modelName + ".phi");
        //initialize gamma
        gamma = new double[M][][];
    }

    @Override
    public void updateParams(boolean average, boolean finalSample) {
        numSamples++;
        computeTheta(finalSample);
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
                for (int k = 0; k < K; k++) {
                    theta[d][k] /= numSamples;
                }
                // normalize
                theta[d] = Utils.normalize(theta[d], 1);
            }
        }
        return theta;
    }

    @Override
    public double[][] inference() {
        
        for (int i = 1; i <= niters; i++) {
            //if(i%5==0) System.out.println(new Date()+" "+i);
            for (int d = 0; d < M; d++) {
                update(d);
            }
            if (i > nburnin && i % samplingLag == 0) {
                updateParams(true, false);
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

    @Override
    public void update(int d) {
        int ppxCounter = 0;
        int documentLength = data.getDocs().get(d).getWords().size();
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            double probs[] = new double[K];
            for (int k = 0; k < K; k++) {
                double oldGamma = gamma[d][w][k];
                nd[d][k] -= oldGamma;

                probs[k] = phi[k][word] * (nd[d][k] + alpha[k]);
            }
            probs = Utils.normalize(probs, 1);

            //update
            for (int k = 0; k < K; k++) {
                double newGamma = probs[k];
                gamma[d][w][k] = newGamma;
                nd[d][k] += newGamma;
            }

            if (perplexity && ppxCounter >= (documentLength / 2)) {
                break;
            }
            ppxCounter++;
        }
    }
}
