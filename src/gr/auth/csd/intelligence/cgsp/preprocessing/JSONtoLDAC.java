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
package gr.auth.csd.intelligence.cgsp.preprocessing;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import gr.auth.csd.intelligence.cgsp.utils.CmdOption;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class JSONtoLDAC {

    /**
     *
     * Create a vocabulary and a documents corpus out of a JSON data set, in order to be used by 
     * https://github.com/blei-lab/onlineldavb implementation.
     * 
     * @param args
     */
    public static void main(String[] args) {
        CmdOption option = new CmdOption(args);
        JSONtoLDAC json2mallet = new JSONtoLDAC(option);
    }

    /**
     *
     */
    public JSONtoLDAC(CmdOption option) {
        CorpusJSON corpus = new CorpusJSON(option.trainingFile);
        Dictionary dictionary = new Dictionary(corpus, option.lowUnigrams, option.highUnigrams,
                option.lowBigrams, option.highBigrams);
        Vectorizetf v = new Vectorizetf(dictionary);
        v.vectorizeUnlabeled(corpus, "training.txt");
        v.vectorizeUnlabeled(new CorpusJSON(option.testFile), "testing.txt");
    }
}
