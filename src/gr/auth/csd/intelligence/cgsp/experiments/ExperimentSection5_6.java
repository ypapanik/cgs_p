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
import gr.auth.csd.intelligence.cgsp.lda.Evaluate;
import gr.auth.csd.intelligence.cgsp.lda.LDA;
import gr.auth.csd.intelligence.cgsp.lda.LDACGS_p;
import gr.auth.csd.intelligence.cgsp.lda.LDADataset;
import gr.auth.csd.intelligence.cgsp.lda.models.InferenceModel;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import java.io.File;

/**
 * Perplexities for CGS, CGS_p estimators for different numbers of K. 
 * 
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class ExperimentSection5_6 {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        LDACmdOption option = new LDACmdOption(args);
        option.trainingFile = "reutersTrain";
        option.testFile = "reutersTest";
        int[] K = {20, 50, 100, 200, 300, 500};
        for (int t : K) {
            System.out.print(t + " ");
            option.K = t;
            option.alpha = 0.1;
            option.niters = 100;
            LDA lda = new LDACGS_p(option);
            lda.estimation();
            lda = new LDA(option);
            LDADataset data = null;
            double[][] theta = null, theta_p = null, phi = null;
            Model model = lda.inference();

            phi = model.getPhi();
            data = model.data;
            theta = model.getTheta();
            theta_p = ((InferenceModel) model).getTheta_p();
            System.out.print(Evaluate.perplexity(data, phi, theta) + " "
                    + Evaluate.perplexity(data, phi, theta_p));

            File file = new File(option.modelName + ".phi");
            File file2 = new File(option.modelName + ".phi.std");
            boolean success = file.renameTo(file2);

            File file3 = new File(option.modelName + ".phi_p");
            success = file3.renameTo(file);

            lda = new LDA(option);
            data = null;
            theta = null;
            theta_p = null;
            phi = null;
            model = lda.inference();

            phi = model.getPhi();
            data = model.data;
            theta = model.getTheta();
            theta_p = ((InferenceModel) model).getTheta_p();
            System.out.println(" " + Evaluate.perplexity(data, phi, theta) + " "
                    + Evaluate.perplexity(data, phi, theta_p));
        }
    }

}
