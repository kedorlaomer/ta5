import java.util.*;
import java.io.*;

public class POSTagger
{
    private static String getModelFilename(int fileNo)
    {
        return ("model" + fileNo + ".data");
    }

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

    private static void saveLearner(HMMLearner learner, int fileNo) throws IOException
    {
        DataOutputStream output = new DataOutputStream(
                                new FileOutputStream(getModelFilename(fileNo)));
        try {
            writeModel(output, learner.probabilityModel);
            writeModel(output, learner.probabilityInitial);
        }
        finally {
            output.close();
        }
    }

    private static void initLearner(HMMLearner learner, int fileNo) throws IOException
    {
        DataInputStream input = new DataInputStream(
                                new FileInputStream(getModelFilename(fileNo)));
        try {
            readModel(input, learner.probabilityModel);
            readModel(input, learner.probabilityInitial);
        }
        finally {
            input.close();
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
        int k = 3;
        HMMLearner[] learners = new HMMLearner[k];

        for (int i = 0; i < k; i++) {
            learners[i] = new HMMLearner(new File(directory), i + 1);
            saveLearner(learners[i], i + 1);
            System.out.println(learners[i].probabilityModel.size());            
        }
    }


    private static void annotate(String path) throws IOException
    {
        int k = 3;
        HMMLearner[] learners = new HMMLearner[k];
        for (int i = 0; i < k; i++) {
            learners[i] = new HMMLearner(i + 1);
            initLearner(learners[i], i + 1);
        }

        Viterbi vit = new Viterbi(learners, new double[]{1, 1, 1});
        File directory = new File(path);
        if (directory.isDirectory()) {
            for (File f2 : directory.listFiles()) {
                annotateFile(f2, vit);
        }}
    }


    private static void annotateFile(File inFile, Viterbi vit) throws IOException {

        File outFile = new File(inFile.getName() + ".pos");
        if (!outFile.exists()) {
            outFile.createNewFile();
        }

        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(
                                    outFile.getAbsoluteFile()));

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
