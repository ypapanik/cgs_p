/* 
 * Copyright (C) 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.auth.csd.intelligence.preprocessing;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import gnu.trove.set.hash.THashSet;
import gr.auth.csd.intelligence.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Grigorios Tsoumakas
 * @version 2013.07.19
 */
public class Document implements Comparable  {

    private static final StanfordCoreNLP pipeline;
    private String pmid;
    private String title="";
    private String abs="";
    private String journal = "";
    private int year=0;
    private String body="";
    private String refs;
    private THashSet<String> labels = new THashSet<>();
    //private static StopWords sw;

    static {
        Properties props = new Properties();
        //props.put("annotators", "tokenize, cleanxml, ssplit, pos, lemma");
        props.put("annotators", "tokenize, ssplit");
        RedwoodConfiguration.empty().capture(System.err).apply();
        pipeline = new StanfordCoreNLP(props);
        RedwoodConfiguration.current().clear().apply();
        //sw = new StopWords();
    }

    public Document() {        
    }

    public String getAbs() {
        return abs;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    public void setJournal(String text) {
        journal = text;
    }
    
    public void setPmid(String text) {
        pmid = text;
    }
    
    public void setTitle(String text) {
        title = text;
    } 
    
    public String getTitle() {
        return title;
    }
   
    public void setYear(int aYear) {
        year = aYear;
    }
    
    public void setAbstract(String text) {
        abs = text;        
    }
       
    public void setLabels(THashSet<String> aSetOfLabels) {
        labels = aSetOfLabels; 
    }
    
    public THashSet<String> getLabels() {
        return labels;
    }

    public String getId() {
        return pmid;
    }

    public int getYear() {
        return year;
    }

    public String getJournal() {
        return journal;
    }

    public List<String> getContentAsSentencesOfTokens(boolean ignoreNumbers) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(abs).append(body);
        Annotation document = new Annotation(sb.toString());
        pipeline.annotate(document);

        //* Stanford full
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<String> lines = new ArrayList<>();
        StringBuilder builder;
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            builder = new StringBuilder();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                //* replace numbers
                if (Utils.isNumber(word)) {
                   if(ignoreNumbers) word = "";
//                    else word = "NumNUMNum";
                }
                //if(!word.matches("\\d*\\.?\\d*-?\\d*\\.?\\d*")&&!(word.matches("\\+\\d*"))) {
                    builder.append(word.toLowerCase()).append(" ");
                    
                //}
                //String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                //String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            }
            lines.add(builder.toString());
        }
        return lines;
    }

    @Override
    public int compareTo(Object t) {
        Document doc = (Document) t;
        return this.pmid.compareTo(doc.pmid);
    }
}
