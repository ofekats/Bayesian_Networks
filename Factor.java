import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class Factor implements Comparable<Factor> {
    private Map<String, List<String>> map_var_to_all_the_options = new HashMap<>();
    private List<String> variables_list = new ArrayList<>();
    private Map<String, Double> probabilities = new HashMap<>();
    int size;

    public Factor()
    {this.size = 0;}

    public int compareTo(Factor other) {
        if(this.size != other.size){
            return Integer.compare(this.size, other.size);
        }
        return Integer.compare(this.ascii_var(), other.ascii_var());
    }

    //return the ascii sum of the variables
    public int ascii_var()
    {
        int sum = 0;
        for (String var : this.variables_list) {
            if (var != null && var.length() == 1) {
                char ch = var.charAt(0); // get the character
                sum += ch;
            }
        }
        return sum;
    }
    public void new_map_var_to_all_the_options(Map<String, List<String>> new_map)
    {
        this.map_var_to_all_the_options = new_map;
    }

    public void add_to_variables_list(String var)
    {
        if(!this.variables_list.contains(var)){
            this.variables_list.add(var);
        }
    }

    public void add_to_values_to_map(String var, double val) // "A=T|E=T,B=T":NUMBER
    {
        this.probabilities.put(var, val);
        this.size = probabilities.size();
    }

    public boolean is_var_in_factor(String var){
        return this.variables_list.contains(var);
    }

    public List<String> get_variables_list(){
        return this.variables_list;
    }

    public Double get_probability(String vars){
        for(String vals_and_option : this.probabilities.keySet())
        {
            if(vals_and_option.equals(vars))
            {
                return this.probabilities.get(vars);
            }
        }
        return -1.0;
    }

    public Map<String, List<String>> get_option_map()
    {
        return this.map_var_to_all_the_options;
    }

    public Map<String, Double> get_probability_map(){
        return this.probabilities;
    }

    public void remove_from_probability_map(String vars){
        this.probabilities.remove(vars);
    }

    public void remove_var_from_variables_list(String var){
        this.variables_list.remove(var);
    }
    public void remove_var_that_not_show(){
        int flag =0;
        List<String> to_remove = new ArrayList<>();
        for(String var : this.variables_list){
            for(String key : this.probabilities.keySet()){
                if(key.contains(var)){
                    flag++;
                }
            }
            if(flag == 0){
                to_remove.add(var);
            }
            flag = 0;
        }

        for(String del : to_remove){
            this.variables_list.remove(del);
        }
    }

    public void update_size()
    {
        this.size = probabilities.size();
    }

    public String toString(){
        String st = "variables_list: ";
        for (String var : this.variables_list)
            {
                st += var + " ";
            }
        st += "\n options list: ";
        for(String option : this.map_var_to_all_the_options.keySet()){
            st += "var:"+ option +" options:";
            for (String options :  this.map_var_to_all_the_options.get(option))
            {
                st += options + " ";
            }
            st+= "\n";
        }
        st+= "\nsize " + this.size;
        st += "\nvalues_list: ";
        if(this.probabilities != null)
        {
            for (String var : this.probabilities.keySet()){
                st += var + ": " + probabilities.get(var) + "\n";
            }
        }
        else{
            st += "\n this.probabilities == null\n";
        }
        
        st += "\n ";
        return st;
    }


}
