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
package gr.auth.csd.intelligence.mlclassification.labeledlda.models;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gr.auth.csd.intelligence.mlclassification.labeledlda.LDADataset;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Arrays;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class CGS_pEstimationModel extends Model {

    public CGS_pEstimationModel(LDADataset data, int thread, double beta, String trainedModelName, int threads, int iters, int burnin) {
        super(data, thread, beta, false, trainedModelName, threads, iters, burnin);
    }

    @Override
    protected TIntDoubleHashMap[] computePhi(int totalSamples) {
        //accumulate probabilities
        for (int d = 0; d < M; d++) {
            int[] labels = data.getDocs().get(d).getLabels();
            TIntArrayList words = data.getDocs().get(d).getWords();
            for (int w = 0; w < words.size(); w++) {
                double[] p = new double[labels.length];
                int word = data.getDocs().get(d).getWords().get(w);
                int topic = z[d].get(w);
                nd[d].adjustValue(topic, -1);
                for (int k = 0; k < labels.length; k++) {
                    int label = labels[k] - 1;
                    p[k] = this.probability(word, label, d);
                }
                nd[d].adjustValue(topic, 1);
                p = Utils.normalize(p, 1.0);
                for (int k = 0; k < labels.length; k++) {

                    int label = labels[k] - 1;
                    phi[label].adjustValue(word, p[k]);
                }
            }
        }

        for (int k = 0; k < K; k++) {
            TIntDoubleIterator iterator = phi[k].iterator();
            while (iterator.hasNext()) {
                iterator.advance();
                iterator.setValue(iterator.value() + beta);

            }
            phi[k] = Utils.normalize(phi[k], 1.0);
        }
        //System.out.println(phi[0]);
        return phi;
    }
}
