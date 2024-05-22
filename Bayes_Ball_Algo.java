import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public String run_algo(){
        //print
        System.out.println(" new problem base ball algo: ");
        System.out.println("problem: " + problem);
        System.out.println("source: " + source);
        System.out.println("target: " + target);
        System.out.println("evidence: " + evidence);

        this.create_nodes();


        String result = this.problem;
        return result;
    }
}
