import java.util.LinkedList;
import java.util.List;

public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    int count_add;
    int count_mul;
    String problem;
    List<Factor> factors_list = new LinkedList<>();
    List<String> hidden_vars_list = new LinkedList<>();

    public Variable_Elimination_Algo(String problem){
        count_add = 0;
        count_mul = 0;
        this.problem = problem;

    }

    private void create_factors(){ //what to get and return?
        System.out.println("create factors");
    }

    private void join_factor(){ //what to get and return?
        System.out.println("join");
    }

    private void eliminate(){ //what to get and return?
        System.out.println("eliminate");
    }

    private void normalization(){ //what to get and return?
        System.out.println("normalization");
    }

    public String run_algo(){
        this.create_factors();
        while (hidden_vars_list.size() != 0){
            this.join_factor();
            this.eliminate();
        }
        this.normalization();
        String result = this.problem + "," + count_add + "," + count_mul;
        return result;
    }
}
