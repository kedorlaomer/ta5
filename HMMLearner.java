import java.util.*;
import java.io.*;

/*
 * Maximum likelihood learner for hidden markow model (HMM).
 */

public class HMMLearner
{
    /* all k-tuples */
    private HashMap<List<TaggedToken>, Integer> model = new HashMap<List<TaggedToken>, Integer>();

    private HashMap<List<String>, Integer> formatedModel = new HashMap<List<String>, Integer>();

    /* k-tuples at sentence openings */
    private HashMap<List<TaggedToken>, Integer> initial = new HashMap<List<TaggedToken>, Integer>();

    private int k;

    /*
     * recursively reads all files in the directory and saves all the
     * frequencies of all k-tuples to model and initial
     */

    public HMMLearner(File directory, int k) throws IOException
    {
        this.k = k;
        readRecursively(directory);
    }

    /*
     * doesn't handle directories that (indirectly) contain
     * themselves (loops in that case)
     */

    private void readRecursively(File f) throws IOException
    {
        if (f.isDirectory())
            for (File f2 : f.listFiles())
                readRecursively(f2);
        else
        {
            readModel(f);
            readInitial(f);
        }
    }

    /* how often does tt occur in our corpus? */
    public int get(TaggedToken[] tt)
    {
        Integer rv = model.get(Arrays.asList(tt));
        return rv == null? 0 : rv;
    }

    /* how often does tt occur in the beginning of sentences in our corpus? */
    public int getInitial(TaggedToken[] tt)
    {
        Integer rv = initial.get(Arrays.asList(tt));
        return rv == null? 0 : rv;
    }

    private void readToModelWithIterator(File f, HashMap<List<TaggedToken>, Integer> model, Iterator<TaggedToken[]> iter)
        throws IOException
    {
        while (iter.hasNext())
        {
            TaggedToken[] tt = (TaggedToken[]) iter.next();

            List<TaggedToken> list = Arrays.asList(tt);
            Integer old = model.get(list);
            int n = old == null? 1 : old + 1;
            model.put(list, n);
        }
    }

    private void readModel(File f) throws IOException
    {
        Reader reader = null;
        try
        {
            reader = new FileReader(f);
            readToModelWithIterator(f, model, new TupleIterator(new BrownReader(reader), k));
            formatModel();
        }
        finally
        {
            if (reader != null)
                reader.close();
        }
    }

    private void readInitial(File f) throws IOException
    {
        Reader reader = null;
        try
        {
            reader = new FileReader(f);
            readToModelWithIterator(f, initial, new SentenceReader(reader, k));
        }

        finally
        {
            if (reader != null)
                reader.close();
        }
    }

    /* transforms the keys of model (token_0/tag_0, token_1/tag_1 ... token_k-1/tag_k-1)
    * into new keys of the form (tag_0, tag_1, ... , tag_k-2, token_k-1, tag_k-1)
    */
    private void formatModel()
    {
        System.out.println("########################");
        int aux = 0;
        for(List<TaggedToken> key : model.keySet())
        {

            ArrayList<String> newKey = new ArrayList<String>();
            for(int i = 0; i < key.size(); i++)
            {
                if(i == key.size()-1)
                {
                    newKey.add(key.get(i).token());
                    newKey.add(key.get(i).tag());
                }
                else
                {
                    newKey.add(key.get(i).tag());                        
                }
            }
            if(aux < 10){
                System.out.println("----------------------");
                
                System.out.println("aux = "+new Integer(aux).toString());
                System.out.println("oldKey = "+key.toString());
                System.out.println("newKey = "+newKey.toString());
            }
            Integer value = new Integer(0);
            if(formatedModel.containsKey(newKey))
            {
                value += formatedModel.get(newKey);
            }
            formatedModel.put(newKey,value + new Integer(this.get((TaggedToken[])key.toArray())));
        aux++;
        }
        for(List<TaggedToken> key : model.keySet())
        {
            System.out.println("");
        }
    }
}
