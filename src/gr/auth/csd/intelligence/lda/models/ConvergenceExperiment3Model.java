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
import gr.auth.csd.intelligence.lda.Evaluate;
import gr.auth.csd.intelligence.lda.LDADataset;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class ConvergenceExperiment3Model extends InferenceModel {

    private double[][] theta2;

    public double[][] getTheta_p() {
        return theta2;
    }

    public ConvergenceExperiment3Model(LDADataset data, double a, boolean perp, int niters, int nburnin, String modelName, int sl) {
        super(data, a, perp, niters, nburnin, modelName, sl);
                theta2 = new double[M][K];
    }


    protected double[][] computeTheta_p(boolean finalSample) {

        for (int d = 0; d < M; d++) {
            double[] tempTheta = new double[K];
            double[] p = new double[K];
            TIntArrayList words = data.getDocs().get(d).getWords();
            for (int w = 0; w < words.size(); w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                for (int k = 0; k < K; k++) {
                    p[k] = (alpha[k] + nd[d][k]) * phi[k][word];
                }
                p = Utils.normalize(p, 1);

                for (int k = 0; k < K; k++) {
                    tempTheta[k] += p[k];
                }
            }
            tempTheta = Utils.normalize(tempTheta, 1);

            for (int k = 0; k < K; k++) {
                //if (average) 
                theta2[d][k] += tempTheta[k];
                //else 
                //theta2[d][k] = tempTheta[k];

            }
        }

        if (finalSample) {
            for (int d = 0; d < M; d++) {
                theta2[d] = Utils.normalize(theta2[d], 1);
            }
        }
        return theta2;
    }

    protected double[][] computeTheta(boolean finalSample) {
        double[] tempTheta = new double[K];
        for (int d = 0; d < M; d++) {
            for (int k = 0; k < K; k++) {
                tempTheta[k] = nd[d][k] + alpha[k];
            }
            tempTheta = Utils.normalize(tempTheta, 1);
            for (int k = 0; k < K; k++) {
                //if (average) 
                theta[d][k] += tempTheta[k];
            }
        }
        //System.out.println(Arrays.toString(theta[0]));
        if (finalSample) {
            for (int d = 0; d < M; d++) {
                theta[d] = Utils.normalize(theta[d], 1);
            }
        }
        //System.out.println(theta[0][0]+" "+theta2[0][0]);
        return theta;
    }

    @Override
    public void updateParams(boolean average, boolean finalSample) {
        this.computeTheta(finalSample);
        this.computeTheta_p(finalSample);
    }

    public double[][] inference() {
        System.out.println(niters);
        for (int i = 1; i <= niters; i++) {
            //if(i%5==0) System.out.println(new Date()+" "+i);
            exec();
            if (i > nburnin && i % samplingLag == 0) {
                updateParams(true, false);
                System.out.println("theta "+i+" "+Arrays.toString(theta[0]));
                System.out.println("theta2 "+i+" "+Arrays.toString(theta2[0]));
                                    if (perplexity) {
//            System.out.println(Evaluate.perplexity(data, this.getPhi(), theta)+" "
//                    + Evaluate.perplexity(data, this.getPhi(), theta2));
            }

        }
            
        }
        updateParams(true, true);
        if (perplexity) {
            System.out.println(Evaluate.perplexity(data, this.getPhi(), theta)+" "
                    + Evaluate.perplexity(data, this.getPhi(), theta2));
        }
        save(modelName, 1);
        return this.getTheta();
    }
}
