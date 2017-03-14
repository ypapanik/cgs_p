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

import gr.auth.csd.intelligence.cgsp.utils.WordFreq;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import gr.auth.csd.intelligence.cgsp.utils.StopWords;
import gr.auth.csd.intelligence.cgsp.utils.Utils;
import java.io.*;
import java.util.*;

/**
 * @author Grigorios Tsoumakas
 * @version 2013.07.19
 */
public class Dictionary implements Serializable {

    static final long serialVersionUID = 8350541949393366632L;

    /**
     *
     */
    protected Map<NGram, Integer> id;

    /**
     *
     */
    protected Map<Integer, NGram> ngram;
    private int corpusSize;

    /**
     *
     */
    protected TObjectIntHashMap<NGram> documentFrequency;
    private List<Integer> nGramSizes;
    private static final Set<String> tokensToIgnore;
    //protected static Tokenizer tokenizer = new Tokenizer();
    private static final StopWords sw = new StopWords(4);

    static {
        tokensToIgnore = new HashSet<>();
        tokensToIgnore.addAll(Arrays.asList(new String[]{":", ";", ".", ",", "-lrb-", "-rrb-", "--"}));
    }

    /**
     *
     */
    public Dictionary() {
        nGramSizes = new ArrayList<>();
        documentFrequency = new TObjectIntHashMap<>();
        //tokenizer = new Tokenizer();
    }

    /**
     *
     * @param corpus
     * @param lowUnigrams
     * @param highUnigrams
     * @param lowBigrams
     * @param highBigrams
     */
    public Dictionary(CorpusJSON corpus, int lowUnigrams, int highUnigrams, int lowBigrams, int highBigrams) {
        nGramSizes = new ArrayList<>();
        documentFrequency = new TObjectIntHashMap<>();
        //tokenizer = new Tokenizer();
        addNGrams(corpus, 2, lowUnigrams, highUnigrams, lowBigrams, highBigrams);
        finalizeDictionary();
        System.out.println("Dictionary size: " + id.size());
        //showSorted();
    }

    /**
     *
     * @return
     */
    public TObjectIntHashMap<NGram> getDocumentFrequency() {
        return documentFrequency;
    }

    /**
     *
     * @return
     */
    public Map<NGram, Integer> getId() {
        return id;
    }

    /**
     *
     * @param id
     * @return
     */
    public NGram getNgram(int id) {
        return ngram.get(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public String getString(int id) {
        String feat = "";
        for (String s : ngram.get(id).getList()) {
            feat += s;
        }
        return feat;
    }

    /**
     *
     * @return
     */
    public static Set<String> getTokensToIgnore() {
        return tokensToIgnore;
    }

    /**
     *
     * @return
     */
    public List<Integer> getNGramSizes() {
        return nGramSizes;
    }

    /**
     *
     * @return
     */
    public int getCorpusSize() {
        return corpusSize;
    }

    /**
     *
     */
    public void showStats() {
        TObjectIntIterator iterator = documentFrequency.iterator();
        int unigrams = 0;
        int bigrams = 0;
        while (iterator.hasNext()) {
            iterator.advance();
            NGram ngram = (NGram) iterator.key();
            int size = ngram.getList().size();
            if (size == 1) {
                unigrams++;
            }
            if (size == 2) {
                bigrams++;
            }
        }
        System.out.printf("The dictionary contains %d unigrams and %d bigrams \n", unigrams, bigrams);
    }

    /**
     *
     */
    public void showSorted() {
        TreeSet<WordFreq> sorted = new TreeSet<>();
        TObjectIntIterator iterator = documentFrequency.iterator();
        while (iterator.hasNext()) {
            iterator.advance();
            NGram ngram = (NGram) iterator.key();
            int frequency = iterator.value();
            WordFreq w = new WordFreq(ngram, frequency);
            sorted.add(w);
        }
        for (WordFreq w : sorted) {
            System.out.println(w);
        }
    }

    /*
     * Returns the next document as one line of text
     */

    /**
     *
     */

    protected void finalizeDictionary() {
        TObjectIntIterator<NGram> iterator;
        id = new HashMap<>();
        ngram = new HashMap<>();
        iterator = documentFrequency.iterator();
        int counter = 0;
        while (iterator.hasNext()) {
            iterator.advance();
            id.put(iterator.key(), counter);
            ngram.put(counter, iterator.key());
            counter++;
        }
    }

    /**
     * Reads a document of space separated words
     *
     * @param lines
     * @param n
     * @param frequent
     * @return 
     */
    protected Set<NGram> nGramsFromTokenSentences(List<String> lines, int n, Set<NGram> frequent) {
        Set<NGram> ngrams = new HashSet<>();
        for (String line : lines) {
            String[] tokens = line.split(" ");
            //for(byte b:tokens[0].getBytes()) System.out.println(b& 0xff);
            
	//System.out.println(Arrays.toString(tokens));
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
                        if (tokensToIgnore.contains(token)) {
                            skip = true;
                            break;
                        }
                    }
                    if (skip == true) {
                        continue;
                    }
                    //ommit ngrams that contain as subgrams, terms that have been pruned
                    List<NGram> subgrams = ngram.getSubgrams();
                    if (frequent.contains(subgrams.get(0)) && frequent.contains(subgrams.get(1))) {
                        ngrams.add(ngram);
                    }
                } else {
                    if (tokensToIgnore.contains(list.get(0))) {
                        continue;
                    }
                    ngrams.add(ngram);
                }
            }
        }
        return ngrams;
    }

    /**
     *
     * @param corpus
     * @param nGramSize
     * @param unigramLower
     * @param unigramUpper
     * @param bigramLower
     * @param bigramUpper
     */
    protected void addNGrams(Corpus corpus, int nGramSize, int unigramLower, int unigramUpper, int bigramLower, int bigramUpper) {
        // System.out.println(new Date() + " Adding " + nGramSize + "-grams...");
        for (int i = 1; i <= nGramSize; i++) {
            nGramSizes.add(i);
        }
        Set<NGram> oldDocumentFrequency = null;
        Set<NGram> ngrams;
        for (int i = 0; i < nGramSize; i++) {
            int counter = 0;
            Document doc;
            List<String> lines;
            corpus.reset();
            TObjectIntHashMap<NGram> currentDocumentFrequency = new TObjectIntHashMap<>();
            while ((doc = corpus.nextDocument()) != null) {
                counter++;
                lines = doc.getContentAsSentencesOfTokens(false);
                ngrams = nGramsFromTokenSentences(lines, i + 1, oldDocumentFrequency);
                for (NGram ngram : ngrams) {
                    // remove numbers and stopwords
                    boolean remove = false;
                    for (String word : ngram.getList()) {
                        if (sw.isStopWord(word) || sw.ommit(word)) {
                            remove = true;
                            break;
                        }
                    }
                    if (remove) {
                        continue;
                    }
                    if (currentDocumentFrequency.containsKey(ngram)) {
                        currentDocumentFrequency.put(ngram, currentDocumentFrequency.get(ngram) + 1);
                    } else {
                        currentDocumentFrequency.put(ngram, 1);
                    }
                }
            }

            corpusSize = counter;
            //System.out.println(new Date() + " Found " + currentDocumentFrequency.size() + " " + (i + 1) + "-grams");
            if (i == 0) {
                prune(currentDocumentFrequency, unigramLower, unigramUpper);
            } else {
                prune(currentDocumentFrequency, bigramLower, bigramUpper);
            }

            //System.out.println(new Date() + " Pruned to " + currentDocumentFrequency.size() + " " + (i + 1) + "-grams");
            oldDocumentFrequency = currentDocumentFrequency.keySet();
            TObjectIntIterator<NGram> entries = currentDocumentFrequency.iterator();
            while (entries.hasNext()) {
                entries.advance();
                documentFrequency.put(entries.key(), entries.value());
            }
        }
        // iterate currentDocumentFrequency and add to documentFrequency

    }

    /**
     *
     * @param map
     * @param thresholdLower
     * @param thresholdUpper
     */
    public final void prune(TObjectIntHashMap<NGram> map, int thresholdLower, int thresholdUpper) {
        TObjectIntIterator<NGram> entries = map.iterator();
        while (entries.hasNext()) {
            entries.advance();
            if (entries.value() < thresholdLower) {
                entries.remove();
            }
            if (entries.value() > thresholdUpper) {
                entries.remove();
            }
        }
    }

    /**
     *
     * @param fileDictionary
     * @return
     */
    public static final Dictionary readDictionary(String fileDictionary) {
        Dictionary dictionary = (Dictionary) Utils.readObject(fileDictionary);
        return dictionary;
    }

    /**
     *
     * @param dictionaryFile
     */
    public void writeDictionary(String dictionaryFile) {
        Utils.writeObject(this, dictionaryFile);
    }
}
