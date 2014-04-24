package controllers;

import edu.stanford.nlp.trees.*;
import typeClassification.BinaryEllipsisClassifier;
import typeClassification.EllipsisType;
import typeClassification.FeatureGenerator;
import weka.core.Attribute;
import weka.core.FastVector;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Handles the classification of input by kind of ellipsis, making use of a number of binary classifiers.
 */
public class EllipsisClassificationController {

    Charset charset = Charset.forName("UTF-8");

    List<BinaryEllipsisClassifier> binaryClassifiers;
    FeatureGenerator featureGenerator;

    FastVector attributes;

    public EllipsisClassificationController(FeatureGenerator featureGenerator){
        binaryClassifiers = new ArrayList<BinaryEllipsisClassifier>();
        this.featureGenerator = featureGenerator;
        attributes = new FastVector();
    }


    /**
     * Initialise some number of binary classifiers, given paths to the .csv files containing training data
     */
    public void initialiseClassifiers(List<String> datasetPaths, List<String> datasetNames, Set<String> featureNames){

        generateAttributes(featureNames);

        for (int i = 0; i < datasetPaths.size(); i++){
            makeNewClassifier(datasetPaths.get(i), datasetNames.get(i));
        }

    }

    public EllipsisType findEllipsisType(Tree parse, Collection typedDependencies){
        Map<String, Integer> featureValues = featureGenerator.genFeatures(parse, typedDependencies);

        FastVector convertedFeatures = convert(featureValues);

        List<Boolean> results = new ArrayList<Boolean>();
        for (BinaryEllipsisClassifier classifier : binaryClassifiers){
            boolean result = classifier.classify(convertedFeatures);
            results.add(result);
        }

        //TODO: interpret results

        return EllipsisType.NONE;
    }

    private void makeNewClassifier(String datasetPath, String datasetName){

        BinaryEllipsisClassifier classifier = new BinaryEllipsisClassifier(attributes, datasetName);

        try{
            BufferedReader reader = Files.newBufferedReader(Paths.get(datasetPath), charset);

            //Read dataset file and add training data to classifier
            String line = reader.readLine();    //read first line i.e. feature names. TODO: does file even need this line?
            while ((line = reader.readLine()) != null){
                FastVector dataItem = convert(line);
                classifier.updateTrainingData(dataItem);
            }
        } catch (Exception e){
            System.err.format("IOException: %s%n", e);
        }

        binaryClassifiers.add(classifier);
    }

    private void generateAttributes(Set<String> featureNames){

        for (String s : featureNames){
            if(!s.equals("class")) {
                attributes.addElement(new Attribute(s));
            }
        }
    }

    /**
     * Convert one line of a .csv file into a FastVector of feature/attribute values
     */
    private FastVector convert(String line){
        FastVector data = new FastVector();
        String[] dataStrings = line.split(",");

        for (String s : dataStrings){
            data.addElement(s.trim());
        }

        return data;
    }

    private FastVector convert(Map<String,Integer> features){
        FastVector data = new FastVector();

        for(String k : features.keySet()){
            data.addElement(features.get(k));
        }
        return data;
    }
}
