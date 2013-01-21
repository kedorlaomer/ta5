import java.util.*;

/*
 * An iterator that transforms another iterator producing x1,
 * x2, … into an iterator producing (x1, …, x_k), (x2, …,
 * x_k+1), …
 *
 * The original iterator should produce at least k elements.
 */

public class TupleIterator implements Iterator
{
    private int k; // this should be a constant
    private Iterator<TaggedToken> iter;
    private TaggedToken[] state;

    public TupleIterator(Iterator<TaggedToken> iter, int k)
    {
        this.k = k;
        this.iter = iter;
        state = new TaggedToken[k];

        for (int i = 1; i < k; i++)
            state[i] = iter.next(); // elements are expected to exist
    }

    public void remove()
    {
        throw new UnsupportedOperationException("Where should the element be removed from?");
    }

    public boolean hasNext()
    {
        return iter.hasNext();
    }

    public TaggedToken[] next()
    {
        System.arraycopy(state, 1, state, 0, k-1);
        state[k-1] = iter.next();
        return (TaggedToken[]) Arrays.copyOf(state, k);
    }
}
