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

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelsperDoc {
    public static void main(String args[]) {
        String dataset = args[0];
        CorpusJSON c = new CorpusJSON(dataset);
        c.reset();
        Document doc;
        TIntIntHashMap lpd = new TIntIntHashMap();
        int corpusSize=0;
        int labelsTotal = 0;
         PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("labelsperdoc"));
        } catch (IOException ex) {
            Logger.getLogger(LabelsperDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        while((doc = c.nextDocument())!=null) {
            
            lpd.adjustOrPutValue(doc.getLabels().size(), 1, 1);
            corpusSize++;
            labelsTotal+=doc.getLabels().size();
        }
        System.out.println(lpd.toString());
        System.out.println("average:"+labelsTotal*1.0/corpusSize);
        TIntIntIterator it = lpd.iterator();
        while(it.hasNext()) {
            it.advance();
            out.println(it.key()+" "+it.value());
        }
        
    }
}
