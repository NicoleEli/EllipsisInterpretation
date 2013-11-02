package typeClassification;

import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.ling.HasWord;

import java.util.Collection;
import java.util.List;


/**
 * @author Nicole Williams
 *         28/10/13
 *
 * Handles the classification of input by kind of ellipsis, making use of a number of binary classifiers.
 */
public class EllipsisClassificationController {

    BinaryEllipsisClassifier[] binaryClassifiers;

    public EllipsisType findEllipsisType(List<HasWord> sentence, Tree parse, Collection typedDependencies){
        //TODO: Implementation
        return EllipsisType.NONE;
    }

    private List<Feature> genFeatures(){
        //TODO: Implementation
        return null;
    }
}
