package de.sternico.railmaster.gui.elements;

import java.util.HashMap;

import javafx.scene.shape.Shape;
import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.entities.Asset;
import de.sternico.railmaster.gui.model.TrackModel;

public class AssetPresentation
{
    private Asset asset;
    
    public AssetPresentation(Asset asset)
    {
        super();
        this.asset = asset;
    }

    protected boolean hasTypeDependentData()
    {
        return false;
    }

    public void applyTypeDependentData(HashMap<String, String> typeDependentData) throws RailMasterException
    {
        // TODO Auto-generated method stub
    }

    public Asset getAsset()
    {
        return asset;
    }

    @SuppressWarnings("restriction")
    public Shape createShape(TrackModel trackModel)
    {
        return null;
    }
}