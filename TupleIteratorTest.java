import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;

public class TupleIteratorTest
{
    @Test public void trivial()
    {
        Integer[] arr = {1, 2, 3, 4, 5, 6};
        List<Integer> asList = Arrays.asList(arr);
        Iterator<Integer> iter = asList.iterator();
        Integer[] expected1 = new Integer[] {1, 2, 3};
        Integer[] expected2 = new Integer[] {2, 3, 4};

        TupleIterator ti = new TupleIterator(iter, 3);
        
        assertTrue(ti.hasNext());
        assertEquals(ti.next(), expected1);
        assertTrue(ti.hasNext());
        assertEquals(ti.next(), expected2);
    }

    @Test public void upToEnd()
    {
        Integer[] arr = {1, 2, 3, 4, 5, 6};
        List<Integer> asList = Arrays.asList(arr);
        Iterator<Integer> iter = asList.iterator();
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
}
