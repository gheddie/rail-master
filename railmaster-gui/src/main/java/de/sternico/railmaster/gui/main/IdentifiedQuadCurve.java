package de.sternico.railmaster.gui.main;

import de.sternico.railmaster.gui.elements.Connection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.QuadCurve;

@SuppressWarnings("restriction")
public class IdentifiedQuadCurve extends QuadCurve implements TrackElementRepresentation
{
    private String identifier;
    
    public IdentifiedQuadCurve(int x1, int y1, double xControl, double yControl, int x2, int y2, String identifier)
    {
        super(x1, y1, xControl, yControl, x2, y2);
        this.identifier = identifier;
    }
    
    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }
    
    public Point2D calculatePositionFormStart(double distance)
    {
        return new Point2D(10, 10);
    }
    
    public double calculateLength()
    {
        return 0.0;
    }
    
    public Point2D getLowerRight()
    {
        return null;
    }
}