package de.sternico.railmaster.gui;

import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.main.TrackViewer;
import de.sternico.railmaster.gui.model.TrackModel;
import de.sternico.railmaster.gui.parser.ElementsParser;

public class ParserTest
{
    private static final String PATH = "D:\\work\\eclipseWorkspaces\\railmaster.test\\railmaster-gui\\src\\main\\resources\\de\\sternico\\railmaster\\tracks\\test456.xml";
    
    public static void main(String[] args)
    {
        ElementsParser trackParser = new ElementsParser();
        try
        {
            trackParser.parseElements(PATH);
            TrackModel trackModel = trackParser.getTrackModel();
            System.out.println(trackModel);
        }
        catch (RailMasterException e)
        {
            e.printStackTrace();
        }
        new TrackViewer();
    }
}