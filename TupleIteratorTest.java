import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class TupleIteratorTest
{
    @Test public void trivial()
    {
        TaggedToken[] arr = {tt("a/1"), tt("b/2"), tt("c/3"), tt("d/4"),
            tt("e/5"), tt("d/6")};
        List<TaggedToken> asList = Arrays.asList(arr);
        Iterator<TaggedToken> iter = asList.iterator();
        TaggedToken[] expected1 = new TaggedToken[] {tt("a/1"), tt("b/2"), tt("c/3")};
        TaggedToken[] expected2 = new TaggedToken[] {tt("b/2"), tt("c/3"), tt("d/4")};

        TupleIterator ti = new TupleIterator(iter, 3);
        
        assertTrue(ti.hasNext());
        assertEquals(ti.next(), expected1);
        assertTrue(ti.hasNext());
        assertEquals(ti.next(), expected2);
    }

    @Test public void upToEnd()
    {
        TaggedToken[] arr = {tt("a/1"), tt("b/2"), tt("c/3"), tt("d/4"),
            tt("e/5"), tt("d/6")};
        List<TaggedToken> asList = Arrays.asList(arr);
        Iterator<TaggedToken> iter = asList.iterator();
        TupleIterator ti = new TupleIterator(iter, 3);
        int expectedLength = 4;

        for (int i = 0; ; i++)
        {
            if (!ti.hasNext())
            {
                assertEquals(expectedLength, i);
                break;
            }
            ti.next();
        }
    }

    @Test public void lastTokens() throws IOException
    {
        TupleIterator ti = new TupleIterator(new BrownReader(new FileReader("brown_learn/cj49")), 3);
        TaggedToken[] expected = new TaggedToken[]
        {
            new TaggedToken("supported/vbn"), 
            new TaggedToken("institutions/nns"), new TaggedToken("./.")
        };
        Object[] last = null;

        while (ti.hasNext())
            last = ti.next();

        assertEquals(expected, last);
    }

    public TupleIteratorTest()
    {
    }

    private TaggedToken tt(String s)
    {
        return new TaggedToken(s);
    }
}
