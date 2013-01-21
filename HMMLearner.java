import java.util.*;
import java.io.*;

/*
 * Maximum likelihood learner for hidden markow model (HMM).
 */

public class HMMLearner
{
    private HashMap<List<TaggedToken>, Integer> model = new HashMap<List<TaggedToken>, Integer>();
    private int k;

    /*
     * recursively reads all files in the directory and saves
     * all the frequencies of all k-tuples to model
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
            FileReader reader = new FileReader(f);
            Iterator<TaggedToken[]> iter = new TupleIterator(new BrownReader(reader), k);

            while (iter.hasNext())
            {
                TaggedToken[] tt = (TaggedToken[]) iter.next();

                List<TaggedToken> list = Arrays.asList(tt);
                Integer old = model.get(list);
                int n = old == null? 1 : old + 1;
                model.put(list, n);
            }

            reader.close();
        }
    }

    public int get(TaggedToken[] tt)
    {
        Integer rv = model.get(Arrays.asList(tt));
        return rv == null? 0 : rv;
    }
}
