import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class ViterbiTest
{
    private Viterbi v;

    @Before public void setUp() throws IOException
    {
        v = new Viterbi(new File("brown_learn"), 3, new double[] {1, 1, 1});
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
        TaggedToken[] sentence = v.tagSentence(tt("this is a simple sentence"));
        TaggedToken[] expected = null; // TODO: do this by hand
        System.out.println(sentence);
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

    public ViterbiTest()
    {
    }
}
