import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class ViterbiTest
{
    private Viterbi v;

    @Before public void setUp() throws IOException
    {
        v = new Viterbi("brown_learn", 3, new double[] {1, 1, 1});
    }

    @Test public void lastElementsNormal() throws IOException
    {
        Viterbi v = new Viterbi();
        String[] given = new String[] {"A", "B", "C"};
        String[] expected = new String[] {"B", "C"};
        assertEquals(expected, v.lastElements(given, 2));
    }

    public ViterbiTest()
    {
    }
}
