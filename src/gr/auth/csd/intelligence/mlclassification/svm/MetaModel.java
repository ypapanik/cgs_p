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
package gr.auth.csd.intelligence.mlclassification.svm;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.InvalidInputDataException;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.ProblemGr;
import de.bwaldvogel.liblinear.SolverType;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import gr.auth.csd.intelligence.preprocessing.Corpus;
import gr.auth.csd.intelligence.preprocessing.Dictionary;
import gr.auth.csd.intelligence.preprocessing.Vectorize;
import gr.auth.csd.intelligence.utils.Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Panagiotis Mandros
 * @author Grigorios Tsoumakas
 * @author Yannis Papanikolaou
 *
 * @version 2013.1.22
 */
public class MetaModel 
{
    private double[][] scores;
    private final int numLabels;
    protected Model model;
    protected ProblemGr trainProb;  
    protected ProblemGr testProb;
    private TIntArrayList targetValues; //a structure for holding the meta data for the datasets (the cardinalities) 

    public MetaModel(int aNumber, String filenameTrainShell, String filenameTestShell, int nr_features) 
    {
        System.out.println(new Date()+" Loading train shell..");
        trainProb=loadProblem(filenameTrainShell, nr_features);
        System.out.println(new Date()+" Finished loading train shell");
        if (filenameTestShell != null) {
            System.out.println(new Date()+" Loading test shell..");        
            testProb=loadProblem(filenameTestShell, nr_features);
            System.out.println(new Date()+" Finished loading test shell");
        }
        numLabels = aNumber;
    }

    public Model getModel() {
        return model;
    }
    
    private ProblemGr loadProblem(String filenameShell, int nr_features)  //load the problem (the dataset)
    {
        ProblemGr problem;
        File file=new File(filenameShell);
        try 
        {
            problem= ProblemGr.readFromFile(file,1, nr_features);
            return problem;
        } catch (IOException | InvalidInputDataException ex) 
        {
            return null;
        }
    }
    
    //create the meta data
    //change a shell so that it has the cardinalities for every document (change its y value)
    //this function can figure on its own which of the 2 datesets has to change
    protected void createTrainMetaData(String filenameMetaData)
    {
        System.out.println("Creating meta data..");
        //load the target values
        try (ObjectInputStream oisTargetValuesFile= new ObjectInputStream(new FileInputStream(filenameMetaData))) 
        {
            targetValues= (TIntArrayList) oisTargetValuesFile.readObject();
        } catch (ClassNotFoundException | IOException  ex) 
        {
            Logger.getLogger(MetaModel.class.getName()).log(Level.SEVERE, null, ex);
        }        
        int i=0;
        int label;
        TIntIterator it = targetValues.iterator(); 
        System.out.println("target values size:" + targetValues.size());
        while(it.hasNext())
        {
            label=it.next();
            //System.out.println("num labels:" +label);
            trainProb.y[i]=label;
            i++;
        }
    }
    
    //builts the model of the train meta dataset
    //first it creates the meta data set, then it builds the model
    public void train(String filenameMetaTrain)
    {
        System.out.println(new Date()+" Training meta dataset");
        createTrainMetaData(filenameMetaTrain);
        Parameter param = new Parameter(SolverType.L2R_L2LOSS_SVR_DUAL, 1, 0.01);
        //Parameter param = new Parameter(SolverType.L2R_L2LOSS_SVC_DUAL, 1, 0.01);
        //Parameter param = new Parameter(SolverType.MCSVM_CS, 1, 0.01);
        model=Linear.train(trainProb, param);
        System.out.println(new Date()+" Finished training meta dataset");
    }
   
    public void test(String filenamePredictions) 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filenamePredictions))) 
        {
            System.out.println(new Date() + " Predicting");
            double[] testScores = new double[numLabels];
            int i=0;
            for(Feature[] x:testProb.x)
            {
                double result=Linear.predict(model, x);
                for (int j = 0; j < numLabels; j++) 
                {
                    testScores[j] = scores[j][i];
                }
                i++;
                if (i == 1) {
                    System.out.println("testScore: " + Arrays.toString(testScores));
                }
                for (int j = 0; j < result; j++) 
                {                    
               
                    int pos = Utils.minIndex(testScores);
                    if (i == 1) {
                        System.out.println("pos: " + pos);
                    }
                    writer.write("Label" + (pos + 1) + ",");
                    writer.write("Label" + (pos + 1) + ",");
                    testScores[pos] = Double.MAX_VALUE;
                }
                writer.write("\n");
            }
        } catch (Exception ex) 
        {
            Logger.getLogger(MetaModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(new Date() + " Finished.");
    }

    public void test2(String filenamePredictions) 
    {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filenamePredictions))) 
        {
            System.out.println(new Date() + " Predicting");
            for(Feature[] x:testProb.x)
            {
                double result=Linear.predict(model, x);
                writer.write(result + "\n");
            }
        } catch (Exception ex) 
        {
            Logger.getLogger(MetaModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(new Date() + " Finished.");
    }

    public static void main(String args[]) 
    {
        long startTimeMs = System.currentTimeMillis( );
        long startSystemTimeNano = Utils.getSystemTime( );
        long startUserTimeNano   = Utils.getUserTime( );

        String filenameTrainShell = args[0];
        String filenameTestShell =args[1];
        String filenameMetaTrain = args[2];
        int numLabels = Integer.parseInt(args[3]);
        String filenamePredictions = args[4];
        String filenameModel = args[5];

        MetaModel ml = new MetaModel(numLabels,filenameTrainShell,filenameTestShell, 10000);        
        ml.train(filenameMetaTrain);
        ml.test2(filenamePredictions);
        ml.saveModel(filenameModel);

        long taskTimeMs  = System.currentTimeMillis( ) - startTimeMs;
        long taskUserTimeNano    = Utils.getUserTime( ) - startUserTimeNano;
        long taskSystemTimeNano  = Utils.getSystemTime( ) - startSystemTimeNano;        
        System.out.println("Wall time (hours): " + taskTimeMs/(1000.0*60*60));
        System.out.println("User time (hours): " + taskUserTimeNano/(1000.0*1000*1000*60*60));
        System.out.println("System time (hours): " + taskSystemTimeNano/(1000.0*1000*1000*60*60));
        System.out.println("CPU time (hours): " + (taskUserTimeNano+taskSystemTimeNano)/(1000.0*1000*1000*60*60));
    }

    public void saveModel(String filename) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filename))) {
            output.writeObject(model);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static Model readModel(String metalabelerFile) {
        Model metalabeler = null;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(metalabelerFile))) {
            metalabeler = (de.bwaldvogel.liblinear.Model) input.readObject();
        } catch (IOException|ClassNotFoundException e) {
            System.out.println(e);
        }
        return metalabeler;
    }
    
    public static double[] getMetaModelPrediction(String metalabelerFile, Corpus test, 
            Dictionary dictionary, int M, String libsvmTestFile) {
        //vectorize testFile
        Vectorize vectorize = new Vectorize(dictionary, false, null);
        vectorize.vectorizeUnlabeled(test, libsvmTestFile);
        
        //read metalabeler
        ProblemGr testProblem = ProblemGr.readProblem(libsvmTestFile, dictionary);
        Model metalabeler = MetaModel.readModel(metalabelerFile);
        
        //get metalbeler's output
        double[] metalabelerPredictions = new double[M];
        //System.out.println(M+":"+metalabelerPredictions.length+" "+testProblem.x.length);
        for(int m = 0; m < M;m++) {
            metalabelerPredictions[m] = de.bwaldvogel.liblinear.Linear.predict(metalabeler, testProblem.x[m]);
//            if (metalabelerPredictions[m] < 10) metalabelerPredictions[m] = 10;
            //System.out.println("meta: "+metalabelerPredictions[m]);
        }

        return metalabelerPredictions;
    }
    
    public static double[] getMetaModelPrediction(Model metalabeler, Corpus test, 
            Dictionary dictionary, int M, String libsvmTestFile) {
        //vectorize testFile
        Vectorize vectorize = new Vectorize(dictionary, false, null);
        vectorize.vectorizeUnlabeled(test, libsvmTestFile);
        
        //read metalabeler
        ProblemGr testProblem = ProblemGr.readProblem(libsvmTestFile, dictionary);
        //get metalebeler's output
        double[] metalabelerPredictions = new double[M];
        //System.out.println(M+":"+metalabelerPredictions.length+" "+testProblem.x.length);
        for(int m = 0; m < M;m++) {
            metalabelerPredictions[m] = de.bwaldvogel.liblinear.Linear.predict(metalabeler, testProblem.x[m]);
//            if (metalabelerPredictions[m] < 10) metalabelerPredictions[m] = 10;
            //System.out.println("meta: "+metalabelerPredictions[m]);
        }

        return metalabelerPredictions;
    }
}
    