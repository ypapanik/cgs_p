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
import java.util.Random;
import gr.auth.csd.intelligence.lda.LDADataset;

/**
 *
 * @author Yannis Papanikolaou
 */
public class CVB0Model extends Model {

    double[][][] gamma;

    public CVB0Model(LDADataset data, double a, boolean inf, double b, boolean perp, 
            int niters, int nburnin, String modelName, int sl) {
        super(data, a, inf, b, perp, niters, nburnin, modelName, sl);
        System.out.println("cvb0");
        //initialize gamma
        gamma = new double[M][][];
    }

    @Override
    public void initialize() {
        Random r = new Random();
        for (int d = 0; d < M; d++) {
            int documentLength = data.getDocs().get(d).getWords().size();
            int ppxCounter = 0;
            gamma[d] = new double[documentLength][K];
            for (int w = 0; w < documentLength; w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                double sum = 0;
                for (int k = 0; k < K; k++) {
                    gamma[d][w][k] = r.nextDouble();
                    sum += gamma[d][w][k];
                }
                //normalize
                for (int k = 0; k < K; k++) {
                    gamma[d][w][k] = gamma[d][w][k] / sum;
                    nd[d][k] += gamma[d][w][k];
                    if (!inference) {
                        nw[k][word] += gamma[d][w][k];
                        nwsum[k] += gamma[d][w][k];
                    }
                }
                if (perplexity && ppxCounter > documentLength / 2) {
                    break;
                }
                ppxCounter++;
            }
        }
    }

    @Override
    public void update(int d) {
        int documentLength = data.getDocs().get(d).getWords().size();
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            double probs[] = new double[K];
            for (int k = 0; k < K; k++) {
                double oldGamma = gamma[d][w][k];
                nw[k][word] -= oldGamma;

                nd[d][k] -= oldGamma;
                nwsum[k] -= oldGamma;
                probs[k] = (nw[k][word] + beta) * (nd[d][k] + alpha[k]) / (nwsum[k] + betaSum);
            }
            probs = Utils.normalize(probs, 1);

            //update
            for (int k = 0; k < K; k++) {
                double newGamma = probs[k];
                gamma[d][w][k] = newGamma;
                nw[k][word] += newGamma;
                nd[d][k] += newGamma;
                nwsum[k] += newGamma;
            }
        }
    }
}
