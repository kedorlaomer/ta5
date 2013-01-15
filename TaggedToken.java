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
        String[] t = s.split("/");
        if (t.length != 2)
        {
            throw new IllegalArgumentException("String '" + s +
                    "' is not a valid tag/token pair.");
        }

        token = t[0];
        tag = t[1];

        /* 
         * find a non-alnum-character that is not one of $*` and
         * skip from there
         */

        for (int i = 0; i < tag.length(); i++)
        {
            char c = tag.charAt(i);
            if (!(('a' <= c && c <= 'z') ||
                  ('A' <= c && c <= 'Z') ||
                  c == '$' || c == '*' || c == '`'))
                tag = tag.substring(0, i);
        }

        this.token = token;
        this.tag = tag;
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
