package de.sternico.railmaster.gui.parser;

import de.sternico.railmaster.core.exception.RailMasterException;

public interface ValueParser
{
    <T> T parseValue(String value) throws RailMasterException;
}