/*
 * Describes a token/tag-pair. Parses from either one single
 * string in the format token/tag+additional-tags, or from two
 * strings giving the token and tag.
 */

public final class TaggedToken
{
    private String token, tag;

    public TaggedToken(String s)
    {
        int where = s.lastIndexOf("/");
        if (where == -1)
            throw new IllegalArgumentException("The pair '" + s +
                    "' is not of the shape token/tag.");

        token = s.substring(0, where);
        tag = s.substring(where+1);

    }

    public String tag()
    {
        return tag;
    }

    public String token()
    {
        return token;
    }

    public String toString()
    {
        return token + "/" + tag;
    }

    public boolean equals(Object o)
    {
        if (o instanceof TaggedToken)
        {
            TaggedToken other = (TaggedToken) o;
            return this.token.equals(other.token) &&
                   this.tag.equals(other.tag);
        }

        else
            return false;
    }

    public int hashCode()
    {
        return tag.hashCode() ^ 0x10001*token.hashCode();
    }
}
