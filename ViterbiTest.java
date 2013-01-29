import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class ViterbiTest
{
    private Viterbi v;

    @Before public void setUp() throws IOException
    {
        v = new Viterbi(new File("brown_learn/ca01"), 2, new double[] {1, 1});
    }

    @Test public void lastElementsNormal() throws IOException
    {
        String[] given = new String[] {"A", "B", "C"};
        String[] expected = new String[] {"B", "C"};
        assertEquals(expected, v.lastElements(given, 2));
    }

    @Test public void tooShort() throws IOException
    {
        String[] given = new String[] {"A", "B", "C"};
        String[] expected = new String[] {"A", "B", "C"};
        assertEquals(expected, v.lastElements(given, 5));
    }

    @Test public void simpleSentence() throws IOException
    {
        String s = "this is a simple sentence";
        TaggedToken[] sentence = v.tagSentence(tt(s));
        TaggedToken[] expected = t2(s, "det bez at jj nn");

        for (TaggedToken t : sentence)
            System.out.print(t + " ");
        System.out.println();

        assertEquals(expected, sentence);
    }

    private TaggedToken[] tt(String s)
    {
        String[] tokens = s.split(" ");
        TaggedToken[] rv = new TaggedToken[tokens.length];

        for (int i = 0; i < tokens.length; i++)
            rv[i] = new TaggedToken(tokens[i] + "/xxx");

        return rv;
    }

    private TaggedToken[] t2(String text, String tags)
    {
        TaggedToken[] rv = tt(text);
        String[] t = tags.split(" ");
        for (int i = 0; i < rv.length; i++)
            rv[i] = new TaggedToken(rv[i].tag() + "/" + t[i]);

        return rv;
    }

    public ViterbiTest()
    {
    }

    public static void main(String[] argv) throws IOException
    {
        ViterbiTest test = new ViterbiTest();
        test.setUp();
        test.simpleSentence();
    }
}
