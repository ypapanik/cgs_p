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
package gr.auth.csd.intelligence.cgsp.mlclassification;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;
import gr.auth.csd.intelligence.cgsp.preprocessing.Corpus;
import gr.auth.csd.intelligence.cgsp.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.Document;
import gr.auth.csd.intelligence.cgsp.preprocessing.Labels;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou
 */
public abstract class MLClassifier {

    /**
     *
     */
    protected int threads;

    /**
     *
     */
    protected double[][] predictions;

    /**
     *
     */
    protected int numLabels = 0;

    /**
     *
     */
    protected Dictionary dictionary = null;

    /**
     *
     */
    protected Labels globalLabels = null;

    /**
     *
     */
    protected Corpus corpus2;

    /**
     *
     */
    protected Corpus corpus;

    /**
     *
     */
    public String[] docMap = null;

    /**
     *
     */
    public String bipartitionsFile = "bipartitions";

    /**
     *
     */
    public String testFile;

    /**
     *
     */
    protected TreeMap<String, THashSet<String>> bipartitions = new TreeMap<>();

    /**
     *
     */
    protected String trainingFile;

    /**
     *
     */
    protected int offset = 0;

    /**
     *
     */
    protected String testFilelibSVM = "testFile.libSVM";;

    /**
     *
     * @param trainingFile
     * @param testFile
     * @param dic
     * @param labels
     * @param threads
     */
    public MLClassifier(String trainingFile, String testFile, String dic, String labels, int threads) {
        this((dic == null)? null:Dictionary.readDictionary(dic), (labels == null)?null:Labels.readLabels(labels),
                ((trainingFile != null) ? new CorpusJSON(trainingFile) : null), 
                ((testFile != null) ? new CorpusJSON(testFile) : null), threads);
        this.trainingFile = trainingFile;
        this.testFile = testFile;
    }

    /**
     *
     * @param trainingFile
     * @param testFile
     * @param dictionary
     * @param labels
     * @param threads
     */
    public MLClassifier(String trainingFile, String testFile, Dictionary dictionary, Labels labels, int threads) {
        this(dictionary, labels, ((trainingFile != null) ? new CorpusJSON(trainingFile) : null),
                ((testFile != null) ? new CorpusJSON(testFile) : null), threads);
        this.trainingFile = trainingFile;
        this.testFile = testFile;
    }

    /**
     *
     * @param dictionary
     * @param labels
     * @param trainingCorpus
     * @param testCorpus
     * @param threads
     */
    public MLClassifier(Dictionary dictionary, Labels labels, Corpus trainingCorpus, Corpus testCorpus, int threads) {
        corpus = trainingCorpus;
        corpus2 = testCorpus;
        this.dictionary = dictionary;
        this.globalLabels = labels;
        numLabels = labels.getSize();
        if (testCorpus != null) {
            bipartitionsFile = "bipartitions";
            createDocMap();
        }
        this.threads = threads;
    }
    
    private void createDocMap() {
        Document doc;
        int id = 0;
        int size = CorpusJSON.size(corpus2);
        docMap = new String[size];
        corpus2.reset();
        while ((doc = corpus2.nextDocument()) != null) {
            docMap[id] = doc.getId();
            id++;
        }
    }

    /**
     *
     * @param bipartitionsFile
     */
    public void bipartitionsWrite(String bipartitionsFile) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(bipartitionsFile)))) {
            Iterator<Map.Entry<String, THashSet<String>>> it = bipartitions.entrySet().iterator();    
            while (it.hasNext()) {
                Map.Entry<String, THashSet<String>> next = it.next();
                Set<String> pred = new TreeSet<>();
                pred.addAll(next.getValue());
                writeToFile(writer, pred, next.getKey());
            }
        } catch (Exception ex) {
            Logger.getLogger(MLClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param writer
     * @param pred
     * @param doc
     */
    protected void writeToFile(PrintWriter writer, Set<String> pred, String doc) {
        writer.write(doc + ": ");
        String[] pred2 = pred.toArray(new String[0]);
        for (int i = 0; i < pred2.length - 1; i++) {
            writer.write(pred2[i] + "; ");
        }
        if (pred2.length != 0) {
            writer.write(pred2[pred2.length - 1] + "\n");
        } else {
            writer.write("\n");
        }
    }
    
    /**
     *
     * @param mc
     * @return
     */
    public TreeMap<String, THashSet<String>> predict(TIntHashSet mc) {
        predictInternal(mc);
        //create bipartitions
        createBipartitions();
        return bipartitions;
   
    }
    
    /**
     *
     * @param mc
     */
    public void predictWithoutBipartitions(TIntHashSet mc) {
        predictInternal(mc);
   
    }
    
    /**
     *
     * @param metalabelerFile
     */
    public void createBipartitionsFromRanking(String metalabelerFile) {
        int corpusSize = CorpusJSON.size(corpus2);
        double[] metalabelerPredictions = new double[corpusSize];
        if (metalabelerFile != null) {
            metalabelerPredictions = MetaModel.getMetaModelPrediction(metalabelerFile, 
                    corpus2, dictionary, corpusSize, this.testFilelibSVM);
        }
        for (int doc = 0; doc < corpusSize; doc++) {
            String pmid = docMap[doc];
            if (!bipartitions.containsKey(pmid)) {
                bipartitions.put(pmid, new THashSet<String>());
            }
            //System.out.println(metalabelerPredictions[doc]);
            int d = offset+ (int) Utils.round(metalabelerPredictions[doc]);
            if (d < 1) {
                d = 1;
            }
            for (int k = 0; k < d; k++) {
                int label = Utils.maxIndex(predictions[doc]);
                //System.out.println(predictions[doc][label]);
                predictions[doc][label] = Double.MIN_VALUE;
                
                bipartitions.get(pmid).add(globalLabels.getLabel(label + 1));
            }
            if(doc==0) System.out.println(bipartitions.get(pmid));
        }
    }
    
    /**
     *
     */
    public void createBipartitions() {
        //System.out.println(predictions[0].length);
        for (int doc = 0; doc < predictions.length; doc++) {
            String pmid = docMap[doc];
            if (!bipartitions.containsKey(pmid)) {
                bipartitions.put(pmid, new THashSet<String>());
            }
            for (int label = 0; label < predictions[doc].length; label++) {
                if (predictions[doc][label] > 0) {
                    bipartitions.get(pmid).add(globalLabels.getLabel(label + 1));
                }
            }
        }
        //System.out.println(bipartitions.size());
    }

    /**
     *
     * @param bipartitions
     */
    public void setBipartitions(TreeMap<String, THashSet<String>> bipartitions) {
        this.bipartitions = bipartitions;
    }
    
    /**
     *
     */
    public abstract void train();

    /**
     *
     * @param mc
     * @return
     */
    public abstract double[][] predictInternal(TIntHashSet mc);
}
