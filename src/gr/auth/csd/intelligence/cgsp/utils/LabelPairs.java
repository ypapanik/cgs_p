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
import gr.auth.csd.intelligence.cgsp.preprocessing.Document;
import gr.auth.csd.intelligence.cgsp.preprocessing.Labels;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelPairs {
    static Labels labels;
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        CorpusJSON c= new CorpusJSON(args[0]);
        labels  = new Labels(c);
        int[][] labelpairs = new int[labels.getSize()][labels.getSize()];
        Document doc;
        c.reset();
        while((doc=c.nextDocument())!=null) {
            Set<String> docLabels = doc.getLabels();
            String[] dl= new String[docLabels.size()];
            int i=0;
            for(String l:docLabels) dl[i++]= l;
            
            for(i=0;i<dl.length;i++) {
                int index1 = labels.getIndex(dl[i]);
                for(int j=i+1;j<dl.length;j++) {
                    int index2 = labels.getIndex(dl[j]);
                    labelpairs[index1][index2]++;
                    labelpairs[index2][index1]++;
                }
            }
        }
        
        for(int i=0;i<labelpairs.length;i++) {
            for(int j=0;j<labelpairs[j].length;j++) {
                
            }
        }
    }
}
