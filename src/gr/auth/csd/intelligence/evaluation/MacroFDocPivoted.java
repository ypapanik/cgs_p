package gr.auth.csd.intelligence.evaluation;

import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Document;
import gr.auth.csd.intelligence.preprocessing.Labels;
import java.util.TreeSet;

/**
 *
 * @author Grigorios Tsoumakas
 * @author Yannis Papanikolaou
 * @version 2013.07.24
 */
public class MacroFDocPivoted extends Evaluator {

    private final int docSize;

    public MacroFDocPivoted(Labels labels, CorpusJSON corpus, String filenamePredictions) {
        this.labels = labels;
        this.corpus = corpus;
        docSize = CorpusJSON.size(corpus);
        this.filenamePredictions = filenamePredictions;
    }

    @Override
    public void evaluate() {
        super.readBipartitions();
        corpus.reset();
        //load predicted labels
        double[] tp, fp, tn, fn;
        tp = new double[docSize];
        fp = new double[docSize];
        fn = new double[docSize];
        Document doc;
        int i = 0;
        while ((doc = corpus.nextDocument()) != null) {
            TreeSet<String> truth = getTruth(doc);
            TreeSet<String> pred = bipartitions.get(doc.getId());
            ConfusionMatrix cm = new ConfusionMatrix(pred, truth, labels);
            tp[i] = cm.getTp();
            fp[i] = cm.getFp();
            fn[i] = cm.getFn();
            i++;
        }

        double macroF = 0;
        for (i = 0; i < docSize; i++) {
            //System.out.print("Doc " + (i + 1) + " ");
            //System.out.printf("tp %.0f ", tp[i]);
            //System.out.printf("fp %.0f ", fp[i]);
            //System.out.printf("tn %.0f ", tn[i]);
            //System.out.printf("fn %.0f ", fn[i]);
            double f = 2.0 * tp[i] / (2.0 * tp[i] + fp[i] + fn[i]);
            if (new Double(f).isNaN()) {
                f = 1;
            }
            macroF += f;
            //System.out.printf("f %.2f", f);
            //System.out.println("");
        }
        System.out.println("MacroF(doc Pivoted): " + macroF / docSize);
    }
}
