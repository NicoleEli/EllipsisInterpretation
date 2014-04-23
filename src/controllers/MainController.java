package controllers;

import dataExtraction.DatasetBuilder;
import typeClassification.FeatureGenerator;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         Date: 17/10/13
 */
public class MainController {


    public static final String FEATURE_NAMES_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\featureNames.txt";
    public static final String RAW_DATA_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\raw-all.txt";
    public static final String PROCESSED_DATA_PATH = "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\attribute-testing.csv";

    public static void main(String[] args) {

        ParsingController parser = new ParsingController();

        DatasetBuilder datasetBuilder = new DatasetBuilder(new FeatureGenerator(FEATURE_NAMES_PATH) {
        }, RAW_DATA_PATH, PROCESSED_DATA_PATH, parser);

        datasetBuilder.buildDataset();

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
