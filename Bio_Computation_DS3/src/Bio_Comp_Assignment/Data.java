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
public class Data {
    int Vars; 
    float[] variables;
    int type;
    
    public Data(int Vars){
        this.Vars = Vars;
        this.variables = new float[Vars];
    }
    
    @Override
    public  String toString(){
        String s = "";
        for(double v:variables){
            s = s+v;
        }
      return  s+type;
      
      
}
}
