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
package gr.auth.csd.intelligence.lda.models;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.lda.LDADataset;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Date;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class EstimationConvergenceExperimentModel extends Phi_pModel {

    private double[][] theta_p;

    public EstimationConvergenceExperimentModel(LDADataset data, double a, boolean inf, double b, boolean perp, int niters, int nburnin, String modelName, int sl) {
        super(data, a, inf, b, perp, niters, nburnin, modelName, sl);
        theta_p = new double[M][K];
    }

    protected double[][] computeTheta_p(boolean finalSample) {

        for (int d = 0; d < M; d++) {
            double[] tempTheta = new double[K];
            double[] p = new double[K];
            TIntArrayList words = data.getDocs().get(d).getWords();
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
            }
            for (int k = 0; k < K; k++) {
                theta_p[d][k] = tempTheta[k];
            }
        }

        for (int d = 0; d < M; d++) {
            for (int k = 0; k < K; k++) {
                theta_p[d][k] += alpha[k];
            }
            // normalize
            theta_p[d] = Utils.normalize(theta_p[d], 1);
        }

        return theta_p;
    }

    @Override
    public double[][] estimate(boolean save) {
        initialize();
        Date begin = new Date(System.currentTimeMillis());
        for (int i = 1; i < niters; i++) {
            if (i % 50 == 0) {
                System.out.println(new Date() + " " + i);
            }

            for (int d = 0; d < M; d++) {
                update(d);
            }
            //if (i > 50 && i % 5 == 0) {
                updateParams(false, false);
           //     Date it = new Date(System.currentTimeMillis());
                System.out.print(/*"log-likelihood (std):"+*/logLikelihood(data, phi, theta));
                System.out.println(/*"log-likelihood (CGS_p):"*/" "+logLikelihood(data, phi_p, theta_p));
            //}
        }
        updateParams(false, true);
        if (save) {
            save(modelName, 10);
        }
        System.out.println("log-likelihood (std) log-likelihood (CGS_p)");
        System.out.println(logLikelihood(data, phi, theta) + " " + logLikelihood(data, phi_p, theta_p));
        return this.getPhi();
    }

    @Override
    public void updateParams(boolean average, boolean finalSample) {
        super.updateParams(average, finalSample);
        this.computeTheta_p(finalSample);
    }
}
