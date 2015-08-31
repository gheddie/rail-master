package de.sternico.railmaster.entities;

public class Asset
{
    private int overallLength;
    
    private String assetNumber;
    
    public String getAssetNumber()
    {
        return assetNumber;
    }
    
    public void setAssetNumber(String assetNumber)
    {
        this.assetNumber = assetNumber;
    }
    
    public int getOverallLength()
    {
        return overallLength;
    }
    
    public void setOverallLength(int overallLength)
    {
        this.overallLength = overallLength;
    }
}