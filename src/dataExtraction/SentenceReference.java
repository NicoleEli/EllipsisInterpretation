package dataExtraction;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         18/01/14
 */
public class SentenceReference {

    String text;

    //sentence IDs within the text
    int antecedent;
    int nsu;

    //according to the NSU taxonomy due to Fernandez, Ginsburg & Lappin
    String nsuCategory;

    public SentenceReference(String text, int antecedent, int nsu, String nsuCategory){
        this.text = text;
        this.antecedent = antecedent;
        this.nsu = nsu;
        this.nsuCategory = nsuCategory;
    }
}
