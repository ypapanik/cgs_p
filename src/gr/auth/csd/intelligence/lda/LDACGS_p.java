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
package gr.auth.csd.intelligence.lda;

import gr.auth.csd.intelligence.LDACmdOption;
import static gr.auth.csd.intelligence.lda.LDA.data;
import gr.auth.csd.intelligence.lda.models.Phi_pModel;
import gr.auth.csd.intelligence.lda.models.EstimationConvergenceExperimentModel;
import gr.auth.csd.intelligence.lda.models.InferenceModel;
import gr.auth.csd.intelligence.lda.models.Model;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LDACGS_p extends LDA {
    private Phi_pModel trnModel;

    public LDACGS_p(LDACmdOption option) {
        super(option);
    }

    @Override
    public double[][] estimation() {
        trnModel = null;
        data.create(corpus);
        trnModel = new Phi_pModel(data, a, false, b, perp, niters, nburnin, modelName, samplingLag);
        return trnModel.estimate(true);
    }

    public Phi_pModel getTrnModel() {
        return trnModel;
    }
    
    
}
