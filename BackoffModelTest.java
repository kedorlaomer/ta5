import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.*;

public class BackoffModelTest
{
    private BackoffModel model;

    @Before public void setUp() throws IOException
    {
        model = new BackoffModel(new File("brown_learn"), 0.2);
    }

    @Test public void testNP()
    {
        double expected = 0.174;
        assertEquals(expected, model.probability("np"), 0.01);
    }

    @Test public void testCD()
    {
        double expected = 0.04204594711746858;
        assertEquals(expected, model.probability("cd"), 0.01);
    }

    @Test(expected=NullPointerException.class) public void unknownTag()
    {
        assertEquals(-1, model.probability("not-existing-tag"), 0.01);
     }

    public BackoffModelTest()
    {
    }
}
