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
        assertEquals(tt.tag(), expectedTag);
        assertEquals(tt.token(), expectedToken);
    }

    @Test public void pairWithNot()
    {
        String input = "not/*";
        String expectedTag = "*";
        String expectedToken = "not";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(tt.tag(), expectedTag);
        assertEquals(tt.token(), expectedToken);
    }

    @Test public void pairWithDollar()
    {
        String input = "Atlanta's/np$";
        String expectedTag = "np$";
        String expectedToken = "Atlanta's";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(tt.tag(), expectedTag);
        assertEquals(tt.token(), expectedToken);
    }

    @Test public void pairWithPlus()
    {
        String input = "They're/ppss+ber";
        String expectedTag = "ppss";
        String expectedToken = "They're";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(tt.tag(), expectedTag);
        assertEquals(tt.token(), expectedToken);
    }

    @Test public void quotationMakr()
    {
        String input = "``/``";
        String expectedTag = "``";
        String expectedToken = "``";
        TaggedToken tt = new TaggedToken(input);
        assertEquals(tt.tag(), expectedTag);
        assertEquals(tt.token(), expectedToken);
    }

    @Test(expected=IllegalArgumentException.class) public void notAPair()
    {
        new TaggedToken("not/a/pair");
    }

    public TaggedTokenTest()
    {
    }
}
