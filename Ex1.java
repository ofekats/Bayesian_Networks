import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

public class Ex1 {

    public static String bayes_ball_algo(String line){
        Bayes_Ball_Algo bb = new Bayes_Ball_Algo(line);
        //return yes or no
        return bb.run_algo();
    }

    public static String variable_elimination_algo(String line){
        Variable_Elimination_Algo ve = new Variable_Elimination_Algo(line);
        return ve.run_algo();
    }

    public static void main(String[] args) {
        try {
            // Open the input file for reading
            FileReader fileReader = new FileReader("input.txt");
            BufferedReader inputReader = new BufferedReader(fileReader);

            // Create a File object for the output file
            File output_file = new File("output.txt");
            // Create the output file with overwrite mode
            FileOutputStream output_fos = new FileOutputStream(output_file, false);

            // Read each line from the input file
            String line = inputReader.readLine();
            //first line is the name of the basesian network file
            String base_net_file = line;

            //open bayesian network file
            

            //for each line in input file call the correct algo and write the output to the output file
            line = inputReader.readLine();
            while (line != null) {
                // Check if the line starts with "P" then call the variable_elimination_algo
                if (line.startsWith("P")) {
                    output_fos.write(variable_elimination_algo(line).getBytes());
                }else { //otherwise call the bayes_ball_algo
                    output_fos.write(bayes_ball_algo(line).getBytes());
                }
                //enter after any line except the last one
                if ((line = inputReader.readLine()) != null){
                    output_fos.write("\n".getBytes());
                }
            }

            // Close the input and output files
            inputReader.close();
            output_fos.close();
        } catch (IOException e) {
            // Handle any errors that occur
            e.printStackTrace();
        }
    }
}
