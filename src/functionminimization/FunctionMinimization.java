
package functionminimization;

/**
 *
 * @author oguzcagiran
 */
public class FunctionMinimization {

    public static void main(String[] args) {
       
        Evolution evolution = new Evolution(0.50, 0.25);
        
        evolution.start(1000);
        
    }
    
}