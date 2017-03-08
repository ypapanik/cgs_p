package gr.auth.csd.intelligence.mlclassification.labeledlda.models;

import gr.auth.csd.intelligence.utils.Utils;
import java.util.Random;
import gr.auth.csd.intelligence.mlclassification.labeledlda.LDADataset;

/**
 *
 * @author Yannis Papanikolaou
 */
public class CVB0InferenceModel extends PriorModel {

    double[][][] gamma;

    public CVB0InferenceModel(LDADataset data, String trainedModelName, int threads, int iters, int burnin) {
        super(data, trainedModelName, threads, iters, burnin);
        //initialize gamma
        gamma = new double[M][][];
    }

    @Override
    public void initialize() {
        Random r = new Random();
        for (int d = 0; d < M; d++) {
            int documentLength = data.getDocs().get(d).getWords().size();
            gamma[d] = new double[documentLength][K];
            for (int w = 0; w < documentLength; w++) {
                double sum = 0;
                for (int k = 0; k < K; k++) {
                    gamma[d][w][k] = r.nextDouble();
                    sum += gamma[d][w][k];
                }
                //normalize
                for (int k = 0; k < K; k++) {
                    gamma[d][w][k] = gamma[d][w][k] / sum;
                    nd[d].adjustOrPutValue(k, gamma[d][w][k], gamma[d][w][k]);
                }
            }
        }
    }

    @Override
    public void update(int d) {
        int documentLength = data.getDocs().get(d).getWords().size();
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            double probs[] = new double[K];
            for (int k = 0; k < K; k++) {
                double oldGamma = gamma[d][w][k];
                nd[d].adjustValue(k, -oldGamma);
                double prob;
                prob = phi[k].get(word) * (nd[d].get(k) + alpha[k]);
                probs[k] = prob;
            }
            probs = Utils.normalize(probs, 1);

            //update
            for (int k = 0; k < K; k++) {
                double newGamma = probs[k];
                gamma[d][w][k] = newGamma;
                nd[d].adjustOrPutValue(k, newGamma, newGamma);
            }
        }
    }
}
