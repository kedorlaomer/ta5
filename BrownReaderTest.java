import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class BrownReaderTest
{
    @Test public void firstThreeWords() throws IOException
    {
        TaggedToken[] expected = new TaggedToken[] {
            new TaggedToken("``/``"), new TaggedToken("she/pps"), // note: she, not She
                new TaggedToken("says/vbz")};

        Iterator iter = new BrownReader(new FileReader("brown_learn/cl18"));
        for (int i = 0; i < 3; i++)
        {
            assertTrue(iter.hasNext());
            assertEquals(expected[i], iter.next());
        }
    }

    @Test public void lastWords() throws IOException
    {
        TaggedToken expected1 = new TaggedToken("cheer/nn"),
                    expected2 = new TaggedToken("./.");
        TaggedToken last1 = null, last2 = null;

        Iterator iter = new BrownReader(new FileReader("brown_learn/cf32"));
        while (iter.hasNext())
        {
            last1 = last2;
            last2 = (TaggedToken) iter.next();
        }

        assertEquals(last1, expected1);
        assertEquals(last2, expected2);
     }

    public BrownReaderTest()
    {
    }
}
