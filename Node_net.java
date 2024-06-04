import java.util.ArrayList;
import java.util.List;

//class of node for the BaseBall algo
//each node contain (1)the var,
//                  (2)parents list,
//                  (3)children list
public class Node_net {
    private String var;
    private List<Node_net> parents = new ArrayList<>();
    private List<Node_net> children = new ArrayList<>();

    public Node_net(String var){
        this.var = var;
    }

    public String get_node_var()
    {
        return this.var;
    }

    public void add_to_parents(Node_net par){
        this.parents.add(par);
    }

    public void add_to_children(Node_net child){
        this.children.add(child);
    }

    public List<Node_net> get_parents(){
        return this.parents;
    }

    public List<Node_net> get_children(){
        return this.children;
    }

    public String toString(){
        String st = "var: " + this.var;
        st += "\nparents: ";
        for (Node_net node : this.parents)
        {
            st += node.get_node_var() + " ";
        }
        st += "\nchildren: ";
        for (Node_net node : this.children)
        {
            st += node.get_node_var() + " ";
        }
        st += "\n ";
        return st;
    }

}
