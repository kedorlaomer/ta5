import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class HMMLearnerTest
{
    @Test public void countComponent() throws IOException
    {
        HMMLearner learner = new HMMLearner(new File("brown_learn"), 1);
        TaggedToken tt = new TaggedToken("component/nn");
        int expectedCount = 23; // yes, we counted this externally (count.pl)
        assertEquals(expectedCount, learner.getModel(new TaggedToken[] {tt}));
    }

    @Test public void countThe() throws IOException
    {
        HMMLearner learner = new HMMLearner(new File("brown_learn/ca01"), 1);
        TaggedToken tt = new TaggedToken("the/at");
        int expectedCount = 155;
        assertEquals(expectedCount, learner.getModel(new TaggedToken[] {tt}));
    }

    @Test public void initialCountThereIs() throws IOException
    {
        HMMLearner learner = new HMMLearner(new File("brown_learn"), 2);
        TaggedToken tt[] = new TaggedToken[] { new TaggedToken("there/ex"),
            new TaggedToken("is/bez")};
        int expectedCount = 190;
        assertEquals(expectedCount, learner.getInitial(tt));
    }

    @Test public void initialCountThe() throws IOException
    {
        HMMLearner learner = new HMMLearner(new File("brown_learn/ca01"), 1);
        TaggedToken tt = new TaggedToken("the/at");
        int expectedCount = 27;
        assertEquals(expectedCount, learner.getInitial(new TaggedToken[] {tt}));
    }

    //<<token1, tag1>, <token1, tag1>, …, <token(k-1),tag(k-1)>> : count
    //<tag1, tag2, …, tagk-2, tokenk-1, tagk-1> : count
    @Test public void checkFormattedModel() throws IOException
    {
        int k = 3;
        HMMLearner learner = new HMMLearner(new File("brown_learn/ca01"), k);

        String text = "The/at Fulton/np County/nn Grand/jj";
        ArrayList<TaggedToken> taggedTokens = new ArrayList<TaggedToken>();
        ArrayList<String> tags = new ArrayList<String>();

        int i = 0;
        TaggedToken tt;

        for (String stt : text.split(" ")) {
            tt = new TaggedToken(stt);
            taggedTokens.add(tt);

            if (i == k-2)
                tags.add(tt.token());
            tags.add(tt.tag());
            if (i == k-1)
                break;
            i++;
        }

        int tagsCount = learner.getModel(taggedTokens);
        int elementCount = learner.getFormatedModel(tags);
        assertEquals(tagsCount, elementCount);
    }

    @Test public void NNtoNN() throws IOException
    {
        HMMLearner learner = new HMMLearner(new File("brown_learn"), 2);
        System.out.println(learner.transitionProbability(new String[] {"at"}, "nn"));
        double expected = 0.0841;
        assertEquals(expected, learner.transitionProbability(new String[] {"nn"}, "nn"), 0.01);
    }

    public HMMLearnerTest()
    {
    }
}
