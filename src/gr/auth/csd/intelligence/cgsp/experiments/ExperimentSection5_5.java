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
package gr.auth.csd.intelligence.cgsp.experiments;

import gr.auth.csd.intelligence.cgsp.utils.LDACmdOption;
import gr.auth.csd.intelligence.cgsp.lda.LDA;

/**
 * Experiment to plot the loglikelihood of CGS vs CGS_p vs CVB0 while training.
 * 
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class ExperimentSection5_5 {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        LDACmdOption option = new LDACmdOption(args);
        //option.trainingFile = "data/reutersTrain";
        option.K =100;
        option.niters = 250;
        option.alpha = 0.1;
        option.modelName = option.K+"";
        //option.method = "conv";
        option.method = "cgsp";
        //option.method = "cvb0";
        LDA lda = new LDA(option);
        lda.estimation();
    }
}
