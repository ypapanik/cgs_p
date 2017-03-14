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
package gr.auth.csd.intelligence.cgsp.mlclassification;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gr.auth.csd.intelligence.cgsp.mlclassification.models.Model;

/**
 *
 * @author Yannis Papanikolaou 29/03/14
 */
//  Create parallel MCMC in order to speed up sampling for estimation/inference
public class ParallelEstimation extends ParallelMCMC {

    private Model[] models;

    /**
     *
     * @param data
     * @param models
     * @param threads
     */
    public ParallelEstimation(LDADataset data, Model[] models, int threads) {
        super(data, threads);
        this.models = models;
    }

    /**
     *
     * @return
     */
    @Override
    public TIntDoubleHashMap[] startThreads() {
        Thread[] t = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            t[i] = new Thread(models[i]);
            t[i].start();
        }
        boolean allDead = false;
        while (!allDead) {
            allDead = true;
            for (int i = 0; i < threads; i++) {
                if (t[i].isAlive()) {
                    allDead = false;
                }
            }
        }
        System.out.println("Threads finished. Averaging....");
        return averageMCMC(models);
        
    }

    private TIntDoubleHashMap[] averageMCMC(Model[] trnModel) {
        Model m;
        TIntDoubleHashMap[] phiSum = trnModel[0].getPhi();
        for (int k = 0; k < trnModel[0].getK(); k++) {
            //sum up probabilities from the different markov chains
            for (int i = 1; i < threads; i++) {
                TIntDoubleIterator it = trnModel[i].getPhi()[k].iterator();
                while (it.hasNext()) {
                    it.advance();
                    phiSum[k].adjustOrPutValue(it.key(), it.value(), it.value());
                }
            }
            //average probabilities from the different markov chains
            TIntDoubleIterator it = phiSum[k].iterator();
            while (it.hasNext()) {
                it.advance();
                it.setValue(it.value() / threads);
            }
        }
        m = trnModel[0];
        m.setPhi(phiSum);
        m.save(15);
        return phiSum;
    }
}
