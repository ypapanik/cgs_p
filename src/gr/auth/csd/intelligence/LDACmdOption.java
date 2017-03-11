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
package gr.auth.csd.intelligence;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.*;
/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class LDACmdOption extends CmdOption implements Serializable {

    @Option(name="-inf", usage="Specify whether we want to do inference")
        public boolean inf = false;

    @Option(name="-method", usage="inference method to take theta samples")
        public String method = "std";

    @Option(name="-model", usage="Specify the model name")
        public String modelName = "model";
    
    @Option(name="-alpha", usage="Specify alpha")
        public double alpha = 0.05;

    @Option(name="-beta", usage="Specify beta")
        public double beta = 0.01;

    @Option(name="-ntopics", usage="Specify the number of topics")
        public int K = 100;

    @Option(name="-niters", usage="Specify the number of iterations")
        public int niters = 100;

    @Option(name="-nburnin", usage="Specify the number of burn-in iterations")
        public int nburnin = 50;

    @Option(name="-samplinglag", usage="Specify the sampling lag")
        public int samplingLag = 5;

    @Option(name="-twords", usage="Specify the number of most likely words to be printed for each topic")
        public int twords = 20;
    @Option(name="-perplexity", usage="Calculate and print perplexity")
        public boolean perplexity=false;
    @Option(name="-mix", usage="CGS2CVB0 mix")
        public boolean mix;
    @Option(name="-chains", usage="Specify the Markov chains")
        public int chains = 1;

    
    
    public LDACmdOption(String[] args) {
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

    public LDACmdOption() {
    }
    
}
