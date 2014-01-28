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


    public static void main(String[] args) {

        ParsingController parser = new ParsingController();

        DatasetBuilder datasetBuilder = new DatasetBuilder(new FeatureGenerator() {
        }, "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\raw-all.txt", "C:\\Users\\Nikki\\IdeaProjects\\EllipsisInterpretation\\Data\\final\\attribute-testing.csv", parser);

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
