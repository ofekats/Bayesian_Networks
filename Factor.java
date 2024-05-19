import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factor {
    private List<String> variables_list = new ArrayList<>();
    private Map<String, Double> probabilities = new HashMap<>();

    public Factor()
    {}

    public void add_to_variables_list(String var)
    {
        if(!this.variables_list.contains(var)){
            this.variables_list.add(var);
        }
    }

    public void add_to_values_to_map(String var, double val) // "A=T|E=T,B=T":NUMBER
    {
        this.probabilities.put(var, val);
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
        return 0.0;
    }

    public Map<String, Double> get_probability_map(){
        return this.probabilities;
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

    public String toString(){
        String st = "variables_list: ";
        for (String var : this.variables_list)
            {
                st += var + " ";
            }
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
