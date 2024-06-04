import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//class of factors for the Variable Elimination algo
//each factor contain (1)map of option- key:variable, value:all the variable option (for example {A:T,F}),
//                    (2)list of all the variables in this factor,
//                    (3)map of probability- key:the variables and options, value:the probability (for example {J=T|A=T: 0.9}),
//                    (4)size of the probability table
//this class implement Comparable to compare factors by size
public class Factor implements Comparable<Factor> {
    private Map<String, List<String>> map_var_to_all_the_options = new HashMap<>(); //for example {A:T,F}
    private List<String> variables_list = new ArrayList<>();
    private Map<String, Double> probabilities = new HashMap<>(); //for example {J=T|A=T: 0.9}
    int size;

    public Factor()
    {this.size = 0;}

    //for the join: the factors need to be from the smallest to the largest size
    //if the size is equal: they will be ordered by the sum of the ASCII values of the factor's variables (from smallest to largest)
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
                char ch = var.charAt(0);
                sum += ch;
            }
        }
        return sum;
    }

    //create the map using an already existing map
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

    //return the probability or if it doesn't exist return -1.0
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

    public int get_size(){
        return this.size;
    }

    public void remove_from_probability_map(String vars){
        this.probabilities.remove(vars);
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
