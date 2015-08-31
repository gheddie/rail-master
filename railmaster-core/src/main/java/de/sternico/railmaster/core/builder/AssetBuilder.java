package de.sternico.railmaster.core.builder;

import de.sternico.railmaster.entities.Asset;

public class AssetBuilder extends AbstractEntityBuilder<Asset>
{
    private String assetNumber;
    
    private int overallLength;

    @SuppressWarnings("unchecked")
    public Asset build()
    {
        Asset asset = new Asset();
        asset.setAssetNumber(assetNumber);
        asset.setOverallLength(overallLength);
        return asset;
    }
    
    public AssetBuilder withOverallLength(int overallLength)
    {
        this.overallLength = overallLength;
        return this;
    }

    public AssetBuilder withAssetNumber(String assetNumber)
    {
        this.assetNumber = assetNumber;
        return this;
    }
}