package de.sternico.railmaster.gui.main;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;

public class IdentifiedRectangle extends Rectangle implements TrackElementRepresentation
{
    private String identifier;
    
    private boolean selected = false;

    public IdentifiedRectangle(int x, int y, int w, int h, String identifier)
    {        
        super(x, y, w, h);
        this.identifier = identifier;
    }
    
    public String getIdentifier()
    {
        return identifier;
    }

    public void toggleSelection()
    {
        this.selected = !(this.selected);
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
        return new Point2D(getX()+getWidth(), getY()+getHeight());
    }
}