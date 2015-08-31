package de.sternico.railmaster.gui.elements;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import de.sternico.railmaster.gui.main.IdentifiedRectangle;
import de.sternico.railmaster.gui.model.TrackModel;
import javafx.scene.shape.Rectangle;

@SuppressWarnings("restriction")
public abstract class LocatedTrackElement extends TrackElement
{
    private int xCoord;
    
    private int yCoord;
    
    public int getxCoord()
    {
        return xCoord;
    }
    
    public void setxCoord(int xCoord)
    {
        this.xCoord = xCoord;
    }
    
    public int getyCoord()
    {
        return yCoord;
    }
    
    public void setyCoord(int yCoord)
    {
        this.yCoord = yCoord;
    }
    
    public String toString()
    {
        return super.toString() + " (x="+xCoord+"|y="+yCoord+")";
    }
        
    public Shape createShape(TrackModel trackModel)
    {
        Shape shape = new IdentifiedRectangle(getxCoord() - 5, getyCoord() - 5, 10, 10,
                getIdentifier());
        ((Rectangle) shape).addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                IdentifiedRectangle identifiedRectangle = (IdentifiedRectangle) event.getSource();
                System.out.println("node selected : " + identifiedRectangle.getIdentifier());
                identifiedRectangle.toggleSelection();
            }
        });        
        return shape;
    }    
}