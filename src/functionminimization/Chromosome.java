
package functionminimization;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author oguzcagiran
 */
public class Chromosome {
    
    private List<String> chromosome = new ArrayList<>();
    private static Random random = new Random();
    
    public Chromosome() {
        
        for (int i = 0; i < 8; i++) {
            boolean gen = random.nextBoolean();
            if(gen) {
                chromosome.add("1");
            }else {
                chromosome.add("0");
            }
        }
        
    }
    
    public Chromosome(List<String> chromosome) {
        this.chromosome = chromosome;
    }
    
    public List<String> getChromosome() {
        return chromosome;
    }
    
    public int calculateFunctionValue() {
        
        byte decimalChromosome = binaryToDecimal();

        int result = (int)((decimalChromosome * decimalChromosome) + (16 * decimalChromosome));
        
        return result;
    }
   
    private byte binaryToDecimal() {
        
        String chromosome = "";
        
        for (int i = 0; i < this.chromosome.size(); i++) {
            chromosome += this.chromosome.get(i);
        }
        
        byte res = (byte)Integer.parseInt(chromosome.trim(), 2);
        
        return res;
    }
}