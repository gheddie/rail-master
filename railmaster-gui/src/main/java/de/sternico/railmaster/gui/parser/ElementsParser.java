package de.sternico.railmaster.gui.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.elements.Connection;
import de.sternico.railmaster.gui.elements.LocatedTrackElement;
import de.sternico.railmaster.gui.elements.TrackElement;
import de.sternico.railmaster.gui.elements.TrackElementFactory;
import de.sternico.railmaster.gui.main.GraphicalTrack;
import de.sternico.railmaster.gui.model.TrackModel;

public class ElementsParser
{        
    private HashMap<String, TrackElement> trackElements;
    
    private TrackModel model;
    
    private HashMap<String, GraphicalTrack> graphicalTracks;

    private List<String> locationCache;
    
    public ElementsParser()
    {
        super();
        trackElements = new HashMap<String, TrackElement>();
        model = new TrackModel();
    }

    public void parseElements(String path) throws RailMasterException
    {
        try
        {
            File file = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            Element root = doc.getDocumentElement();
            root.normalize();
            System.out.println("Root element :" + root.getNodeName());
            model.setRawHeight((int) ParserUtil.parseValue(root.getAttribute(TrackElementFactory.ATTR_RAW_HEIGHT), Integer.class));
            model.setRawWidth((int) ParserUtil.parseValue(root.getAttribute(TrackElementFactory.ATTR_RAW_WIDTH), Integer.class));
            parseElementsRecursive(root);
            finish();
        }
        catch (ParserConfigurationException | SAXException | IOException e)
        {
            e.printStackTrace();
        }        
    }

    private void parseElementsRecursive(Element root) throws RailMasterException
    {
        NodeList childNodes = root.getChildNodes();
        Node child = null;
        TrackElement createdTrackElement = null;
        String identifier = null;
        for (int childIndex = 0; childIndex < childNodes.getLength(); childIndex++)
        {
            child = childNodes.item(childIndex);
            createdTrackElement = TrackElementFactory.getTrackElementByType(child);
            if (createdTrackElement != null)
            {
                if (createdTrackElement instanceof Connection)
                {
                    Connection connection = (Connection) createdTrackElement;
                    checkEndpoints(connection);
                    processConnection(connection);
                }
                else if (createdTrackElement instanceof LocatedTrackElement)
                {
                    LocatedTrackElement element = (LocatedTrackElement) createdTrackElement;
                    checkLocationOverlap(element);
                    model.pushBoundaries(element.getxCoord(), element.getyCoord());
                }                
                identifier = createdTrackElement.getIdentifier();
                if ((identifier == null) || (identifier.length() == 0))
                {
                    throw new RailMasterException("blank identifier detected!!", null);
                }                
                if (trackElements.get(identifier) != null)
                {
                    throw new RailMasterException("node identifier '"+identifier+"' is not unique!!", null);
                }
                trackElements.put(identifier, createdTrackElement);
            }
            if (child instanceof Element)
            {
                parseElementsRecursive((Element) child);
            }
        }
    }
    
    private void checkEndpoints(Connection connection) throws RailMasterException
    {
        if (trackElements.get(connection.getFromVal()) == null)
        {
            throw new RailMasterException("connection '"+connection+"' points to invalid start point ("+connection.getFromVal()+")!!", null);
        }
        if (trackElements.get(connection.getToVal()) == null)
        {
            throw new RailMasterException("connection '"+connection+"' points to invalid end point ("+connection.getToVal()+")!!", null);
        }        
    }

    private void checkLocationOverlap(LocatedTrackElement element) throws RailMasterException
    {
        if (locationCache == null)
        {
            locationCache = new ArrayList<String>();
        }
        String location = element.getxCoord() + "::" + element.getyCoord();
        if (locationCache.contains(location))
        {
            throw new RailMasterException("location '"+location+"' for element '"+element+"' is duplicate!!", null);
        }
        locationCache.add(location);
    }

    private void processConnection(Connection connection) throws RailMasterException
    {
        //update graphical tracks
        if (graphicalTracks == null)
        {
            graphicalTracks = new HashMap<>();
        }        
        String trackReference = connection.getEffectiveTrackRef();
        if (graphicalTracks.get(trackReference) == null)
        {
            graphicalTracks.put(trackReference, new GraphicalTrack(trackReference));
        }
        graphicalTracks.get(trackReference).addConnection(connection);
    }

    public TrackModel getTrackModel()
    {
        return model;
    }

    private void finish() throws RailMasterException
    {
        model.setElements(trackElements);
        model.setTracks(graphicalTracks);
        model.finalizeTracks();
    }
}