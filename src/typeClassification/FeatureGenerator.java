package typeClassification;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         02/11/13
 */
public class FeatureGenerator {

    List<Feature> featureList = new ArrayList<Feature>();
    String[] punctuation = {".",",","\'","\"","-","/","\\","(",")","!","?"};

    //returns list of features
    public List<Feature> genFeatures(Tree parse, Collection typedDependencies){

        List<Word> words = parse.yieldWords();
        List<TaggedWord> tagWords = parse.taggedYield();

        //Sentence length
        featureList.add(new Feature("sentence length", getSentenceLength(words)));

        //POS Counts
        getPOSCounts(tagWords);

        //Exists conjunction?
        featureList.add(new Feature("conjunction",existsConjunction(tagWords)));

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
     * Generates features for number of each kind of POS tag.
     * (Note POS tags include punctuation tags.)
     *
     * @param tagWords     Words & associated POS tags for given sentence.
     */
    private void getPOSCounts(List<TaggedWord> tagWords){
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();
        for (TaggedWord tw : tagWords){
            String key = tw.tag();
            if (tagCounts.containsKey(key)){
                tagCounts.put(key,tagCounts.get(key)+1);
            } else {
                tagCounts.put(key, 1);
            }
        }
        for(String tag : tagCounts.keySet()){
            featureList.add(new Feature(tag+"-count", tagCounts.get(tag)));
        }
    }

    /**
     * Generates features for pairs of neighbouring POS tags.
     *
     * @param tagWords     Words & associated POS tags for up given sentence.
     */
    private void getPOSPairCounts(List<TaggedWord> tagWords){

    }

    private int existsConjunction(List<TaggedWord> tagWords){
        for(TaggedWord tw : tagWords){
            if(tw.tag() == "CC" || tw.tag() == "IN"){
                return 1;
            }
        }
        return 0;
    }

    public void printFeatures(){
        for(Feature f : featureList){
            System.out.println(f.getName()+" : "+f.getValue());
        }
    }


}
