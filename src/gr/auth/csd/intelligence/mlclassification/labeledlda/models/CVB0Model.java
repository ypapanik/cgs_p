package gr.auth.csd.intelligence.mlclassification.labeledlda.models;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.Random;
import gr.auth.csd.intelligence.mlclassification.labeledlda.LDADataset;

public class CVB0Model extends Model {

    double[][][] gamma;
    //constructor used for estimation
    public CVB0Model(LDADataset data, int thread, double beta, boolean inf, 
            String trainedModelName, int threads, int iters, int burnin) {
        super(data, thread, beta, inf, trainedModelName, threads, iters, burnin);
        //initialize gamma
        gamma = new double[M][][];
        System.out.println("Collapsed Variational Bayes 0...");
    }
    
    @Override
    public void initialize() {
        nd = new TIntDoubleHashMap[M];
        for(int d=0;d<M;d++) nd[d] = new TIntDoubleHashMap();
        Random r=new Random();
        for(int d=0;d<M;d++) {
            int[] labels = data.getDocs().get(d).getLabels();
            int K_m = labels.length;
            int documentLength = data.getDocs().get(d).getWords().size();
            gamma[d] = new double[documentLength][K_m];
            for(int w=0;w<documentLength;w++) {
                int word = data.getDocs().get(d).getWords().get(w);
                double sum=0;
                for(int k=0;k<K_m;k++) {
                    gamma[d][w][k] = r.nextDouble();
                    sum+= gamma[d][w][k];
                }
                //normalize
                for(int k=0;k<K_m;k++) {
                    gamma[d][w][k] = gamma[d][w][k]/sum;
                    int label = labels[k]-1;
                    nw[label].adjustOrPutValue(word, gamma[d][w][k], gamma[d][w][k]);
                    nd[d].adjustOrPutValue(label, gamma[d][w][k], gamma[d][w][k]);
                    nwsum[label] += gamma[d][w][k];
                    if(!phi[label].contains(word)) phi[label].put(word, 0);
                }
            }
        }
        initAlpha();
    }
    
    @Override
    public void update(int d) {
        int documentLength = data.getDocs().get(d).getWords().size();
        int[] labels = data.getDocs().get(d).getLabels();
        int K_m = labels.length;
        for (int w = 0; w < documentLength; w++) {
            int word = data.getDocs().get(d).getWords().get(w);
            double probs[] = new double[K_m];
            for (int k = 0; k < K_m; k++) {
                int label = labels[k]-1;
                double oldGamma = gamma[d][w][k];
                nw[label].adjustValue(word, -oldGamma);

                nd[d].adjustValue(label, -oldGamma);
                nwsum[label] -= oldGamma;
                probs[k] = (nw[label].get(word) + beta) * (nd[d].get(label) + alpha[label]) / (nwsum[label] + nw[label].size() * beta);
            }
            probs = Utils.normalize(probs, 1);

            //update
            for (int k = 0; k < K_m; k++) {
                int label = labels[k]-1;
                double newGamma = probs[k];
                gamma[d][w][k] = newGamma;

                nw[label].adjustOrPutValue(word, newGamma, newGamma);
                nd[d].adjustOrPutValue(label, newGamma, newGamma);
                
                nwsum[label] += newGamma;
            }
        }
    }    
}
