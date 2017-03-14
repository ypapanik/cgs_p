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

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Grigorios Tsoumakas
 * @version 2013.07.22
 */
public class Labels implements Serializable {

    private Map<String, Integer> indexOfLabel;
    private Map<Integer, String> labelOfIndex;
    private TObjectIntHashMap<String> positiveInstances;
    private THashSet<String> frequentLabels;
    static final long serialVersionUID = -1340101531076080552L;
    private THashSet<String> rareLabels;

    /**
     *
     * @param corpus
     */
    public Labels(Corpus corpus) {
        System.out.printf("%tc: Extracting labels.%n", new Date());
        Document document;
        Set<String> labels = new HashSet<>();
        positiveInstances = new TObjectIntHashMap<>();
        corpus.reset();
        while ((document = corpus.nextDocument()) != null) {
            labels.addAll(document.getLabels());
            for (String l : document.getLabels()) {
                if (positiveInstances.contains(l)) {
                    positiveInstances.put(l, positiveInstances.get(l) + 1);
                } else {
                    positiveInstances.put(l, 1);
                }
            }
        }

        indexOfLabel = new HashMap<>();
        labelOfIndex = new HashMap<>();

        // establish unique identification of labels
        int index = 1;
        for (String label : labels) {
            indexOfLabel.put(label, index);
            labelOfIndex.put(index, label);
            index++;
        }
        System.out.printf("%tc: Found %d labels.%n", new Date(), labels.size());
        //findFrequent(2000);
        //findRare(2);
        //keep();
        //printPositiveInstances(1000);
        //sampleTenPercent();
    }

    /**
     *
     * @param labels
     */
    public Labels(Set<String> labels) {
        positiveInstances = new TObjectIntHashMap<>();
        indexOfLabel = new HashMap<>();
        labelOfIndex = new HashMap<>();

        // establish unique identification of labels
        int index = 1;
        for (String label : labels) {
            indexOfLabel.put(label, index);
            labelOfIndex.put(index, label);
            index++;
        }
        System.out.printf("%tc: Found %d labels.%n", new Date(), labels.size());
        //findFrequent(2000);
        //findRare(2);
        keep();
        //printPositiveInstances(1000);
        //sampleTenPercent();
    }
    
    /**
     *
     * @param label
     * @return
     */
    public int getIndex(String label) {
        if (!indexOfLabel.containsKey(label)) {
            return -1;
        }
        return indexOfLabel.get(label);
    }

    /**
     *
     * @param index
     * @return
     */
    public String getLabel(int index) {
//        if (!labelOfIndex.containsKey(index)) {
//            return null;
//        }
        return labelOfIndex.get(index);
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return indexOfLabel.size();
    }

    /**
     *
     * @return
     */
    public TObjectIntHashMap<String> getPositiveInstances() {
        return positiveInstances;
    }

    /**
     *
     * @return
     */
    public THashSet<String> getFrequentLabels() {
        return frequentLabels;
    }

    /**
     *
     * @return
     */
    public Set<String> getLabels() {
        return indexOfLabel.keySet();
    }

    /**
     *
     * @param keep
     */
    public void findFrequent(int keep) {
        frequentLabels = new THashSet<>();
        for (String s : positiveInstances.keySet()) {
            if (positiveInstances.get(s) > keep) {
                frequentLabels.add(s);
            }
        }
        System.out.println(new Date() + "Found " + frequentLabels.size() + " labels with more than " + keep + " training examples");
    }

    /**
     *
     * @param keep
     */
    public void findRare(int keep) {
        rareLabels = new THashSet<>();
        for (String s : positiveInstances.keySet()) {
            if (positiveInstances.get(s) < keep) {
                rareLabels.add(s);
            }
        }
        System.out.println(new Date() + "Found " + rareLabels.size() + " labels with less than " + keep + " training examples");
    }

    private void printPositiveInstances(int keep) {
        int i = 0;
        for (String s : positiveInstances.keySet()) {
            if (positiveInstances.get(s) > keep) {
                System.out.println(indexOfLabel.get(s) + ": " + positiveInstances.get(s));
            }
        }
    }

    private void print() {
        for (String s : indexOfLabel.keySet()) {
            System.out.println(s + " " + indexOfLabel.get(s));
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String args[]) {

        String pathTrain = args[0];
        String fileLabels = args[1];
        Corpus corpus = new CorpusJSON(pathTrain);
        Labels labels = new Labels(corpus);
        /*        for(String s:labels.getPositiveInstances().keySet()) {
         if(labels.getPositiveInstances().get(s)>(Integer.parseInt(args[2]))) {
         System.out.println(s);
         }
         }*/
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileLabels))) {
            output.writeObject(labels);
        } catch (IOException e) {
            System.out.println(e);
        }
        //labels.print();
    }

    private void keep() {
        System.out.printf("%tc: Extracting frequent labels.%n", new Date());
        Set<String> labels = new HashSet<>();
        labels.addAll(this.frequentLabels);
        labels.addAll(this.rareLabels);

        indexOfLabel = new HashMap<>();
        labelOfIndex = new HashMap<>();

        // establish unique identification of labels
        int index = 1;
        for (String label : labels) {
            indexOfLabel.put(label, index);
            labelOfIndex.put(index, label);
            index++;
        }
    }

    /**
     *
     * @param labelsFile
     * @return
     */
    public static Labels readLabels(String labelsFile) {
        Labels labels = (Labels) Utils.readObject(labelsFile);
        return labels;
    }

    /**
     *
     * @param labelsFile
     */
    public void writeLabels(String labelsFile) {
        Utils.writeObject(this, labelsFile);
    }
    
    /*Counts how many positive instances of a given labelset exist in the given corpus */

    /**
     *
     * @param c
     * @param labelSet
     * @return
     */

    public static int positiveInstancesInCorpus(Corpus c, Set<String> labelSet) {
        int positiveInstances = 0;
        Document doc;
        c.reset();
        while((doc = c.nextDocument())!=null) {
            TObjectHashIterator<String> it = doc.getLabels().iterator();
            while(it.hasNext()) {
                String docLabel = it.next();
                if(labelSet.contains(docLabel)) {
                    positiveInstances++;
                    break;
                }
            }
        }
        return positiveInstances;
    }
    
    /*Returns a set of labels for the given set of indexes */

    /**
     *
     * @param indexes
     * @return
     */

    public THashSet<String> getLabels(TIntHashSet indexes) {
        THashSet<String> labels=new THashSet<>();
        TIntIterator it = indexes.iterator();
        while(it.hasNext()) {
            labels.add(this.getLabel(it.next()));
        }
        return labels;
    }
}
