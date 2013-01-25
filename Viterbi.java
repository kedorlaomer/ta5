import java.io.*;
import java.util.*;

public class Viterbi
{
    private int k;
    private HMMLearner[] learners;
    private double[] α;
    private String[] allTags;

    /*
     * reads an up to order k HMM model from directory's corpus
     * files, weighting different models with non-zero
     * coefficients α
     */

    public Viterbi(File directory, int k, double[] α) throws IOException
    {
        this.k = k;
        this.α = α;

        learner = HMMLearner[k];

        for (int i = 0; i < k; i++)
            learners[i] = new HMMLearner(directory, i+1);
    }

    /*
     * input: "this/xxx is/xxx an/xxx untagged/xxx sentence/xxx"
     * output: "this/det is/bez an/at untagged/jj sentence/nn"
     */

    public TaggedToken[] tagSentence(TaggedToken[] sentence)
    {
        //ein riesen array erzeugen
        //alle tags durchgehen fuer probability
        //siehe Folien
    }

    /*
     * Returns the probability for token getting tag with this
     * history. 
     */ 

    private double probability(String[] history, String token, String tag)
    {
        double rv = 0;
        double denom = 0;

        for (int i = 0; i < k; i++)
        {
            String[] h = new String[i];
            System.arraycopy(history, k-i-1, h, 0, i);

            double p = learners[i].probability(h, token, tag);
            if (!Double.isNaN(p))
            {
                rv += p;
                denom += α[i];
            }
        }

        // TODO: Back-off-Model einfügen! Soll sein eigenes α
        // bekommen!

        return rv/denom;
    }
}
