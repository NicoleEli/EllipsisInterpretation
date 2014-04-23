package typeClassification;

import edu.stanford.nlp.trees.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Handles the classification of input by kind of ellipsis, making use of a number of binary classifiers.
 */
public class EllipsisClassificationController {

    List<BinaryEllipsisClassifier> binaryClassifiers;

    /**
     * Initialise some number of binary classifiers, given paths to the .csv files containing training data
     */
    public void initialiseClassifiers(List<String> datasetPaths){

        for (String dataset : datasetPaths){
            makeNewClassifier(dataset);
        }

    }

    public EllipsisType findEllipsisType(Tree parse, Collection typedDependencies){
        //TODO: Given a parse and dependency parse, classify the input. Use confidence values?
        return EllipsisType.NONE;
    }

    private void makeNewClassifier(String datasetPath){
        //TODO: Create new binary ellipsis classifier and add to classifier list
    }

}
