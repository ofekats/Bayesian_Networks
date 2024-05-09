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
        this.join_factor();
        this.eliminate();
        this.normalization();
        String result = this.problem + "," + count_add + "," + count_mul;
        return result;
    }
}
