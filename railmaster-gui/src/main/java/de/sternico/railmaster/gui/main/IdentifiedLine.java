package de.sternico.railmaster.gui.main;

import de.sternico.railmaster.gui.elements.Connection;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Line;

@SuppressWarnings("restriction")
public class IdentifiedLine extends Line implements TrackElementRepresentation
{
    private String identifier;

    public IdentifiedLine(int x1, int y1, int x2, int y2, String identifier)
    {
        super(x1, y1, x2, y2);
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
        double d = Math.sqrt(Math.pow(getEndX() - getStartX(), 2) + Math.pow(getEndY() - getStartY(), 2));
        double r = distance / d;
        double x3 = r * getEndX() + (1-r) * getStartX();
        double y3 = r * getEndY() + (1-r) * getStartY();        
        return new Point2D(x3, y3);
    }

    public double calculateLength()
    {
        return Math.sqrt(Math.pow(getEndX() - getStartX(), 2) + Math.pow(getEndY() - getStartY(), 2));
    }

    public Point2D getLowerRight()
    {
        double maxX = getEndX() > getStartX() ? getEndX() : getStartX();
        double maxY = getEndY() > getStartY() ? getEndY() : getStartY();        
        return new Point2D(maxX, maxY);
    }
}