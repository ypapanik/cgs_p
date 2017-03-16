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
 *
 * Experiment comparing \phi+\theta, \phi_p+\theta, \phi+\theta_p, \phi_p+\theta_p
 * vs number of samples
 * 
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class ExperimentSection5_3 {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        LDACmdOption option = new LDACmdOption(args);
        //option.trainingFile = "reutersTrain";
        //option.testFile = "reutersTest";
        int[] K = {20, 50, 100, 200};
        for (int t : K) {
            System.out.println("# topics:"+t);
            option.K = t;
            option.alpha = 0.1;
            option.niters = 100;
            LDA lda = new LDACGS_p(option);
            lda.estimation();
            option.nburnin = 50;
            int chains = 5;

            int[] iters = {0 + option.nburnin, 50 + option.nburnin, 100 + option.nburnin,
                250 + option.nburnin, 500 + option.nburnin, 1000 + option.nburnin
        //                , 5000+option.nburnin
        };
            for (int i : iters) {
                option.niters = i;
                System.out.print(i + " ");
                lda = new LDA(option);
                LDADataset data = null;
                double[][] theta = null, theta_p = null, phi = null;
                for (int j = 1; j <= chains; j++) {
                    Model model = lda.inference();

                    if (j == 1) {
                        phi = model.getPhi();
                        data = model.data;
                        theta = model.getTheta();
                        theta_p = ((InferenceModel)model).getTheta_p();
                        System.out.print(Evaluate.perplexity(data, phi, theta) + " "
                                + Evaluate.perplexity(data, phi, theta_p));
                    } else {
                        for (int m = 0; m < theta.length; m++) {
                            for (int k = 0; k < theta[0].length; k++) {
                                theta[m][k] += model.getTheta()[m][k];
                                theta_p[m][k] += ((InferenceModel)model).getTheta_p()[m][k];
                            }
                        }
                    }
                }
                for (int m = 0; m < theta.length; m++) {
                    for (int k = 0; k < theta[0].length; k++) {
                        theta[m][k] = theta[m][k] / chains;
                        theta_p[m][k] = theta_p[m][k] / chains;
                    }
                }

                System.out.print(" " + Evaluate.perplexity(data, phi, theta) + " "
                        + Evaluate.perplexity(data, phi, theta_p) + " ");

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
                for (int j = 1; j <= chains; j++) {
                    Model model = lda.inference();

                    if (j == 1) {
                        phi = model.getPhi();
                        data = model.data;
                        theta = model.getTheta();
                        theta_p = ((InferenceModel)model).getTheta_p();
                        System.out.print(Evaluate.perplexity(data, phi, theta) + " "
                                + Evaluate.perplexity(data, phi, theta_p));
                    } else {
                        for (int m = 0; m < theta.length; m++) {
                            for (int k = 0; k < theta[0].length; k++) {
                                theta[m][k] += model.getTheta()[m][k];
                                theta_p[m][k] += ((InferenceModel)model).getTheta_p()[m][k];
                            }
                        }
                    }
                }
                for (int m = 0; m < theta.length; m++) {
                    for (int k = 0; k < theta[0].length; k++) {
                        theta[m][k] = theta[m][k] / chains;
                        theta_p[m][k] = theta_p[m][k] / chains;
                    }
                }
                System.out.println(" " + Evaluate.perplexity(data, phi, theta) + " "
                        + Evaluate.perplexity(data, phi, theta_p));

                success = file.renameTo(file3);
                success = file2.renameTo(file);
            }
        }
    }
}
