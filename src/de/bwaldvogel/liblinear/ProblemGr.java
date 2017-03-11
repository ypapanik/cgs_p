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
package de.bwaldvogel.liblinear;

import gr.auth.csd.intelligence.preprocessing.Dictionary;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anithagenilos
 */
public class ProblemGr extends Problem {
    public static ProblemGr readFromFile(File file, double bias, int nr_features) throws IOException, InvalidInputDataException {
        return TrainGr.readProblem(file, bias, nr_features);
    }
    
    public static ProblemGr readProblem(String libsvmTestFile, Dictionary dictionary) {
        ProblemGr testProblem = null;
        try {
            testProblem = ProblemGr.readFromFile(new File(libsvmTestFile), 1, dictionary.getId().size());
            System.out.println(new Date() + " Finished loading shell");
        } catch (IOException | InvalidInputDataException ex) {
            Logger.getLogger(ProblemGr.class.getName()).log(Level.SEVERE, null, ex);
        }
        return testProblem;
    }
}
