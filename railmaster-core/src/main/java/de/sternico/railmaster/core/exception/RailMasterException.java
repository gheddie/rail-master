package de.sternico.railmaster.core.exception;

public class RailMasterException extends Exception
{
    private static final long serialVersionUID = -1857503865095690239L;

    public RailMasterException(Throwable t)
    {
        this(null, t);
    }

    public RailMasterException(String message, Throwable t)
    {
        super(message, t);
    }
}