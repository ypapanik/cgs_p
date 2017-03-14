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
package gr.auth.csd.intelligence.cgsp.utils;

import gr.auth.csd.intelligence.cgsp.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.Document;
import gr.auth.csd.intelligence.cgsp.preprocessing.NGram;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yannis Papanikolaou
 */
public class AverageDocumentLength {
    static Dictionary dic;

    /**
     *
     * @param args
     */
    public static void main(String args[]) {
        String dataset = args[0];
        //String dict = args[1];
        CorpusJSON c = new CorpusJSON(dataset);
        //dic = Dictionary.readDictionary(dict);
        dic = new Dictionary(c, 1, 10000000,2, 1);
        Document doc;
        double sum=0;
        int counter=0;
        c.reset();
        while((doc = c.nextDocument())!=null) {
            List<String> lines = doc.getContentAsSentencesOfTokens(false);
            ArrayList<NGram> temp = nGramsFromSentences(lines);
            //System.out.println(temp.toString());
            System.out.println(temp.size());
            sum+=temp.size();
            counter++;
        }
        sum= sum/counter;
        System.out.println("Average length:"+sum);
    }
    
    /**
     *
     * @param lines
     * @return
     */
    protected static ArrayList<NGram> nGramsFromSentences(List<String> lines) {
        ArrayList<NGram> wordIds = new ArrayList<>();
        for (String line : lines) {
            String[] tokens = line.split(" ");
            for (int i = 0; i < tokens.length; i++) {
                List<String> aList = new ArrayList<>();
                aList.add(tokens[i]);
                NGram ngram = new NGram(aList);
                List<String> list = ngram.getList();
                if (Dictionary.getTokensToIgnore().contains(list.get(0))) continue;
                
                if (dic.getDocumentFrequency().containsKey(ngram)) {
                    wordIds.add(ngram);
                }
            }
        }
        return wordIds;
    }
}
