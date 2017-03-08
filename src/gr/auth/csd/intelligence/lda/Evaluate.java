package gr.auth.csd.intelligence.lda;

import gnu.trove.iterator.TIntIterator;
import gr.auth.csd.intelligence.utils.Utils;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 *
 * @author Yannis Papanikolaou
 */
public class Evaluate {

    private static double[][] readTheta(String file) {
        double[][] m = null;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            m = (double[][]) input.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return m;
    }

    private static double logp(Document doc, double[][] phi, double[] theta, LDADataset data) {
        double[] thetaNormalized = new double[theta.length];
        System.arraycopy(theta, 0, thetaNormalized, 0, theta.length);
        //System.out.println(Arrays.toString(theta));
        //System.out.println("thetaNormalized "+Arrays.toString(thetaNormalized));
        thetaNormalized = Utils.normalize(thetaNormalized, 1);
        //System.out.println("norm "+Utils.summation(thetaNormalized));
        double log = 0;
        int K = phi.length;
        TIntIterator it = doc.getWords().iterator();
        int i = 0;
        while (it.hasNext()) {
            int w = it.next();
            if (i <= (doc.getWords().size() / 2)) {
                i++;
                continue;
            }
            //System.out.println(data.dictionary.getNgram(w));
            double sum = 0;
            for (int k = 0; k < K; k++) {
                double p = phi[k][w] * thetaNormalized[k];
                sum += p;
                //System.out.println(k+" "+w+" "+phi[k][w]+" "+theta[k]+" "+p);
            }
            log += Math.log(sum);
        }
        return log;
    }

    public static double perplexity(LDADataset data, double[][] phi, double[][] theta) {
        double ppx;
        double tokens = 0;
        double sumlogps = 0;
        int d = 0;
        //System.out.println(Utils.summation(theta[0]));
        for (Document doc : data.docs) {
            tokens += doc.getWords().size() * 0.5;
            sumlogps += logp(doc, phi, theta[d], data);
            d++;
        }
        //System.out.println(sumlogps+" "+tokens);
        ppx = Math.exp(-sumlogps / tokens);
        return ppx;
    }
}
