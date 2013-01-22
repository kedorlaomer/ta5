import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class SentenceReaderTest
{
    @Test public void firstSentence() throws IOException
    {
        TaggedToken[] expected = new TaggedToken[] { tt("individuals/nns"),
            tt("possessing/vbg"), tt("unusual/jj") };
        Iterator<TaggedToken[]> iter = new SentenceReader(new FileReader(new File("brown_learn/cd15")), 3);
        assertTrue(iter.hasNext());
        assertEquals(expected, iter.next());
    }

    @Test public void lastSentence() throws IOException
    {
        TaggedToken[] expected = new TaggedToken[] { tt("in/in"),  tt("turn/nn"),
            tt("the/at"), tt("other/ap")};
        TaggedToken[] result = null;
        Iterator<TaggedToken[]> iter = new SentenceReader(new FileReader(new File("brown_learn/cf11")), 4);

        while (iter.hasNext())
            result = iter.next();

        assertEquals(expected, result);
     }

    private TaggedToken tt(String s)
    {
        return new TaggedToken(s);
    }
}
