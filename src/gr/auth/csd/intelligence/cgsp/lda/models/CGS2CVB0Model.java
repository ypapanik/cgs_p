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

import gr.auth.csd.intelligence.cgsp.utils.Utils;
import gr.auth.csd.intelligence.cgsp.lda.LDADataset;
import java.util.Date;

/**
 *
 * @author Yannis Papanikolaou
 */
public class CGS2CVB0Model extends Model {

    double[][][] gamma;
    private double a = 0.8;

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
    public CGS2CVB0Model(LDADataset data, double a, boolean inf, double b, boolean perp, int niters, int nburnin, String modelName, int sl) {
        super(data, a, inf, b, perp, niters, nburnin, modelName, sl);
        gamma = new double[M][][];
    }

    /**
     *
     */
    public void mix() {
        for (int d = 0; d < M; d++) {
            int documentLength = data.getDocs().get(d).getWords().size();
            int ppxCounter = 0;
            gamma[d] = new double[documentLength][K];
            for (int w = 0; w < documentLength; w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                double sum = 0;
                for (int k = 0; k < K; k++) {
                    gamma[d][w][k] = (nw[k][word] + beta) * (nd[d][k] + alpha[k]) / (nwsum[k] + betaSum);
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

    /**
     *
     * @param d
     */
    public void update2(int d) {
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

    /**
     *
     * @param save
     * @return
     */
    @Override
    public double[][] estimate(boolean save) {
        initialize();
        System.out.println("Sampling " + niters + " iterations");
        for (int i = 1; i < (1 - a) * niters; i++) {
            if (i % 50 == 0) {
                System.out.println(new Date() + " " + i);
            }

            for (int d = 0; d < getM(); d++) {
                update(d);
            }
            updateParams(false, false);
            System.out.println(/*"log-likelihood:"*/i + " " + logLikelihood(data, phi, theta));
        }
        System.out.println("Mix");
        mix();

        for (int i = (int) ((1 - a) * niters); i < niters; i++) {
            if (i % 50 == 0) {
                System.out.println(new Date() + " " + i);
            }

            for (int d = 0; d < getM(); d++) {
                update2(d);
            }
            updateParams(false, false);
            System.out.println(/*"log-likelihood:"*/i + " " + logLikelihood(data, phi, theta));
        }
        System.out.println(" finished");
        updateParams(false, true);
        //updateParams(true, true);
        if (save) {
            save(modelName, 10);
        }
        System.out.println("log-likelihood:" + logLikelihood(data, phi, theta));
        return this.getPhi();
    }

}
