import java.text.DecimalFormat;
import java.util.*;

import org.w3c.dom.NodeList;

public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    private int count_add;
    private int count_mul;
    private String problem;
    String problem_vars;
    private String query_var;
    private String query_var_with_answer;
    private Map<String, String> evidence_vars_dict = new HashMap<>(); // for example B=T
    private List<String> hidden_vars_list = new ArrayList<>();
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
        this.query_var = problem.substring(this.problem.indexOf("P")+2, this.problem.indexOf("="));
        this.query_var_with_answer = problem.substring(this.problem.indexOf("P")+2, this.problem.indexOf("|"));
        System.out.println("query_var_with_answer: "+ query_var_with_answer);
        //evidence
        int index_end_evidence = problem.indexOf(")");
        int index_start_evidence = problem.indexOf("|")+1;
        if(problem.length() > index_start_evidence && index_end_evidence > index_start_evidence ){
            String evidence_str = problem.substring(index_start_evidence, index_end_evidence);
            String[] evidence_str_parts = evidence_str.split(",");
            for(String evidence : evidence_str_parts){
                String[] var_bool = evidence.split("=");
                this.evidence_vars_dict.put(var_bool[0], var_bool[1]);
            }
        }

        //hidden
        if(problem.length() > index_end_evidence+2){
            String hidden_str = problem.substring(index_end_evidence+2);
            String[] hidden_arr = hidden_str.split("-");
            for(String hid : hidden_arr){
                this.hidden_vars_list.add(hid);
            }
        }

        //for get_probability
        problem_vars = problem.substring(this.problem.indexOf("(")+1,index_end_evidence);
    }

    private void create_factors(){
        System.out.println("create factors");
        factors_list = Base_net_handler.create_factors(net_file_nodeList);

        System.out.println("factors before removing oneline:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac);
        }

        //only the needed values given with the evidence vars
        List<String> to_remove = new ArrayList<>();
        for (String evidence : evidence_vars_dict.keySet()) {
            for (Factor fac : factors_list) {
                if (fac.is_var_in_factor(evidence)) {
                    to_remove.clear();
                    for (String prob_vars : fac.get_probability_map().keySet()) {
                        String evidence_option = evidence + "=" + evidence_vars_dict.get(evidence);
                            System.out.println("evidence:" + evidence + " evidence_option: " + evidence_option);
                        if (!prob_vars.contains(evidence_option)) {
                            to_remove.add(prob_vars);
                        }
                    }
                    for (String remove : to_remove) {
                        fac.remove_from_probability_map(remove);
                    }
                    fac.update_size();
                }
//                if(fac.is_var_in_factor(this.query_var)){
//                    to_remove.clear();
//                    for (String prob_vars : fac.get_probability_map().keySet()) {
//                        String query_var_with_answer = problem.substring(this.problem.indexOf("P")+2, this.problem.indexOf("|"));
//                        if (!prob_vars.contains(query_var_with_answer)) {
//                            to_remove.add(prob_vars);
//                        }
//                    }
//                    for (String remove : to_remove) {
//                        fac.remove_from_probability_map(remove);
//                    }
//                    fac.update_size();
//                }
            }
        }

        remove_factors_with_one_val();
        System.out.println("factors from create factors:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac);
        }
        //remove all vars that not ancestor to query or evidence - need to find then call eliminate
        List<String> parents = new ArrayList<>();
        //add query and evidence to parent
        parents.add(this.query_var);
        parents.addAll(this.evidence_vars_dict.keySet());
        List<String> add_to_parents = new ArrayList<>();
//        for(Factor fac : this.factors_list) {
//            System.out.println("before parents: " + parents);
//            System.out.println("fac.get_variables_list(): " + fac.get_variables_list());
//            for (String par : parents) {
//                if (fac.is_var_in_factor(par)) {
//                    add_to_parents.addAll(fac.get_variables_list());
//                }
//            }
//        }
        Map<String, Node_net> new_nodes = Base_net_handler.create_nodes(this.net_file_nodeList);
        for(String node : new_nodes.keySet())
        {
            if(parents.contains(node)){
                for(Node_net par_node : new_nodes.get(node).get_parents()){
                    parents.add(par_node.get_node_var());
                }
                add_to_parents.addAll(get_parents(new_nodes.get(node), parents));
            }
        }
        parents.addAll(add_to_parents);
        System.out.println("after parents: " + parents);
        List<String> to_eliminate = new ArrayList<>();
        for(Factor fac : this.factors_list)
        {
            for(String var : fac.get_variables_list())
            {
                System.out.println("var: " + var);
                if(!parents.contains(var)){
                    to_eliminate.add(var);
                }
            }
        }
        System.out.println("vars to eliminate: " + to_eliminate);
        for(String var : to_eliminate)
        {
//            eliminate(var);
            this.evidence_vars_dict.remove(var);
            this.hidden_vars_list.remove(var);
            List<Factor> to_rem = new ArrayList<>();
            for(Factor fac : this.factors_list)
            {
                if(fac.is_var_in_factor(var))
                {
                    to_rem.add(fac);
                }
            }
            for(Factor fac : to_rem)
            {
                this.factors_list.remove(fac);
            }
        }

        //remove all vars that independent of query by knowing evidence - baseball find then call eliminate
        this.remove_vars_by_BaseBall();
        this.count_add = 0;
        this.count_mul = 0;
    }

    private List<String> get_parents(Node_net node, List<String> parents)
    {
        System.out.println("get_parents");
        System.out.println("node: " + node.get_node_var());
        System.out.println("parents: " + parents);
        List<String> add_to_parents = new ArrayList<>();
        add_to_parents.add(node.get_node_var());
        for(Node_net node_dad : node.get_parents()){
            add_to_parents.add(node_dad.get_node_var());
            for(Node_net node_grandad : node_dad.get_parents()){
                add_to_parents.addAll(parents);
                add_to_parents.add(node_dad.get_node_var());
                System.out.println("add_to_parents: " + add_to_parents);
                add_to_parents.addAll(get_parents(node_grandad, add_to_parents));
            }
        }
        parents.addAll(add_to_parents);
        System.out.println("add_to_parents: " + add_to_parents);
        return parents;
    }

    //remove all vars that independent of query by knowing evidence - baseball find then call eliminate
    private void remove_vars_by_BaseBall(){
        String new_baseball_problem;
        List<String> to_remove_var = new ArrayList<>();
        //hidden
        for(String hidden : this.hidden_vars_list){
            new_baseball_problem = "";
            new_baseball_problem += this.query_var + "-" + hidden + "|";
            for(String evidence : this.evidence_vars_dict.keySet()){
                new_baseball_problem += evidence + "=" + this.evidence_vars_dict.get(evidence) + ",";
            }
            new_baseball_problem = new_baseball_problem.substring(0, new_baseball_problem.length()-1);
            System.out.println("new_baseball_problem: " + new_baseball_problem);
            Bayes_Ball_Algo bb = new Bayes_Ball_Algo(new_baseball_problem, net_file_nodeList);
            //return yes or no
            String result = bb.run_algo();
            System.out.println("the result: " + result);
            if(result.equals("yes"))
            {
//                this.eliminate(hidden);
                to_remove_var.add(hidden);
                List<Factor> to_rem = new ArrayList<>();
                for(Factor fac : this.factors_list)
                {
                    if(fac.is_var_in_factor(hidden))
                    {
                        to_rem.add(fac);
                    }
                }
                for(Factor fac : to_rem)
                {
                    this.factors_list.remove(fac);
                }
            }
        }
        for(String remove_hidden : to_remove_var)
        {
            this.hidden_vars_list.remove(remove_hidden);
        }
        //evidence
        to_remove_var.clear();
        for(String evid : this.evidence_vars_dict.keySet()){
            new_baseball_problem = "";
            new_baseball_problem += this.query_var + "-" + evid + "|";
            for(String evidence : this.evidence_vars_dict.keySet()){
                if(!evidence.equals(evid)){
                    new_baseball_problem += evidence + "=" + this.evidence_vars_dict.get(evidence) + ",";
                }
            }
            if(new_baseball_problem.contains(",")){
                new_baseball_problem = new_baseball_problem.substring(0, new_baseball_problem.length()-1);
            }
            System.out.println("new_baseball_problem: " + new_baseball_problem);
            Bayes_Ball_Algo bb = new Bayes_Ball_Algo(new_baseball_problem, net_file_nodeList);
            //return yes or no
            String result = bb.run_algo();
            System.out.println("the result: " + result);
            if(result.equals("yes"))
            {
//                this.eliminate(hidden);
                to_remove_var.add(evid);
                List<Factor> to_rem = new ArrayList<>();
                for(Factor fac : this.factors_list)
                {
                    if(fac.is_var_in_factor(evid))
                    {
                        to_rem.add(fac);
                    }
                }
                for(Factor fac : to_rem)
                {
                    this.factors_list.remove(fac);
                }
            }
        }
        for(String remove_evidence : to_remove_var)
        {
            this.evidence_vars_dict.remove(remove_evidence);
        }
    }

    //check if there are factors with one value and if so remove them
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

    //join all the factors with the hidden value
    private void join_factor(String hidden){
        System.out.println("join");
        System.out.println("hidden:" + hidden);
        System.out.println("factors:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac);
        }
        List<Factor> factors_to_join = new ArrayList<>();
        //only the factor the hidden is in
        for(Factor fac : factors_list){
            if(fac.is_var_in_factor(hidden)){
                factors_to_join.add(fac);
            }
        }
        // remove the factors to join from the real factor list
        factors_list.removeAll(factors_to_join);

        //sort the factors by size
        Collections.sort(factors_to_join);

        if(factors_to_join.size() > 1) {
            List<Factor> new_factors = the_joining(factors_to_join);
            System.out.println("before while");
            while(new_factors.size() > 1)
            {
                //sort the factors by size
                Collections.sort(new_factors);
                new_factors = the_joining(new_factors);
            }

            System.out.println("after while");

            for(Factor fac: new_factors){
                fac.remove_var_that_not_show();
            }
            factors_list.addAll(new_factors);
        }else{
            System.out.println("only one factor not need to join");
            factors_list.addAll(factors_to_join);
        }

        for(Factor fac : factors_list) {
            //print
            System.out.println(fac);
        }
        System.out.println("end join");
        remove_factors_with_one_val();
        if(!hidden.equals(this.query_var))
        {
            this.eliminate(hidden);
        }
    }

    //join a list of factors the sort by size or ascii sum (if size is equal)
    private List<Factor> the_joining(List<Factor> factors_to_join) {
        //create new factor - join of two
        Factor new_factor = new Factor();

        int factors_to_join_size = factors_to_join.size();
        System.out.println("before factors_to_join size: " + factors_to_join.size());
        //if there is only one factor
        if(factors_to_join_size == 1){
            return factors_to_join;
        }
        System.out.println("factors_to_join size: " + factors_to_join.size());

        new_factor = this.join_2_factors(factors_to_join.get(0), factors_to_join.get(1));
        factors_to_join.remove(0);
        factors_to_join.remove(0);
        System.out.println("after joining");
        factors_to_join.add(new_factor);
            return factors_to_join;
        }

    private Factor join_2_factors(Factor f1, Factor f2){
        Factor new_factor = new Factor();
        //add the vars into the new factor - combine the two lists
        for(String var : f1.get_variables_list()){
            new_factor.add_to_variables_list(var);
        }
        for(String var : f2.get_variables_list()){
            new_factor.add_to_variables_list(var);
        }
        //option list like in all the factors
        new_factor.new_map_var_to_all_the_options(f1.get_option_map());
        System.out.println("the new var list: " + new_factor.get_variables_list());

        //add the new values
        Map<String, Double> map1;
        Map<String, Double> map2;
        //map1 is bigger then map2
        map1 = f1.get_probability_map();
        map2 = f2.get_probability_map();
        //the vars that in both of the list that needs to be matched for mul
        List<String> var_the_same = new ArrayList<>(f1.get_variables_list());
        var_the_same.retainAll(f2.get_variables_list());

        for(String evid : this.evidence_vars_dict.keySet()){
            if(var_the_same.contains(evid)){
                var_the_same.remove(evid);
            }
        }

        //print
        System.out.println("list1: "+ f1.get_variables_list());
        System.out.println("map1: "+ map1);
        System.out.println("list2: "+ f2.get_variables_list());
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
                    int last = index_same + 3;
                    System.out.println("var: " + same_var+ ", var_from1: " + var_from1);
                    System.out.println("index_same: " + index_same+ ", last: " + last);
                    String need = "";
                    if(var_from1.length() >= last){
                        need = var_from1.substring(index_same, last);
                    }
                    System.out.println("need: " + need);
                    need_to_have.add(need);
                }
            }
            int flag = 0;
            double mul = map1.get(var_from1);
            for(String var_from2 : map2.keySet()){
                System.out.println("var_from2: " + var_from2);
                for(String needed : need_to_have){
                    if(!var_from2.contains(needed)){
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0){
                    mul *= map2.get(var_from2);
                    new_factor.add_to_values_to_map(var_from1 +","+ var_from2, mul);
                    System.out.println("add_to_values_to_map: vars- " + var_from1 +","+ var_from2 + " value- " + mul);
                    this.count_mul++;
                    System.out.println("count_mul: " + count_mul);
                }
                flag = 0;
                mul = map1.get(var_from1);
            }
        }
       return new_factor;
    }

    //eliminate hidden from all factors
    private void eliminate(String hidden){ //what to get and return?
        System.out.println("eliminate");
        System.out.println("hidden:"+ hidden);
        System.out.println("factors:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac);
        }
        List<Factor> factors_to_eliminate = new ArrayList<>();
        //only the factor the hidden is in
        for(Factor fac : factors_list){
            if(fac.is_var_in_factor(hidden)){
                factors_to_eliminate.add(fac);
            }
        }
        // remove the factors to eliminate from the real factor list
        factors_list.removeAll(factors_to_eliminate);


        String hidden_options = "";
        // process each factor to eliminate
        for(Factor fac : factors_to_eliminate)
        {
            Factor new_factor = new Factor();
            for(String var : fac.get_variables_list()) {
                new_factor.add_to_variables_list(var);
            }
            new_factor.new_map_var_to_all_the_options(fac.get_option_map());
            System.out.println(fac.get_option_map());

            Map<String, Double> to_add = new HashMap<>();
            for(String option : fac.get_option_map().get(hidden)) {
                hidden_options = hidden + "=" + option;
                System.out.println("hidden_options: " + hidden_options);

                for (String var : fac.get_probability_map().keySet()) {
                    if (!var.equals(var.replace(hidden_options, ""))) {
                        System.out.println("original var: " + var);
                        String no_hidden = var.replace(hidden_options, "");
                        System.out.println("no_hidden: " + no_hidden);
                        if(to_add.containsKey(no_hidden)) {
                            to_add.put(no_hidden, fac.get_probability_map().get(var)+to_add.get(no_hidden));
                            this.count_add++;
                            System.out.println("count_add: " + count_add);
                        }else {
                            if(no_hidden.contains(hidden)){
                                continue;
                            }
                            to_add.put(no_hidden, fac.get_probability_map().get(var));
                        }
                        System.out.println("to_add: " + to_add);
                    }
                }
            }
            for(String var : to_add.keySet()){
                new_factor.add_to_values_to_map(var, to_add.get(var));
            }
            this.factors_list.remove(fac);
            new_factor.remove_var_that_not_show();
            this.factors_list.add(new_factor);
        }


        System.out.println("factors:");
        for(Factor fac : factors_list) {
            //print
            System.out.println(fac);
        }

        remove_factors_with_one_val();
        System.out.println("end eliminate");

    }

    //normalization - add all probability and divide each one by the sum
    private void normalization(){
        System.out.println("normalization");
        double sum_all = 0;
        for(Factor fac : this.factors_list){
            if(fac.is_var_in_factor(this.query_var))
            {
                for(String var : fac.get_probability_map().keySet()){
                    sum_all += fac.get_probability_map().get(var);
                }
                this.count_add++;
                System.out.println("count_add: "+ count_add);
                for(String var : fac.get_probability_map().keySet()){
                    fac.get_probability_map().put(var,fac.get_probability_map().get(var) / sum_all);
                }
            }
        }
    }

    public String run_algo(){

        // create a DecimalFormat instance for 5 decimal places
        DecimalFormat df = new DecimalFormat("0.00000");

        //prints
        System.out.println("\n  new problem--- ");
        System.out.println("problem: " + problem);
        System.out.println("query var: " + query_var);
        System.out.println("evidence_vars_dict: " + evidence_vars_dict);
        System.out.print("hidden_vars_arr: ");
        for (String val : hidden_vars_list){
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
                if (flag_have_answer == 1 && fac.get_variables_list().size() == 1+this.evidence_vars_dict.keySet().size()){
                    if(fac.get_probability(problem_vars) != -1.0)
                    {
                        System.out.println("have the answer!");
                        System.out.println("problem_vars: " + problem_vars);
                        String result = df.format(fac.get_probability(problem_vars)) + ",0,0";
                        return result;
                    }
                    if(this.evidence_vars_dict.keySet().size() == 0){
                        if(fac.get_probability(this.query_var_with_answer) != -1.0)
                        {
                            System.out.println("have the answer!");
                            System.out.println("this.query_var_with_answer: " + this.query_var_with_answer);
                            String result = df.format(fac.get_probability(this.query_var_with_answer)) + ",0,0";
                            return result;
                        }
                    }
                }
                flag_have_answer = 1;
            }
        }

        for (String hidden : hidden_vars_list){
            this.join_factor(hidden); //in join call eliminate with the hidden value
        }
        this.join_factor(this.query_var);
        this.normalization();
        System.out.println("\n");
        double res = 0;
        String needed = this.problem.substring(2,5);
        System.out.println("needed: " + needed);
        for(Factor fac : this.factors_list)
        {
            System.out.println("factor: " + fac);
            if(fac.is_var_in_factor(this.query_var))
            {
                for(String var : fac.get_probability_map().keySet())
                {
                    if(var.contains(needed))
                    {
                        res = fac.get_probability_map().get(var);
                    }
                }
            }
        }
        String result = df.format(res) + "," + count_add + "," + count_mul;
        return result;
    }
}
