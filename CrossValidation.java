import java.util.*;
import java.io.*;
import java.nio.*;

/*
 * .
 */

public class CrossValidation
{
    private ArrayList<ArrayList<File>> splitCorpus = new ArrayList<ArrayList<File>>(10);

    public CrossValidation()
    {
        for(int i = 0; i < 10; i++)
            splitCorpus.add(i,new ArrayList<File>());
    }

    public List<File> get(int i)
    {
        return (List<File>) splitCorpus.get(i);
    }

    private void splitCorpus(File f) throws IOException
    {
        if (f.isDirectory()){
            List<File> files = Arrays.asList(f.listFiles());
            Collections.shuffle(files);
            for(int i = 0; i < files.size(); i++){
                ArrayList<File> aux = splitCorpus.get(i%10);
                aux.add(files.get(i));
                splitCorpus.set(i%10,aux);                                
            }
        }
    }

    private void writeToFile(List<File> files, String output)
    {
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(output));
            for(File f : files)
                try 
                {
                    BufferedReader in =  new BufferedReader(new FileReader(f));
                    try
                    {
                        String line = null;
                        while (( line = in.readLine()) != null)
                            out.write(line+"\n");
                    }
                    finally
                    {
                        in.close();
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }                   
            out.close(); 
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void makeCrossValidation(String folder) throws IOException
    {
        splitCorpus(new File(folder));
        for(int i = 0; i < splitCorpus.size(); i++)
            writeToFile(splitCorpus.get(i),"file"+new Integer(i).toString()+".crossval");
    }
}

