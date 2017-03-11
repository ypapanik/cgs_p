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
package gr.auth.csd.intelligence.experiments;

import gr.auth.csd.intelligence.LDACmdOption;
import gr.auth.csd.intelligence.lda.Evaluate;
import gr.auth.csd.intelligence.lda.LDA;
import gr.auth.csd.intelligence.lda.LDADataset;
import gr.auth.csd.intelligence.lda.models.Model;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class ExperimentSection5_4CVB0 {

    public static void main(String args[]) {
        LDACmdOption option = new LDACmdOption(args);
        option.trainingFile = "reutersTrain";
        option.testFile = "reutersTest";
        int[] K = {20, 50, 100, 200, 300, 500};
        for (int t : K) {
            System.out.println("# topics:" + t);
            option.K = t;
            option.method = "cvb0";
            option.alpha = 0.1;
            option.niters = 100;
            LDA lda = new LDA(option);
            lda.estimation();
            option.nburnin = 50;
            int chains = 5;

            option.niters = 100;
            lda = new LDA(option);
            LDADataset data = null;
            double[][] theta = null, phi = null;
            for (int j = 1; j <= chains; j++) {
                Model model = lda.inference();

                if (j == 1) {
                    phi = model.getPhi();
                    data = model.data;
                    theta = model.getTheta();
                    System.out.print(Evaluate.perplexity(data, phi, theta) + " ");
                } else {
                    for (int m = 0; m < theta.length; m++) {
                        for (int k = 0; k < theta[0].length; k++) {
                            theta[m][k] += model.getTheta()[m][k];
                        }
                    }
                }
            }
            for (int m = 0; m < theta.length; m++) {
                for (int k = 0; k < theta[0].length; k++) {
                    theta[m][k] = theta[m][k] / chains;
                }
            }
            System.out.print(" " + Evaluate.perplexity(data, phi, theta));
        }
    }
}
