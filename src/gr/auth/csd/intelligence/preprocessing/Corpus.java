package gr.auth.csd.intelligence.preprocessing;

import java.io.Serializable;

/**
 *
 * @author Grigorios Tsoumakas
 * @version 2013.07.22
 */
public abstract class Corpus implements Serializable {    
    
    public abstract Document nextDocument();
    
    public abstract void reset();
    public abstract void close();
}
