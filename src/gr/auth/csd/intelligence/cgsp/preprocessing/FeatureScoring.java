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

import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.Document;
import gr.auth.csd.intelligence.cgsp.preprocessing.Labels;
import gr.auth.csd.intelligence.cgsp.preprocessing.NGram;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import org.apache.commons.lang3.text.WordUtils;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public abstract class FeatureScoring {

    /**
     *
     */
    protected static int corpusSize;

    /**
     *
     */
    protected static Dictionary dictionary;

    /**
     *
     */
    protected final boolean zoning;
    
    /**
     *
     * @param dict
     * @param zoning
     */
    public FeatureScoring(Dictionary dict, boolean zoning) {
        dictionary = dict;
        corpusSize = dictionary.getCorpusSize();
        this.zoning = zoning;
    }

    /**
     *
     * @param feature
     * @param doc
     * @param wf
     * @param labels
     * @return
     */
    public abstract double fsMethod(NGram feature, Document doc, int wf, Labels labels);

    /**
     *
     * @param feature
     * @param doc
     * @param wf
     * @param labels
     * @return
     */
    protected double zoning(NGram feature, Document doc, double wf, Labels labels) {
        double wf_temp = wf;
        String token = Utils.concatenateArrayOfStrings(feature.getList()).toLowerCase();
        String title = doc.getTitle().toLowerCase();
        String [] titletokens = title.split(" ");
        for(String tok:titletokens) {
            if(token.contentEquals(tok)) {
                wf_temp*=2;
                break;
            }
        }
        if(labels.getIndex(WordUtils.capitalize(token))!=-1) {
            wf_temp*=1.25;
        }
        return wf_temp;
    }

    /**
     *
     * @param label
     * @param pos
     */
    public abstract void read(int label, int pos);

    /**
     *
     * @param i
     * @param featureFrequency
     * @return
     */
    public double fsMethod(int i, int featureFrequency) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}