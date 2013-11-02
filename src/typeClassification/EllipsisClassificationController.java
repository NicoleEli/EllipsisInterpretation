package typeClassification;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.HasWord;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Handles the classification of input by kind of ellipsis, making use of a number of binary classifiers.
 */
public class EllipsisClassificationController {

    BinaryEllipsisClassifier[] binaryClassifiers;

    public EllipsisType findEllipsisType(Tree parse, Collection typedDependencies){
        //TODO: Implementation
        return EllipsisType.NONE;
    }

    //returns list of features
    private List<Feature> genFeatures(){

        List<Feature> featureList = new ArrayList<Feature>();

        //Sentence length


        //TODO: Implementation
        return null;
    }
}
