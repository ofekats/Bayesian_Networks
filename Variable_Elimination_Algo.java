import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    int count_add;
    int count_mul;
    String problem;
    String query_var;
    Map<String, String> evedence_vars_dict = new HashMap<>(); // for example B=T
    String[] hidden_vars_arr;
    List<Factor> factors_list = new LinkedList<>();

    public Variable_Elimination_Algo(String problem){
        count_add = 0;
        count_mul = 0;
        this.problem = problem;
        this.analyse_problem();
    }

    //get the query, evedence and hidden vars from the problem
    private void analyse_problem(){
        //query
        this.query_var = problem.substring(2, 3);
        //evidence
        int index_end_evedence = problem.indexOf(")");
        String evedence_str = problem.substring(6, index_end_evedence);
        String[] evedence_str_parts = evedence_str.split(",");
        for(String evedevce : evedence_str_parts){
            String[] var_bool = evedevce.split("=");
            this.evedence_vars_dict.put(var_bool[0], var_bool[1]);
        }
        //hidden
        if(problem.length() > index_end_evedence+1){
            String hidden_str = problem.substring(index_end_evedence+1);
            this.hidden_vars_arr = hidden_str.split("-");
        }
    }

    private void create_factors(){ //what to get and return?
        System.out.println("create factors");
    }

    private void join_factor(){ //what to get and return?
        System.out.println("join");
    }

    private void eliminate(){ //what to get and return?
        System.out.println("eliminate");
    }

    private void normalization(){ //what to get and return?
        System.out.println("normalization");
    }

    public String run_algo(){
        //prints
        System.out.println("problem: " + problem);
        System.out.println("query var: " + query_var);
        System.out.println("evedence_vars_dict: " + evedence_vars_dict);
        System.out.print("hidden_vars_arr: ");
        for (String val : hidden_vars_arr){
            System.out.print(val + " ");
        }
        System.out.println();


        this.create_factors();
        while (hidden_vars_arr.length == 0){ //!= 0
            this.join_factor();
            this.eliminate();
        }
        this.normalization();
        String result = this.problem + "," + count_add + "," + count_mul;
        return result;
    }
}
