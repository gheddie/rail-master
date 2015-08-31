package de.sternico.railmaster.gui.parser;

public class StringValueParser implements ValueParser
{
    @SuppressWarnings("unchecked")
    public <T> T parseValue(String value)
    {
        return (T) value;
    }
}