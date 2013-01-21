import java.util.*;
import java.io.*;

/*
 * Reads the first (k-1) words of a sentence.
 */

public class SentenceReader implements Iterator<TaggedToken[]>
{
    private Reader reader;
    private int k;

    public SentenceReader(Reader reader, int k) throws IOException
    {
        this.reader = reader;
        this.k = k;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Can't remove tokens from the corpus.");
    }

    public void next()
    {
        try
        {
        }

        catch (IOException exc)
        {
            throw new 
        }
    }
}
