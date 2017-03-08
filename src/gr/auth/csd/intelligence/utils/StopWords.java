package gr.auth.csd.intelligence.utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StopWords { 
    protected static HashSet m_Words = null;     
    public StopWords(int n) { 
        m_Words = new HashSet();
        addFromFile(10000, "stoplists/en.txt");

        
    }
    
    /** * adds the given word to the stopword list (isStopWord automatically converted to * lower case and trimmed) * * @param word the word to add */ 
    private void add(String word) { 
        if (word.trim().length() > 0) m_Words.add(word.trim().toLowerCase());
    }
    public boolean isStopWord(String word) { 
        return m_Words.contains(word.toLowerCase());
    }
    private void addFromFile(int threshold, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int numWords=0;
            String word;
            while ((numWords<threshold)&&(word = reader.readLine()) != null) {
                add(word);
                numWords++;
            }
        } catch (IOException ex) {
            Logger.getLogger(StopWords.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean ommit (String word) {
        if(isStopWord(word)) return true;
        if(word.matches("\\d*")||word.matches("\\d*\\.?\\d*[-|/|\\+|;|:]?\\d*\\.?\\d*")||
                word.matches("(\\+|-)\\d*%?(:|;)?")||word.matches("\\d*%")||word.matches(".\\W{2}.")) {
            //System.out.println(word);
            return true;
        }
        return false;
    }
}
