package gr.auth.csd.intelligence.preprocessing;

import gr.auth.csd.intelligence.features.FeatureScoring;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import gr.auth.csd.intelligence.features.Wf_Idf;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vectorize {

    protected static Dictionary dictionary;
    protected FeatureScoring fs;
    private final Labels labels;

    public Vectorize(Dictionary d, boolean zoning, Labels labels) {
        dictionary = d;
        fs = new Wf_Idf(d, zoning);
        this.labels = labels;
    }

    protected Map<NGram, Integer> nGramFrequencyFromTokenSentences(List<String> lines, int n) {
        Map<NGram, Integer> termFrequency = new HashMap<>();
        for (String line : lines) {
            String[] tokens = line.split(" ");
            for (int i = 0; i < tokens.length + 1 - n; i++) {
                List<String> aList = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    aList.add(tokens[i + j]);
                }
                NGram ngram = new NGram(aList);
                List<String> list = ngram.getList();
                if (n > 1) {
                    boolean skip = false;
                    for (String token : list) {
                        if (Dictionary.getTokensToIgnore().contains(token)) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip == true) {
                        continue;
                    }
                } else if (Dictionary.getTokensToIgnore().contains(list.get(0))) {
                    continue;
                }
                if (dictionary.getDocumentFrequency().containsKey(ngram)) {
                    if (termFrequency.containsKey(ngram)) {
                        termFrequency.put(ngram, termFrequency.get(ngram) + 1);
                    } else {
                        termFrequency.put(ngram, 1);
                    }
                }
            }
        }
        return termFrequency;
    }

    protected Map<Integer, Double> vectorize(List<String> lines, boolean lengthNormalization, Document doc) {
        Map<Integer, Double> vector = new TreeMap<>();
        for (int j = 0; j < dictionary.getNGramSizes().size(); j++) {
            Map<NGram, Integer> termFrequency = nGramFrequencyFromTokenSentences(lines, dictionary.getNGramSizes().get(j));
            Iterator<Map.Entry<NGram, Integer>> entries;
            entries = termFrequency.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<NGram, Integer> entry = entries.next();
                vector.put(dictionary.getId().get(entry.getKey()), fs.fsMethod(entry.getKey(), doc, entry.getValue(), labels));
            }
        }

        if (lengthNormalization) {
            vector = normalizeVector(vector);
        }
        return vector;
    }

    protected Map<Integer, Double> normalizeVector(Map<Integer, Double> vector) {
        Collection<Double> weights = vector.values();
        double length = 0;
        for (Double d : weights) {
            length += d * d;
        }
        length = Math.sqrt(length);
        if (length == 0) {
            length = 1;
        }
        Iterator<Map.Entry<Integer, Double>> values = vector.entrySet().iterator();
        while (values.hasNext()) {
            Map.Entry<Integer, Double> entry = values.next();
            entry.setValue(entry.getValue() / length);

        }
        return vector;
    }

    public void vectorizeTrain(Corpus corpus, String libsvmFilename, String labelsFilename, String metaLabelsFilename, Labels labels) {
//        File f = new File(libsvmFilename);
//        File f2 = new File(labelsFilename);
//        File f3 = new File(metaLabelsFilename);
//        if(f.exists()&&f2.exists()&&f3.exists()) {System.out.println("Found existing vectorized file "+ libsvmFilename);return;}
        vectorizeLabeled(corpus, libsvmFilename, labelsFilename, true, metaLabelsFilename, labels);
    }

    public void vectorizeUnlabeled(Corpus aCorpus, String libsvmFilename) {
//        File f = new File(libsvmFilename);
//        if(f.exists()) {System.out.println("Found existing vectorized file "+ libsvmFilename);return;}
        try (BufferedWriter output = Files.newBufferedWriter(Paths.get(libsvmFilename), Charset.forName("UTF-8"))) {
            //System.out.println(new Date() + " Vectorizing unlabeled data...");

            // read each file in given directory and parse the text as follows
            List<String> lines;
            Document doc;
            int counter = 0;
            aCorpus.reset();
            while ((doc = aCorpus.nextDocument()) != null) {
                counter++;
                lines = doc.getContentAsSentencesOfTokens(false);
                Map<Integer, Double> vector = vectorize(lines, true, doc);
                if (vector != null) {
                    // output features in shell libsvm file
                    Iterator<Map.Entry<Integer, Double>> values = vector.entrySet().iterator();
                    StringBuilder sb = new StringBuilder();
                    sb.append("0"); //the label value
                    while (values.hasNext()) {
                        Map.Entry<Integer, Double> entry = values.next();
                        if (entry.getValue() != 0) {
                            sb.append(" ");
                            sb.append(entry.getKey() + 1).append(":").append(String.format(Locale.US, "%.6f", entry.getValue()));
                        }
                    }
                    sb.append("\n");
                    output.write(sb.toString());
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(new Date() + " Finished vectorizing unlabeled data.");
    }

    public ArrayList<TIntDoubleHashMap> vectorizeUnlabeled(Corpus aCorpus) {
        ArrayList<TIntDoubleHashMap> corpusVectors = new ArrayList<>();
        // read each file in given directory and parse the text as follows
        List<String> lines;
        Document doc;
        aCorpus.reset();
        while ((doc = aCorpus.nextDocument()) != null) {
            lines = doc.getContentAsSentencesOfTokens(false);
            Map<Integer, Double> vector = vectorize(lines, false, doc);
            if (vector != null) {
                // output features in shell libsvm file
                TIntDoubleHashMap v = new TIntDoubleHashMap();
                v.putAll(vector);
                corpusVectors.add(v);
//                    Iterator<Map.Entry<Integer, Double>> values = vector.entrySet().iterator();
//                    while (values.hasNext()) {
//                        Map.Entry<Integer, Double> entry = values.next();
//                        if (entry.getValue() != 0) {
//                            
//                            sb.append(entry.getKey() + 1).append(":").append(String.format(Locale.US, "%.6f", entry.getValue()));
//                        }
//                    }
            }

        }
        return corpusVectors;
    }

    protected void vectorizeLabeled(Corpus corpus, String libsvmFilename, String labelsFile, boolean perLabel, String metaTrainFileName, Labels corpusLabels) {
        corpus.reset();
        try (BufferedWriter output = Files.newBufferedWriter(Paths.get(libsvmFilename), Charset.forName("UTF-8"));
                ObjectOutputStream outLabels = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(labelsFile)))) {
            //System.out.println(new Date() + " Vectorizing labeled data...");

            TIntObjectHashMap<TreeSet<Integer>> labelValues = new TIntObjectHashMap<>();
            TIntArrayList targetValues = new TIntArrayList();
            ArrayList<TIntList> targetValuesPerDoc = new ArrayList<>();

            // read each file in given directory and parse the text as follows
            List<String> lines;
            Document doc;
            int counter = 0;
            while ((doc = corpus.nextDocument()) != null) {
                counter++;
                lines = doc.getContentAsSentencesOfTokens(false);
                Map<Integer, Double> vector = vectorize(lines, true, doc);
                if (vector != null) {
                    // output features in shell libsvm file
                    Iterator<Map.Entry<Integer, Double>> values = vector.entrySet().iterator();
                    StringBuilder sb = new StringBuilder();
                    sb.append("0"); //the label value
                    while (values.hasNext()) {
                        Map.Entry<Integer, Double> entry = values.next();
                        if (entry.getValue() != 0) {
                            sb.append(" ");
                            sb.append(entry.getKey() + 1).append(":").append(String.format(Locale.US, "%.6f", entry.getValue()));
                        }
                    }
                    sb.append("\n");
                    output.write(sb.toString());
                }

                // record labels
                Set<String> meshTerms = doc.getLabels();
                int cardinality = 0;
                TIntList docLabels = new TIntArrayList();
                for (String term : meshTerms) {
                    //System.out.println("line: " + line);
                    Integer x = corpusLabels.getIndex(term);
                    if (x == -1) { //CHANGE in week 5
                        //System.out.println("Label " + line + " not in training corpus");                                
                    } else {
                        docLabels.add(x);
                        cardinality++;
                        TreeSet<Integer> sortedSet;
                        if (labelValues.contains(x)) {
                            sortedSet = labelValues.get(x);
                        } else {
                            sortedSet = new TreeSet<>();
                        }
                        sortedSet.add(counter - 1);
                        labelValues.put(x, sortedSet);
                    }
                }
                //System.out.println("docLabels:" + Arrays.toString(docLabels.toArray()));
                targetValuesPerDoc.add(docLabels);
                targetValues.add(cardinality);
            }

            if (perLabel) {
                outLabels.writeObject(labelValues);
                try (ObjectOutputStream metaTrain = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(metaTrainFileName)))) {
                    metaTrain.writeObject(targetValues);
                }
            } else {
                outLabels.writeObject(targetValuesPerDoc);
            }

        } catch (IOException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println(new Date() + " Finished vectorizing labeled data.");
    }

    public void vectorizeMetalabels(Corpus corpus, String metaTrainFileName,
            Labels corpusLabels, TIntHashSet modelChoice) {
        corpus.reset();
        //System.out.println(new Date() + " Vectorizing for metalabels...");
        TIntArrayList targetValues = new TIntArrayList();
        Document doc;
        int counter = 0;
        while ((doc = corpus.nextDocument()) != null) {
            counter++;
            Set<String> meshTerms = doc.getLabels();
            int cardinality = 0;
            int cardinalitytotal = 0;
            TIntList docLabels = new TIntArrayList();
            for (String term : meshTerms) {
                Integer x = corpusLabels.getIndex(term);
                if ((x != null) && (!modelChoice.contains(x))) {
                    docLabels.add(x);
                    cardinality++;
                }
                cardinalitytotal++;
            }
            targetValues.add(cardinality);
            System.out.println(doc.getId() + ": " + cardinalitytotal + ":" + cardinality);
        }
        try (ObjectOutputStream metaTrain = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(metaTrainFileName)))) {
            metaTrain.writeObject(targetValues);
        } catch (IOException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println(new Date() + " Finished vectorizing for metalabels.");
    }
}
