/* 
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
package gr.auth.csd.intelligence.cgsp.utils;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.*;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */

public class LLDACmdOption extends LDACmdOption implements Serializable {
       
    /**
     *
     */
    @Option(name = "-parallel", usage = "Estimate/Infer parameters in parallel")
    public boolean parallel = false;

    /**
     *
     */
    @Option(name = "-theta", usage = "predictions")
    public String theta = "theta";

    /**
     *
     * @param args
     */
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
