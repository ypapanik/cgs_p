package gr.auth.csd.intelligence.lda;
import gnu.trove.list.array.TIntArrayList;
import java.io.Serializable;

public class Document implements Serializable {

    private final TIntArrayList words;
    private final String pmid;
    private final String journal;
    private int[] labels = null;

    public TIntArrayList getWords() {
        return words;
    }

    public String getPmid() {
        return pmid;
    }

    public String getJournal() {
        return journal;
    }

    
    public Document(TIntArrayList doc, String id, int[] tlabels, String j)
    {
        words = doc;
        pmid = id;
        journal = j;
        if(tlabels != null) labels = tlabels;
    }

    public int[] getLabels() {
        return labels;
    }

}
