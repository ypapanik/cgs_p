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
package gr.auth.csd.intelligence.cgsp.lda;

import gr.auth.csd.intelligence.cgsp.utils.LDACmdOption;
import static gr.auth.csd.intelligence.cgsp.lda.LDA.data;
import gr.auth.csd.intelligence.cgsp.lda.models.Phi_pModel;
import gr.auth.csd.intelligence.cgsp.lda.models.EstimationConvergenceExperimentModel;
import gr.auth.csd.intelligence.cgsp.lda.models.InferenceModel;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import gr.auth.csd.intelligence.cgsp.preprocessing.CorpusJSON;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LDACGS_p extends LDA {
    private Phi_pModel trnModel;

    /**
     *
     * @param option
     */
    public LDACGS_p(LDACmdOption option) {
        super(option);
    }

    /**
     *
     * @return
     */
    @Override
    public double[][] estimation() {
        trnModel = null;
        data.create(corpus);
        trnModel = new Phi_pModel(data, a, false, b, perp, niters, nburnin, modelName, samplingLag);
        return trnModel.estimate(true);
    }

    /**
     *
     * @return
     */
    public Phi_pModel getTrnModel() {
        return trnModel;
    }
    
    
}
