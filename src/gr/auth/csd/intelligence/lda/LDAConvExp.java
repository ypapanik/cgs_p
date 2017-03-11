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
package gr.auth.csd.intelligence.lda;

import gr.auth.csd.intelligence.LDACmdOption;
import gr.auth.csd.intelligence.lda.models.ConvergenceExperiment3Model;
import gr.auth.csd.intelligence.lda.models.Model;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LDAConvExp extends LDA {

    public LDAConvExp(LDACmdOption option) {
        super(option);
    }

    public ConvergenceExperiment3Model inference3() {
        ConvergenceExperiment3Model newModel = null;
        data.create(new CorpusJSON(testFile));
        newModel = new ConvergenceExperiment3Model(data, a, perp, niters, nburnin, modelName, samplingLag);
        newModel.initialize();
        newModel.inference();
        return newModel;
    }
}
