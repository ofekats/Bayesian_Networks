import java.text.DecimalFormat;
import java.util.*;
import org.w3c.dom.NodeList;

//class for the Variable Elimination algo implementation
public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    private int count_add;
    private int count_mul;
    //the current problem needed to be solve
    private String problem;
    //only query and evidence vars from the problem with their options
    private String problem_vars;
    private String query_var;
    private String query_var_with_answer;
    //map of all the evidence and their option: key: evidence var, value: option
    private Map<String, String> evidence_vars_dict = new HashMap<>(); // for example B=T
    private List<String> hidden_vars_list = new ArrayList<>();
    private List<Factor> factors_list = new LinkedList<>();
    //the bayesian network xml file object
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
            Collections.addAll(this.hidden_vars_list, hidden_arr);
        }

        //for get_probability
        problem_vars = problem.substring(this.problem.indexOf("(")+1,index_end_evidence);
    }

    //using the Base_net_handler class to help creating the factors
    //and after removing what not necessary to have:
    //(1)removing each probability that doesn't have the option of the given evidence
    //(2)remove factors that only have one value in the probability map with the help of func- remove_factors_with_one_val
    //(3)remove all vars that not ancestor to query or evidence with the help of func- get_parents
    //(4)remove all vars that independent of query by knowing evidence - find with BaseBall algo with the help of func- remove_vars_by_BaseBall
    private void create_factors(){
        factors_list = Base_net_handler.create_factors(net_file_nodeList);

        //only the needed values given with the evidence vars
        //go through each evidence and each factors- if the evidence in that factor remove all probabilities that have different option that what needed
        List<String> to_remove = new ArrayList<>();
        for (String evidence : evidence_vars_dict.keySet()) {
            for (Factor fac : factors_list) {
                if (fac.is_var_in_factor(evidence)) {
                    to_remove.clear();
                    for (String prob_vars : fac.get_probability_map().keySet()) {
                        String evidence_option = evidence + "=" + evidence_vars_dict.get(evidence);
                        if (!prob_vars.contains(evidence_option)) {
                            to_remove.add(prob_vars);
                        }
                    }
                    for (String remove : to_remove) {
                        fac.remove_from_probability_map(remove);
                    }
                    fac.update_size();
                }
            }
        }

        remove_factors_with_one_val();

        //remove all vars that not ancestor to query or evidence
        List<String> parents = new ArrayList<>();
        //add query and evidence to parent
        parents.add(this.query_var);
        parents.addAll(this.evidence_vars_dict.keySet());
        List<String> add_to_parents = new ArrayList<>();
        //using the Base_net_handler class to help creating the nodes and know all the parents
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
        List<String> vars_to_remove = new ArrayList<>();
        for(Factor fac : this.factors_list)
        {
            for(String var : fac.get_variables_list())
            {
                if(!parents.contains(var)){
                    vars_to_remove.add(var);
                }
            }
        }
        for(String var : vars_to_remove)
        {
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

        //remove all vars that independent of query by knowing evidence - find with BaseBall algo
        this.remove_vars_by_BaseBall();
        this.count_add = 0;
        this.count_mul = 0;
    }


    //recursive function that gets all the ancestor of a vars using nodes
    private List<String> get_parents(Node_net node, List<String> parents)
    {
        List<String> add_to_parents = new ArrayList<>();
        add_to_parents.add(node.get_node_var());
        for(Node_net node_dad : node.get_parents()){
            add_to_parents.add(node_dad.get_node_var());
            for(Node_net node_grandad : node_dad.get_parents()){
                add_to_parents.addAll(parents);
                add_to_parents.add(node_dad.get_node_var());
                add_to_parents.addAll(get_parents(node_grandad, add_to_parents));
            }
        }
        parents.addAll(add_to_parents);
        return parents;
    }

    //remove all vars that independent of query by knowing evidence - find by BaseBall algo
    private void remove_vars_by_BaseBall(){
        String new_baseball_problem;
        List<String> to_remove_var = new ArrayList<>();
        // for every hidden var run BaseBall to know if independent or not
        for(String hidden : this.hidden_vars_list){
            new_baseball_problem = "";
            new_baseball_problem += this.query_var + "-" + hidden + "|";
            for(String evidence : this.evidence_vars_dict.keySet()){
                new_baseball_problem += evidence + "=" + this.evidence_vars_dict.get(evidence) + ",";
            }
            new_baseball_problem = new_baseball_problem.substring(0, new_baseball_problem.length()-1);
            Bayes_Ball_Algo bb = new Bayes_Ball_Algo(new_baseball_problem, net_file_nodeList);
            //return yes or no
            String result = bb.run_algo();
            //if are independent - remove all factors with that var
            if(result.equals("yes"))
            {
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
        // for every evidence var run BaseBall to know if independent or not
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
            Bayes_Ball_Algo bb = new Bayes_Ball_Algo(new_baseball_problem, net_file_nodeList);
            //return yes or no
            String result = bb.run_algo();
            //if are independent - remove all factors with that var
            if(result.equals("yes"))
            {
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
        List<Factor> factors_to_remove = new LinkedList<>();
        for(Factor fac : factors_list){
            if(fac.get_size() <= 1){
                factors_to_remove.add(fac);
            }
        }
        for(Factor fac_to_remove : factors_to_remove){
            this.factors_list.remove(fac_to_remove);
        }
    }

    //join all the factors with the hidden value, with the help of func- the_joining
    private void join_factor(String hidden){

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
            while(new_factors.size() > 1)
            {
                //sort the factors by size
                Collections.sort(new_factors);
                new_factors = the_joining(new_factors);
            }


            for(Factor fac: new_factors){
                fac.remove_var_that_not_show();
            }
            factors_list.addAll(new_factors);
        }else{
            //if there is only one factor not need to join
            factors_list.addAll(factors_to_join);
        }


        remove_factors_with_one_val();
        //don't want to eliminate the query var
        if(!hidden.equals(this.query_var))
        {
            this.eliminate(hidden);
        }
    }

    //join 2 factors from a list of factors that sort by size or ascii sum (if size is equal), with the help of func- join_2_factors
    //only join 2 to make sure the sizing of the factors (including the new one) is correct
    private List<Factor> the_joining(List<Factor> factors_to_join) {
        //create new factor - join of two
        Factor new_factor = new Factor();

        int factors_to_join_size = factors_to_join.size();

        //if there is only one factor
        if(factors_to_join_size == 1){
            return factors_to_join;
        }

        new_factor = this.join_2_factors(factors_to_join.get(0), factors_to_join.get(1));
        factors_to_join.remove(0);
        factors_to_join.remove(0);
        factors_to_join.add(new_factor);
        return factors_to_join;
    }

    //join 2 factors and return the new factor that been made
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

        //add the new values
        Map<String, Double> map1;
        Map<String, Double> map2;
        //map1 is bigger then map2 (or equal)
        map1 = f1.get_probability_map();
        map2 = f2.get_probability_map();
        //the vars that in both of the list that needs to be matched for mul
        List<String> var_the_same = new ArrayList<>(f1.get_variables_list());
        var_the_same.retainAll(f2.get_variables_list());
        //evidence vars are with the same option always (made sure in create_factors)- so don't need to be in var_the_same
        for(String evid : this.evidence_vars_dict.keySet()){
            var_the_same.remove(evid);
        }


        //check to mul only the correct values that matched
        for(String var_from1 : map1.keySet())
        {

            //mul the correct values
            List<String> need_to_have = new ArrayList<>();
            for(String same_var : var_the_same){
                if(var_from1.contains(same_var)){
                    int index_same = var_from1.indexOf(same_var+"=");
                    int last;
                    if(var_from1.replace("|",",").substring(index_same).contains(",")){
                        last = index_same + var_from1.replace("|",",").substring(index_same).indexOf(",");
                    }else{
                        last = var_from1.length();
                    }
                    String need = "";
                    if(var_from1.length() >= last){
                        need = var_from1.substring(index_same, last);
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
                if(flag == 0){ //if var_from1 and var_from2 matched and need to be multiplying
                    mul *= map2.get(var_from2);
                    new_factor.add_to_values_to_map(var_from1 +","+ var_from2, mul);
                    this.count_mul++;
                }
                flag = 0;
                mul = map1.get(var_from1);
            }
        }
       return new_factor;
    }

    //eliminate hidden from all factors
    private void eliminate(String hidden){

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
            //add the vars to the new factor
            for(String var : fac.get_variables_list()) {
                new_factor.add_to_variables_list(var);
            }
            //add the option map to the new factor
            new_factor.new_map_var_to_all_the_options(fac.get_option_map());

            //add the matched values only
            Map<String, Double> to_add = new HashMap<>();
            for(String option : fac.get_option_map().get(hidden)) {
                hidden_options = hidden + "=" + option;

                for (String var : fac.get_probability_map().keySet()) {
                    if (!var.equals(var.replace(hidden_options, ""))) {
                        String no_hidden = var.replace(hidden_options, "");
                        if(to_add.containsKey(no_hidden)) {
                            to_add.put(no_hidden, fac.get_probability_map().get(var)+to_add.get(no_hidden));
                            this.count_add++;
                        }else {
                            if(no_hidden.contains(hidden+"=")){
                                continue;
                            }
                            to_add.put(no_hidden, fac.get_probability_map().get(var));
                        }
                    }
                }
            }
            for(String var : to_add.keySet()){
                new_factor.add_to_values_to_map(var, to_add.get(var));
            }
            //remove the fac with no elimination and add the new one that after the elimination
            this.factors_list.remove(fac);
            new_factor.remove_var_that_not_show();
            this.factors_list.add(new_factor);
        }

        remove_factors_with_one_val();

    }

    //normalization - add all probability and divide each one by the sum
    private void normalization(){

        double sum_all = 0;
        for(Factor fac : this.factors_list){
            if(fac.is_var_in_factor(this.query_var))
            {
                for(String var : fac.get_probability_map().keySet()){
                    sum_all += fac.get_probability_map().get(var);
                    this.count_add++;
                }
                this.count_add--; //only count each add - I counted each number in the addition so need to remove one
                for(String var : fac.get_probability_map().keySet()){
                    fac.get_probability_map().put(var,fac.get_probability_map().get(var) / sum_all);
                }
            }
        }
    }

    private String check_if_already_have_the_answer(DecimalFormat df){
        int flag_have_answer = 1;
        String result = "";
        for(Factor fac : factors_list)
        {

            //if we have factor with all the query and evidence
            if (fac.is_var_in_factor(query_var)){
                for (String var : evidence_vars_dict.keySet())
                {
                    if(!fac.is_var_in_factor(var)){
                        flag_have_answer = 0;
                    }
                }

                if (flag_have_answer == 1 && fac.get_variables_list().size() == 1+this.evidence_vars_dict.keySet().size()){
                    //if there are no evidence
                    if(this.evidence_vars_dict.keySet().size() == 0){
                        if(fac.get_probability(this.query_var_with_answer) != -1.0)
                        {
                            result = df.format(fac.get_probability(this.query_var_with_answer)) + ",0,0";
                            return result;
                        }
                    }
                    //if there are evidence
                    double probability = fac.get_probability(problem_vars);
                    //if the probability map have the vars organised like in the problem
                    if(probability != -1.0)
                    {
                        result = df.format(probability) + ",0,0";
                        return result;
                    }else{ //if the evidence vars organised different in the probability map
                        int flag = 1;
                        String[] need_to_have = problem_vars.replace("|",",").split(",");
                        need_to_have[0] = need_to_have[0]+"|";
                        String vars_prob = "";
                        for(String vars : fac.get_probability_map().keySet()){
                            vars_prob = vars;
                            for(String need : need_to_have){
                                if(!vars.contains(need)){
                                    flag = 0;
                                    break;
                                }
                            }
                            if(flag == 1 && fac.get_probability(vars_prob) != -1.0){
                                result = df.format(fac.get_probability(vars_prob)) + ",0,0";
                                return result;
                            }
                        }
                    }
                }
                flag_have_answer = 1;
            }
        }
        return result;
    }

    //run the algo part by part:
    //(1) create the factors
    //(2) check if the answer already known
    //(3) for each hidden:
    //                    (3.1) do join to all the factors with the hidden
    //                    (3.2) do eliminate
    //(4) join to all the remaining factors
    //(5) do normalization
    //() return result
    public String run_algo(){

        // create a DecimalFormat instance for 5 decimal places
        DecimalFormat df = new DecimalFormat("0.00000");

        this.create_factors();

        //if already have an answer
        String result = check_if_already_have_the_answer(df);
        if(result != ""){
            return result;
        }

        //for each hidden do join and then eliminate
        for (String hidden : hidden_vars_list){
            this.join_factor(hidden); //in join call eliminate with the hidden value
        }

        this.join_factor(this.query_var);
        this.normalization();

        //return the result
        double res = 0;
        String needed = this.problem.substring(2,5);

        for(Factor fac : this.factors_list)
        {
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
        result = df.format(res) + "," + count_add + "," + count_mul;
        return result;
    }
}
