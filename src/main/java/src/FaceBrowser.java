package src;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class FaceBrowser extends JPanel {

    private static final long serialVersionUID = 1L;
    private ArrayList<FaceItem> faceItems;
    private int totalHeight = 0;
    private HashMap<FaceItem, Face> itemToFaceMap;
    private HashMap<Face, FaceItem> faceToItemMap;

    //Creating a new FaceBrowser instance
    //This initializes collections for managing face items and sets up the panel layout
     
    public FaceBrowser() {
        faceItems = new ArrayList<>();
        itemToFaceMap = new HashMap<>();
        faceToItemMap = new HashMap<>();
        this.setPreferredSize(new Dimension(280, 500));
        this.setBackground(ModernStyles.LIGHT_BG);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void init() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)),
                "");
        titledBorder.setTitleFont(ModernStyles.BODY_FONT);
        titledBorder.setTitleColor(ModernStyles.DARK_BG);
        this.setBorder(titledBorder);
        this.setBackground(Color.WHITE);
        this.setOpaque(true);

        JLabel imageLabelLocal = new JLabel();
        imageLabelLocal.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel textLabelLocal = new JLabel("");
        textLabelLocal.setVerticalAlignment(JLabel.TOP);
        textLabelLocal.setFont(ModernStyles.BODY_FONT);

        this.add(imageLabelLocal, BorderLayout.WEST);
        this.add(textLabelLocal, BorderLayout.CENTER);

        setPreferredSize(new Dimension(260, 120));
    }

    //Refreshes all face items in the browser
    //This updates the display of each face item to reflect any changes
     
    public void refresh() {
        for (FaceItem item : itemToFaceMap.keySet()) {
            item.refresh();
        }
    }

    //Adding a new face to the browser
    //This creates a face item for the face and adds it to the display
    
    public void addFace(Face face) {
        FaceItem item = new FaceItem(face);
        this.add(item);
        itemToFaceMap.put(item, face);
        faceToItemMap.put(face, item);
        faceItems.add(item);
    }

    //Removing all faces from the browser
    //Clearing all collections and removes all face items from display
    
    public void empty() {
        this.removeAll();
        faceItems.clear();
        itemToFaceMap.clear();
        faceToItemMap.clear();
        doLayout();
    }

    //Getting the minimum size of the browser panel
    //The width is fixed while the height accommodates all face items
     
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(256, totalHeight);
    }

    //Gets the preferred size of the browser panel
    //Matches the minimum size to ensure proper display of all items

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    //Highlighting face items with a specific classification
    //Adding a red border to matching items and resets others
     
    public void highlightClassifiedAs(String classification) {
        for (FaceItem item : faceItems) {
            if (item.getFace().getClassification() != null &&
                    item.getFace().getClassification().equals(classification)) {
                item.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                item.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        }
    }

    //Reordering face items based on their distances
    //Removing and re-adding items in the order specified by the distance pairs
     
    public void orderAs(FeatureSpace.FaceDistancePair[] faceDistances) {
        removeAll();
        for (FeatureSpace.FaceDistancePair pair : faceDistances) {
            add(new FaceItem(pair.getFace()));
        }
        revalidate();
        repaint();
    }

    //Laying out the components in the panel
    //Arranging face items vertically and updates the total height
     
    @Override
    public void doLayout() {
        super.doLayout();

        Component[] components = this.getComponents();
        int currentY = 0;
        for (Component component : components) {
            component.setLocation(0, currentY);
            component.setSize(this.getWidth(), component.getHeight());
            currentY += component.getHeight();
        }

        totalHeight = currentY;
        this.revalidate();
    }
}

class FaceItem extends JPanel {

    private static final long serialVersionUID = 1L;
    private Face face;
    private ImageIcon image;
    private JLabel imageLabel;
    private JLabel textLabel;
    private TitledBorder border;
    private ImageIcon eigenfaceImage;
    private JLabel eigenfaceLabel;
    private double distance = -1;

    //Creating a new empty FaceItem
    //Initializes the panel without associating a face
     
    public FaceItem() {
        init();
        textLabel.setText(""); // Clear text for empty items
        this.setVisible(false); // Hide by default until face is set
    }

    //Creating a new FaceItem with the specified face
    //This initializes the panel and associates it with the given face
     
    public FaceItem(Face f) {
        init();
        if (f != null) {
            setFace(f);
        } else {
            textLabel.setText(""); // Clear text for empty items
            this.setVisible(false); // Hide empty items
        }
    }

    //Setting the distance value for this face item
    //Updating the display to reflect the similarity to a reference face
    public void setDistance(double dist) {
        this.distance = dist;
        updateLabel();

        double amt = dist / 4096;
        if (amt > 1) {
            amt = 1;
        }
        amt = 0.5 + amt / 2;
        this.setBackground(new Color((float) amt, 1.0f, (float) amt));
        this.setOpaque(true);
    }

    //Updating the text label with current face information
    //Displaying classification, distance, description, and file path
    
    private void updateLabel() {
        if (this.face == null) {
            textLabel.setText(""); // Clear text if no face is set
            return;
        }

        String text = "<html>";
        text += "<font size=+1><font color=#7f7f7f>Classification:</font> ";
        if (this.face.getClassification() == null) {
            text += "<font color=#7f0000>[unclassified]</font>";
        } else {
            text += "<font color=#00007f>" + this.face.getClassification() + "</font>";
        }
        text += "</b></font>";

        if (this.distance >= 0) {
            text += ("<br><b>" + "Distance: " + String.format("%.2f", this.distance) + "</b>");
        }

        text += "<br>" + this.face.getDescription() + "";
        text += "<br><font size=-2 color=#7f7f7f>" + this.face.getFile().getName() + "</font>";
        text += "</html>";
        textLabel.setText(text);
    }

    //Setting the highlight state of this face item
    //Changes the border and opacity to indicate selection
     
    public void setHighlighted(boolean b) {
        this.setOpaque(b);
        if (b) {
            border.setTitleColor(Color.BLACK);
            border.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        } else {
            border.setTitleColor(Color.GRAY);
            border.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
    }

    //Refreshes the face image display
    //Updating the image icon with the current face picture
    public void refresh() {
        if (this.face != null && this.face.getPicture() != null) {
            this.image = new ImageIcon(this.face.getPicture().getImage());
            imageLabel.setIcon(this.image);
        }
    }

    //Associates a face with this item and updates the display
    //Setting the face image, title, and metadata information
    public void setFace(Face f) {
        this.face = f;
        this.setVisible(true); // Make visible when face is set
        refresh();
        if (border != null) {
            border.setTitle(f.getFile().getName());
        }
        updateLabel();
        if (image != null) {
            Insets i = imageLabel.getInsets();
            imageLabel.setPreferredSize(
                    new Dimension(
                            image.getIconWidth() + i.left + i.right,
                            image.getIconHeight() + i.top + i.bottom));
        }
    }

    //Initializes the face item components
    //Setting up the layout and creates necessary UI elements
     
    private void init() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);

        border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(this.getBackground()), "");
        this.setBorder(border);
        this.setOpaque(false);

        imageLabel = new JLabel();
        imageLabel.setBorder(BorderFactory.createBevelBorder(1));
        textLabel = new JLabel("");
        textLabel.setVerticalAlignment(JLabel.TOP);
        eigenfaceLabel = new JLabel();
        eigenfaceLabel.setBorder(BorderFactory.createBevelBorder(1));

        this.add(imageLabel, BorderLayout.WEST);
        this.add(textLabel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(300, 150));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 150);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(300, 150);
    }

    public Face getFace() {
        return face;
    }
}