package testing;

import controllers.EllipsisClassificationController;
import edu.stanford.nlp.trees.Tree;
import typeClassification.BinaryEllipsisClassifier;
import typeClassification.FeatureGenerator;
import weka.core.FastVector;

import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * A special case of the ellipsis classification controller which handles the case where there is only one binary classifier in use
 * TODO: There has to be a better way of doing this...
 *
 * @author Nicole Williams
 *         27/04/14
 */
public class SingleClassifierController extends EllipsisClassificationController {

    public SingleClassifierController(FeatureGenerator featureGenerator){
        super(featureGenerator);
    }

    public boolean classifyDataItem(Tree parse, Collection typedDependencies){

        Map<String, Integer> featureValues = featureGenerator.genFeatures(parse, typedDependencies);

        FastVector convertedFeatures = convert(featureValues);

        boolean result = false;
        for (BinaryEllipsisClassifier classifier : binaryClassifiers){
            result = classifier.classify(convertedFeatures);
        }

        return result;

    }

    public boolean classifyDataItem(String line){

        FastVector features = super.convert(line);

        boolean result = false;
        for (BinaryEllipsisClassifier classifier : binaryClassifiers){
            result = classifier.classify(features);
        }

        return result;

    }

}
