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
package gr.auth.csd.intelligence.evaluation;

import gnu.trove.map.hash.TIntObjectHashMap;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;
import gr.auth.csd.intelligence.preprocessing.Labels;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou
 */
public abstract class Evaluator {
    CorpusJSON corpus;
    protected String filenamePredictions;
    Labels labels;
    protected HashMap<String, TreeSet<String>> bipartitions = new HashMap<>();
    public abstract void evaluate();

    protected TreeSet<String> getTruth(Document doc) {
        TreeSet<String> truth = new TreeSet<>();
        for (String label : doc.getLabels()) {
            if (labels.getLabels().contains(label)) {
                truth.add(label);
            }
        }
        return truth;
    }
    
    protected void readBipartitions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filenamePredictions))) {
            String line;
            while ((line = reader.readLine()) != null) {
                
                String[] tokens = line.split(": ");
                String pmid = tokens[0];
                if (tokens.length > 1) {
                    tokens = tokens[1].split("; ");
                }
                TreeSet<String> pred = new TreeSet<>();
                pred.addAll(Arrays.asList(tokens));
                bipartitions.put(pmid, pred);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Evaluator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
