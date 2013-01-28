import java.io.*;
import java.util.*;

public class Viterbi
{
    private int k;
    private HMMLearner[] learners;
    private double[] a; 
    private double???? transM; 
    private String[] allTags;
    private double[][] probM = new double[150][300]; //150 rows and 300 columns

    /*
     * reads an up to order k HMM model from directory's corpus
     * files, weighting different models with non-zero
     * coefficients a
     */
    public Viterbi(File directory, int k, double[] a) throws IOException
    {
        this.k = k;
        this.a = a;

        learner = HMMLearner[k];

        for (int i = 0; i < k; i++)
            learners[i] = new HMMLearner(directory, i+1);
        
        /* initialize the probability matrix for Viterbi alg.
         * First row and first column have entries set at 0.
         * ProbM[0][0] = 1
         */
        for(int row = 0; row < probM.length; row++)
            probM[row][0] = 0;
    
        for(int col = 0; col < probM[0].length; col++)
            probM[0][col] = 0;

        probM[0][0] = 1;
    }

    /*
     * input: "this/xxx is/xxx an/xxx untagged/xxx sentence/xxx"
     * output: "this/det is/bez an/at untagged/jj sentence/nn"
     */
    public TaggedToken[] tagSentence(TaggedToken[] sentence)
    {
        clearMatrix();
        String[] history = new  String[]();
        for(int i = 0; i < sentence.length; i++)
        {
            int index = 0;
            TaggedToken tt = sentence[i];
            
            if(i == 0)
                for(int row = 0; row < allTags.length; row++){
                    probM[row + 1][i+1] = log(initialProbability(tt.token(),allTags[row]));
                    index = probM[row + 1][i+1] > probM[index+1][i+1] ? row : index;
                }
            else
            {
                //compute and store log(e_t(S[i+1])) + prevColMax 
                for(int row = 0; row < allTags.length; row++){

                    //compute max(v_s(i)+log(a_s(t)))
                    double prevColMax = 0.0;
                    for(int prevRow = 0; prevRow < allTags.length; prevRow++)
                        if(prevColMax < (probMax[prevRow+1][i] + log(transM[allTags[prevRow]][allTags[row]])))
                            prevColMax = probMax[prevRow+1][i] + log(transM[allTags[prevRow]][allTags[row]])); 
                    
                    // store the probability in the matrix
                    String[] kHist = String[k];
                    if(history.length > k)
                    {
                        for(int j = 0; j < k; j++)
                        {
                            kHist[j] = history[history.length -k +j];
                        }
                    }
                    else
                    {
                        kHist = history;
                    }
                    probM[row + 1][i+1] = log(probability(history,tt.token(),allTags[row])) + prevColMax;
                    index = probM[row + 1][i+1] > probM[index+1][i+1] ? row : index;
                }
            }
            //update history
            history[i] = allTags(index);
        }
        // Now we have the matrix initialized
        // We go backwards getting the best tags
        TaggedToken[] r = TaggedToken[]();
        for(int i = 0; i < history.length; i++)
            r[i] = new TaggedToken(sentence[i].token()+"/"+history[i]);

        return r;
    }

    /*
     * Clear the matrix probM, but not the first row and column
     */
    private void clearMatrix()
    {
        for(int col = 1; col < probM.length; col++)
            for(int row = 1; row < probM[0].length; row++)
                probM[row][col] = 0.0;
    }

    /*
     * Returns the initial probability for token having the given tag
     */
    private double initialProbability(String token, String tag)
    {
        double aux = learners[0].initialProbability(token,tag);
        return !Double.isNaN(aux)? aux : 0.0;
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
            if(i < history.length)
            {
                String[] h = new String[i];
                System.arraycopy(history, k-i-1, h, 0, i);
                double p = learners[i].probability(h, token, tag);
                if (!Double.isNaN(p))
                {
                    rv += p;
                    denom += a[i];
                }
            }
        return rv/denom;
    }
}