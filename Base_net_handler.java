import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//this class read from the XML file of the bayesian network
//help take the necessary information for creating Factors or Nodes
public class Base_net_handler {

    //help function to get the permutation as needed
    //to move the first element of list to be the last
    public static List<String> moveFirstToLast(List<String> variables_list) {
        if (!variables_list.isEmpty()) {
            String firstVar = variables_list.remove(0);
            variables_list.add(firstVar);
        }
        return variables_list;
    }

    //permutation of all the variables options
    private static List<List<String>> generatePermutations(List<String> vars, Map<String, List<String>> map) {
        List<List<String>> permutations = new ArrayList<>();
        generatePermutationsHelper(vars, map, 0, new ArrayList<>(), permutations); //help function
        return permutations;
    }

    private static void generatePermutationsHelper(List<String> vars, Map<String, List<String>> map, int index, List<String> current, List<List<String>> permutations) {
        if (index == vars.size()) {
            permutations.add(new ArrayList<>(current));
            return;
        }

        String currentVar = vars.get(index);
        List<String> options = map.get(currentVar);

        //loop through each option for the current variable
        for (String option : options) {
            current.add(option);
            generatePermutationsHelper(vars, map, index + 1, current, permutations); // recurse to the next variable
            current.remove(current.size() - 1);
        }
    }

    //create a map of key:variables, value: all the variable option to be (for example - T/F)
    //from the bayesian network xml file
    private static Map<String, List<String>> help_map_option(NodeList net_file_nodeList){
        //for each var what are his options (for example - T/F or 0/1/2)
        Map<String, List<String>> map_var_to_all_the_options = new HashMap<>();
        String name = "";
        List<String> options = new ArrayList<>();
        // loop through each element
        for (int temp = 0; temp < net_file_nodeList.getLength(); temp++) {
            Node node = net_file_nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element definitionElement = (Element) node;
                if (definitionElement.getTagName().equals("VARIABLE")) {
                    NodeList childNodes = definitionElement.getChildNodes();
                    //lLoop over child elements
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            // check for "NAME" and "OUTCOME" elements
                            if (childElement.getTagName().equals("NAME")) {
                                name = childElement.getTextContent();
                            }
                            if (childElement.getTagName().equals("OUTCOME")) {
                                options.add(childElement.getTextContent());
                            }
                        }
                    }
                    map_var_to_all_the_options.put(name, new ArrayList<>(options)); // create a new list for each variable
                    options.clear(); // clear the list for the next variable
                }
            }
        }
        return map_var_to_all_the_options;
    }

    //create all the factors for the VE algo to use
    //from the bayesian network xml file
    public static List<Factor> create_factors(NodeList net_file_nodeList){
        //for each var what are his options (for example - T/F or 0/1/2)
        Map<String, List<String>> map_var_to_all_the_options = help_map_option(net_file_nodeList);

        //create factors
        List<Factor> factors_list = new LinkedList<>();
        // loop through each element
        for (int temp = 0; temp < net_file_nodeList.getLength(); temp++) {
            Node node = net_file_nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element definitionElement = (Element) node;
                if (definitionElement.getTagName().equals("DEFINITION")) {
                    Factor new_factor = new Factor();
                    new_factor.new_map_var_to_all_the_options(map_var_to_all_the_options);
                    NodeList childNodes = definitionElement.getChildNodes();
                    // loop over child elements
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            // check for "FOR", "GIVEN", and "TABLE" elements
                            if (childElement.getTagName().equals("FOR") ||
                                childElement.getTagName().equals("GIVEN")) {
                                new_factor.add_to_variables_list(childElement.getTextContent());
                            }
                            if (childElement.getTagName().equals("TABLE")) {
                                String values = childElement.getTextContent();
                                String[] values_parts = values.split(" ");
                                List<String> vars = moveFirstToLast(new_factor.get_variables_list());
                                List<List<String>> perList = generatePermutations(vars, map_var_to_all_the_options);
                                //make the probability map of key: variables options, value: the probability
                                for (int i = 0; i < perList.size(); i++) {
                                    try {
                                        double doubleValue = Double.parseDouble(values_parts[i]);
                                        String st = "";
                                        int k = 0;
                                        for (; k < perList.get(i).size() -1 ; k++) {
                                            st += vars.get(k) + "=" + perList.get(i).get(k) + ",";
                                        }
                                        String res = "";
                                        if(st != ""){
                                            res = (vars.get(k) + "=" + perList.get(i).get(k)+ "|").concat(st.substring(0, st.length() - 1));
                                        }
                                        else{
                                            res = vars.get(k) + "=" + perList.get(i).get(k);
                                        }
                                        // add the double value to your list
                                        new_factor.add_to_values_to_map(res,doubleValue);

                                    } catch (NumberFormatException e) {
                                        // handle the case where the value cannot be parsed as a double
                                        System.err.println("Failed to parse value as double: " + values_parts[i]);
                                    }
                                }
                                
                            }
                        }
                    }
                    factors_list.add(new_factor);
                }
            }
        }
        return factors_list;
    }


    //create all the nodes for the BaseBall algo to use
    //from the bayesian network xml file
    public static Map<String, Node_net> create_nodes(NodeList net_file_nodeList){
        Map<String, Node_net> new_node_map = new HashMap<>();
        //for each var what are his options (for example - T/F or 0/1/2)
        Map<String, List<String>> map_var_to_all_the_options = help_map_option(net_file_nodeList);
        //create new node to each var and add it to the node list
        for(String var : map_var_to_all_the_options.keySet())
        {
            Node_net new_node = new Node_net(var);
            new_node_map.put(var, new_node);
        }

        //search for children and parents
        // loop through each element
        for (int temp = 0; temp < net_file_nodeList.getLength(); temp++) {
            Node node = net_file_nodeList.item(temp);
            String this_var = "";
            String parent = "";
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element definitionElement = (Element) node;
                if (definitionElement.getTagName().equals("DEFINITION")) {
                    NodeList childNodes = definitionElement.getChildNodes();
                    // loop over child elements
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            // check for "FOR" elements
                            if (childElement.getTagName().equals("FOR")) {
                                this_var = childElement.getTextContent();
                            }
                            // check for "GIVEN" elements
                            if (childElement.getTagName().equals("GIVEN")) {
                                parent = childElement.getTextContent();
                                for(String var_node : new_node_map.keySet())
                                {
                                    if(var_node.equals(parent)){
                                        //add this_var to be the child of parent
                                        new_node_map.get(var_node).add_to_children(new_node_map.get(this_var));
                                        //add parent to be the parent of this_var
                                        new_node_map.get(this_var).add_to_parents(new_node_map.get(var_node));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new_node_map;
    }
}
