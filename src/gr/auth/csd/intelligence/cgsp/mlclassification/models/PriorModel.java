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
package gr.auth.csd.intelligence.cgsp.mlclassification.models;

import gr.auth.csd.intelligence.cgsp.preprocessing.Labels;
import gr.auth.csd.intelligence.cgsp.mlclassification.LDADataset;

/**
 *
 * @author Yannis Papanikolaou
 */
public class PriorModel extends Model {

    private final Labels labels;

    /**
     *
     * @param data
     * @param trainedModelName
     * @param threads
     * @param iters
     * @param burnin
     */
    public PriorModel(LDADataset data, String trainedModelName, int threads, int iters, int burnin) {
        super(data, 1, 0.01, true, trainedModelName, threads, iters, burnin);
        labels = data.getLabels();
    }

    /**
     *
     */
    @Override
    public void initAlpha() {
        alpha = new double[K];
        double sumOfFrequencies = 0;
        for (int i = 1; i < labels.getSize(); i++) {
            String label = labels.getLabel(i);
            sumOfFrequencies += labels.getPositiveInstances().get(label);
        }
        for (int k = 0; k < K; k++) {
            //convert k+1 index to the Labels object index in order to retrieve label's frequency
            String label = labels.getLabel(k + 1);
            double frequency = labels.getPositiveInstances().get(label);
            alpha[k] = 50.0 * frequency / sumOfFrequencies + 30.0 / K;
        }
    }

}
