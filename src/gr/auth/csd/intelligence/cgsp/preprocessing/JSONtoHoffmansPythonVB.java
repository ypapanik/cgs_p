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
public class JSONtoHoffmansPythonVB {

    /**
     *
     * Create a vocabulary and a documents corpus out of a JSON data set, in order to be used by 
     * https://github.com/blei-lab/onlineldavb implementation.
     * 
     * @param args
     */
    public static void main(String[] args) {
        CmdOption option = new CmdOption(args);
        JSONtoHoffmansPythonVB json2mallet = new JSONtoHoffmansPythonVB(option);
    }

    /**
     *
     */
    public JSONtoHoffmansPythonVB(CmdOption option) {
        
        CorpusJSON corpus = new CorpusJSON(option.trainingFile);
        
        //extract and write vocabulary
        Dictionary dictionary = new Dictionary(corpus, option.lowUnigrams, option.highUnigrams,
                option.lowBigrams, option.highBigrams);
        Iterator<Map.Entry<NGram, Integer>> it = dictionary.getId().entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            for(String word:it.next().getKey().getList()) sb.append(word).append(" ");
            sb.append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("vocab.txt"))) {
            bw.append(sb);
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(JSONtoHoffmansPythonVB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        corpus.reset();
        Document doc;
        sb = new StringBuilder();
        while ((doc = corpus.nextDocument()) != null) {
            List<String> sentences = doc.getContentAsSentencesOfTokens(true);
            TObjectIntHashMap<String> phrase = new TObjectIntHashMap<>();
            for(String sentence:sentences) {
                String[] split = sentence.split(" ");
                for(String w:split) phrase.adjustOrPutValue(w, 1, 1);
            }       
            TObjectIntIterator<String> it2 = phrase.iterator();
            sb.append(phrase.size()).append(" ");
            while(it2.hasNext()) {
                it2.advance();
                sb.append(it2.key()).append(":").append(it2.value()).append(" ");
            }
            sb.append("\n");
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("training.txt"))) {
            bw.append(sb);
            bw.flush();
        } catch (IOException ex) {
            Logger.getLogger(JSONtoHoffmansPythonVB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
