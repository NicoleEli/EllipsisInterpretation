package testing;

import controllers.EllipsisClassificationController;
import typeClassification.FeatureGenerator;
import weka.core.FastVector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    /**
     * @param n                n-fold cross-validation e.g. n=10 for 10-fold crossval
     * @param name             String to identify classifier being crossval'ed
     * @param featureGenerator feature generator
     */
    public CrossValidator(int n, String name, FeatureGenerator featureGenerator) {
        this.n = n;
        classificationController = new EllipsisClassificationController(featureGenerator);
        featureNames = featureGenerator.getFeatureNames();

        datasetPaths = new ArrayList<String>();
        datasetPaths.add(TRAIN_PATH);

        datasetNames = new ArrayList<String>();
        baseName = "crossval-%d"+name;
    }

    /**
     *
     * @param dataPath      Path to file containing pre-processed data
     */
    public void validateClassifier(String dataPath) {

        //Perform n rounds of cross-validation
        for (int round = 0; round < n; round++) {
            buildDataSets(round, dataPath);

            //Customise dataset name for this round
            datasetNames.set(0,String.format(baseName,round));

            classificationController.initialiseClassifiers(datasetPaths, datasetNames, featureNames);

            //TODO: classify test set and record accuracy
        }

    }

    private void buildDataSets(int round, String dataPath) {
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(dataPath), charset);
            BufferedWriter trainWriter = Files.newBufferedWriter(Paths.get(TRAIN_PATH), charset);
            BufferedWriter testWriter = Files.newBufferedWriter(Paths.get(TEST_PATH), charset);

            String line;
            int linesRead = 0;
            //Send 1/nth of data to test file, rest to training file. Different 1/nth sent to test each round.
            while ((line=reader.readLine()) != null){

                System.out.println("Reading line "+linesRead);

                if ((linesRead % n) == round){
                    testWriter.append(line);
                    System.out.println("test data");
                } else {
                    trainWriter.append(line);
                    System.out.println("training data");
                }

                linesRead++;
            }

            reader.close();
            trainWriter.close();
            testWriter.close();

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

}
