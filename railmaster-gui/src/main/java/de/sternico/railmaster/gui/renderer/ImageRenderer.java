package de.sternico.railmaster.gui.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import javafx.geometry.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.sternico.railmaster.gui.elements.Connection;
import de.sternico.railmaster.gui.elements.LocatedTrackElement;
import de.sternico.railmaster.gui.elements.TrackElement;
import de.sternico.railmaster.gui.model.TrackModel;

public class ImageRenderer
{
    private static boolean DEBUG = true;

    public static void render(TrackModel trackModel) throws IOException
    {
        BufferedImage bi = new BufferedImage(trackModel.getRawWidth(), trackModel.getRawHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = bi.createGraphics();

        Font font = new Font("", Font.BOLD, 10);
        g2.setFont(font);
        g2.setPaint(Color.black);
        
        LocatedTrackElement locatedTrackElement = null;
        Connection connection = null;
        Line2D edge = null;
        for (TrackElement trackElement : trackModel.getElements().values())
        {
            if (trackElement instanceof LocatedTrackElement)
            {
                locatedTrackElement = (LocatedTrackElement) trackElement;
                if (DEBUG)
                {   
                    g2.drawString(trackElement.getIdentifier() + " ["+locatedTrackElement.getClass().getSimpleName()+"] (x:"+locatedTrackElement.getxCoord()+"/y:"+locatedTrackElement.getyCoord()+")", locatedTrackElement.getxCoord(), locatedTrackElement.getyCoord());
                }                
                g2.drawRect(locatedTrackElement.getxCoord()-10, locatedTrackElement.getyCoord()-10, 20, 20);   
            }
            else if (trackElement instanceof Connection)
            {                
                connection = (Connection) trackElement;
                edge = trackModel.getEdge(connection);
                Point2D control = connection.getControl();
                if (control != null)
                {
                    QuadCurve2D curve = new QuadCurve2D.Double();
                    if (DEBUG)
                    {
                        drawCrossHair(g2, control);
                    }
                    curve.setCurve((int) edge.getX1(), (int) edge.getY1(), control.getX(), control.getY(), (int) edge.getX2(), (int) edge.getY2());
                    g2.draw(curve);
                }
                else
                {                    
                    g2.drawLine((int) edge.getX1(), (int) edge.getY1(), (int) edge.getX2(), (int) edge.getY2());        
                }
            }
        }
        ImageIO.write(bi, "PNG", new File("d:\\tmp\\raildrop\\track.PNG"));
    }

    private static void drawCrossHair(Graphics2D g2, Point2D control)
    {
        g2.drawLine((int) control.getX()-5, (int) control.getY()-5, (int) control.getX()+5, (int) control.getY()+5);
        g2.drawLine((int) control.getX()+5, (int) control.getY()-5, (int) control.getX()-5, (int) control.getY()+5);
    }
}