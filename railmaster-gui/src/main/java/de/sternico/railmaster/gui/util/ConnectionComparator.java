package de.sternico.railmaster.gui.util;

import java.util.Comparator;

import de.sternico.railmaster.gui.elements.Connection;

public class ConnectionComparator implements Comparator<Connection>
{
    public int compare(Connection o1, Connection o2)
    {
        if (o1.getTrackSegment() < o2.getTrackSegment())
        {
            return -1;
        }
        else if (o1.getTrackSegment() > o2.getTrackSegment())
        {
            return 1;
        }        
        return 0;
    }
}