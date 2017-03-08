package gr.auth.csd.intelligence.utils;

import gr.auth.csd.intelligence.preprocessing.NGram;

/**
 * @author Grigorios Tsoumakas
 * @date 2013.05.07
 */
public class WordFreq implements Comparable<WordFreq> {
    NGram ngram;
    int frequency;
    
    public WordFreq(NGram ngram, int frequency) {
        this.ngram = ngram;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return ngram.toString() + " : " + frequency;
    }
    
    @Override
    public int compareTo(WordFreq o) {
        return (o.frequency-this.frequency)*10000+o.ngram.toString().compareTo(ngram.toString());
    }
}
