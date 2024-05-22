import org.w3c.dom.NodeList;

import java.util.List;

public class Bayes_Ball_Algo {
    private String problem;
    private NodeList net_file_nodeList;
    private List<Node_net> nodes;
    private String source;
    private String target;
    private List<String> evidence;


    public Bayes_Ball_Algo(String problem, NodeList net_file_nodeList)
    {
        this.net_file_nodeList = net_file_nodeList;
        this.problem = problem;
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
        //print
        System.out.println("source: " + source);
        System.out.println("target: " + target);
        System.out.println("evidence: " + evidence);
    }

    public String run_algo(){
        //print
        System.out.println(" new problem ----: ");
        System.out.println("problem: " + problem);

        String result = this.problem;
        return result;
    }
}
