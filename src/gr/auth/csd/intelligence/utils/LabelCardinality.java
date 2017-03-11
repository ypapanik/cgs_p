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
package gr.auth.csd.intelligence.utils;

import gr.auth.csd.intelligence.preprocessing.Corpus;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelCardinality {
    public static void main (String args[]) {
        String pathTrain = args[0];
        Corpus corpus = new CorpusJSON(pathTrain);        
        corpus.reset();
        Document document;
        double sum =0;
        double i=0;
        while ((document = corpus.nextDocument()) != null) {
            sum+=document.getLabels().size();
            i++;
        }
        System.out.println(sum/i);        
    }
}
