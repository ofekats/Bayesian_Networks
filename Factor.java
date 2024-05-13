import java.util.LinkedList;
import java.util.List;

public class Factor {
    List<String> variables_list = new LinkedList<>();
    List<Integer> values_list = new LinkedList<>();

    public Factor()
    {

    }

    public boolean is_var_in_factor(String var){
        return variables_list.contains(var);
    }

}
