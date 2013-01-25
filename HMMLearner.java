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

    //not necessary
    private HashMap<List<String>, Integer> formatedInitial = new HashMap<List<String>, Integer>();

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
    public int getModel(TaggedToken[] tt)
    {
        Integer rv = model.get(Arrays.asList(tt));
        return rv == null? 0 : rv;
    }
    public int getModel(ArrayList<TaggedToken> tt)
    {
        Integer rv = model.get(tt);
        return rv == null? 0 : rv;
    }
    public int getFormatedModel(String[] key)
    {
        Integer rv = formatedModel.get(Arrays.asList(key));
        return rv == null? 0 : rv;
    }
    public int getFormatedModel(ArrayList<String> key)
    {
        Integer rv = formatedModel.get(key);
        return rv == null? 0 : rv;
    }

    /* how often does tt occur in the beginning of sentences in our corpus? */
    public int getInitial(TaggedToken[] tt)
    {
        Integer rv = initial.get(Arrays.asList(tt));
        return rv == null? 0 : rv;
    }
    public int getInitial(ArrayList<String> key)
    {
        Integer rv = initial.get(key);
        return rv == null? 0 : rv;
    }

    public int getFormatedInitial(TaggedToken[] tt)
    {
        Integer rv = formatedInitial.get(Arrays.asList(tt));
        return rv == null? 0 : rv;
    }
    public int getFormatedInitial(ArrayList<String> key)
    {
        Integer rv = formatedInitial.get(key);
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
        for(List<TaggedToken> key : model.keySet())
        {
            ArrayList<String> newKey = new ArrayList<String>();
            for(int i = 0; i < key.size(); i++)
                if(i == key.size()-1)
                {
                    newKey.add(key.get(i).token());
                    newKey.add(key.get(i).tag());
                }
                else
                    newKey.add(key.get(i).tag());                        
            Integer value = new Integer(0);
            if(formatedModel.containsKey(newKey))
                value += new Integer(this.getFormatedModel(newKey));
            formatedModel.put(newKey,value + new Integer(this.getModel((TaggedToken[])key.toArray())));
        }
    }

    /* Gives the probability that a given token has a given tag with respect to the given history of tags.
    */
    public double probability(String[] history, String token, String tag)
    {
        // What we are looking for is a list of Strings of the form <history0,...historyk-2,token,tag>
        ArrayList <String> searchKey = new ArrayList <String>();
        searchKey.addAll(Arrays.asList(history));
        searchKey.add(token);
        searchKey.add(tag);
        Integer sum = new Integer(0);
        LOOP:
        /* We go through all keys in formatedModel and we are looking for keys of the form <history0,...,historyk-2,token, ??>
        *  to get their probability and add it to sum 
        */
        for(List<String> key : formatedModel.keySet()){
            for(int i = 0; i < k-1; i++)
                if(!history[i].equals(key.get(i)))
                    continue LOOP; 
            if(!key.get(k-1).equals(token))
                continue LOOP;
            sum += this.getFormatedModel((ArrayList<String>)key);
        }
        /* the returning value is NaN if sum == 0 and otherwise it is "value of searchkey/value of all keys with same history and token"
        */
        return sum == 0? Double.NaN : new Integer(this.getFormatedModel(searchKey)).doubleValue()/sum.doubleValue();
    }

    public double initialProbability(String token, String tag)
    {
        ArrayList <String> searchKey = new ArrayList <String>();
        searchKey.add(token);
        searchKey.add(tag);
        Integer sum = new Integer(0);
        LOOP:
        for(List<String> key : formatedInitial.keySet()){
            if(!key.get(0).equals(token))
                continue LOOP;
            sum += this.getFormatedInitial((ArrayList<String>)key);
        }
        // return sum == 0? Double.NaN : Math.log(new Integer(this.getFormatedInitial(searchKey)).doubleValue()/sum.doubleValue());
        return sum == 0? Double.NaN : new Integer(this.getFormatedInitial(searchKey)).doubleValue()/sum.doubleValue();
    }
}