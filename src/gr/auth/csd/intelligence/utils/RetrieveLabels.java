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

import gr.auth.csd.intelligence.preprocessing.Labels;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class RetrieveLabels {
    private static Labels labels;
    static int[] a = {3922};

    public static void main(String args[]) {
        labels = Labels.readLabels(args[0]);
        for(int i = 0;i<a.length;i++) {
            System.out.println(labels.getLabel(a[i]));
        }
    }
}
