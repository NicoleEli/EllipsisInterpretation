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

    List<Feature> featureList = new ArrayList<Feature>();
    String[] punctuation = {".",",","\'","\"","-","/","\\","(",")"};

    //returns list of features
    public List<Feature> genFeatures(Tree parse, Collection typedDependencies){

        List<Word> words = parse.yieldWords();

        //Sentence length
        featureList.add(new Feature("sentence length", getSentenceLength(words)));

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
     * @param words     The words making up the given sentence.
     * @return          The sentence length.
     */
    private int getSentenceLength(List<Word> words){
        int size = words.size();
        for (Word w : words){
            boolean isPunctuation = Arrays.asList(punctuation).contains(w.value().trim());
            if (isPunctuation){
                size -= 1;
            }
        }
        return size;
    }

    /**
     * Generates single-word features for the given sentence.
     *
     * @param words     Words making up given sentence.
     */
    private void getSingleWordFeatures(List<Word> words){

    }

    /**
     * Generates two-word features for the given sentence.
     *
     * @param words     Words making up given sentence.
     */
    private void getWordPairFeatures(List<Word> words){

    }

}
