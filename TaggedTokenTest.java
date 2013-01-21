import org.junit.*;
import static org.junit.Assert.*;

public class TaggedTokenTest
{
    @Test public void simplePair()
    {
        String input = "September-October/np";
        String expectedTag = "np";
        String expectedToken = "September-October";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(expectedTag, tt.tag());
        assertEquals(expectedToken, tt.token());
    }

    @Test public void pairWithNot()
    {
        String input = "not/*";
        String expectedTag = "*";
        String expectedToken = "not";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(expectedTag, tt.tag());
        assertEquals(expectedToken, tt.token());
    }

    @Test public void pairWithDollar()
    {
        String input = "Atlanta's/np$";
        String expectedTag = "np$";
        String expectedToken = "Atlanta's";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(expectedTag, tt.tag());
        assertEquals(expectedToken, tt.token());
    }

    @Test public void pairWithoutPlus() // was pairWithPlus, but the corpus has been corrected
    {
        String input = "They're/ppss";
        String expectedTag = "ppss";
        String expectedToken = "They're";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(expectedTag, tt.tag());
        assertEquals(expectedToken, tt.token());
    }

    @Test public void quotationMakr()
    {
        String input = "``/``";
        String expectedTag = "``";
        String expectedToken = "``";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(expectedTag, tt.tag());
        assertEquals(expectedToken, tt.token());
    }

    @Test public void notAPair()
    {
        String input = "not/a/pair";
        String expectedTag = "pair";
        String expectedToken = "not/a";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(expectedTag, tt.tag());
        assertEquals(expectedToken, tt.token());
     }

    public TaggedTokenTest()
    {
    }
}
