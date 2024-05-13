import java.util.LinkedList;
import java.util.List;

public class Factor {
    private List<String> variables_list = new LinkedList<>();
    private List<Double> values_list = new LinkedList<>();

    public Factor()
    {
    }

    public Factor(List<String> variables_list, List<Double> values_list)
    {
        this.variables_list = variables_list;
        this.values_list = values_list;
    }

    public void add_to_variables_list(String var)
    {
        this.variables_list.add(var);
    }

    public void add_to_values_list(double val)
    {
        this.values_list.add(val);
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
        for (double val : this.values_list)
            {
                st += val + " ";
            }
        return st;
    }


}
