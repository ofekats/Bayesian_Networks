import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Base_net_handler {

    //help function to get the permutation as needed
    //move the first element of list to be the last
    public static List<String> moveFirstToLast(List<String> variables_list) {
        if (!variables_list.isEmpty()) { // check if the list is not empty
            String firstVar = variables_list.remove(0); // remove the first variable
            variables_list.add(firstVar); // add the first variable to the end
        }
        return variables_list;
    }

    //permutation of all the varieables options
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
    
    
    public static List<Factor> create_factors(NodeList net_file_nodeList){
        //for each var what are his options (for example - T/F or 0/1/2)
        Map<String, List<String>> map_var_to_all_the_options = new HashMap<>();
        String name = "";
        List<String> options = new ArrayList<>();
        // Loop through each element
        for (int temp = 0; temp < net_file_nodeList.getLength(); temp++) {
            // Process your XML nodes here
            Node node = net_file_nodeList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element definitionElement = (Element) node;
                if (definitionElement.getTagName() == "VARIABLE") {
                    // Get child elements within "VARIABLE"
                    NodeList childNodes = definitionElement.getChildNodes();
                    // Loop over child elements
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);
                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            // Check for "for", "table", and "given" elements
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


        //create factors
        List<Factor> factors_list = new LinkedList<>();
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
                                List<String> vars = moveFirstToLast(new_factor.get_variables_list());
                                List<List<String>> perList = generatePermutations(vars, map_var_to_all_the_options);

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
                                        // Add the double value to your list
                                        new_factor.add_to_values_to_map(res,doubleValue);

                                    } catch (NumberFormatException e) {
                                        // Handle the case where the value cannot be parsed as a double
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
}
