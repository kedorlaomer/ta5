import java.io.*;
import java.util.*;

/*
 * A model that tries to extrapolate from learned to the
 * unknown.
 */

public class BackoffModel
{
    private Map<TaggedToken, Integer> everything;
    private Set<String> known;
    public Map<String, Double> distribution;
    private double fraction;

    /*
     * Reads all files in directory, saves all token/tag pairs
     * except for a fraction f, reads the fraction again and
     * learns about the tag distribution amongst the unknown
     * tokens in the formerly ignored fraction. This should give
     * a rough estimate about the tag distribution of unknown
     * tokens.
     */

    public BackoffModel(File directory, double f) throws IOException
    {
        fraction = f;
        everything = new HashMap<TaggedToken, Integer>();
        known = new HashSet<String>();
        readRecursively(directory);
        estimateUnknownProbabilities();
    }

    public BackoffModel()
    {
        this.distribution = new HashMap<String, Double>();
    }

    private void readRecursively(File f) throws IOException
    {
        if (f.isDirectory())
            for (File f2 : f.listFiles())
                readRecursively(f2);
        else
        {
            FileReader reader = null;
            try
            {
                reader = new FileReader(f);
                Iterator iter = new BrownReader(reader);

                while (iter.hasNext())
                {
                    TaggedToken tt = (TaggedToken) iter.next();
                    Integer old = everything.get(tt);
                    everything.put(tt, old == null? 1 : old+1);

                    if (Math.random() > fraction)
                        known.add(tt.token());
                }
            }

            finally
            {
                if (reader != null)
                    reader.close();
            }
        }
    }

    private void estimateUnknownProbabilities()
    {
        distribution = new HashMap<String, Double>();
        for (TaggedToken tt : everything.keySet())
        {
            if (!known.contains(tt.token()))
            {
                String tag = tt.tag();
                Double old = distribution.get(tag);
                double o = old == null? 0 : old;
                distribution.put(tag, o+everything.get(tt));
            }
        }

            double total = 0;
        for (Double d : distribution.values())
            total += d;

        for (String tag : distribution.keySet())
            distribution.put(tag, distribution.get(tag)/total);
    }

    public double initialProbability(String tag)
    {
        return probability(tag);
    }

    public double probability(String tag)
    {
        return distribution.get(tag);
    }

    public void dump()
    {
        System.out.println(distribution);
    }
}
