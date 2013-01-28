import java.util.*;
import java.io.*;

public class POSTagger
{
    private static void learn(String directory) throws IOException
    {
        HMMLearner learner = new HMMLearner(new File(directory), 1);
        DataOutputStream output = new DataOutputStream(
                                new FileOutputStream("model.data"));

        for (List<String> key : learner.probabilityModel.keySet())
        {
            // output.writeString();
            // output.writeDouble();
        }

        output.close();
    }


    private static void annotate(String directory) throws IOException
    {
        ;
    }


    public static void main(String[] args) throws IOException
    {
        if (args.length > 1) {

            String directory = args[1];

            if (args[0] == "learn") {
                learn(directory);
            }
            else if (args[0] == "annotate") {
                annotate(directory);
            }
        }
    }
}
