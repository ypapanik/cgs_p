package gr.auth.csd.intelligence.mlclassification.labeledlda;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gr.auth.csd.intelligence.mlclassification.labeledlda.models.Model;

/**
 *
 * @author Yannis Papanikolaou 29/03/14
 */
//  Create parallel MCMC in order to speed up sampling for estimation/inference
public class ParallelEstimation extends ParallelMCMC {

    private Model[] models;

    public ParallelEstimation(LDADataset data, Model[] models, int threads) {
        super(data, threads);
        this.models = models;
    }

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
