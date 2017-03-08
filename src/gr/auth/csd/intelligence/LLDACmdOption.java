package gr.auth.csd.intelligence;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.*;

public class LLDACmdOption extends LDACmdOption implements Serializable {
  
    @Option(name = "-gamma", usage = "gamma for Dependency LDA")
    public double gamma = 0.01;
    
    @Option(name = "-jM", usage = "journalsModel")
    public String jm = null;
    
    @Option(name = "-allMesh", usage = "allMesh")
    public String allMesh = null;
    
    @Option(name = "-parallel", usage = "Estimate/Infer parameters in parallel")
    public boolean parallel = false;

    @Option(name = "-theta", usage = "predictions")
    public String theta = "theta";

    public LLDACmdOption(String[] args) {
        super();
        CmdLineParser parser = new CmdLineParser(this);
        if (args.length == 0) {
            parser.printUsage(System.out);
            return;
        }
        try {
            parser.parseArgument(args);
        } catch (CmdLineException ex) {
            Logger.getLogger(CmdOption.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
