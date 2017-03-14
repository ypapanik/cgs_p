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
package gr.auth.csd.intelligence.cgsp.lda;
import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class Document implements Serializable {

    private final TIntArrayList words;
    private final String pmid;
    private final String journal;
    private int[] labels = null;

    /**
     *
     * @return
     */
    public TIntArrayList getWords() {
        return words;
    }

    /**
     *
     * @return
     */
    public String getPmid() {
        return pmid;
    }

    /**
     *
     * @return
     */
    public String getJournal() {
        return journal;
    }

    /**
     *
     * @param doc
     * @param id
     * @param tlabels
     * @param j
     */
    public Document(TIntArrayList doc, String id, int[] tlabels, String j)
    {
        words = doc;
        pmid = id;
        journal = j;
        if(tlabels != null) labels = tlabels;
    }

    /**
     *
     * @return
     */
    public int[] getLabels() {
        return labels;
    }

}
