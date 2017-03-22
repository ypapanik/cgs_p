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

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * Vectorize a data set int tf.
 * 
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class Vectorizetf extends Vectorize {

    public Vectorizetf(Dictionary d) {
        super(d, false, null);
    }


    /**
     *
     * @param lines
     * @param lengthNormalization
     * @param doc
     * @return
     */
    @Override
    protected Map<Integer, Double> vectorize(List<String> lines, boolean lengthNormalization, Document doc) {
        Map<Integer, Double> vector = new TreeMap<>();
        for (int j = 0; j < dictionary.getNGramSizes().size(); j++) {
            Map<NGram, Integer> termFrequency = nGramFrequencyFromTokenSentences(lines, dictionary.getNGramSizes().get(j));
            Iterator<Map.Entry<NGram, Integer>> entries;
            entries = termFrequency.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<NGram, Integer> entry = entries.next();
                vector.put(dictionary.getId().get(entry.getKey()), entry.getValue().doubleValue());
            }
        }
        return vector;
    }



    /**
     *
     * @param aCorpus
     * @param libsvmFilename
     */
    @Override
    public void vectorizeUnlabeled(Corpus aCorpus, String libsvmFilename) {
//        File f = new File(libsvmFilename);
//        if(f.exists()) {System.out.println("Found existing vectorized file "+ libsvmFilename);return;}
        try (BufferedWriter output = Files.newBufferedWriter(Paths.get(libsvmFilename), Charset.forName("UTF-8"))) {
            //System.out.println(new Date() + " Vectorizing unlabeled data...");

            // read each file in given directory and parse the text as follows
            List<String> lines;
            Document doc;
            int counter = 0;
            aCorpus.reset();
            while ((doc = aCorpus.nextDocument()) != null) {
                counter++;
                lines = doc.getContentAsSentencesOfTokens(false);
                Map<Integer, Double> vector = vectorize(lines, true, doc);
                if (vector != null) {
                    // output features in shell libsvm file
                    Iterator<Map.Entry<Integer, Double>> values = vector.entrySet().iterator();
                    StringBuilder sb = new StringBuilder();
                    sb.append(vector.size()); //the label value
                    while (values.hasNext()) {
                        Map.Entry<Integer, Double> entry = values.next();
                        if (entry.getValue() != 0) {
                            sb.append(" ");
                            sb.append(entry.getKey() + 1).append(":").append(entry.getValue().intValue());
                        }
                    }
                    sb.append("\n");
                    output.write(sb.toString());
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(new Date() + " Finished vectorizing unlabeled data.");
    }
}
