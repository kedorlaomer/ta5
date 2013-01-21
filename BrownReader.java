import java.io.*;
import java.util.*;

/*
 * Reads the POS-tagged Brown-Corpus. Lowercases everything.
 */

public class BrownReader implements Iterator<TaggedToken>
{
    /*
     * Read line-wise from reader; save it to line; return a
     * TaggedToken that started in positionInLine
     */

    private Reader reader;
    private StringBuilder accu;
    private boolean eof = false;

    public boolean hasNext()
    {
        return !eof;
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Can't remove tokens from the corpus.");
    }

    public TaggedToken next()
    {
        String rv = null;
        try
        {
            readNonWhite();
            rv = accu.toString();
            skipWhite();
        }

        catch (IOException exc)
        {
            throw new RuntimeException(exc);
        }

        return new TaggedToken(rv.toLowerCase());
    }

    public BrownReader(Reader reader) throws IOException
    {
        this.reader = reader;
        accu = new StringBuilder(50);
        skipWhite();
    }

    private void skipWhite() throws IOException
    {
        int ch;
        boolean finished;
        do
        {
            ch = reader.read();
            eof = ch == -1;
            finished = eof || !Character.isWhitespace(ch);
        }
        while (!finished);

        if (!eof)
        {
            accu.setLength(0);
            accu.append((char) ch);
        }
    }

    private void readNonWhite() throws IOException
    {
        int ch;
        boolean finished;
        do
        {
            ch = reader.read();
            eof = ch == -1;
            finished = eof || Character.isWhitespace(ch);
            if (!finished)
                accu.append((char) ch);
        }
        while (!finished);
    }
}
