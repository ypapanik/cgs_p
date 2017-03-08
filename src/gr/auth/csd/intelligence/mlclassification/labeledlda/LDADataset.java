package gr.auth.csd.intelligence.mlclassification.labeledlda;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.THashSet;
import gr.auth.csd.intelligence.lda.Document;
import gr.auth.csd.intelligence.preprocessing.Corpus;
import gr.auth.csd.intelligence.preprocessing.Dictionary;
import gr.auth.csd.intelligence.preprocessing.Labels;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LDADataset extends gr.auth.csd.intelligence.lda.LDADataset implements Serializable {

    static final long serialVersionUID = 5716568340666075332L;
    protected Labels labels = null;
    protected HashMap<String, THashSet<String>> labelvalues;

    public LDADataset(Dictionary dic, Labels labs, boolean inf) {
        super(dic, inf, 0, null);
        labels = labs;
        this.K = labels.getSize();
    }

    @Override
    public void create(Corpus corpus) {
        docs = new ArrayList<>();
        gr.auth.csd.intelligence.preprocessing.Document doc;
        corpus.reset();

        while ((doc = corpus.nextDocument()) != null) {
            
        
            int[] labelIds = null;
            if(!inference) labelIds = addLabels(doc.getLabels());
            List<String> lines = doc.getContentAsSentencesOfTokens(true);
            TIntArrayList wordIds = new TIntArrayList();
            for (int j = 0; j < dictionary.getNGramSizes().size(); j++) {
                TIntArrayList temp = nGramsFromSentences(lines, dictionary.getNGramSizes().get(j));
                wordIds.addAll(temp);
            }
            setDoc(new Document(wordIds, doc.getId(), labelIds, doc.getJournal()), docs.size());

        }
    }

    public Labels getLabels() {
        return labels;
    }

    public String getLabel(int id) {
        return labels.getLabel(id);
    }

    protected int[] addLabels(Set<String> labs) {
        TIntArrayList lids = new TIntArrayList();
        for (String s : labs) {
            int index = labels.getIndex(s);
            if (index != -1) {
                lids.add(index);
            }
        }
        return lids.toArray();
    }

    /**
     *
     * @param docId
     * @param labelsDoc
     * @return
     */
    protected TIntArrayList addLabels(String docId, String labelsDoc) {
        TIntArrayList lids = new TIntArrayList();

        for (String s : labelvalues.get(docId)) {
            int index = labels.getIndex(s);
            if (index != -1) {
                lids.add(index);
            }
        }
        return lids;
    }

    public double[] freq() {
        double freq[] = new double[V];

        double totalWords = 0;
        for (Document d : docs) {
            totalWords += d.getWords().size();
        }
        for (Document d : docs) {
            TIntIterator it = d.getWords().iterator();
            while (it.hasNext()) {
                freq[it.next()]++;
            }
        }
        for (int w = 0; w < V; w++) {
            freq[w] /= totalWords;
        }
        return freq;
    }
}
