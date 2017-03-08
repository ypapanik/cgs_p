package gr.auth.csd.intelligence.lda;

import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.LDACmdOption;
import gr.auth.csd.intelligence.lda.models.Model;
import gr.auth.csd.intelligence.preprocessing.Corpus;
import gr.auth.csd.intelligence.preprocessing.Dictionary;
import gr.auth.csd.intelligence.preprocessing.Labels;
import java.io.Serializable;
import java.util.ArrayList;

public class LabelsDataset extends LDADataset implements Serializable {

    private final Labels labels;

    public LabelsDataset(Dictionary dic, boolean inf, int K, String trainedPhi, String labs) {
        super(dic, inf, K, trainedPhi);
        labels = Labels.readLabels(labs);
        V = labels.getSize();
        if (!inf) {
            this.K = K;
        } else {
            this.K = Model.readPhi(trainedPhi).length;
        }
    }

    //Creates the dataset by reading data, dictionary, labels, etc   

    @Override
    public void create(Corpus corpus) {
        gr.auth.csd.intelligence.preprocessing.Document doc;
        docs = new ArrayList<>();
        corpus.reset();
        while ((doc = corpus.nextDocument()) != null) {
            TIntArrayList wordIds = new TIntArrayList();
            for (String l : doc.getLabels()) {
                wordIds.add(labels.getIndex(l) - 1);
            }
            setDoc(new Document(wordIds, doc.getId(), null, doc.getJournal()), docs.size());
        }

    }

    @Override
    public String getWord(Integer index) {
        return labels.getLabel(index + 1);
    }
}
