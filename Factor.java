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
        this.variables_list.add(var);
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
