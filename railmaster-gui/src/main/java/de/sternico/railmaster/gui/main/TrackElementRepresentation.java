package de.sternico.railmaster.gui.main;

import javafx.geometry.Point2D;

public interface TrackElementRepresentation
{
    String getIdentifier();

    Point2D calculatePositionFormStart(double distance);

    double calculateLength();

    Point2D getLowerRight();
}