package gr.auth.csd.intelligence;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.*;

public class CmdOption implements Serializable {

    public CmdOption(String args[]) {
        CmdLineParser parser = new CmdLineParser(this);
        if (args.length == 0) {
            parser.printUsage(System.out);
            return;
        }
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            Logger.getLogger(CmdOption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public CmdOption() {
    }

    static final long serialVersionUID = -91327807901735687L;

    @Option(name = "-dictionary", usage = "features dictionary")
    public String dictionary = "dictionary";

    @Option(name = "-labels", usage = "labels set")
    public String labels = "labels";

    @Option(name = "-trainingFile", usage = "training dataset")
    public String trainingFile = null;

    @Option(name = "-testFile", usage = "testing dataset")
    public String testFile = null;

    @Option(name = "-testFilelibSVM", usage = "testing dataset libSVM")
    public String testFilelibSVM = "testFile.libSVM";

    @Option(name = "-bipartitionsFile", usage = "bipartitions file")
    public String bipartitionsFile = "bipartitions";

    @Option(name = "-threads", usage = "number of threads")
    public int threads = 1;

    @Option(name = "-offset", usage = "adjust the MetaLabeler's predictions by offset")
    public int offset = -1;

    @Option(name = "-fsMethod", usage = "feature selection method")
    public int fsMethod = 1;

    @Option(name = "-zoning", usage = "perform zoning of title/labels.")
    public boolean zoning = true;

    @Option(name = "-predictions", usage = "predictions")
    public String predictionsFile = "predictions";

    @Option(name = "-tp", usage = "true positives.")
    public String tpFile = "tp/tp";

    @Option(name = "-trainFilelibSVM", usage = "training dataset libSVM ")
    public String fileTrainLibsvm = "train.Libsvm";

    @Option(name = "-trainLabels", usage = "trainLabels")
    public String fileTrainLabels = "trainLabels";

    @Option(name = "-metaLabels", usage = "metaLabels")
    public String fileMetaTrainLabels = "metaTrainLabels";

    @Option(name = "-metaLabeler", usage = "MetaLabeler File")
    public String metalabelerFile = "metamodel";

    @Option(name = "-modelsFolder", usage = "models folder")
    public String modelsDirectory = "models";

    @Option(name = "-score", usage = "specify if the predictions will be scores or binary")
    public boolean score = false;

    @Option(name = "-threshold", usage = "specify if for the metalabeler we will employ threshold instead of cardinality predictions")
    public boolean threshold = false;
    @Option(name = "-C", usage = "C parameter for the SVMs.")
    public double C = 1;

    @Option(name = "-N", usage = "N parameter for thresholding the N most relevant predictions.")
    public int N = 10;
    
    @Option(name = "-low1Grams", usage = "lower threshold for unigrams")
    public int lowUnigrams = 1;
    
    @Option(name = "-high1Grams", usage = "higher threshold for unigrams")
    public int highUnigrams = 2000000;
    
    @Option(name = "-low2Grams", usage = "lower threshold for bigrams")
    public int lowBigrams = 20;
    
    @Option(name = "-high2Grams", usage = "higher threshold for bigrams")
    public int highBigrams = 2;
    
    @Option(name = "-mlc", usage = "mlc in use")
        public String mlc = "meta";
}
