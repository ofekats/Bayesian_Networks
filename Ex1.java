import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//the main class of the assignment - read the input file line by line
//call the correct algo and put the result in an output file
public class Ex1 {

    //call the BaseBall algo
    public static String bayes_ball_algo(String line, NodeList net_file_nodeList){
        Bayes_Ball_Algo bb = new Bayes_Ball_Algo(line, net_file_nodeList);
        //return yes or no
        return bb.run_algo();
    }

    //call the variable elimination algo
    public static String variable_elimination_algo(String line, NodeList net_file_nodeList){
        Variable_Elimination_Algo ve = new Variable_Elimination_Algo(line, net_file_nodeList);
        return ve.run_algo();
    }

    public static void main(String[] args) {
        try {
            // open the input file for reading
            FileReader fileReader = new FileReader("input.txt");
            BufferedReader inputReader = new BufferedReader(fileReader);

            // create a File object for the output file
            File output_file = new File("output.txt");
            // create the output file with overwrite mode
            FileOutputStream output_fos = new FileOutputStream(output_file, false);

            // read each line from the input file
            String line = inputReader.readLine();
            //first line is the name of the bayesian network file
            String base_net_file = line;

            //open bayesian network file
            File base_netFile = new File(base_net_file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(base_netFile);
            doc.getDocumentElement().normalize();
            NodeList net_file_nodeList = doc.getElementsByTagName("*"); // get all elements

            //for each line in input file call the correct algo and write the output to the output file
            line = inputReader.readLine();
            while (line != null) {
                // check if the line starts with "P" then call the variable_elimination_algo
                if (line.startsWith("P")) {
                    output_fos.write(variable_elimination_algo(line, net_file_nodeList).getBytes());
                }else { //otherwise call the bayes_ball_algo
                    output_fos.write(bayes_ball_algo(line, net_file_nodeList).getBytes());
                }
                //enter after any line except the last one
                if ((line = inputReader.readLine()) != null){
                    output_fos.write("\n".getBytes());
                }
            }

            // close the input and output files
            inputReader.close();
            output_fos.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // handle any errors that occur
            e.printStackTrace();
        }
    }
}
