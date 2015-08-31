package de.sternico.railmaster.gui.model;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.elements.Connection;
import de.sternico.railmaster.gui.elements.LocatedTrackElement;
import de.sternico.railmaster.gui.elements.TrackElement;
import de.sternico.railmaster.gui.main.GraphicalTrack;

public class TrackModel
{
    private HashMap<String, TrackElement> elements;
    
    private int rawWidth;

    private int rawHeight;

    private HashMap<String, GraphicalTrack> tracks;   

    public HashMap<String, TrackElement> getElements()
    {
        return elements;
    }

    public void setElements(HashMap<String, TrackElement> elements)
    {
        this.elements = elements;
    }
    
    public int getRawHeight()
    {
        return rawHeight;
    }
    
    public void setRawHeight(int rawHeight)
    {
        this.rawHeight = rawHeight;
    }
    
    public int getRawWidth()
    {
        return rawWidth;
    }
    
    public void setRawWidth(int rawWidth)
    {
        this.rawWidth = rawWidth;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("------------------------------------------\n");
        Object[] arguments =
        {
                rawWidth, rawHeight
        };
        MessageFormat format = new MessageFormat("TrackModel (w={0}, h={1})\n");
        buffer.append(format.format(arguments));
        buffer.append("------------------------------------------\n");
        int i = 0;
        for (TrackElement element : elements.values())
        {            
            buffer.append("["+(i++)+"]" + element + "\n");    
        }
        buffer.append("------------------------------------------\n");
        return buffer.toString();
    }

    public List<Line2D> getEdges()
    {
        List<Line2D> edges = new ArrayList<>();
        Connection connection = null;
        for (TrackElement element : elements.values())
        {            
            if (element instanceof Connection)
            {
                connection = (Connection) element;
                Point2D from = asPoint((LocatedTrackElement) elements.get(connection.getFromVal()));
                Point2D to = asPoint((LocatedTrackElement) elements.get(connection.getToVal()));                
                Line2D edge = new Line2D.Double(from, to);
                edges.add(edge);
            }
        }        
        return edges;
    }
    
    public Line2D getEdge(Connection connection)
    {
        Point2D from = asPoint((LocatedTrackElement) elements.get(connection.getFromVal()));
        Point2D to = asPoint((LocatedTrackElement) elements.get(connection.getToVal()));                
        Line2D edge = new Line2D.Double(from, to);
        return edge;
    }

    private Point2D asPoint(LocatedTrackElement element)
    {
        LocatedTrackElement moo = (LocatedTrackElement) elements.get(element.getIdentifier());
        return new Point2D.Double(moo.getxCoord(), moo.getyCoord());
    }

    public void setTracks(HashMap<String, GraphicalTrack> tracks)
    {
        this.tracks = tracks;
    }
    
    public void finalizeTracks() throws RailMasterException
    {
        if (tracks != null)
        {
            for (GraphicalTrack track : tracks.values())
            {
                track.finish();
            }
            dumpTracks();   
        }
    }    
    
    private void dumpTracks()
    {
        for (GraphicalTrack track : tracks.values())
        {
            track.dump();
        }
    }

    public GraphicalTrack getTrack(String trackReference)
    {
        return tracks.get(trackReference);
    }

    public void pushBoundaries(int getxCoord, int getyCoord)
    {
        // TODO Auto-generated method stub        
    }
}