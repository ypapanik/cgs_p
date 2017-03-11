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
package gr.auth.csd.intelligence.mlclassification.labeledlda.models;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.mlclassification.labeledlda.LDADataset;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Arrays;

/**
 *
 * @author Yannis Papanikolaou
 */
public class CGS_pPriorModel extends PriorModel {

    public CGS_pPriorModel(LDADataset data, String trainedModelName, int threads, int iters, int burnin) {
        super(data, trainedModelName, threads, iters, burnin);
    }
       
    @Override
    protected double[][] computeTheta(int totalSamples) {
        System.out.println("Updating parameters...");
        for (int d = 0; d < M; d++) {
            double[] p = new double[K];
            TIntArrayList words = data.getDocs().get(d).getWords();
            for (int w = 0; w < words.size(); w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                int topic = z[d].get(w);
                nd[d].adjustValue(topic, -1);
                for (int k = 0; k < K; k++) {
                    p[k] = (nd[d].get(k) + alpha[k]) * phi[k].get(word);
                }
                
                nd[d].adjustValue(topic, 1);
                //p = Utils.normalize(p, 1);

                //sum probabilities over the document
                for (int k = 0; k < K; k++) {
                    theta[d][k] += p[k];
                }
            }
        }

        //System.out.println(Arrays.toString(theta[0]));
        if (numSamples == totalSamples) {
            for (int m = 0; m < M; m++) {
                for (int k = 0; k < K; k++) {
                    //average over samples
                    theta[m][k] /= numSamples;
                    theta[m][k] += alpha[k];
                }
                //average over Nd and Sum_alpha
                theta[m] = Utils.normalize(theta[m], 1.0);
            }
        }
        return theta;
    }
}
