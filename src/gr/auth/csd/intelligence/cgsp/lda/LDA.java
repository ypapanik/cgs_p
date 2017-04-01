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
import gr.auth.csd.intelligence.cgsp.lda.models.CVB0InferenceModel;
import gr.auth.csd.intelligence.cgsp.lda.models.CVB0Model;
import gr.auth.csd.intelligence.cgsp.lda.models.EstimationConvergenceExperimentModel;
import gr.auth.csd.intelligence.cgsp.lda.models.InferenceExpModel;
import gr.auth.csd.intelligence.cgsp.lda.models.InferenceModel;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import gr.auth.csd.intelligence.cgsp.lda.models.Phi_pModel;
import gr.auth.csd.intelligence.cgsp.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LDA {

    static LDADataset data;
    CorpusJSON corpus;
    Dictionary dictionary;

    final String method;
    final String testFile;
    final double a, b;
    final boolean perp;
    final int niters, nburnin;
    final String modelName;

    /**
     *
     */
    protected final int chains;

    /**
     *
     */
    protected final int samplingLag;
    final String trainedPhi;

    /**
     *
     * @param option
     */
    public LDA(LDACmdOption option) {
        corpus = new CorpusJSON(option.trainingFile);
        dictionary = new Dictionary(corpus, 2, 610, 10, 9);
        this.method = option.method;
        trainedPhi = option.modelName + ".phi";
        data = new LDADataset(dictionary, option.inf, option.K, trainedPhi);
        dictionary.writeDictionary(option.dictionary);
        this.chains = option.chains;
        this.modelName = option.modelName;
        this.niters = option.niters;
        nburnin = option.nburnin;
        this.a = option.alpha;
        this.b = option.beta;
        this.perp = option.perplexity;
        this.testFile = option.testFile;
        this.samplingLag = option.samplingLag;
    }

    /**
     *
     * @return
     */
    public double[][] estimation() {
        Model trnModel = null;
        data.create(corpus);
        if (null != method) {
            switch (method) {
                case "conv":
                    trnModel = new EstimationConvergenceExperimentModel(data, a, false, b, perp, niters, nburnin, modelName, samplingLag);
                    break;
                case "cvb0":
                    trnModel = new CVB0Model(data, a, false, b, perp, niters, nburnin, modelName, samplingLag);
                    break;
                case "cgsp":
                    trnModel = new Phi_pModel(data, a, false, b, perp, niters, nburnin, modelName, samplingLag);
                    break;
                default:
                    trnModel = new Model(data, a, false, b, perp, niters, nburnin, modelName, samplingLag);
                    break;
            }
        }
        return trnModel.estimate(true);
    }
    
    /**
     *
     * @return
     */
    public Model inference() {
        Model newModel = null;
        data.create(new CorpusJSON(testFile));
        if(method.equals("cvb0")) newModel = new CVB0InferenceModel(data, a, perp, niters, nburnin, modelName, samplingLag);
        else newModel = new InferenceModel(data, a, perp, niters, nburnin, modelName, samplingLag);
        newModel.initialize();
        newModel.inference();
        return newModel;
    }
   
    /**
     *
     * @return
     */
    public InferenceExpModel inference2() {
        InferenceExpModel newModel = null;
        data.create(new CorpusJSON(testFile));
        newModel = new InferenceExpModel(data, a, perp, niters, nburnin, modelName, samplingLag);
        newModel.initialize();
        newModel.inference();
        return newModel;
    }
}
