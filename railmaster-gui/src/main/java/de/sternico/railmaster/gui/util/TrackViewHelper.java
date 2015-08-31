package de.sternico.railmaster.gui.util;

import java.util.HashMap;

import javafx.geometry.Point2D;
import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.elements.Connection;
import de.sternico.railmaster.gui.main.GraphicalTrack;
import de.sternico.railmaster.gui.main.TrackElementRepresentation;

@SuppressWarnings("restriction")
public class TrackViewHelper
{    
    public static Point2D calculatePositionOnTrack(GraphicalTrack track, HashMap<String, TrackElementRepresentation> renderElements, int distanceFromStart) throws RailMasterException
    {
        if (track == null)
        {
            throw new RailMasterException("track must not be NULL!!", null);
        }
        TrackElementRepresentation representation = null;
        double totalElementLength = 0.0;
        TrackElementRepresentation affectedRepresentation = null;
        double remainingDist = distanceFromStart;
        for (Connection conn : track.getConnections())            
        {
            representation = renderElements.get(conn.getIdentifier());
            double elementLength = representation.calculateLength();
            System.out.println(conn + " -> " + representation + ", length : " + elementLength);
            totalElementLength += elementLength;
            if (distanceFromStart < totalElementLength)
            {
                affectedRepresentation = representation;
                break;
            }
            remainingDist -= elementLength;
        }
        System.out.println("affected : " + affectedRepresentation);
        if (affectedRepresentation == null)
        {
            throw new RailMasterException("a distance of "+distanceFromStart+" exceeds legth of track '"+track.getTrackRef()+"'!!", null);
        }
        return affectedRepresentation.calculatePositionFormStart(remainingDist);
    }
}