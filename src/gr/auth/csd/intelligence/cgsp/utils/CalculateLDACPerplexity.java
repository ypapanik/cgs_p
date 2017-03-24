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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Yannis Papanikolaou <ypapanik@csd.auth.gr>
 */
public class CalculateLDACPerplexity {
    
    public static void main(String args[]) throws IOException {
        
        List<String> lines = Files.readAllLines(Paths.get(new File(args[0]).getPath()),
                Charset.defaultCharset());
        double sumlogps = 0;
        for (String line : lines) {
            sumlogps += Double.parseDouble(line);
        }
        double tokens = 0;
        lines = Files.readAllLines(Paths.get(new File(args[1]).getPath()),
                Charset.defaultCharset());
        for (String line : lines) {
            String[] feats = line.split(" ");
            for(int i=1;i<feats.length;i++) {
                String[] split = feats[i].split(":");
                tokens +=Integer.parseInt(split[1]);
            }
            
        }
        
        
        System.out.println(Math.exp(-sumlogps / tokens));
        
    }
}
