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
        assertEquals(expectedCount, learner.get(new TaggedToken[] {tt}));
    }

    @Test public void countThe() throws IOException
    {
        HMMLearner learner = new HMMLearner(new File("brown_learn/ca01"), 1);
        TaggedToken tt = new TaggedToken("the/at");
        int expectedCount = 155;
        assertEquals(expectedCount, learner.get(new TaggedToken[] {tt}));
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

    public HMMLearnerTest()
    {
    }
}
