package de.sternico.railmaster.gui.elements;

import javafx.geometry.Point2D;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.parser.ParserUtil;

public class TrackElementFactory
{
    public static final String NODE_NAME_NODE = "node";
    
    public static final String NODE_NAME_CONNECTION = "connection";
    
    private static final String TAGNAME_RELATIVE_LOCATION = "relativelocation";
    
    private static final String TAGNAME_TYPE_DEPENDENT_DATA = "typeDependentData";
    
    private static final String TAGNAME_TYPE_DEPENDENT_ATTRIBUTE = "typeDependentAttribute";
    
    private static final String TAGNAME_CONTROL = "control";
    
    private static final String TAGNAME_TRACK_REF = "trackRef";
    
    private static final String ATTR_NAME_VALUE = "value";

    private static final String ATTR_NAME_KEY = "key";    
    
    private static final String ATTR_NAME_X = "x";
    
    private static final String ATTR_NAME_Y = "y";
    
    private static final String ATTR_NAME_FROM = "from";
    
    private static final String ATTR_NAME_TO = "to";
    
    public static final String ATTR_RAW_WIDTH = "rawWidth";
    
    public static final String ATTR_RAW_HEIGHT = "rawHeight";
    
    private static final String ATTR_TRACK_REF = "ref";

    private static final String ATTR_TRACK_SEGMENT = "segment";    

    private static final String TYPENAME_JUNCTION = "junction";

    private static final String TYPENAME_BEND = "bend";         
    
    private static HashMap<String, Class<? extends TrackElement>> nodeTypeMappings = new HashMap<>();
    static
    {
        nodeTypeMappings.put(TYPENAME_JUNCTION, Junction.class);
        nodeTypeMappings.put(TYPENAME_BEND, Bend.class);
    }
    
    public static TrackElement getTrackElementByType(Node child) throws RailMasterException
    {
        String nodeName = child.getNodeName();
        switch (nodeName)
        {            
            case NODE_NAME_NODE:
                return parseNode((Element) child);
            case NODE_NAME_CONNECTION:
                return parseConnection((Element) child);
        }
        return null;   
    }
    
    private static TrackElement parseNode(Element trackNode) throws RailMasterException
    {       
        TrackElement element = null;
        String nodeType = null;
        String identifier = null;
        try
        {
            nodeType = trackNode.getAttribute("type");
            element = nodeTypeMappings.get(nodeType).newInstance();
            
            Element relativelocationNode = (Element) trackNode.getElementsByTagName(TAGNAME_RELATIVE_LOCATION).item(0);
            String xCoord = relativelocationNode.getElementsByTagName(ATTR_NAME_X).item(0).getTextContent();
            String yCoord = relativelocationNode.getElementsByTagName(ATTR_NAME_Y).item(0).getTextContent();
            identifier = trackNode.getAttribute("id");
            System.out.println(" @@@ parsed node : [id:"+identifier+"|type:"+nodeType+"|x:"+xCoord+"|y:"+yCoord+"]");
            element.setIdentifier(identifier);
            if (element instanceof LocatedTrackElement)
            {
                ((LocatedTrackElement) element).setxCoord((int) ParserUtil.parseValue(xCoord, Integer.class));
                ((LocatedTrackElement) element).setyCoord((int) ParserUtil.parseValue(yCoord, Integer.class));   
            }
            if (element.hasTypeDependentData())
            {
                element.applyTypeDependentData(readTypeDependentData((Element) trackNode.getElementsByTagName(TAGNAME_TYPE_DEPENDENT_DATA).item(0), nodeType));
            }
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RailMasterException(e);
        }
        return element;
    }
    
    @SuppressWarnings("restriction")
    private static TrackElement parseConnection(Element connectionNode) throws RailMasterException
    {
        String fromVal = connectionNode.getElementsByTagName(ATTR_NAME_FROM).item(0).getTextContent();
        String toVal = connectionNode.getElementsByTagName(ATTR_NAME_TO).item(0).getTextContent();        
        String identifier = connectionNode.getAttribute("id");
        System.out.println(" @@@ parsed connection : [id:"+identifier+"|from:"+fromVal+"|to:"+toVal+"]");
        Connection connection = new Connection(fromVal, toVal);
        connection.setIdentifier(identifier);
        
        Element controlNode = (Element) connectionNode.getElementsByTagName(TAGNAME_CONTROL).item(0);
        if (controlNode != null)
        {
            int xCoord = ParserUtil.parseValue(controlNode.getElementsByTagName(ATTR_NAME_X).item(0).getTextContent(), Integer.class);
            int yCoord = ParserUtil.parseValue(controlNode.getElementsByTagName(ATTR_NAME_Y).item(0).getTextContent(), Integer.class);
            connection.setControl(new Point2D(xCoord, yCoord));
        }
        
        Element trackRefNode = (Element) connectionNode.getElementsByTagName(TAGNAME_TRACK_REF).item(0);
        String trackRef = null;
        int trackSegment = -1;
        //track info is not mandatory!!
        if (trackRefNode != null)
        {
            trackRef = ParserUtil.parseValue(trackRefNode.getAttribute(ATTR_TRACK_REF), String.class);
            trackSegment = ParserUtil.parseValue(trackRefNode.getAttribute(ATTR_TRACK_SEGMENT), Integer.class);
        }
                
        connection.setTrackRef(trackRef);
        connection.setTrackSegment(trackSegment);
        
        return connection;
    }    
    
    private static HashMap<String, String> readTypeDependentData(Element dataNode, String type) throws RailMasterException
    {
        if (dataNode == null)
        {
            throw new RailMasterException("type dependent data excepted for type '"+type+"'.", null);
        }
        HashMap<String, String> data = new HashMap<>();
        NodeList elements = dataNode.getElementsByTagName(TAGNAME_TYPE_DEPENDENT_ATTRIBUTE);
        Element attributeNode = null;
        String key = null;
        String value = null;
        for (int childIndex = 0; childIndex < elements.getLength(); childIndex++)
        {
            attributeNode = (Element) elements.item(childIndex);
            key = attributeNode.getAttribute(ATTR_NAME_KEY);
            value = attributeNode.getAttribute(ATTR_NAME_VALUE);
            System.out.println("read type dependent data : [key:"+key+"|value:"+value+"]");
            data.put(key, value);
        }        
        return data;
    }
}