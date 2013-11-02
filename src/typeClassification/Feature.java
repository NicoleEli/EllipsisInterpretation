package typeClassification;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Nicole Williams
 *         02/11/13
 */
public class Feature {

    private String name;

    private int value;

    public Feature(String name, int value){
        this.setName(name);
        this.setValue(value);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
