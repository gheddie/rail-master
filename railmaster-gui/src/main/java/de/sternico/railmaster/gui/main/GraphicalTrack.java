package de.sternico.railmaster.gui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.elements.Connection;
import de.sternico.railmaster.gui.util.ConnectionComparator;

public class GraphicalTrack
{
    private String trackRef;
    
    private List<Connection> connections;

    private Set<Integer> utiliziedSegmentNumbers;

    private boolean valid;

    public GraphicalTrack(String trackRef)
    {
        super();
        this.connections = new ArrayList<Connection>();
        this.trackRef = trackRef;
        this.utiliziedSegmentNumbers = new HashSet<>();
        this.valid = true;
    }

    public void addConnection(Connection connection) throws RailMasterException
    {
        int segment = new Integer(connection.getTrackSegment());
        if (utiliziedSegmentNumbers.contains(segment))            
        {
            throw new RailMasterException("duplicate segemt number '"+segment+"' in track '"+trackRef+"'!!", null);
        }
        connections.add(connection);
        utiliziedSegmentNumbers.add(segment);
    }

    public void dump()
    {
        System.out.println("------------------------------------------------");
        System.out.println("Gleis :");
        System.out.println("------------------------------------------------");
        for (Connection connection : connections)
        {
            System.out.println(connection);
        }
        System.out.println("------------------------------------------------");
    }

    public void finish() throws RailMasterException
    {
        Collections.sort(connections, new ConnectionComparator());
        check();
    }

    private void check() throws RailMasterException
    {
        Connection predecessor = null;
        for (Connection connection : connections)
        {            
            if (predecessor != null)
            {
                if (!(connection.getFromVal().equals(predecessor.getToVal())))
                {
                    throw new RailMasterException("ungültiger Streckenverlauf ["+predecessor+" -> "+connection+"] in Gleis '"+trackRef+"'!!", null);
                }
                int segmentNo = connection.getTrackSegment();
                int segmentNoPredecessor = predecessor.getTrackSegment();
                if (!(segmentNo == ((segmentNoPredecessor + 1))))
                {
                    throw new RailMasterException("ungültige Segmentfolge ["+segmentNoPredecessor+" -> "+segmentNo+"] in Gleis '"+trackRef+"'!!", null);
                }                
            }
            predecessor = connection;
        }        
    }

    public List<Connection> getConnections()
    {
        return connections;
    }
    
    public String getTrackRef()
    {
        return trackRef;
    }
}