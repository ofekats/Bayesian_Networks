import java.util.ArrayList;
import java.util.List;

public class Node_net {
    private String var;
    private List<Node_net> parents = new ArrayList<>();
    private List<Node_net> children = new ArrayList<>();

    public Node_net(String var){
        this.var = var;
    }


}
