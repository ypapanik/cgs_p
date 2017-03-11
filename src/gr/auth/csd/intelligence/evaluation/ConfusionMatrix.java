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

package gr.auth.csd.intelligence.evaluation;

import gr.auth.csd.intelligence.preprocessing.Labels;
import java.util.TreeSet;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class ConfusionMatrix {

    private int tp=0, fp=0, fn=0, tn=0;

    public int getTp() {
        return tp;
    }

    public int getFp() {
        return fp;
    }

    public int getFn() {
        return fn;
    }

    public int getTn() {
        return tn;
    }

    public ConfusionMatrix(TreeSet<String> pred, TreeSet<String> truth, Labels labels) {
        //add true and predicted labels to a single set and then iterate over it
        TreeSet<String> all = new TreeSet<>(truth);
        all.addAll(pred);
        for (String label : all) {
            int j = labels.getIndex(label) - 1;
            if (j == -2) {
                //System.out.println(label);
                continue;
            }
            if (truth.contains(label)) {
                if (pred.contains(label)) {
                    tp++;
                } else {
                    fn++;
                }
            } else {
                if (pred.contains(label)) {
                    fp++;
                } else {
                    tn++;
                }
            }
        }
    }

    public ConfusionMatrix(TreeSet<String> pred, TreeSet<String> truth, Labels labels, double[] tpPerLabel, 
            double[] fnPerLabel, double[] fpPerLabel, double[] tnPerLabel) {
        //add true and predicted labels to a single set and then iterate over it
        TreeSet<String> all = new TreeSet<>(truth);
        all.addAll(pred);
        for (String label : all) {
            int j = labels.getIndex(label) - 1;
            if (j == -2) {
                //System.out.println(label);
                continue;
            }
            if (truth.contains(label)) {
                if (pred.contains(label)) {
                    tpPerLabel[j]++;
                    tp++;
                } else {
                    fnPerLabel[j]++;
                    fn++;
                }
            } else {
                if (pred.contains(label)) {
                    fpPerLabel[j]++;
                    fp++;
                } else {
                    tnPerLabel[j]++;
                    tn++;
                }
            }
        }
    }

}
