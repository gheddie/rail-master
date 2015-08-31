package de.sternico.railmaster.gui.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import de.sternico.railmaster.core.builder.AssetBuilder;
import de.sternico.railmaster.core.exception.RailMasterException;
import de.sternico.railmaster.gui.elements.AssetPresentation;
import de.sternico.railmaster.gui.elements.Connection;
import de.sternico.railmaster.gui.elements.TrackElement;
import de.sternico.railmaster.gui.model.TrackModel;
import de.sternico.railmaster.gui.parser.ElementsParser;
import de.sternico.railmaster.gui.util.TrackViewHelper;

@SuppressWarnings("restriction")
public class TrackViewer extends Application
{
    private static final int ASSET_OFFSET = 15;

    private static final int DEFAULT_ASSET_LENGTH = 17;

    private static final double DEFAULT_STROKE_WIDTH = 5.0;

    private static final Color COLOR_SELECTED = new Color(1, 0, 0, 1);

    private static final Color COLOR_NOMINAL = new Color(0, 0, 0, 1);

    private static final Color COLOR_TEST = Color.RED;

    private static final String PATH =
            "D:\\work\\eclipseWorkspaces\\railmaster.test\\railmaster-gui\\src\\main\\resources\\de\\sternico\\railmaster\\tracks\\testABC.xml";

    public static void main(String[] args)
    {
        launch(args);
    }

    private Group renderingTarget;

    private int translateY = 0;

    private int translateX = 0;

    private HashMap<String, TrackElement> trackElements;

    private HashMap<String, List<TrackElement>> trackMappings;

    private HashMap<String, TrackElementRepresentation> renderElements;

    private List<TrackElementRepresentation> selectedTrackSegements;

    private TrackModel trackModel;
    
    private double maxRenderBoundsX;
    
    private double maxRenderBoundsY;

    private Parent zoomPane;

    public void start(final Stage stage)
    {
        try
        {
            buildRenderingTarget();
            
            createZoomPane();

            ScrollBar hScrollBar = new ScrollBar();
            hScrollBar.valueProperty().addListener((ObservableValue<? extends Number> ov, 
                    Number old_val, Number new_val) -> {
                        System.out.println(-new_val.doubleValue());
                });  
            hScrollBar.setOrientation(Orientation.HORIZONTAL);

            ScrollBar vScrollBar = new ScrollBar();
            vScrollBar.valueProperty().addListener((ObservableValue<? extends Number> ov, 
                    Number old_val, Number new_val) -> {
                        System.out.println(-new_val.doubleValue());
                });              
            vScrollBar.setOrientation(Orientation.VERTICAL);

            VBox layout = new VBox();
            layout.getChildren().setAll(createMenuBar(stage), zoomPane);

            VBox.setVgrow(zoomPane, Priority.ALWAYS);
            Scene scene = new Scene(layout);
            System.out.println("getAntiAliasing : " + scene.getAntiAliasing());

            stage.setTitle("TrackViewer");
            stage.setWidth(maxRenderBoundsX);
            stage.setHeight(maxRenderBoundsY);
            stage.setScene(scene);
            stage.show();            
        }
        catch (RailMasterException e)
        {
            e.printStackTrace();
        }
    }

    private void buildRenderingTarget() throws RailMasterException
    {
        ElementsParser trackParser = new ElementsParser();
        trackParser.parseElements(PATH);
        trackModel = trackParser.getTrackModel();
        Collection<Node> nodes = processTrackModel(trackModel);
        renderingTarget = new Group();
        renderingTarget.getChildren().addAll(nodes);
    }

    private Collection<Node> processTrackModel(TrackModel trackModel)
    {
        trackElements = new HashMap<>();
        Collection<Node> nodes = new ArrayList<Node>();
        for (TrackElement trackElement : trackModel.getElements().values())
        {
            Shape shape = trackElement.createShape(trackModel);
            if (trackElement instanceof Connection)
            {
                // process and reorder graphical tracks
                updateTrackReferences((Connection) trackElement);
            }
            trackElements.put(trackElement.getIdentifier(), trackElement);
            setupCommon(trackElement, shape);
            pushBoundaries((TrackElementRepresentation) shape);
            nodes.add(shape);
        }
        return nodes;
    }

    private void pushBoundaries(TrackElementRepresentation representation)
    {
        if (representation.getLowerRight().getX() > maxRenderBoundsX)
        {
            maxRenderBoundsX = representation.getLowerRight().getX();
        }
        if (representation.getLowerRight().getY() > maxRenderBoundsY)
        {
            maxRenderBoundsY = representation.getLowerRight().getY();
        }        
    }

    private void updateTrackReferences(Connection connection)
    {
        if (trackMappings == null)
        {
            trackMappings = new HashMap<>();
        }
        String trackRef = connection.getEffectiveTrackRef();
        if (trackMappings.get(trackRef) == null)
        {
            trackMappings.put(trackRef, new ArrayList<>());
        }
        trackMappings.get(trackRef).add(connection);
    }

    private void setupCommon(TrackElement trackElement, Shape shape)
    {
        shape.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                Shape source = (Shape) event.getSource();
                String identifier = ((TrackElementRepresentation) source).getIdentifier();
                handleCollision(trackElements.get(identifier), true);
            }
        });
        shape.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                Shape source = (Shape) event.getSource();
                String identifier = ((TrackElementRepresentation) source).getIdentifier();
                handleCollision(trackElements.get(identifier), false);
            }
        });
        Tooltip.install(shape, new Tooltip("ID = " + trackElement.getIdentifier() + " [" + trackElement + "]"));
        if (renderElements == null)
        {
            renderElements = new HashMap<>();
        }
        renderElements.put(trackElement.getIdentifier(), (TrackElementRepresentation) shape);
        shape.setStrokeWidth(DEFAULT_STROKE_WIDTH);
    }

    private void handleCollision(TrackElement trackElement, boolean entered)
    {
        System.out.println("handling mouse over : " + trackElement);
        if (trackElement instanceof Connection)
        {
            String trackRef = ((Connection) trackElement).getEffectiveTrackRef();

            if (entered)
            {
                resetTrackSegmentSelection();

                List<TrackElement> affectedSegments = null;
                if (trackMappings != null)
                {
                    affectedSegments = trackMappings.get(trackRef);
                }
                if ((affectedSegments != null) && (affectedSegments.size() > 0))
                {
                    System.out.println(affectedSegments.size() + " segments affected.");
                    int i = 0;
                    TrackElementRepresentation representation = null;
                    for (TrackElement affectedSegment : affectedSegments)
                    {
                        representation = renderElements.get(affectedSegment.getIdentifier());
                        System.out.println("[" + (i++) + "] : " + affectedSegment + " --> " + representation);
                        addSelectedTrackSegment(representation);
                    }
                    highlightTrackSelection(trackRef, true);
                }
                else
                {
                    System.out.println("NO segments affected.");
                }
            }
            else
            {
                System.out.println("exited : " + trackElement);
                highlightTrackSelection(trackRef, false);
                resetTrackSegmentSelection();
            }
        }
    }

    private void highlightTrackSelection(String trackRef, boolean highlight)
    {
        if ((selectedTrackSegements == null) || (selectedTrackSegements.size() == 0))
        {
            return;
        }
        for (TrackElementRepresentation selected : selectedTrackSegements)
        {
            if (highlight)
            {
                ((Shape) selected).setStroke(COLOR_SELECTED);
            }
            else
            {
                ((Shape) selected).setStroke(COLOR_NOMINAL);
            }
            renderingTarget.requestLayout();
        }
    }

    private void addSelectedTrackSegment(TrackElementRepresentation representation)
    {
        if (selectedTrackSegements == null)
        {
            selectedTrackSegements = new ArrayList<>();
        }
        selectedTrackSegements.add(representation);
    }

    private void resetTrackSegmentSelection()
    {
        if (selectedTrackSegements != null)
        {
            selectedTrackSegements.clear();
        }
    }

    @SuppressWarnings("incomplete-switch")
    private void handleTranslation(KeyEvent e)
    {
        System.out.println("OnKeyReleased : " + e.getCode());
        switch (e.getCode())
        {
            case UP:
                retranslateTarget(0, 1, false);
                break;
            case DOWN:
                retranslateTarget(0, -1, false);
                break;
            case LEFT:
                retranslateTarget(1, 0, false);
                break;
            case RIGHT:
                retranslateTarget(-1, 0, false);
                break;
        }
    }

    private void retranslateTarget(double diffX, double diffY, boolean dragged)
    {
        translateX += diffX;
        translateY += diffY;
        System.out.println("translating to [x:" + translateX + "|y:" + translateY + "].");
        renderingTarget.setTranslateX(dragged
                ? (translateX - (maxRenderBoundsX / 2))
                : translateX);
        renderingTarget.setTranslateY(dragged
                ? (translateY - (maxRenderBoundsY / 2))
                : translateY);
    }

    private void createZoomPane()
    {
        final double SCALE_DELTA = 1.1;
        zoomPane = new StackPane();

        ((StackPane) zoomPane).getChildren().add(renderingTarget);

        // necessary for enabling key pressed events...
        zoomPane.setFocusTraversable(true);

        zoomPane.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            private double oldX;

            private double oldY;

            public void handle(MouseEvent e)
            {
                double diffX = e.getX() - oldX;
                double diffY = e.getY() - oldY;
                System.out.println("setOnMouseDragged : [diffX:" + diffX + "|diffY:" + diffY + "]");
                retranslateTarget(diffX, diffY, true);
                oldX = e.getX();
                oldY = e.getY();
            }
        });

        zoomPane.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            public void handle(KeyEvent e)
            {
                System.out.println("OnKeyPressed : " + e.getCode());
                handleTranslation(e);
            }
        });

        zoomPane.setOnScroll(new EventHandler<ScrollEvent>()
        {
            public void handle(ScrollEvent event)
            {
                event.consume();

                if (event.getDeltaY() == 0)
                {
                    return;
                }

                double scaleFactor = (event.getDeltaY() > 0)
                        ? SCALE_DELTA
                        : 1 / SCALE_DELTA;

                renderingTarget.setScaleX(renderingTarget.getScaleX() * scaleFactor);
                renderingTarget.setScaleY(renderingTarget.getScaleY() * scaleFactor);
            }
        });

        zoomPane.layoutBoundsProperty().addListener(new ChangeListener<Bounds>()
        {
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldBounds, Bounds bounds)
            {
                zoomPane.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(),
                        bounds.getHeight()));
            }
        });              
    }

    public void renderTrackSequence(GraphicalTrack track, List<AssetPresentation> assets) throws RailMasterException
    {
        int distanceOnTrack = ASSET_OFFSET;
        Point2D assetFrom = null;
        Point2D assetTo = null;
        for (AssetPresentation presentation : assets)
        {
            assetFrom = TrackViewHelper.calculatePositionOnTrack(track, renderElements, distanceOnTrack);
            assetTo =
                    TrackViewHelper.calculatePositionOnTrack(track, renderElements,
                            (distanceOnTrack + presentation.getAsset().getOverallLength()));

            Line line = new Line(assetFrom.getX(), assetFrom.getY(), assetTo.getX(), assetTo.getY());
            line.setStroke(COLOR_TEST);
            line.setStrokeWidth(10.0);
            line.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent event)
                {
                    System.out.println("asset : " + event.getSource());
                }
            });
            renderingTarget.getChildren().add(line);

            distanceOnTrack += (presentation.getAsset().getOverallLength() + ASSET_OFFSET);
        }
    }

    private MenuBar createMenuBar(final Stage stage)
    {
        Menu fileMenu = new Menu("_File");
        MenuItem exitMenuItem = new MenuItem("E_xit");
        exitMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                stage.close();
            }
        });
        fileMenu.getItems().setAll(exitMenuItem);
        Menu zoomMenu = new Menu("_Zoom");
        MenuItem zoomResetMenuItem = new MenuItem("Zoom _Reset");
        zoomResetMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        zoomResetMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                resetScalingAndZoom();
            }
        });
        MenuItem zoomInMenuItem = new MenuItem("Zoom _In");
        zoomInMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.I));
        zoomInMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                renderingTarget.setScaleX(renderingTarget.getScaleX() * 1.5);
                renderingTarget.setScaleY(renderingTarget.getScaleY() * 1.5);
            }
        });
        MenuItem zoomOutMenuItem = new MenuItem("Zoom _Out");
        zoomOutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O));
        zoomOutMenuItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                renderingTarget.setScaleX(renderingTarget.getScaleX() * 1 / 1.5);
                renderingTarget.setScaleY(renderingTarget.getScaleY() * 1 / 1.5);
            }
        });
        MenuItem applyModelItem = new MenuItem("Apply Model");
        applyModelItem.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                System.out.println("applyModelItem");
                try
                {
                    applyModel();
                }
                catch (RailMasterException e)
                {
                    e.printStackTrace();
                }
            }
        });
        zoomMenu.getItems().setAll(zoomResetMenuItem, zoomInMenuItem, zoomOutMenuItem, applyModelItem);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().setAll(fileMenu, zoomMenu);
        return menuBar;
    }
    
    private void resetScalingAndZoom()
    {
        renderingTarget.setScaleX(1);
        renderingTarget.setScaleY(1);
        
        renderingTarget.setTranslateX(0);
        renderingTarget.setTranslateY(0);
    }    

    private void applyModel() throws RailMasterException
    {
        GraphicalTrack track2 = trackModel.getTrack("TA6:TA2");

        List<AssetPresentation> assetsTrack2 = new ArrayList<>();
        AssetBuilder assetBuilder = new AssetBuilder();
        assetsTrack2.add(new AssetPresentation(assetBuilder.withAssetNumber("123456")
                .withOverallLength(DEFAULT_ASSET_LENGTH)
                .build()));
        assetsTrack2.add(new AssetPresentation(assetBuilder.withAssetNumber("123456")
                .withOverallLength(DEFAULT_ASSET_LENGTH)
                .build()));
        assetsTrack2.add(new AssetPresentation(assetBuilder.withAssetNumber("123456")
                .withOverallLength(DEFAULT_ASSET_LENGTH)
                .build()));

        renderTrackSequence(track2, assetsTrack2);
    }
}