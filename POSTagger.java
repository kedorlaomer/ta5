import java.util.*;
import java.io.*;

public class POSTagger
{
    private static final String modelFilename = "model.data";


    private static void writeModel(DataOutputStream output,
        HashMap<List<String>, Double> model) throws IOException
    {
        output.writeInt(model.size());
        for (List<String> key : model.keySet())
        {
            output.writeInt(key.size());
            for (String s : key) {
                output.writeUTF(s);
            }
            output.writeDouble(model.get(key));
        }
    }

    private static void readModel(DataInputStream input,
        HashMap<List<String>, Double> model) throws IOException
    {
        int modelSize, keySize;
        Double tmpProb;
        ArrayList<String> tmpKey;

        modelSize = input.readInt();
        while (model.size() < modelSize)
        {
            keySize = input.readInt();
            tmpKey = new ArrayList<String>(keySize);

            while (tmpKey.size() < keySize)
            {
                tmpKey.add(input.readUTF());
            }

            tmpProb = input.readDouble();
            model.put(tmpKey, tmpProb);
        }
    }


    private static void learn(String directory) throws IOException
    {
        HMMLearner learner = new HMMLearner(new File(directory), 1);
        learner.initProbabilityFrom(
            learner.probabilityModel, learner.formatedModel);

        learner.initProbabilityFrom(
            learner.probabilityInitial, learner.formatedInitial);

        DataOutputStream output = new DataOutputStream(
                                new FileOutputStream(modelFilename));
        writeModel(output, learner.probabilityModel);
        writeModel(output, learner.probabilityInitial);
        output.close();
    }


    private static void annotate(String directory) throws IOException
    {
        DataInputStream input = new DataInputStream(
                                new FileInputStream(modelFilename));
        HMMLearner learner = new HMMLearner(3);
        readModel(input, learner.probabilityInitial);
        readModel(input, learner.probabilityModel);
        input.close();
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
