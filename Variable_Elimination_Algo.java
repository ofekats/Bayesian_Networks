public class Variable_Elimination_Algo {
    //to count how many times performed addition or multiplication
    int count_add;
    int count_mul;
    String problem;

    public Variable_Elimination_Algo(String problem){
        count_add = 0;
        count_mul = 0;
        this.problem = problem;

    }

    public void join_factor(){ //what to get and return?
        System.out.println("join");
    }

    public void eliminate(){ //what to get and return?
        System.out.println("eliminate");
    }

    public void normalization(){ //what to get and return?
        System.out.println("normalization");
    }

    public String run_algo(){
        String result = this.problem + "," + count_add + "," + count_mul;
        return result;
    }
}
