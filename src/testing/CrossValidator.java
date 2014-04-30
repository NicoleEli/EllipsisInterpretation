package testing;

import controllers.EllipsisClassificationController;
import controllers.ParsingController;
import edu.stanford.nlp.trees.Tree;
import typeClassification.FeatureGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         27/04/14
 */
public class CrossValidator {

    Charset charset = Charset.forName("UTF-8");

    int n;          //n-fold crossval
    EllipsisClassificationController classificationController;

    final String TRAIN_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\crossval\\training.csv";
    final String TEST_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\crossval\\testing.txt";

    List<String> datasetPaths;
    List<String> datasetNames;
    String baseName;

    Set<String> featureNames;

    List<Float> precision;
    List<Float> recall;

    ParsingController parser;
    FeatureGenerator featureGenerator;

    /**
     * @param n                n-fold cross-validation e.g. n=10 for 10-fold crossval
     * @param featureGenerator feature generator
     */
    public CrossValidator(int n, FeatureGenerator featureGenerator, ParsingController parser) {
        this.n = n;
        classificationController = new EllipsisClassificationController(featureGenerator);
        featureNames = featureGenerator.getFeatureNames();

        datasetPaths = new ArrayList<String>();
        datasetPaths.add(TRAIN_PATH);

        datasetNames = new ArrayList<String>();
        datasetNames.add("placeholder");

        this.parser = parser;
        this.featureGenerator = featureGenerator;

    }

    /**
     *
     * @param processedDataPath      Path to file containing pre-processed data
     * @param rawDataPath
     */
    public void validateClassifier(String name, String processedDataPath, String rawDataPath) {
        System.out.printf("Validating classifier for %s...%n", name);

        //Perform n rounds of cross-validation
        for (int round = 0; round < n; round++) {
            baseName = "crossval-%d-"+name;
            buildDataSets(round, processedDataPath, rawDataPath);

            //Customise dataset name for this round
            datasetNames.set(0,String.format(baseName,round));

            classificationController.initialiseClassifiers(datasetPaths, datasetNames, featureNames);

            classifyTestData(datasetNames.get(0));

            //Reset classification controller for re-use in next round
            classificationController.reset();

            System.out.printf("Cross-validation round %d complete.%n", round);
        }

        //Work out average precision/recall across n rounds
        float avgPrecision = 0;
        float avgRecall = 0;
        for (int i = 0; i < n; i++){
            avgPrecision += precision.get(i);
            avgRecall += recall.get(i);
        }
        avgPrecision = avgPrecision / n;
        avgRecall = avgRecall / n;

        System.out.printf("Average precision over %d rounds: %f%n", n, avgPrecision);
        System.out.printf("Average recall over %d rounds: %f%n",n,avgRecall);

    }

    /**
     * Split data into two datasets with ratio n-1:1 training:test
     * @param round         which round of cross-val are we in? Determines how file is split
     * @param processedDataPath      path to dataset file to be split
     * @param rawDataPath
     */
    private void buildDataSets(int round, String processedDataPath, String rawDataPath) {
        try {
            BufferedReader trainReader = Files.newBufferedReader(Paths.get(processedDataPath), charset);
            BufferedReader testReader = Files.newBufferedReader(Paths.get(rawDataPath), charset);
            BufferedWriter trainWriter = Files.newBufferedWriter(Paths.get(TRAIN_PATH), charset);
            BufferedWriter testWriter = Files.newBufferedWriter(Paths.get(TEST_PATH), charset);

            //Clear dataset files before beginning
            trainWriter.write("");
            testWriter.write("");

            String rawLine = testReader.readLine();        //read header line
            String processedLine = trainReader.readLine();
            int linesRead = 0;
            //Send 1/nth of data to test file, rest to training file. Different 1/nth sent to test each round.
            while ((rawLine=testReader.readLine()) != null && (processedLine=trainReader.readLine()) != null){

                if ((linesRead % n) == round){
                    testWriter.append(rawLine+"\n");
                } else {
                    trainWriter.append(processedLine+"\n");
                }

                linesRead++;
            }

            trainReader.close();
            testReader.close();
            trainWriter.close();
            testWriter.close();

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }


    private void classifyTestData(String datasetName){

        System.out.printf("Running classifier on test data...%n");

        try{
            BufferedReader reader = Files.newBufferedReader(Paths.get(TEST_PATH), charset);

            precision = new ArrayList<Float>();
            recall = new ArrayList<Float>();

            int total = 0;          //total number of data items
            int conditionPos = 0;   //number of data items with true class "true"
            int testPos = 0;        //number of data items classified as "true"
            int truePos = 0;        //number of data items *correctly* classified as "true"
            int correct = 0;        //number of data items correctly classified

            String line;
            while ((line = reader.readLine()) != null){

                //Detach "true" or "false" class from line
                String[] splitLine = line.split("::");

                String classification = splitLine[1].trim();
                String classlessLine = splitLine[0].trim();


                //Classify item
                Tree parse = parser.getParse(classlessLine);
                Collection dependencies = parser.getDependencies(parse);
                String assignedClass = classificationController.findEllipsisType(parse, dependencies);

                String trueClass;
                if(classification.equals("true")){
                    trueClass = datasetName;
                    conditionPos++;
                } else {
                    trueClass = "none";
                }

                if (assignedClass.equals(datasetName)){
                    testPos++;
                }

                if (assignedClass.equals(trueClass)){
                    correct++;
                    if(assignedClass.equals(datasetName)){
                        truePos++;
                    }
                }

                total++;
            }

            //record precision and recall for this round
            precision.add(((float) truePos)/testPos);
            recall.add(((float) truePos)/conditionPos);


        } catch (IOException e){
            System.err.format("IOException: %s%n", e);
        }

    }

}
