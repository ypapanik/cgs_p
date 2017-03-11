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
