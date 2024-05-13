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
    private String query_var;
    private Map<String, String> evedence_vars_dict = new HashMap<>(); // for example B=T
    private String[] hidden_vars_arr;
    private List<Factor> factors_list = new LinkedList<>();
    private NodeList net_file_nodeList;

    public Variable_Elimination_Algo(String problem, NodeList net_file_nodeList){
        count_add = 0;
        count_mul = 0;
        this.problem = problem;
        this.analyse_problem();
        this.net_file_nodeList = net_file_nodeList;
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

        // Loop through each element
        for (int temp = 0; temp < net_file_nodeList.getLength(); temp++) {
            // Process your XML nodes here
            Node node = net_file_nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element definitionElement = (Element) node;
                if (definitionElement.getTagName() == "DEFINITION") {
                    Factor new_factor = new Factor();
                    // Get child elements within "DEFINITION"
                    NodeList childNodes = definitionElement.getChildNodes();
                    // Loop over child elements
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            // Check for "for", "table", and "given" elements
                            if (childElement.getTagName().equals("FOR") ||
                                childElement.getTagName().equals("GIVEN")) {
                                new_factor.add_to_variables_list(childElement.getTextContent());
                            }
                            if (childElement.getTagName().equals("TABLE")) {
                                String values = childElement.getTextContent();
                                String[] values_parts = values.split(" ");
                                for (String val : values_parts) {
                                    try {
                                        double doubleValue = Double.parseDouble(val);
                                        // Add the double value to your list
                                        new_factor.add_to_values_list(doubleValue);
                                    } catch (NumberFormatException e) {
                                        // Handle the case where the value cannot be parsed as a double
                                        System.err.println("Failed to parse value as double: " + val);
                                    }
                                }
                                
                            }
                        }
                    }
                    factors_list.add(new_factor);
                }
            }
        }
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
                    String result = this.problem + ",0,0"; //how to get the correct answer???
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
