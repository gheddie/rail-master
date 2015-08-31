package de.sternico.railmaster.gui.util;

import java.awt.geom.*;

public class QuadLength
{
    public static void main(String[] args)
    {
        PathIterator iter = (new QuadCurve2D.Double(0, 0, 100, 0, 100, 100)).getPathIterator(null, 0.5);
        double length = 0;
        double[] curSeg = new double[2];
        iter.currentSegment(curSeg);
        iter.next();
        double x0 = curSeg[0];
        double y0 = curSeg[1];
        while (!iter.isDone())
        {
            iter.currentSegment(curSeg);
            length += Math.sqrt((curSeg[0] - x0) * (curSeg[0] - x0) + (curSeg[1] - y0) * (curSeg[1] - y0));
            x0 = curSeg[0];
            y0 = curSeg[1];
            iter.next();
        }
        System.out.println("Curve length:" + length);
    }
}