import java.io.*;
import java.util.*;

public class Viterbi
{
    private int k;
    private HMMLearner[] learners;
    private double[] a; 
    private double[][] probM = new double[150][300]; //150 rows and 300 columns
    private BackoffModel backoff;

    /* all brown tags */
    private String[] allTags = { "'", "''", "(", ")", "*", ",",
        "--", ".", ":", "``", "abl", "abn", "abx", "ap", "ap$",
        "at", "be", "bed", "bedz", "beg", "bem", "ben", "ber",
        "bez", "cc", "cd", "cd$", "cs", "do", "dod", "doz", "dt",
        "dt$", "dti", "dts", "dtx", "ex", "fw", "hv", "hvd", "hvg",
        "hvn", "hvz", "in", "jj", "jj$", "jjr", "jjs", "jjt", "md",
        "nil", "nn", "nn$", "nns", "nns$", "np", "np$", "nps",
        "nps$", "nr", "nr$", "nrs", "od", "pn", "pn$", "pp$",
        "pp$$", "ppl", "ppls", "ppo", "pps", "ppss", "ql", "qlp",
        "rb", "rb$", "rbr", "rbt", "rn", "rp", "to", "uh", "vb",
        "vbd", "vbg", "vbn", "vbz", "wdt", "wp$", "wpo", "wps",
        "wql", "wrb" };

    /*
     * reads an up to order k HMM model from directory's corpus
     * files, weighting different models with non-zero
     * coefficients a; the last of these is used for the
     * back-off model and thus (a kind of) smoothing
     */
    public Viterbi(File directory, int k, double[] a) throws IOException
    {
        this.k = k;
        this.a = a;

        learners = new HMMLearner[k];

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

        backoff = new BackoffModel(new File("brown_learn"),0.2);
    }

    /*
     * has already got the learners; like Viterbi(File, int, double[])
     */

    public Viterbi(HMMLearner[] learners, double[] a) throws IOException
    {
        this.learners = learners;
        this.a = a;
        this.k = learners.length;
    }

    /*
     * input: "this/xxx is/xxx an/xxx untagged/xxx sentence/xxx"
     * output: "this/det is/bez an/at untagged/jj sentence/nn"
     */
    public TaggedToken[] tagSentence(TaggedToken[] sentence)
    {
        System.out.println(new Date());
        clearMatrix();
        ArrayList<String> history = new ArrayList<String>();
        System.out.println("incoming sentence:");
        for(int i = 0; i < sentence.length; i++)
            System.out.println(sentence[i].toString());
        
        for(int i = 0; i < sentence.length; i++)
        {
            int index = 0;
            TaggedToken tt = sentence[i];
            
            System.out.println("===================== column "+new Integer(i+1).toString()+" =====================");
            System.out.println("===================== token "+tt.token()+" =====================");


            if(i == 0)
                for(int row = 0; row < allTags.length; row++){
                    probM[row + 1][i+1] = Math.log(initialProbability(tt.token(),allTags[row]));
                    if(probM[row + 1][i+1] > probM[index+1][i+1])
                    {
                        System.out.println("index changed at column: "+new Integer(i).toString());
                        index = row;
                    }
                    // else
                    // {
                        // System.out.println("value for index "+new Integer(index).toString()+" : "+new Double(probM[index+1][i+1]).toString());
                        // System.out.println("new value "+new Integer(row).toString()+" : "+new Double(probM[index+1][i+1]).toString());
                    // }
                }
            else
            {
                //compute and store log(e_t(S[i+1])) + prevColMax 
                ArrayList<String> kHist = lastElements(history,k);
                ArrayList<String> prevHist = (ArrayList<String>)kHist.clone();
                for(int row = 0; row < allTags.length; row++)
                {
                    //compute max(v_s(i)+log(a_s(t)))




                    double prevColMax = Double.NEGATIVE_INFINITY;
                    for(int prevRow = 0; prevRow < allTags.length; prevRow++){
                        prevHist.set(prevHist.size()-1,allTags[prevRow]);
                        // System.out.println("prevHist: "+prevHist.toString());
                        prevColMax = Math.max(prevColMax , probM[prevRow+1][i] + 
                            Math.log(transitionProbability((String[])prevHist.toArray(new String[prevHist.size()]) , allTags[row])));
                    }
                    prevColMax = Math.max(prevColMax , 0);
                    System.out.println("prevColMax: "+new Double(prevColMax).toString());

                    


                    // store the probability in the matrix
                    // for(int j=0;j<history.size(); j++){System.out.println("history["+new Integer(j).toString()+"] = "+history.get(j));}
                    // for(int j=0;j<kHist.size(); j++){System.out.println("kHist["+new Integer(j).toString()+"] = "+kHist.get(j));}
                    System.out.println("k: "+new Integer(k).toString());
                    // probM[row + 1][i+1] = Math.log(probability((String[])kHist.toArray(new String[kHist.size()]),tt.token(),allTags[row])) + prevColMax;
                    probM[row + 1][i+1] = Math.log(probability(kHist,tt.token(),allTags[row])) + prevColMax;
                    System.out.println("probM[row + 1][i+1]: "+new Double(probM[row + 1][i+1]).toString());
                    System.out.println("probM[index+1][i+1]: "+new Double(probM[index+1][i+1]).toString());

                    System.out.println("prevColMax: "+new Double(prevColMax).toString());

                    // index = probM[row + 1][i+1] >= probM[index+1][i+1] ? row : index;
                
                    if(probM[row + 1][i+1] > probM[index+1][i+1] || Double.isNaN(probM[index+1][i+1]))
                    {
                        System.out.println("index changed at column: "+new Integer(i).toString());
                        index = row;
                    }
                    else
                    {
                        System.out.println("value for index "+new Integer(index).toString()+" : "+new Double(probM[index+1][i+1]).toString());
                        System.out.println("new value "+new Integer(row).toString()+" : "+new Double(probM[index+1][i+1]).toString());
                    }
                }
            }
            System.out.println("Index for column "+new Integer(i).toString()+" is: "+new Integer(index).toString());
            //update history
            history.add(allTags[index]);
        }
        System.out.println(new Date());
        // Now we have the matrix initialized
        // We go backwards getting the best tags
        TaggedToken [] r = new TaggedToken [sentence.length];
        for(int i = 0; i < sentence.length; i++)
            r[i] = new TaggedToken(sentence[i].token()+"/"+history.get(i));

        System.out.println("outcoming sentence:");
        for(int i = 0; i < sentence.length; i++)
            System.out.println(r[i].toString());
        
        
        return r;
    }

    /*
     * Clear the matrix probM, but not the first row and column
     */
    private void clearMatrix()
    {
        for(int row = 1; row < probM.length; row++)
            for(int col = 1; col < probM[0].length; col++)
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
    private double probability(ArrayList<String> history, String token, String tag)
    {
        System.out.println("================ probability =================");
        System.out.println("token: "+token);
        System.out.println("tag: "+tag);
        System.out.println("history: "+history.toString());

        double rv = 0;
        double denom = 0;

        for (int i = 0; i < k; i++)
            if(i <= history.size())
            {
                ArrayList<String> h = lastElements(history,i);
                System.out.println("h: "+h.toString());
                
                double p = learners[i].probability((String[])h.toArray(new String[h.size()]), token, tag);
                System.out.println("learner["+new Integer(i).toString()+"] gives probability: "+new Double(p).toString());
                if (!Double.isNaN(p))
                {
                    System.out.println(" in if case, update variables ");
                    rv += p;
                    denom += a[i];
                }
            }
        System.out.println("rv: "+new Double(rv).toString());
        System.out.println("denom: "+new Double(denom).toString());
        System.out.println("rv/denom: "+new Double(rv/denom).toString());

        // rv += backoff.probability(tag);
        // denom += a[k];
        if(denom == 0.0)
            return 0.0;
        
        return rv/denom;
    }

    /*
     * returns the n last elements of arr
     */
    protected ArrayList<String> lastElements(ArrayList<String> v, int numElements)
    {
        int n = Math.min(numElements, v.size());
        ArrayList<String> rv = new ArrayList <String>();
        System.out.println(v.toString());
        if(!v.isEmpty())
            rv.addAll(v.subList(v.size()-n,v.size()));
        return rv;
    }

    private double transitionProbability(String tags[], String tag)
    {
        // System.out.println("before transitionProbability with tag: "+tag);
        // System.out.println(new Date());
        // System.out.println("tags size is: "+tags.length);
        double x = learners[tags.length-1].transitionProbability(tags, tag);
        // System.out.println("after transitionProbability");
        // System.out.println(new Date());
        return Double.isNaN(x) ? 0.0 : x;
    }
}
