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
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class Pair implements Comparable<Pair> {

    public static int indexOf(Pair[] arr, int topic) {
        int i;
        for (i = 0; i < arr.length; i++) {
            if (topic == (Integer) arr[i].first) {
                break;
            }
        }
        return i;
    }
    public Object first;
    public Comparable second;
    private double value;
    public static boolean naturalOrder = false;

    public Pair(Object k, Comparable v){
        first = k;
        value = (double) v;
        second = v;		
    }

    public Pair(Object k, Comparable v, boolean naturalOrder){
        first = k;
        second = v;
        value = (double) v;
        Pair.naturalOrder = naturalOrder; 
    }

    @Override
    public int compareTo(Pair p){
        if (naturalOrder)
            return this.second.compareTo(p.second);
        else return -this.second.compareTo(p.second);
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void decrease() {
        value--;
        this.second = value;
    }
    public void increase() {
        value++;
        this.second = value;
    }
    
    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }
    
    
}
