package de.sternico.railmaster.gui.parser;

import java.util.HashMap;

import de.sternico.railmaster.core.exception.RailMasterException;

public class ParserUtil
{
    private static HashMap<Class<?>, ValueParser> valueParsers = new HashMap<>();
    static
    {
        valueParsers.put(String.class, new StringValueParser());
        valueParsers.put(Integer.class, new IntegerValueParser());
    }
    public static <T> T parseValue(String value, Class<?> targetClass) throws RailMasterException
    {
        ValueParser parserInstance = valueParsers.get(targetClass);
        if (parserInstance == null)
        {
            throw new RailMasterException("can not determine parser for type '"+targetClass.getCanonicalName()+"'!!", null);
        }
        return parserInstance.parseValue(value);
    }
}