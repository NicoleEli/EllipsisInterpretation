import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.*;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author Nicole Williams
 * Date: 17/10/13
 */
public class MainController {


    public static void main(String[] args){
        if (args.length > 0){
            String filename = args[0];

            ParsingController parser = new ParsingController();

            for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
                Tree parse = parser.getParse(sentence);
                Collection typedDependencies = parser.getDependencies(parse);

                System.out.println(sentence + " " + sentence.size());
                parse.pennPrint();
                System.out.println(parse.yield());
                System.out.println(typedDependencies);
            }
        }
    }

}
