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

import java.io.Serializable;
import java.util.*;

/**
 * Holds an n-gram
 *
 * @author Grigorios tsoumakas
 * @version 2012.08.08
 */
public class NGram implements Serializable {

    private List<String> nGram;

    /**
     *
     * @param aList
     */
    public NGram(List<String> aList) {
        nGram = aList;
        //Collections.sort(nGram);
    }

    /**
     *
     * @return
     */
    public List<String> getList() {
        return nGram;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return Arrays.toString(nGram.toArray());
    }

    /**
     *
     * @return
     */
    public List<NGram> getSubgrams() {
        if (nGram.size() == 1) {
            return null;
        } else {
            List<String> aList;
            aList = new ArrayList<>();
            for (int i = 0; i < nGram.size() - 1; i++) {
                aList.add(nGram.get(i));
            }
            NGram child1 = new NGram(aList);
            aList = new ArrayList<>();
            for (int i = 1; i < nGram.size(); i++) {
                aList.add(nGram.get(i));
            }
            NGram child2 = new NGram(aList);
            List<NGram> subgrams = new ArrayList<>();
            subgrams.add(child1);
            subgrams.add(child2);
            return subgrams;
        }
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NGram)) {
            return false;
        }
        NGram object = (NGram) o;
        if (nGram.size() != object.nGram.size()) {
            return false;
        }
        for (int i = 0; i < nGram.size(); i++) {
            if (!nGram.get(i).equals(object.nGram.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < nGram.size(); i++) {
            hash = 89 * hash + Objects.hashCode(nGram.get(i));
        }
        return hash;
    }
}
