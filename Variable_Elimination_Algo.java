import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    private int count_add;
    private int count_mul;
    private String problem;
    String problem_vars;
    private String query_var;
    private Map<String, String> evedence_vars_dict = new HashMap<>(); // for example B=T
    private String[] hidden_vars_arr;
    private List<Factor> factors_list = new LinkedList<>();
    private NodeList net_file_nodeList;

    public Variable_Elimination_Algo(String problem, NodeList net_file_nodeList){
        count_add = 0;
        count_mul = 0;
        this.problem = problem;
        this.analyse_problem(); //get query, eveidence and hidden
        this.net_file_nodeList = net_file_nodeList;
    }

    //get the query, evedence and hidden vars from the problem
    private void analyse_problem(){
        //query
        this.query_var = problem.substring(2, 3);
        //evidence
        int index_end_evedence = problem.indexOf(")");
        if(problem.length() > 6 && index_end_evedence > 6 ){
            String evedence_str = problem.substring(6, index_end_evedence);
            String[] evedence_str_parts = evedence_str.split(",");
            for(String evedevce : evedence_str_parts){
                String[] var_bool = evedevce.split("=");
                this.evedence_vars_dict.put(var_bool[0], var_bool[1]);
            }
        }
        
        //hidden
        if(problem.length() > index_end_evedence+1){
            String hidden_str = problem.substring(index_end_evedence+1);
            this.hidden_vars_arr = hidden_str.split("-");
        }

        //for get_probability
        if(evedence_vars_dict.size() != 1){
            problem_vars = problem.substring(2,index_end_evedence);
        }else{
            problem_vars = problem.substring(2);
        }
    }

    private void create_factors(){ 
        System.out.println("create factors");
        factors_list = Base_net_handler.create_factors(net_file_nodeList);
    }

    private void join_factor(String hidden){ //what to get and return?
        System.out.println("join");
    }

    private void eliminate(String hidden){ //what to get and return?
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

        //if already have an answer
        int flag_have_answer = 1;
        for(Factor fac : factors_list)
        {
            //print
            System.out.println(fac.toString());


            if (fac.is_var_in_factor(query_var)){
                for (String var : evedence_vars_dict.keySet())
                {
                    if(fac.is_var_in_factor(var) == false){
                        flag_have_answer = 0;
                    }
                }
                if (flag_have_answer == 1){
                    System.out.println("have the answer!");
                    System.out.println("problem_vars: " + problem_vars);
                    String result = fac.get_propability(problem_vars) + ",0,0";
                    return result;
                }
                flag_have_answer = 1;
            }
        }

        for (String hidden : hidden_vars_arr){
            this.join_factor(hidden);
            this.eliminate(hidden);
        }
        this.normalization();
        System.out.println("\n");
        String result = this.problem + "," + count_add + "," + count_mul;
        return result;
    }
}
