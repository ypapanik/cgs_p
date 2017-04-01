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

import gr.auth.csd.intelligence.cgsp.lda.LDADataset;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import gr.auth.csd.intelligence.cgsp.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class WritePhiTopWords {

    public static void main(String args[]) {
        double[][] phi = Model.readPhi(args[0]);
        Dictionary dictionary = new Dictionary(new CorpusJSON(args[2]), 2, 610, 10, 9);
        LDADataset data = new LDADataset(dictionary, true, phi.length, args[0]);
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]), "UTF-8"))) {
            for (int k = 0; k < phi.length; k++) {
                ArrayList<Pair> wordsProbsList = new ArrayList<>();
                for (int w = 0; w < phi[0].length; w++) {
                    Pair pair = new Pair(w, phi[k][w], false);
                    wordsProbsList.add(pair);
                }
                //print topic				
                writer.write("Label no" + (k + 1) + ":\n");
                Collections.sort(wordsProbsList);
                int iterations = (10 > wordsProbsList.size()) ? wordsProbsList.size() : 10;
                for (int i = 0; i < iterations; i++) {
                    Integer index = (Integer) wordsProbsList.get(i).first;
                    writer.write("\t" + data.getWord(index) + "\t" + wordsProbsList.get(i).second + "\n");
                }
            }
        } catch (IOException e) {
        }
    }
}
