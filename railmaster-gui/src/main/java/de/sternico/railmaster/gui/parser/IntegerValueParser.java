package de.sternico.railmaster.gui.parser;

public class IntegerValueParser implements ValueParser
{
    @SuppressWarnings("unchecked")
    public <T> T parseValue(String value)
    {
        return (T) new Integer(Integer.parseInt(value));
    }
}
