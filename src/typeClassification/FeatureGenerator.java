package typeClassification;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         02/11/13
 */
public class FeatureGenerator {

    String[] punctuation = {".",",","\'","\"","-","/","\\","(",")"};

    //returns list of features
    public List<Feature> genFeatures(Tree parse, Collection typedDependencies){

        List<Feature> featureList = new ArrayList<Feature>();

        //Sentence length
        featureList.add(new Feature("sentence length", getSentenceLength(parse)));

        System.out.println("## Features: ##");
        for (Feature f : featureList){
            System.out.println(f.getName() + ", " + f.getValue());
        }

        //TODO: Implementation
        return null;
    }

    /**
     * Returns the length of the sentence represented by the given parse tree.
     *
     * @param parse     Parse tree of relevant sentence.
     * @return          The sentence length.
     */
    private int getSentenceLength(Tree parse){
        List<Word> words = parse.yieldWords();
        int size = words.size();

        for (Word w : words){
            boolean isPunctuation = Arrays.asList(punctuation).contains(w.value().trim());
            if (isPunctuation){
                size -= 1;
            }
        }

        return size;
    }

}
