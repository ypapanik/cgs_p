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
package gr.auth.csd.intelligence.cgsp.lda;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.cgsp.lda.models.Model;
import gr.auth.csd.intelligence.cgsp.preprocessing.Dictionary;
import gr.auth.csd.intelligence.cgsp.preprocessing.NGram;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import gr.auth.csd.intelligence.cgsp.preprocessing.Corpus;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LDADataset implements Serializable {

    static final long serialVersionUID = 5716568340666075332L;

    /**
     *
     */
    protected ArrayList<Document> docs;                   // a list of documents				 		// number of documents

    /**
     *
     */
    protected int V = 0;  		 		// number of words

    /**
     *
     */
    protected int K;

    /**
     *
     */
    protected Dictionary dictionary;

    /**
     *
     */
    protected NGram[] ngrams;
    //protected TIntArrayList inferenceDictionary = null;

    /**
     *
     */
    public final boolean inference;

    /**
     *
     * @param dic
     * @param inf
     * @param K
     * @param trainedPhi
     */
    public LDADataset(Dictionary dic, boolean inf, int K, String trainedPhi) {
        this.inference = inf;
        dictionary = dic;
        if (trainedPhi != null) {
            if (!inf) {
                this.K = K;
            } else {
                K = Model.readPhi(trainedPhi).length;
            }
        }
        V = dictionary.getId().size();
    }

    /**
     *
     * @param corpus
     */
    public void create(Corpus corpus) {
        gr.auth.csd.intelligence.cgsp.preprocessing.Document doc;
        docs = new ArrayList<>();
        corpus.reset();
        while ((doc = corpus.nextDocument()) != null) {
            List<String> lines = doc.getContentAsSentencesOfTokens(true);
            TIntArrayList wordIds = new TIntArrayList();
            for (int j = 0; j < dictionary.getNGramSizes().size(); j++) {
                TIntArrayList temp = nGramsFromSentences(lines, dictionary.getNGramSizes().get(j));
                wordIds.addAll(temp);
            }
            setDoc(new Document(wordIds, doc.getId(), null, doc.getJournal()), docs.size());
        }
    }

    //get n-grams from each document

    /**
     *
     * @param lines
     * @param n
     * @return
     */
    protected TIntArrayList nGramsFromSentences(List<String> lines, int n) {
        TIntArrayList wordIds = new TIntArrayList();
        for (String line : lines) {
            String[] tokens = line.split(" ");
            for (int i = 0; i < tokens.length + 1 - n; i++) {
                List<String> aList = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    aList.add(tokens[i + j]);
                }
                NGram ngram = new NGram(aList);
                List<String> list = ngram.getList();

                if (dictionary.getDocumentFrequency().containsKey(ngram)) {
                    wordIds.add(dictionary.getId().get(ngram));
                    //if(inference&&!inferenceDictionary.contains(dictionary.getId().get(ngram))) inferenceDictionary.add(dictionary.getId().get(ngram));
                }
            }
        }
        return wordIds;
    }

    /**
     *
     * @return
     */
    public int getV() {
        return V;
    }

    /**
     *
     * @return
     */
    public Dictionary getDictionary() {
        return dictionary;
    }

    /**
     *
     * @return
     */
    public ArrayList<Document> getDocs() {
        return docs;
    }

    /**
     *
     * @param doc
     * @param idx
     */
    public void setDoc(Document doc, int idx) {
        if (0 <= idx && idx < docs.size()) {
            docs.set(idx, doc);
        } else {
            docs.add(idx, doc);
        }
    }

    /**
     *
     * @return
     */
    public int getK() {
        return K;
    }

    /**
     *
     * @param index
     * @return
     */
    public String getWord(Integer index) {
        return dictionary.getNgram(index).toString();
    }
}
