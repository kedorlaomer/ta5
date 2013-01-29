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
        HMMLearner learner = new HMMLearner(new File(directory), 3);

        DataOutputStream output = new DataOutputStream(
                                new FileOutputStream(modelFilename));
        writeModel(output, learner.probabilityModel);
        writeModel(output, learner.probabilityInitial);
        output.close();
    }


    private static void annotate(String path) throws IOException
    {
        DataInputStream input = new DataInputStream(
                                new FileInputStream(modelFilename));
        try {
            HMMLearner learner = new HMMLearner(3);
            readModel(input, learner.probabilityModel);
            readModel(input, learner.probabilityInitial);
        }
        finally {
            input.close();
        }

        int k = 3;
        HMMLearner[] learners = new HMMLearner[k];
        for (int i = 1; i =< k; i++)
            learners[i] = new HMMLearner(directory, i);

        Viterbi vit = new Viterbi(learners, new double[]{1, 1, 1});
        File directory = new File(path);
        if (directory.isDirectory()) {
            for (File f2 : directory.listFiles()) {
                annotateFile(f2, vit);
        }}
    }


    private static void annotateFile(File inFile, Viterbi vit) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        File outFile = new File(inFile.getName() + ".pos");
        if (!outFile.exists()) {
            outFile.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(
            new FileWriter(outFile.getAbsoluteFile()));

        try {

            String sentence;
            ArrayList ttSentence;
            while ((sentence = reader.readLine()) != null) {

                ttSentence = new ArrayList<TaggedToken>();

                for (String word : sentence.split(" ")) {
                    ttSentence.add(new TaggedToken(word + "/xxx"));
                }
                for(TaggedToken tt : vit.tagSentence((TaggedToken[])ttSentence.toArray())) {
                    writer.write(tt.toString() + " ");
                }
                writer.newLine();
            }
        }
        finally {
            writer.close();
            reader.close();
        }
    }


    public static void printUsage() {
        System.out.println("Usage: <learn|annotate> <directory>");
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            String directory = args[1];

            if ("learn".equals(args[0])) 
                learn(directory);
            else if ("annotate".equals(args[0]))
                annotate(directory);
            else
               printUsage();
        } 
        else 
            printUsage();
    }
}
