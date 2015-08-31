package de.sternico.railmaster.core.builder;

public abstract class AbstractEntityBuilder<T>
{
    public abstract <T> T build();
}