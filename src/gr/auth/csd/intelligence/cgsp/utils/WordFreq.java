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

import gr.auth.csd.intelligence.cgsp.preprocessing.NGram;

/**
 * @author Grigorios Tsoumakas
 * @date 2013.05.07
 */
public class WordFreq implements Comparable<WordFreq> {
    NGram ngram;
    int frequency;
    
    /**
     *
     * @param ngram
     * @param frequency
     */
    public WordFreq(NGram ngram, int frequency) {
        this.ngram = ngram;
        this.frequency = frequency;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return ngram.toString() + " : " + frequency;
    }
    
    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(WordFreq o) {
        return (o.frequency-this.frequency)*10000+o.ngram.toString().compareTo(ngram.toString());
    }
}
