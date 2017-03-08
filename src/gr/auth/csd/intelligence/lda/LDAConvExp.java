package gr.auth.csd.intelligence.lda;

import gr.auth.csd.intelligence.LDACmdOption;
import gr.auth.csd.intelligence.lda.models.ConvergenceExperiment3Model;
import gr.auth.csd.intelligence.lda.models.Model;
import gr.auth.csd.intelligence.preprocessing.CorpusJSON;

public class LDAConvExp extends LDA {

    public LDAConvExp(LDACmdOption option) {
        super(option);
    }

    public ConvergenceExperiment3Model inference3() {
        ConvergenceExperiment3Model newModel = null;
        data.create(new CorpusJSON(testFile));
        newModel = new ConvergenceExperiment3Model(data, a, perp, niters, nburnin, modelName, samplingLag);
        newModel.initialize();
        newModel.inference();
        return newModel;
    }
}
