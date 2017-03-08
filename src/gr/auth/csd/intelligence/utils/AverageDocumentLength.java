package gr.auth.csd.intelligence.utils;

import gr.auth.csd.intelligence.preprocessing.CorpusJSON;
import gr.auth.csd.intelligence.preprocessing.Dictionary;
import gr.auth.csd.intelligence.preprocessing.Document;
import gr.auth.csd.intelligence.preprocessing.NGram;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yannis Papanikolaou
 */
public class AverageDocumentLength {
    static Dictionary dic;
    public static void main(String args[]) {
        String dataset = args[0];
        //String dict = args[1];
        CorpusJSON c = new CorpusJSON(dataset);
        //dic = Dictionary.readDictionary(dict);
        dic = new Dictionary(c, 1, 10000000,2, 1);
        Document doc;
        double sum=0;
        int counter=0;
        c.reset();
        while((doc = c.nextDocument())!=null) {
            List<String> lines = doc.getContentAsSentencesOfTokens(false);
            ArrayList<NGram> temp = nGramsFromSentences(lines);
            //System.out.println(temp.toString());
            System.out.println(temp.size());
            sum+=temp.size();
            counter++;
        }
        sum= sum/counter;
        System.out.println("Average length:"+sum);
    }
    
     protected static ArrayList<NGram> nGramsFromSentences(List<String> lines) {
        ArrayList<NGram> wordIds = new ArrayList<>();
        for (String line : lines) {
            String[] tokens = line.split(" ");
            for (int i = 0; i < tokens.length; i++) {
                List<String> aList = new ArrayList<>();
                aList.add(tokens[i]);
                NGram ngram = new NGram(aList);
                List<String> list = ngram.getList();
                if (Dictionary.getTokensToIgnore().contains(list.get(0))) continue;
                
                if (dic.getDocumentFrequency().containsKey(ngram)) {
                    wordIds.add(ngram);
                }
            }
        }
        return wordIds;
    }
}
