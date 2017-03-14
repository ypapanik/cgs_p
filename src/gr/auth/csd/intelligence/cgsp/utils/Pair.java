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
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class Pair implements Comparable<Pair> {

    /**
     *
     * @param arr
     * @param topic
     * @return
     */
    public static int indexOf(Pair[] arr, int topic) {
        int i;
        for (i = 0; i < arr.length; i++) {
            if (topic == (Integer) arr[i].first) {
                break;
            }
        }
        return i;
    }

    /**
     *
     */
    public Object first;

    /**
     *
     */
    public Comparable second;
    private double value;

    /**
     *
     */
    public static boolean naturalOrder = false;

    /**
     *
     * @param k
     * @param v
     */
    public Pair(Object k, Comparable v){
        first = k;
        value = (double) v;
        second = v;		
    }

    /**
     *
     * @param k
     * @param v
     * @param naturalOrder
     */
    public Pair(Object k, Comparable v, boolean naturalOrder){
        first = k;
        second = v;
        value = (double) v;
        Pair.naturalOrder = naturalOrder; 
    }

    /**
     *
     * @param p
     * @return
     */
    @Override
    public int compareTo(Pair p){
        if (naturalOrder)
            return this.second.compareTo(p.second);
        else return -this.second.compareTo(p.second);
    }

    /**
     *
     * @param second
     */
    public void setSecond(int second) {
        this.second = second;
    }

    /**
     *
     */
    public void decrease() {
        value--;
        this.second = value;
    }

    /**
     *
     */
    public void increase() {
        value++;
        this.second = value;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }
    
    
}
