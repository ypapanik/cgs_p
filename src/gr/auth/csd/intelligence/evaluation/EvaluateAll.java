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

import gnu.trove.map.hash.TObjectDoubleHashMap;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Labels;
import gr.auth.csd.intelligence.utils.Utils;
import java.io.File;
import java.util.TreeMap;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class EvaluateAll {

    public EvaluateAll(Labels labels, String testFile, String bipartitionsFile, String probabilitiesFile) {
        if (probabilitiesFile == null || !(new File(probabilitiesFile)).exists()) {
            MicroAndMacroFLabelPivoted ev = new MicroAndMacroFLabelPivoted(labels, new CorpusJSON(testFile), bipartitionsFile);
            ev.evaluate();
            MacroFDocPivoted ev2 = new MacroFDocPivoted(labels, new CorpusJSON(testFile), bipartitionsFile);
            ev2.evaluate();
            return;
        }
        TreeMap<String, TObjectDoubleHashMap<String>> probs
                = (TreeMap<String, TObjectDoubleHashMap<String>>) Utils.readObject(probabilitiesFile);
        MicroAndMacroFLabelPivoted ev = new MicroAndMacroFLabelPivoted(labels, new CorpusJSON(testFile), bipartitionsFile);
        ev.evaluate();
        MacroFDocPivoted ev2 = new MacroFDocPivoted(labels, new CorpusJSON(testFile), bipartitionsFile);
        ev2.evaluate();
    }

    public EvaluateAll(Labels labels, String testFile, String bipartitionsFile) {
        MicroAndMacroFLabelPivoted ev = new MicroAndMacroFLabelPivoted(labels, new CorpusJSON(testFile), bipartitionsFile);
        ev.evaluate();
        MacroFDocPivoted ev2 = new MacroFDocPivoted(labels, new CorpusJSON(testFile), bipartitionsFile);
        ev2.evaluate();
    }
    
    public EvaluateAll(Labels labels, CorpusJSON testFile, String bipartitionsFile) {
        MicroAndMacroFLabelPivoted ev = new MicroAndMacroFLabelPivoted(labels, testFile, bipartitionsFile);
        ev.evaluate();
        MacroFDocPivoted ev2 = new MacroFDocPivoted(labels, testFile, bipartitionsFile);
        ev2.evaluate();
    }

}
