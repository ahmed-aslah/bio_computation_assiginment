
package Bio_Comp_Assignment;

/**
 *
 * @author aslah
 */
public class Rule {

    int ConL = 5;
    float[] cond;
    int out;

    // change to a new size of condition
    public Rule(int ConL) {
        this.ConL = ConL;
        this.cond = new float[ConL];
        this.out = 2;
    }

    public Rule() {
        this.cond = new float[ConL];
        this.out = 2;
    }
}
