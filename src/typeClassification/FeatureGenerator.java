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

    public static final String START_MARKER = "START";
    List<Feature> featureList = new ArrayList<Feature>();
    String[] punctuationArray = {".",",","'","\"","-","/","\\","(",")","!","?"};
    List<String> punctuation = Arrays.asList(punctuationArray);

    //returns list of features
    public List<Feature> genFeatures(Tree parse, Collection typedDependencies){

        List<Word> words = parse.yieldWords();
        List<TaggedWord> tagWords = parse.taggedYield();
        tagWords.add(0,new TaggedWord("", START_MARKER));     //special start-of-sentence marker

        //Sentence length
        featureList.add(new Feature("sentence length", getSentenceLength(words)));

        //POS counts
        getPOSCounts(tagWords);

        //POS pair counts
        getPOSPairCounts(tagWords);

        //Exists conjunction?
        featureList.add(new Feature("conjunction",existsConjunction(tagWords)));

        //WP-final?
        featureList.add(new Feature("WPfinal", isWPFinal(tagWords)));

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
            boolean isPunctuation = punctuation.contains(w.value().trim());
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
            if(!key.equals(START_MARKER));
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
        Map<String, Integer> tagCounts = new HashMap<String,Integer>();

        for(int i=0; i < tagWords.size()-1; i++){
            String pair = tagWords.get(i).tag() + "," + tagWords.get(i+1).tag() + "-count";
            if (tagCounts.containsKey(pair)){
                tagCounts.put(pair, tagCounts.get(pair)+1);
            } else {
                tagCounts.put(pair,1);
            }
        }
        for(String key : tagCounts.keySet()){
            featureList.add(new Feature(key, tagCounts.get(key)));
        }

    }

    /**
     * Determines whether the given sentence contains a conjunction.
     *
     * @param tagWords      Words & associated POS tags for up given sentence.
     * @return              1 if there is a conjunction, 0 otherwise
     */
    //TODO: Differentiate between prep & sub-conj ("IN")
    private int existsConjunction(List<TaggedWord> tagWords){
        for(TaggedWord tw : tagWords){
            if(tw.tag().equals("CC") || tw.tag().equals("IN")){
                return 1;
            }
        }
        return 0;
    }

    private int existsNegFinalVP(Tree parse){
        //TODO: Implementation
        return 0;
    }

    /**
     * Is the last word of the sentence a Wh-pronoun?
     *
     * @param tagWords      Words & associated POS tags for up given sentence.
     * @return              1 if true, 0 otherwise
     */
    private int isWPFinal(List<TaggedWord> tagWords){
        int finalIndex = tagWords.size() - 1;
        TaggedWord finalWord = tagWords.get(finalIndex);
        while (punctuation.contains(finalWord.tag())){
            tagWords.remove(finalIndex);
            finalIndex = tagWords.size() - 1;
            finalWord = tagWords.get(finalIndex);
        }
        if (finalWord.tag().equals("WP")){
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Do phrases like "does so", "does too", "doesn't" occur in the given sentence?
     *
     * @param words
     * @param tagWords
     * @return
     */
    private int existsDoesPhrase(List<Word> words, List<TaggedWord> tagWords){
        //TODO: Implementation
        return 0;
    }

    /**
     * Return a list of elements which encode POS in the given sentence augmented with
     * information from their ancestors in the parse tree.
     *
     * @param parse         parse tree of given sentence
     * @return              list of augmented POS tags
     */
    private List<String> getAugmentedPOS(Tree parse){
        //TODO: Implementation
        return null;
    }

    public void printFeatures(){
        for(Feature f : featureList){
            System.out.println(f.getName()+" : "+f.getValue());
        }
    }

    public void reset(){
        featureList = new ArrayList<Feature>();
    }


}
