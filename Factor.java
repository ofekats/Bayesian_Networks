import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factor {
    private List<String> variables_list = new ArrayList<>();
    // private List<Double> values_list = new ArrayList<>();
    private Map<String, Double> probabilities = new HashMap<>();

    public Factor()
    {
    }

    // public Factor(List<String> variables_list, List<Double> values_list)
    // {
    //     this.variables_list = variables_list;
    //     this.values_list = values_list;
    // }

    public void add_to_variables_list(String var)
    {
        this.variables_list.add(var);
    }

    public void add_to_values_list(String var, double val) // "A=T|E=T,B=T":NUMBER
    {
        // this.values_list.add(val);
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
        // for (double val : this.values_list)
        //     {
        //         st += val + " ";
        //     }
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
