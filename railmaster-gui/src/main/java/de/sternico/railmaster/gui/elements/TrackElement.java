package de.sternico.railmaster.gui.elements;

import java.util.HashMap;

import javafx.scene.shape.Shape;
import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.model.TrackModel;

public abstract class TrackElement
{
    private String identifier;    
    
    protected abstract boolean hasTypeDependentData();
    
    public abstract void applyTypeDependentData(HashMap<String, String> typeDependentData) throws RailMasterException;
    
    public TrackElement()
    {
        super();
    }

    public String getIdentifier()
    {
        return this.identifier;
    }       

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }       
    
    public String toString()
    {
        return getClass().getSimpleName() + " : [identifier:"+identifier+"]";
    }

    public abstract Shape createShape(TrackModel trackModel);
}