
package BioComputationAssignment;

import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.midi.MidiDevice;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class B_C_Assignment {

    XYSeries[] sry;
    public B_C_Assignment() {
        int x = 0;
        sry = new XYSeries[10];
        for(int i = 0; i < sry.length; i++)
        {
            x += 10;
            sry[i] = new XYSeries("gen "+x);
        }
    }
    
    
    
    public void graph(String file)
    {
        JFrame window = new JFrame();
        window.setName("graph window");
        window.setSize(800,800);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        XYSeriesCollection collection = new XYSeriesCollection();
        for(int i = 0; i < sry.length; i++) collection.addSeries(sry[i]);
           

        
        JFreeChart chart = ChartFactory.createXYLineChart(file, "index", "fitness value", collection);
        window.add(new ChartPanel (chart), BorderLayout.CENTER);
        window.setVisible(true);
    }
    
    public void graphdata(int index, int fitness,XYSeries seriesData )
    {
        seriesData.add(index, fitness);    
    }
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        B_C_Assignment x = new B_C_Assignment();
        String file = "";
      
        String filename = "data2.txt"; //adding data set (data1.txt || data2.txt)
        x.graph(filename);

        // ArrayList of Data objects from the file
        ArrayList<Data> data_set = create_data_set(filename);
        int temp = 0;
        int no_rules = 5; // number of no_rules
        int condition_len = data_set.get(0).Vars; // condition_len length
        int pop_size = 100; // population size
        int iteration = 100; // amount of generations 
        int no_GA = 100; // controlls how many differnt GA to run.
        int correct_rules = 0;
        int gene_size = (condition_len + 1) * no_rules; // size of gene per solution
        double mute_rate = 0.02; // Equal to 2% chance of mutation

        Individual fittest = new Individual(gene_size, no_rules, condition_len); // Store the best solution found
        Individual[] population = GA.initiateArray(pop_size, gene_size, no_rules, condition_len);
        Individual[] offspring = GA.initiateArray(pop_size, gene_size, no_rules, condition_len);

        int index = 1;
        while (index < no_GA) { // Start loop that allow multple GAs to run sequentially 
            Individual best = new Individual(gene_size, no_rules, condition_len); // Store the best solution found
            
            population = GA.createPopulation(population); //Created an iniitial population with random genes

            for (Individual pop : population) {
                GA.score_fitness(pop, data_set); // works
            }
            
            int generation = 0;
            while (generation < iteration) 
            {
                // create offspring using tourniment selection
                offspring = GA.tournment(population);
                for (Individual pop : offspring) {
                    GA.score_fitness(pop, data_set);
                }

                // Perform crossover
                offspring = GA.crossover(offspring);
                for (Individual pop : offspring) {
                    GA.score_fitness(pop, data_set);
                }

                // Perform mutation 
                offspring = GA.mutation(offspring, mute_rate);
                for (Individual pop : offspring) {
                    GA.score_fitness(pop, data_set);
                }

                // evaluate
                best = GA.evaluate(offspring, best);

                for (int i = 0; i < pop_size; i++) {
                    population[i] = new Individual(offspring[i]);
                }

                generation++;
                    boolean isNext10 = (index % 10 == 0) ? true : false;
                    if(isNext10)
                    {
                        temp = ((index/10)-1);
                        x.graphdata(generation, best.fitness, x.sry[temp]);
                    }
                    
                file += best.fitness + ",";
            }
            file += "\n";
            System.out.println("Best fitness is " + best.fitness);
            if(best.fitness == 64 ) correct_rules++;
            index++;
            //check completed GA's best solution and compare with the previous GA
            if (best.fitness > fittest.fitness) {
                fittest = new Individual(best);
            }
        }
        
        System.out.println(GA.print_rules(fittest.rulebase));
        System.out.println(file);
        System.out.println("==== END ===");
       
    }

    public static ArrayList<String> dataset_to_array (String fieName)
    {
        ArrayList <String> dataset = new ArrayList();
        Scanner scan = new Scanner(B_C_Assignment.class.getResourceAsStream(fieName));
        scan.nextLine();
        
        String line1 = null;
        while(scan.hasNextLine())
        {
            line1 = scan.nextLine();
            String s = "";
            for (int i = 0; i < line1.length(); i++) {
                if ((line1.charAt(i) == '0') || (line1.charAt(i) == '1')) {
                    s = s + line1.charAt(i);
                }
            }
            System.out.println(s);
            dataset.add(s);
        }
        
        return dataset;
    }

    // Read the data from file and passes back array list. 
    public static ArrayList<Data> create_data_set(String filename) throws IOException {
        ArrayList<String> file_array = dataset_to_array(filename);
        int size_data = file_array.get(0).length() - 1;
        int k = 0;
        ArrayList<Data> tempA = new ArrayList<>();

        for (String a : file_array) {
            Data temp = new Data(size_data);
            for (int i = 0; i < size_data; i++) {
                temp.variables[i] = Character.getNumericValue(a.charAt(i));
            }
            temp.type = Character.getNumericValue(a.charAt(size_data));
            tempA.add(temp);
        }
        return tempA;
    }
}
