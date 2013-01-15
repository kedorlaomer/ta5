import java.util.*;

/*
 * An iterator that transforms another iterator producing x1,
 * x2, … into an iterator producing (x1, …, x_k), (x2, …,
 * x_k+1), …
 *
 * The original iterator should produce at least k elements.
 */

public class TupleIterator<E> implements Iterator<E[]>
{
    private int k; // this should be a constant
    private Iterator<E> iter;
    private E[] state;

    public TupleIterator(Iterator<E> iter, int k)
    {
        this.k = k;
        this.iter = iter;
        state = (E[]) new Object[k];

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

    public E[] next()
    {
        System.arraycopy(state, 1, state, 0, k-1);
        state[k-1] = iter.next();
        return Arrays.copyOf(state, k);
    }
}
