import java.util.*;
import java.io.*;

/*
 * Maximum likelihood learner for hidden markow model (HMM).
 */

public class HMMLearner
{
    /* all k-tuples */
    private HashMap<List<TaggedToken>, Integer> model = new HashMap<List<TaggedToken>, Integer>();

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
}
