/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bio_Comp_Assignment;


/**
 *
 * @author aslah
 */

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
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
    

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        int temp = 0;
        String file = "";
        String filename = "data3.txt";
        B_C_Assignment x = new B_C_Assignment();
        x.graph(filename);
        // ArrayList of Data objects to hold the input data
        ArrayList<Data> data_set = create_data_set(filename);
        ArrayList<Data> training_set = new ArrayList<>();
        ArrayList<Data> test_set = new ArrayList<>();

        // Splits input file in training and test sets
        for (Data t : data_set) {
            double d = Math.random();
            if (d < 0.75) {
                training_set.add(t);
            } else {
                test_set.add(t);
            }
        }

        // Settings for running the GA 
        int no_rules = 5; // number of rules
        int condition_len = data_set.get(0).Vars * 2; // condition length
        int pop_size = 100; // population size 
        int iteration = 100; // amoutn of generations 
        int no_GA = 100; // controlls how many differnt GA to run.
        int gene_size = (condition_len + 1) * no_rules; // size of gene per solution
        double mute_rate = 0.02;//(1 / ((double) gene_size*2));
        float mute_size = (float) 0.5;
       
        Individual global_best = new Individual(gene_size, no_rules, condition_len); // Store the best solution found
        Individual[] population = GA.initiateArray(pop_size, gene_size, no_rules, condition_len);
        Individual[] offspring = GA.initiateArray(pop_size, gene_size, no_rules, condition_len);

        int index = 0;
        while (index < no_GA) { // Start if loop that allow multple GAs to run sequentially 
            Individual best = new Individual(gene_size, no_rules, condition_len); // Store the best solution found
            
            //Created an iniitial population with random genes and score
            population = GA.createPopulation(population);
            for (Individual pop : population) {
                GA.score_fitness(pop, training_set); // works
            }

            int generation = 0;
            while (generation < iteration) {

                // create offspring using tourniment selection
                offspring = GA.tournment(population);
                for (Individual pop : offspring) {
                    GA.score_fitness(pop, training_set);
                }

                // Perform crossover
                offspring = GA.crossover(offspring);
                for (Individual pop : offspring) {
                    GA.score_fitness(pop, training_set);
                }

                // Perform mutation 
                offspring = GA.mutation(offspring, mute_rate, mute_size);
                for (Individual pop : offspring) {
                    GA.score_fitness(pop, training_set);
                }

                // evaluate
                best = GA.evaluate(offspring, best);

                // Replace population with offspring and score
                for (int i = 0; i < pop_size; i++) {
                    population[i] = new Individual(offspring[i]);
                }     

            file +=  "\n"; 

            index++;
                generation++;
                boolean isNext10 = (index % 10 == 0) ? true : false;
                    if(isNext10)
                    {
                        temp = ((index/10)-1);
                        x.graphdata(generation, best.fitness, x.sry[temp]);
                    }
                file += best.fitness + ",";
            }
            //check completed GA's best solution and compare with the previous GA
            if (best.fitness > global_best.fitness) {
                global_best = new Individual(best);
            }
            TimeUnit.SECONDS.sleep(1);
        }

        // Prints to show the rsults of the GAs
        System.out.println("Trained using " + training_set.size() + " sets of data");
        System.out.println("Best's fitness on the training set " + global_best.fitness);
        System.out.println(GA.print_rules(global_best.rulebase));
        System.out.println(GA.printRulesBitString(global_best.rulebase));
        System.out.println(file); // TO use as a list of best per GA
        double percent_training = ((double) 100 / training_set.size()) * global_best.fitness;

        GA.score_fitness(global_best, test_set);
        System.out.println("Tested using " + test_set.size() + " pieces of data");
        System.out.println("Best's fitness over test set " + global_best.fitness);
        System.out.format("Equals %.2f%% accuracy on training set\n", percent_training);
        double percent_test = ((double) 100 / test_set.size()) * global_best.fitness;
        System.out.format("Equals %.2f%% accuracy on test set\n", percent_test);
    }

    public static ArrayList<String> file_to_string_array(String filename) throws FileNotFoundException, IOException {
        ArrayList<String> array = new ArrayList<>();
        // Read the Data file and create a single String of the contents
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        reader.readLine(); // this will read the first line
        String line1 = null;
        while ((line1 = reader.readLine()) != null) {
            String s = "";
            for (int i = 0; i < line1.length(); i++) {
                if ((line1.charAt(i) != '\n')) {
                    s = s + line1.charAt(i);
                }
            }
            array.add(s);
        }
        return array;
    }

    // Read the data from file and passes back array list. 
    public static ArrayList<Data> create_data_set(String filename) throws IOException {
        ArrayList<String> file_array = file_to_string_array(filename);
        ArrayList<Data> tempA = new ArrayList<>();

        for (String a : file_array) {
            ArrayList<Float> temp = new ArrayList<>();
            Scanner scanner = new Scanner(a);
            scanner.useDelimiter(" ");
            while (scanner.hasNext()) {
                temp.add(scanner.nextFloat());
            }

            Data data_temp = new Data(temp.size() - 1);
            for (int i = 0; i <= data_temp.Vars - 1; i++) {
                data_temp.variables[i] = temp.get(i);
            }
            data_temp.type = Character.getNumericValue(a.charAt(a.length() - 1));
            tempA.add(data_temp);
        }
        return tempA;
    }
}
