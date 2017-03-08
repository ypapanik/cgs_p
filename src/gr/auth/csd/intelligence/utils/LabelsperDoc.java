package gr.auth.csd.intelligence.utils;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.hash.TIntIntHashMap;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannis Papanikolaou
 */
public class LabelsperDoc {
    public static void main(String args[]) {
        String dataset = args[0];
        CorpusJSON c = new CorpusJSON(dataset);
        c.reset();
        Document doc;
        TIntIntHashMap lpd = new TIntIntHashMap();
        int corpusSize=0;
        int labelsTotal = 0;
         PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter("labelsperdoc"));
        } catch (IOException ex) {
            Logger.getLogger(LabelsperDoc.class.getName()).log(Level.SEVERE, null, ex);
        }
        while((doc = c.nextDocument())!=null) {
            
            lpd.adjustOrPutValue(doc.getLabels().size(), 1, 1);
            corpusSize++;
            labelsTotal+=doc.getLabels().size();
        }
        System.out.println(lpd.toString());
        System.out.println("average:"+labelsTotal*1.0/corpusSize);
        TIntIntIterator it = lpd.iterator();
        while(it.hasNext()) {
            it.advance();
            out.println(it.key()+" "+it.value());
        }
        
    }
}
