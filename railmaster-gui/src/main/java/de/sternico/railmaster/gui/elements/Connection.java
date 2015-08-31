package de.sternico.railmaster.gui.elements;

import java.awt.geom.Line2D;
import java.util.HashMap;

import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import javafx.scene.shape.QuadCurve;
import javafx.scene.paint.Color;
import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.main.IdentifiedLine;
import de.sternico.railmaster.gui.main.IdentifiedQuadCurve;
import de.sternico.railmaster.gui.model.TrackModel;

public class Connection extends TrackElement
{
    private String fromVal;
    
    private String toVal;

    private Point2D control;

    private String trackRef;

    private int trackSegment;

    public Connection(String fromVal, String toVal)
    {
        super();
        this.fromVal = fromVal;
        this.toVal = toVal;
    }

    protected boolean hasTypeDependentData()
    {
        return false;
    }

    public void applyTypeDependentData(HashMap<String, String> typeDependentData) throws RailMasterException
    {
        // TODO Auto-generated method stub
    }
    
    public String toString()
    {
        return super.toString() + " [from '"+fromVal+"' to '"+toVal+"'] (track ref : "+trackRef+", segment : "+trackSegment+")";
    }
    
    public String getFromVal()
    {
        return fromVal;
    }
    
    public String getToVal()
    {
        return toVal;
    }

    public Point2D getControl()
    {
        return this.control;
    }
    
    public void setControl(Point2D control)
    {
        this.control = control;
    }
    
    public String getTrackRef()
    {
        return trackRef;
    }      

    public void setTrackRef(String trackRef)
    {
        this.trackRef = trackRef;
    }
    
    public int getTrackSegment()
    {
        return trackSegment;
    }

    public void setTrackSegment(int trackSegment)
    {
        this.trackSegment = trackSegment;
    }

    public boolean hasTrackReference()
    {
        return (trackRef != null);
    }

    @SuppressWarnings("restriction")
    public Shape createShape(TrackModel trackModel)
    {
        Line2D edge = trackModel.getEdge(this);
        Point2D control = getControl();
        Shape shape = null;
        if (control != null)
        {
            shape =
                    new IdentifiedQuadCurve((int) edge.getX1(), (int) edge.getY1(), control.getX(),
                            control.getY(), (int) edge.getX2(), (int) edge.getY2(),
                            getIdentifier());
            ((QuadCurve) shape).setStroke(new Color(0, 0, 0, 1));
            ((QuadCurve) shape).setFill(null);
        }
        else
        {
            shape =
                    new IdentifiedLine((int) edge.getX1(), (int) edge.getY1(), (int) edge.getX2(),
                            (int) edge.getY2(), getIdentifier());
        }
        return shape;
    }
    
    public String getEffectiveTrackRef()
    {
        return (trackRef != null ? trackRef : getIdentifier());
    }
}