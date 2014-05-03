package controllers;

import dataExtraction.DatasetBuilder;
import edu.stanford.nlp.trees.Tree;
import testing.CrossValidator;
import ellipsisDetection.FeatureGenerator;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         Date: 17/10/13
 */
public class MainController {


    public static final String FEATURE_NAMES_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\featureNames.txt";

    public static final String NPE_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\binary-NPE.txt";
    public static final String NPE_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-NPE.csv";
    public static final String VPE_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\binary-VPE.txt";
    public static final String VPE_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-VPE.csv";
    public static final String NSU_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\binary-NSU.txt";
    public static final String NSU_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-NSU.csv";

    public static final String DEBUG_PROCESSED_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\vectors-debug.csv";
    public static final String DEBUG_RAW_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\binary-debug.txt";

    //Booleans for turning on and off bits of functionality - largely for development/debugging use.
    public static boolean buildDatasets = false;
    public static boolean buildClassifiers = false;
    public static boolean takeInput = false;
    public static boolean runCrossVal = true;

    public static void main(String[] args) {

        Console console = System.console();

        ParsingController parser = new ParsingController();
        FeatureGenerator featureGenerator = new FeatureGenerator(FEATURE_NAMES_PATH);
        featureGenerator.initialiseFeatures();
        Set<String> featureNames = featureGenerator.getFeatureNames();

        //Build datasets for each kind of ellipsis
        if (buildDatasets) {

            DatasetBuilder npeDatasetBuilder = new DatasetBuilder(featureGenerator, NPE_RAW_PATH, NPE_PROCESSED_PATH, parser);
            DatasetBuilder vpeDatasetBuilder = new DatasetBuilder(featureGenerator, VPE_RAW_PATH, VPE_PROCESSED_PATH, parser);
            DatasetBuilder nsuDatasetBuilder = new DatasetBuilder(featureGenerator, NSU_RAW_PATH, NSU_PROCESSED_PATH, parser);

            System.out.println("Building NPE dataset.");
            npeDatasetBuilder.buildDataset();
            System.out.println("Built NPE dataset.");
            System.out.println("Building VPE dataset.");
            vpeDatasetBuilder.buildDataset();
            System.out.println("Built VPE dataset.");
            System.out.println("Building NSU dataset.");
            nsuDatasetBuilder.buildDataset();
            System.out.println("Built NSU dataset.");

        }

        EllipsisClassificationController classificationController = new EllipsisClassificationController(featureGenerator);

        if (buildClassifiers) {

            List<String> datasetPaths = new ArrayList<String>();
            datasetPaths.add(NPE_PROCESSED_PATH);
            datasetPaths.add(VPE_PROCESSED_PATH);
            datasetPaths.add(NSU_PROCESSED_PATH);

            List<String> datasetNames = new ArrayList<String>();
            datasetNames.add("NPE");
            datasetNames.add("VPE");
            datasetNames.add("NSU");

            classificationController.initialiseClassifiers(datasetPaths, datasetNames, featureNames);
            System.out.println("Initialised classifiers.");

        }

        if (takeInput){

            String sentence = "Bill's dogs are brown and Bob's dogs are black.";

            System.out.println(sentence);

            Tree parse = parser.getParse(sentence);
            Collection typedDependencies = parser.getDependencies(parse);
            classificationController.findEllipsisType(parse,typedDependencies);

            System.out.println("Finished trying to classify sentence: "+sentence);

        }

        if (runCrossVal){

            CrossValidator crossValidator = new CrossValidator(10, featureGenerator, parser);

            //crossValidator.validateClassifier("small", DEBUG_PROCESSED_PATH, DEBUG_RAW_PATH);


            crossValidator.validateClassifier("NPE", NPE_PROCESSED_PATH, NPE_RAW_PATH);
            crossValidator.validateClassifier("VPE", VPE_PROCESSED_PATH, VPE_RAW_PATH);
            crossValidator.validateClassifier("NSU", NSU_PROCESSED_PATH, NSU_RAW_PATH);

        }

        /*
        if (args.length > 0){
            String filename = args[0];

            ParsingController parser = new ParsingController();
            FeatureGenerator fGen = new FeatureGenerator();

            for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
                Tree parse = parser.getParse(sentence);
                Collection typedDependencies = parser.getDependencies(parse);

                System.out.println(sentence + " " + sentence.size());

                fGen.genFeatures(parse,typedDependencies);

                System.out.println("## Features: ##");
                fGen.printFeatures();
                fGen.reset();
            }
        }
        */
    }

}
