package typeClassification;

import edu.stanford.nlp.trees.*;

import java.util.Collection;

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

}
