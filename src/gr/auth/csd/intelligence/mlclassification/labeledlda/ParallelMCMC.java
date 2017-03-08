package gr.auth.csd.intelligence.mlclassification.labeledlda;

import gnu.trove.map.hash.TIntDoubleHashMap;

/**
 *
 * @author Yannis Papanikolaou
 * 29/03/14
 */
public abstract class ParallelMCMC {
    LDADataset data;
//  Create parallel MCMC in order to speed up sampling for estimation/inference
    final int threads;

    public ParallelMCMC(LDADataset data, int threads) {
        this.data = data;
        this.threads = threads;
    }   
    public abstract TIntDoubleHashMap[] startThreads();
}
