package de.sternico.railmaster.gui.elements;

import java.util.HashMap;

import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.parser.ParserUtil;

public class Junction extends LocatedTrackElement
{
    private String trackRoot;
    
    public boolean hasTypeDependentData()
    {
        return true;
    }
    
    public void applyTypeDependentData(HashMap<String, String> typeDependentData) throws RailMasterException
    {
        trackRoot = ParserUtil.parseValue(typeDependentData.get(TypeDataKeys.JunctionKeys.TRACK_ROOT), String.class);
    }
    
    public String toString()
    {
        return super.toString() + " (trackRoot="+trackRoot+")";
    }
}