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

    //not necessary
    public HashMap<List<String>, Integer> formatedModel = new HashMap<List<String>, Integer>();
    public HashMap<List<String>, Integer> formatedInitial = new HashMap<List<String>, Integer>();
    
    public HashMap<List<String>, Double> probabilityModel = new HashMap<List<String>, Double>();
    public HashMap<List<String>, Double> probabilityInitial = new HashMap<List<String>, Double>();

    
    public String[] allTags = {".","(",")","*","--",",",":","ABL","ABN","ABX","AP","AT","BE","BED","BEDZ",
        "BEG","BEM","BEN","BER","BEZ","CC","CD","CS","DO","DOD","DOZ","DT","DTI","DTS","DTX",
        "EX","FW","HV","HVD","HVG","HVN","IN","JJ","JJR","JJS","JJT","MD","NC","NN","NN$","NNS","NNS$","NP",
        "NP$","NPS","NPS$","NR","OD","PN","PN$","PP$","PP$$","PPL","PPLS","PPO","PPS","PPSS","PRP",
        "PRP$","QL","QLP","RB","RBR","RBT","RN","RP","TO","UH","VB","VBD","VBG","VBN","VBP","VBZ","WDT","WP$","WPO","WPS","WQL","WRB"};
    private int k;

    /*
     * recursively reads all files in the directory and saves all the
     * frequencies of all k-tuples to model and initial
     */

    public HMMLearner(int k)
    {
        this.k = k;
    }

    public HMMLearner(File directory, int k) throws IOException
    {
        this.k = k;
        readRecursively(directory);
        formatModel();
        formatInitial();
        getProbabilityFrom(probabilityModel, formatedModel);
        getInitialProbabilityFrom(probabilityInitial, formatedInitial);
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
    public int getInitial(ArrayList<TaggedToken> key)
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

    private void formatInitial()
    {
        if(k < 2)
            for(List<TaggedToken> key : initial.keySet())
            {
                ArrayList<String> newKey = new ArrayList<String>();
                newKey.add(key.get(0).tag());
                Integer value = new Integer(this.getInitial((TaggedToken[])key.toArray()));
                if(formatedInitial.containsKey(newKey))
                    value += new Integer(this.getFormatedInitial(newKey));
                formatedInitial.put(newKey,value);
            }
    }

    // public void getProbabilityFrom(HashMap<List<String>, Double> model,
    //     HashMap<List<String>, Integer> from)
    // {
    //     String [] prevTags;
    //     String token, tag;
    //     for (List<String> key : from.keySet())
    //     {
    //         prevTags = key.toArray(new String[key.size() - 2]);
    //         token = key.get(key.size() - 2);
    //         tag = key.get(key.size() - 1);

    //         model.put(key, probability(prevTags, token, tag));
    //     }
    // }

    public void getProbabilityFrom(HashMap<List<String>, Double> model,
        HashMap<List<String>, Integer> from)
    {
        System.out.println("============================");
        System.out.println("in getProbabilityFrom");
        System.out.println("============================");
        
        String [] prevTags = new String[k-1];
        String token, tag;
        for (List<String> key : from.keySet())
        {
            // if(k == 1)
            // {
            //     System.out.println("+++++++++++++++++");
            //     System.out.println(key.toString());
            //     System.out.println(key.size());
            
            // }
            tag = key.get(key.size() - 1);
            token = key.get(key.size() - 2);
            if(k > 1)
            {
                for(int j = 0; j<key.size()-2; j++)
                    prevTags[j]=key.get(j);
                // prevTags = (String[])((key.subList(0,key.size()-2)).toArray());
                // System.out.println("\n");
                // System.out.println("previous tags");
                // System.out.println(prevTags.toString());
            }
            Integer sum = new Integer(0);
            for(String currentTag : allTags)
            {
                ArrayList<String> auxList = ((ArrayList<String>)((ArrayList<String>)key).clone());
                auxList.set(auxList.size()-1,currentTag);
                // System.out.println("auxList: "+auxList.toString());
                sum += this.getFormatedModel(auxList);
                // System.out.println("value for auxlist found: "+new Integer(this.getFormatedInitial(auxList)).toString());
                // System.out.println("sum: "+sum.toString());
            }
            // System.out.println("key: "+key.toString());
            // System.out.println("key value: "+new Integer(this.getFormatedModel((ArrayList<String>)key)).toString());
            // System.out.println("sum: "+new Integer(sum).toString());

            model.put(key, sum == 0? Double.NaN : new Integer(this.getFormatedModel((ArrayList<String>)key)).doubleValue()/sum.doubleValue());
        }
    }

    public void getInitialProbabilityFrom(HashMap<List<String>, Double> model,
        HashMap<List<String>, Integer> from)
    {
        if(k < 2)
        {
            System.out.println("============================");
            System.out.println("in getInitialProbabilityFrom");
            System.out.println("============================");
            String tag;
            for (List<String> key : from.keySet())
            {
                tag = key.get(key.size() - 1);
                
                Integer sum = new Integer(0);
                for(String currentTag : allTags){
                    ArrayList<String> auxList = ((ArrayList<String>)((ArrayList<String>)key).clone());
                    auxList.set(auxList.size()-1,currentTag);

                    // System.out.println("auxList: "+auxList.toString());
                    sum += new Integer(this.getFormatedInitial(auxList));
                    // System.out.println("value for auxlist found: "+new Integer(this.getFormatedInitial(auxList)).toString());
                    // System.out.println("sum: "+sum.toString());
                }
                // System.out.println("key: "+key.toString());
                // System.out.println("key value: "+new Integer(this.getFormatedInitial((ArrayList<String>)key)).toString());
                // System.out.println("sum: "+new Integer(sum).toString());

                model.put(key, sum == 0? Double.NaN : new Integer(this.getFormatedInitial((ArrayList<String>)key)).doubleValue()/sum.doubleValue());
            }
        }
    }


    /* Gives the probability that a given token has a given tag with respect to the given history of tags.
     */
    public double probability(String[] history, String token, String tag)
    {
        // ArrayList<String> ar = (ArrayList<String>)Arrays.asList(history);
        ArrayList<String> ar = new ArrayList<String>();
        for(int i = 0; i < history.length; i++)
            ar.add(history[i]);
        ar.add(token);
        ar.add(tag);
        if (probabilityModel.containsKey(ar))
            return probabilityModel.get(ar);
        else 
            return Double.NaN;
    }

    public double initialProbability(String token, String tag)
    {
        if(probabilityInitial.containsKey(Arrays.asList(tag)))
            return probabilityInitial.get(Arrays.asList(tag));
        else
            return Double.NaN;
    }

    /*
     * Given a history as in „tags“, what is the probability for
     * seeing „tag“? Assume tags.length == k-1
     */

    public double transitionProbability(String tags[], String tag)
    {
        int good = 0, all = 0;
LOOP:
        for (List<TaggedToken> list : model.keySet())
        {
            for (int i = 0; i < k-1; i++)
                if (!list.get(i).tag().equals(tags[i]))
                    continue LOOP;

            all += model.get(list);

            if (!list.get(k-1).tag().equals(tag))
                continue LOOP;

            good += model.get(list);
        }

        return ((double) good)/all;
    }
}
