import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.NodeList;

public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    private int count_add;
    private int count_mul;
    private String problem;
    String problem_vars;
    private String query_var;
    private Map<String, String> evidence_vars_dict = new HashMap<>(); // for example B=T
    private String[] hidden_vars_arr;
    private List<Factor> factors_list = new LinkedList<>();
    private NodeList net_file_nodeList;

    public Variable_Elimination_Algo(String problem, NodeList net_file_nodeList){
        count_add = 0;
        count_mul = 0;
        this.problem = problem;
        this.analyse_problem(); //get query, evidence and hidden
        this.net_file_nodeList = net_file_nodeList;
    }

    //get the query, evidence and hidden vars from the problem
    private void analyse_problem(){
        //query
        this.query_var = problem.substring(2, 3);
        //evidence
        int index_end_evidence = problem.indexOf(")");
        if(problem.length() > 6 && index_end_evidence > 6 ){
            String evidence_str = problem.substring(6, index_end_evidence);
            String[] evidence_str_parts = evidence_str.split(",");
            for(String evidence : evidence_str_parts){
                String[] var_bool = evidence.split("=");
                this.evidence_vars_dict.put(var_bool[0], var_bool[1]);
            }
        }
        
        //hidden
        if(problem.length() > index_end_evidence+1){
            String hidden_str = problem.substring(index_end_evidence+1);
            this.hidden_vars_arr = hidden_str.split("-");
        }

        //for get_probability
        if(evidence_vars_dict.size() != 1){
            problem_vars = problem.substring(2,index_end_evidence);
        }else{
            problem_vars = problem.substring(2);
        }
    }

    private void create_factors(){ 
        System.out.println("create factors");
        factors_list = Base_net_handler.create_factors(net_file_nodeList);

        System.out.println("factors before removing oneline:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac.toString());
        }

        //only the needed values given with the evidence vars
        List<String> to_remove = new ArrayList<>();
        for (String evidence : evidence_vars_dict.keySet()) {
            for (Factor fac : factors_list) {
                if (fac.is_var_in_factor(evidence)) {
                    for (String prob_vars : fac.get_probability_map().keySet()) {
                        String evidence_option = evidence + "=" + evidence_vars_dict.get(evidence);
    //                        System.out.println("evidence:" + evidence + " evidence_option: " + evidence_option);
                        if (!prob_vars.contains(evidence_option)) {
                            to_remove.add(prob_vars);
                        }
                    }
                    for (String remove : to_remove) {
                        fac.remove_from_probability_map(remove);
                    }
                }
            }
        }

        remove_factors_with_one_val();
        System.out.println("factors from create factors:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac.toString());
        }
    }
        

    private void remove_factors_with_one_val(){ 
        System.out.println("remove factors with one val");
        List<Factor> factors_to_remove = new LinkedList<>();
        for(Factor fac : factors_list){
            if(fac.get_probability_map().size() <= 1){
                factors_to_remove.add(fac);
            }
        }
        for(Factor fac_to_remove : factors_to_remove){
            this.factors_list.remove(fac_to_remove);
        }
    }

    private void join_factor(String hidden){ 
        System.out.println("join");
        System.out.println("hidden:" + hidden);
        System.out.println("factors:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac.toString());
        }
        List<Factor> factors_to_join = new ArrayList<>();
        //only the factor the hidden is in
        for(Factor fac : factors_list){
            if(fac.is_var_in_factor(hidden)){
                factors_to_join.add(fac);
            }
        }
        //remove the join from the real factor list
        for(Factor fac_to_remove : factors_to_join){
            this.factors_list.remove(fac_to_remove);
        }
        //create new factor - join of two
        Factor new_factor = new Factor();

        int factors_to_join_size = factors_to_join.size();
        System.out.println("before factors_to_join size: " + factors_to_join.size());

        for(int i = 0; i < factors_to_join_size - 1; i+=2){

            System.out.println("i:"+ i + ", factors_to_join size: " + factors_to_join.size());

            //add the vars into the new factor - combine the two lists
            for(String var : factors_to_join.get(i).get_variables_list()){
                new_factor.add_to_variables_list(var);
            }
            for(String var : factors_to_join.get(i+1).get_variables_list()){
                new_factor.add_to_variables_list(var);
            }

            System.out.println("the new var list: " + new_factor.get_variables_list());

            //add the new values
            Map<String, Double> map1 = new HashMap<>();
            Map<String, Double> map2 = new HashMap<>();
            //map1 is bigger then map2
            if(factors_to_join.get(i).get_probability_map().size() >=  factors_to_join.get(i+1).get_probability_map().size())
            {
                map1 = factors_to_join.get(i).get_probability_map();
                map2 = factors_to_join.get(i+1).get_probability_map();
            }else{
                map1 = factors_to_join.get(i+1).get_probability_map();
                map2 = factors_to_join.get(i).get_probability_map();
            }

            //the vars that in both of the list that needs to be matched for mul
            List<String> var_the_same = new ArrayList<>(factors_to_join.get(i).get_variables_list());
            var_the_same.retainAll(factors_to_join.get(i+1).get_variables_list());

            //print
            System.out.println("list1: "+ factors_to_join.get(i).get_variables_list());
            System.out.println("map1: "+ map1);
            System.out.println("list2: "+ factors_to_join.get(i+1).get_variables_list());
            System.out.println("map2: "+ map2);
            System.out.println("var_the_same: "+ var_the_same);


            for(String var_from1 : map1.keySet())
            {
                //print
                System.out.println("var_the_same: ");
                for(String same_var : var_the_same){
                    System.out.print(same_var + " ");
                }
                System.out.println();

                //mul the correct values
                List<String> need_to_have = new ArrayList<>();
                for(String same_var : var_the_same){
                    if(var_from1.contains(same_var)){
                        int index_same = var_from1.indexOf(same_var);
                        int last = index_same + 2;
                        System.out.println("var: " + same_var+ ", var_from1: " + var_from1);
                        System.out.println("index_same: " + index_same+ ", last: " + last);
                        String need = "";
                        if(index_same != 0){
                            need = var_from1.substring(index_same, last);
                        }else{
                            need = var_from1.substring(last);
                        }

                        need_to_have.add(need);
                    }
                }
                int flag = 0;
                double mul = map1.get(var_from1);
                for(String var_from2 : map2.keySet()){
                    for(String needed : need_to_have){
                        if(!var_from2.contains(needed)){
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 0){
                        mul *= map2.get(var_from2);
                        new_factor.add_to_values_to_map(var_from1 +","+ var_from2, mul);
                        this.count_mul++;
                        break;
                    }
                    flag = 0;
                }
            }

            factors_to_join.add(new_factor);
            if(factors_to_join.size() == 1){
                break;
            }
            factors_to_join_size++;
        }

        System.out.println("after factors_to_join size: " + factors_to_join.size());

        for(Factor fac: factors_to_join){
            fac.remove_var_that_not_show();
        }
        int size_join = factors_to_join.size() -1;
        if(size_join >= 0){
            factors_list.add(factors_to_join.get(size_join));
        }

        for(Factor fac : factors_list) {
            //print
            System.out.println(fac.toString());
        }
            System.out.println("end join");
    }

    private void eliminate(String hidden){ //what to get and return?
        System.out.println("eliminate");

    }

    private void normalization(){ //what to get and return?
        System.out.println("normalization");
    }

    public String run_algo(){
        //prints
        System.out.println("\n  new problem--- ");
        System.out.println("problem: " + problem);
        System.out.println("query var: " + query_var);
        System.out.println("evidence_vars_dict: " + evidence_vars_dict);
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
                for (String var : evidence_vars_dict.keySet())
                {
                    if(!fac.is_var_in_factor(var)){
                        flag_have_answer = 0;
                    }
                }
                if (flag_have_answer == 1){
                    System.out.println("have the answer!");
                    System.out.println("problem_vars: " + problem_vars);
                    String result = fac.get_probability(problem_vars) + ",0,0";
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
