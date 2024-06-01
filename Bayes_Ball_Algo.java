import org.w3c.dom.NodeList;
import java.util.*;

//class for the BaseBall algo implementation
public class Bayes_Ball_Algo {
    //the current problem needed to be solve
    private String problem;
    //the bayesian network xml file object
    private NodeList net_file_nodeList;
    //map of nodes of key:var, value:node of the var (which contain (1)the var, (2)parents list, (3)children list)
    private Map<String, Node_net> nodes_map = new HashMap<>();
    //the source var - the var which we begin the algo from
    private String source;
    //the target var - the var we want to check if the source is independent of or not
    private String target;
    //list of the evidence vars
    private List<String> evidence = new ArrayList<>();


    public Bayes_Ball_Algo(String problem, NodeList net_file_nodeList)
    {
        this.net_file_nodeList = net_file_nodeList;
        this.problem = problem;
        this.analyse_problem();
    }

    //get the source, target and evidence vars from the problem
    private void analyse_problem(){
        //source, target
        this.source = problem.substring(0, problem.indexOf("-")); //for example from "B-E|J=T" to "B"
        this.target = problem.substring(problem.indexOf("-")+1, problem.indexOf("|")); //for example from "B-E|J=T" to "E"
        //evidence
        //for example from "B-E|J=T" to "J"
        int index_start_evidence = problem.indexOf("|")+1;
        if(problem.length() > index_start_evidence){
            String evidence_only = problem.substring(index_start_evidence);
            String[] evidence_str_parts = evidence_only.split(",");
            for(String evidence : evidence_str_parts){
                String[] evidence_name = evidence.split("=");
                this.evidence.add(evidence_name[0]);
            }
        }
    }

    //using the Base_net_handler class to help creating the nodes
    public void create_nodes()
    {
//        System.out.println("create nodes");
        this.nodes_map = Base_net_handler.create_nodes(net_file_nodeList);

//        //print
//        System.out.println("nodes: ");
//        for (String node : this.nodes_map.keySet())
//        {
//            System.out.println(this.nodes_map.get(node));
//        }
    }

    //the main part of the algo
    //goes from the source by the next roles:
    //(1) if the var is evidence and come from a parent, then: go to every parent
    //(2) if the var is evidence and come from a child, then: stop
    //(3) if the var is not evidence and come from a parent, then: go to every child
    //(4) if the var is not evidence and come from a child, then: go to every child and every parent
    //if gets to the target in each point return "no" because the source and the target are not independent
    //otherwise return "yes" because the source and the target are independent
    public String search(String var, String comefrom, String res, List<String> already_checked){
//        System.out.println("searching");
//        System.out.println("res= " + res);
//        System.out.println("var= " + var);
//        System.out.println("target= " + this.target);
//        System.out.println("is var evidence? = " + this.evidence.contains(var));
        if(res.equals("no"))
        {
            return res;
        }
        if(already_checked.contains(var)){
            return res;
        }
        if(this.evidence.contains(var)){ //if the var from evidence
            if(comefrom.equals("DAD")){ //if come from parent go to every parent
//                System.out.println("var from evidence and come from parent go to every parent");
                for(Node_net par : this.nodes_map.get(var).get_parents())
                {
                    if(par.get_node_var().equals(this.target)){
                        res = "no";
                    }
                    if(search(par.get_node_var(), "CHILD", res, already_checked).equals("no")){
                        res = "no";
                        break;
                    }
                }
            }else{ //if come from child stop
//                System.out.println("var from evidence and come from child stop");
                return res;
            }
        }else{ //if not from evidence
            if(comefrom.equals("DAD")){ //if come from parent go to every child
//                System.out.println("var not from evidence and come from parent go to every child");
                if(this.nodes_map.get(var).get_children().isEmpty())
                {
                    res = "yes";
                }
                for(Node_net child : this.nodes_map.get(var).get_children())
                {
                    if(child.get_node_var().equals(this.target)){
                        res = "no";
                    }
                    if(search(child.get_node_var(), "DAD", res, already_checked).equals("no")){
                        res = "no";
                        break;
                    }
                }
            }else{ //if come from child go to every child and every parent
                //to not run forever if already check that var not to do that again
                already_checked.add(var);
//                System.out.println("var not from evidence and come from child go to every child and every parent");
                for(Node_net par : this.nodes_map.get(var).get_parents())
                {
                    if(par.get_node_var().equals(this.target)){
                        res = "no";
                    }
                    if(search(par.get_node_var(), "CHILD", res, already_checked).equals("no")){
                        res = "no";
                        break;
                    }
                }
                for(Node_net child : this.nodes_map.get(var).get_children())
                {
                    if(child.get_node_var().equals(this.target)){
                        res = "no";
                    }
                    if(search(child.get_node_var(), "DAD", res, already_checked).equals("no")){
                        res = "no";
                        break;
                    }
                }
            }
        }
        return res;
    }

    //run the algo part by part
    //(1) create the nodes
    //(2) start from the source (as it came from the child) and get the result
    //(3) return result
    public String run_algo(){
//        //print
//        System.out.println(" new problem base ball algo: ");
//        System.out.println("problem: " + problem);
//        System.out.println("source: " + source);
//        System.out.println("target: " + target);
//        System.out.println("evidence: " + evidence);

        this.create_nodes();
        List<String> already_checked = new ArrayList<>();
        String result = this.search(this.source,"CHILD", "yes", already_checked);
        if (result.equals("")){
            return "no";
        }
//        System.out.println("result: " + result);
        return result;
    }
}
