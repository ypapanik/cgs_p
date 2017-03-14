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

import gr.auth.csd.intelligence.cgsp.utils.CmdOption;
import gr.auth.csd.intelligence.cgsp.utils.LLDACmdOption;
import gr.auth.csd.intelligence.cgsp.mlclassification.evaluation.EvaluateAll;
import gr.auth.csd.intelligence.cgsp.mlclassification.MLClassifier;
import gr.auth.csd.intelligence.cgsp.mlclassification.LLDA;
import gr.auth.csd.intelligence.cgsp.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.Labels;
import gr.auth.csd.intelligence.cgsp.utils.Timer;

/**
 * Multi-label experiments with Labeled LDA (Prior LDA).
 * 
 * @author Yannis Papanikolaou
 */
public class MultiLabelExperiment {

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        Timer timer = new Timer();

        CmdOption option = new CmdOption(args);
        CorpusJSON corpus = new CorpusJSON(option.trainingFile);
        Labels labels = new Labels(corpus);
        labels.writeLabels(option.labels);
        Dictionary dictionary = new Dictionary(corpus, option.lowUnigrams, option.highUnigrams,
                option.lowBigrams, option.highBigrams);
        System.out.println(timer.duration());
        dictionary.writeDictionary(option.dictionary);
        MLClassifier mlc = null;

        LLDACmdOption option2 = new LLDACmdOption(args);
        option2.niters = 55;
        option2.chains = 1;
        //option2.method = "cvb0";
        option2.method = "cgs_p";
        //option2.method = "std";
        mlc = new LLDA(option2);
        mlc.train();
        mlc.predict(null);
        mlc.bipartitionsWrite(option.bipartitionsFile);
        EvaluateAll ea = new EvaluateAll(labels, option.testFile, option.bipartitionsFile);      
    }
}
