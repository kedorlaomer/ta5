import java.util.*;
import java.io.*;

/*
 * Reads the first k words of a sentence. Assumes that there is
 * at least one sentence.
 */

public class SentenceReader implements Iterator<TaggedToken[]>
{
    private BufferedReader reader;
    private int k;
    private String line;
    private boolean eof;

    public SentenceReader(Reader reader, int k) throws IOException
    {
        this.reader = new BufferedReader(reader);
        this.k = k;
        gotoNonemptyLine();
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Can't remove tokens from the corpus.");
    }

    public TaggedToken[] next()
    {
        if (eof)
        {
            throw new NoSuchElementException();
        }

        try
        {
            TaggedToken[] rv = new TaggedToken[k];
            int i = 0;
            for (String token : line.split("\\s+"))
            {
                if (!token.equals(""))
                {
                    rv[i++] = new TaggedToken(token);
                    if (i == k)
                    {
                        gotoNonemptyLine();
                        return rv;
                    }
                }
            }

            gotoNonemptyLine();
            return rv;
        }

        catch (IOException exc)
        {
            throw new RuntimeException(exc); // we have to rethrow this as RuntimeException
        }
    }

    public boolean hasNext()
    {
        return !eof;
    }

    private void gotoNonemptyLine() throws IOException
    {
        while (true)
        {
            line = reader.readLine();

            if (line == null)
            {
                eof = true;
                return;
            }

            line = line.trim().toLowerCase();

            if (!line.equals(""))
                return;
        }
    }
}
