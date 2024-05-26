import org.w3c.dom.NodeList;

import java.util.*;

public class Bayes_Ball_Algo {
    private String problem;
    private NodeList net_file_nodeList;
    private Map<String, Node_net> nodes_map = new HashMap<>();
    private String source;
    private String target;
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
        this.source = problem.substring(0, 1);
        this.target = problem.substring(2, 3);
        //evidence
        int index_start_evidence = problem.indexOf("|")+1;
        if(problem.length() > index_start_evidence){
            String evidence_only = problem.substring(index_start_evidence);
            String[] evidence_str_parts = evidence_only.split(",");
            for(String evidence : evidence_str_parts){
                this.evidence.add(evidence.substring(0,1));
            }
        }
    }

    public void create_nodes()
    {
        System.out.println("create nodes");
        this.nodes_map = Base_net_handler.create_nodes(net_file_nodeList);

        //print
        System.out.println("nodes: ");
        for (String node : this.nodes_map.keySet())
        {
            System.out.println(this.nodes_map.get(node));
        }
    }

    public String search(String var, String comefrom, String res){
        System.out.println("searching");
        System.out.println("res= " + res);
        System.out.println("var= " + var);
        System.out.println("target= " + this.target);
        System.out.println("is var evidence? = " + this.evidence.contains(var));
        if(this.evidence.contains(var) || comefrom.equals("CHILD")){ //if the var from evidence or from child go to parents only
            for(Node_net par : this.nodes_map.get(var).get_parents())
            {
                if(par.get_node_var().equals(this.target)){
                    res = "no";
                }
                if(search(par.get_node_var(), "CHILD", res).equals("no")){
                    res = "no";
                    break;
                }
            }
        }else{ //if not from evidence and come from parent go to every child
            for(Node_net child : this.nodes_map.get(var).get_children())
            {
                if(child.get_node_var().equals(this.target)){
                    res = "no";
                }
                if(search(child.get_node_var(), "DAD", res).equals("no")){
                    res = "no";
                    break;
                }
            }
        }
        return res;
    }

    public String run_algo(){
        //print
        System.out.println(" new problem base ball algo: ");
        System.out.println("problem: " + problem);
        System.out.println("source: " + source);
        System.out.println("target: " + target);
        System.out.println("evidence: " + evidence);

        this.create_nodes();
        System.out.println("result: " + this.search(this.source,"DAD", "yes"));

        return this.search(this.source,"DAD", "yes");
    }
}
