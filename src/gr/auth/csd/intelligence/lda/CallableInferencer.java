package gr.auth.csd.intelligence.lda;

import gr.auth.csd.intelligence.lda.models.Model;
import java.util.concurrent.Callable;


public class CallableInferencer implements Callable<Boolean>{	

    protected Model newModel;
    private final int from, to;
    private final int threads;
    private final int mod;

    public CallableInferencer(Model model, int from, int to, int threads, int mod) {
        newModel = model;
        this.from = from;
        this.to = to;
        this.threads = threads;
        this.mod = mod;
    }
    
    @Override
    public Boolean call() {
        for (int i = from; i <= to; i++) {
            if (i % threads == mod) {
                newModel.update(i);
            }
        }
        return true;
    }
}
