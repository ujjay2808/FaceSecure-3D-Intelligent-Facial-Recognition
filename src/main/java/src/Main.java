package src;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;
    private static final Dimension IDEAL_IMAGE_SIZE = new Dimension(48, 64);
    private static final int NUM_EIGEN_VECTORS = 10;
    private static final int CLASSIFICATION_THRESHOLD = 5;
    private static final int FACE_BROWSER_WIDTH = 400;
    private static final int FACE_BROWSER_HEIGHT = 300;
    private static final int PROGRESS_TIMER_DELAY = 100;

    private TSCD eigenFaces;
    private FeatureSpace featureSpace;
    private JPanel main;
    private JProgressBar statusBar;
    private JButton loadImageButton;
    private JButton trainButton;
    private JButton probeButton;
    private JButton cropImageButton;
    private ImageIcon averageFaceIcon;
    private JLabel averageFaceLabel;
    private Container container;
    private FaceItem faceCandidate;
    private FaceBrowser faceBrowser;
    private JScrollPane faceBrowserScrollPane;
    private JButton displayFeatureSpaceButton;
    private JButton logoutButton;
    private FeatureVector lastFeatureVector;
    private ArrayList<Face> faces;
    private ArrayList<FeatureVector> trainingSet;
    private String classification;

    //Creates a new Main instance

    public Main() {
        eigenFaces = new TSCD();
        featureSpace = new FeatureSpace();
        faceBrowser = new FaceBrowser();
        trainingSet = new ArrayList<>();
        faces = new ArrayList<>();
        initializeUI();
    }

    //Initializes the user interface with a modern design
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Create modern header
        JPanel headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);

        // Create main content area
        main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initializeModernButtons();
        initializeFaceCandidate();
        initializeFaceBrowser();
        initializeModernRightPanel();

        // Add modern status bar to bottom with better visibility
        add(createModernStatusBar(), BorderLayout.SOUTH);

        // Add main panel to center
        add(main, BorderLayout.CENTER);
    }

    //Creates a modern header panel with title, subtitle, and logout button

    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        header.setPreferredSize(new Dimension(getWidth(), 80));

        JLabel titleLabel = new JLabel("FaceSecure 3D: Intelligent Facial Recognition");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Advanced Criminal Face Detection System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 200));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        // Add logout button to header
        logoutButton = new JButton("🚪 Logout");
        styleLogoutButton(logoutButton);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(logoutButton);

        header.add(textPanel, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        return header;
    }

    //Styles the logout button with modern design

    private void styleLogoutButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 35, 51), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 35, 51));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 53, 69));
            }
        });
    }

    //Initializes modern styled buttons with better visibility

    private void initializeModernButtons() {
        loadImageButton = new JButton("📁 Load Images");
        cropImageButton = new JButton("✂️ Crop Images");
        trainButton = new JButton("🧠 Compute Eigen Vectors");
        probeButton = new JButton("🔍 Identify Face");
        displayFeatureSpaceButton = new JButton("📊 Display Result Chart");

        // Set initial enabled states
        loadImageButton.setEnabled(true);
        cropImageButton.setEnabled(false);
        trainButton.setEnabled(false);
        probeButton.setEnabled(false);
        displayFeatureSpaceButton.setEnabled(false);

        // Add action listeners
        loadImageButton.addActionListener(this);
        cropImageButton.addActionListener(this);
        trainButton.addActionListener(this);
        probeButton.addActionListener(this);
        displayFeatureSpaceButton.addActionListener(this);
    }

    //Styles a button for better visibility in the modern UI

    private void styleButtonForVisibility(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(12, 25, 12, 25)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(280, 50));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    //Creates a modern status bar panel with progress bar

    private JPanel createModernStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(60, 60, 60));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        statusPanel.setPreferredSize(new Dimension(getWidth(), 35));

        statusBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        statusBar.setBorder(BorderFactory.createEmptyBorder());
        statusBar.setBackground(new Color(100, 100, 100));
        statusBar.setForeground(new Color(0, 150, 255));
        statusBar.setStringPainted(true);
        statusBar.setString("READY - Load images to begin");
        statusBar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        UIManager.put("ProgressBar.foreground", Color.WHITE);
        UIManager.put("ProgressBar.selectionForeground", Color.WHITE);

        statusPanel.add(statusBar, BorderLayout.CENTER);

        return statusPanel;
    }

    //Initializes the face candidate display component

    private void initializeFaceCandidate() {
        faceCandidate = null;
    }

    //Initializes the face browser scroll pane

    private void initializeFaceBrowser() {
        faceBrowserScrollPane = new JScrollPane(faceBrowser);
        faceBrowserScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        faceBrowserScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        faceBrowserScrollPane.setPreferredSize(new Dimension(FACE_BROWSER_WIDTH, FACE_BROWSER_HEIGHT));
        faceBrowserScrollPane.setVisible(false);
        faceBrowserScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    }

    //Initializes the modern right panel with control buttons

    private void initializeModernRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        right.setPreferredSize(new Dimension(320, 600));

        // Create control panel
        JPanel controlCard = new JPanel();
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        controlCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add title to control panel
        JLabel controlTitle = new JLabel("Face Recognition Controls");
        controlTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        controlTitle.setForeground(Color.BLACK);
        controlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlCard.add(controlTitle);
        controlCard.add(Box.createVerticalStrut(20));

        addModernButtonsToPanel(controlCard);

        right.add(controlCard);

        add(right, BorderLayout.EAST);
    }

    //Adds modern styled buttons to the given panel

    private void addModernButtonsToPanel(JPanel panel) {
        styleButtonForVisibility(loadImageButton, new Color(135, 206, 250));
        styleButtonForVisibility(cropImageButton, new Color(144, 238, 144));
        styleButtonForVisibility(trainButton, new Color(255, 182, 193));
        styleButtonForVisibility(probeButton, new Color(221, 160, 221));
        styleButtonForVisibility(displayFeatureSpaceButton, new Color(255, 255, 0));

        panel.add(Box.createVerticalStrut(5));
        panel.add(loadImageButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(cropImageButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(trainButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(probeButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(displayFeatureSpaceButton);
        panel.add(Box.createVerticalStrut(10));
    }

    //Handles button actions

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == loadImageButton) {
            loadImage();
        } else if (source == cropImageButton) {
            crop();
        } else if (source == trainButton) {
            train();
        } else if (source == probeButton) {
            probe();
        } else if (source == displayFeatureSpaceButton) {
            displayFeatureSpace();
        } else if (source == logoutButton) {
            logout();
        }
    }

    // Handles logout functionality with complete session clearing

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?\n\nAll training data will be cleared.",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            // CRITICAL: Clear all sensitive data FIRST
            clearSessionData();

            // Close current window
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }

            // Create NEW login frame (don't reuse old one)
            SwingUtilities.invokeLater(() -> {
                JFrame loginFrame = new JFrame("FaceSecure 3D: Intelligent Facial Recognition");
                FrontEnd loginPanel = new FrontEnd();

                loginFrame.setContentPane(loginPanel);
                loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                loginFrame.setSize(1366, 730);
                loginFrame.setLocationRelativeTo(null);
                loginFrame.setResizable(false);
                loginFrame.setVisible(true);

                System.out.println("✅ Logout successful - Session cleared");
            });
        }
    }

    //Clears all session data for security - COMPLETE CLEANUP
     
    private void clearSessionData() {
        System.out.println("🔒 Clearing session data...");

        // Clear all data structures - CREATE NEW INSTANCES
        if (faces != null) {
            faces.clear();
        }
        faces = new ArrayList<>();

        if (trainingSet != null) {
            trainingSet.clear();
        }
        trainingSet = new ArrayList<>();

        // Clear feature space completely
        if (featureSpace != null) {
            featureSpace.clear();
        }
        featureSpace = new FeatureSpace();

        // Reset eigenfaces completely
        if (eigenFaces != null) {
            eigenFaces.reset();
        }
        eigenFaces = new TSCD();

        // Clear browser and UI
        if (faceBrowser != null) {
            faceBrowser.empty();
        }
        faceBrowser = new FaceBrowser();

        // Reset all references
        classification = null;
        lastFeatureVector = null;
        faceCandidate = null;
        averageFaceIcon = null;
        averageFaceLabel = null;

        // Reset button states
        if (loadImageButton != null)
            loadImageButton.setEnabled(true);
        if (trainButton != null)
            trainButton.setEnabled(false);
        if (cropImageButton != null)
            cropImageButton.setEnabled(false);
        if (probeButton != null)
            probeButton.setEnabled(false);
        if (displayFeatureSpaceButton != null)
            displayFeatureSpaceButton.setEnabled(false);

        // Clear UI components
        if (main != null) {
            main.removeAll();
            main.revalidate();
            main.repaint();
        }

        // Force garbage collection to clear memory
        System.gc();

        updateStatus("🔒 Session cleared - All data removed");
        System.out.println("✅ Session data cleared successfully");
    }

    //Displays the feature space visualization for the last identified face

    private void displayFeatureSpace() {
        if (lastFeatureVector == null) {
            showModernMessage("No feature data available. Please identify a face first.");
            return;
        }

        double[][] features = featureSpace.get3dFeatureSpace(lastFeatureVector);
        if (features == null || features.length == 0) {
            showModernMessage("No feature space data available.");
            return;
        }

        FeatureSpaceVisualizer visualizer = new FeatureSpaceVisualizer(features, classification);
        visualizer.setVisible(true);
    }

    //Shows a modern message dialog with the given message

    private void showModernMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    //Handles the probe button action for face identification

    private void probe() {
        if (!eigenFaces.isTrained()) {
            showModernMessage("Please train the system first by computing eigen vectors.");
            return;
        }

        try {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setDialogTitle("Select Face Image to Identify");

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                long startTime = System.currentTimeMillis();
                updateStatus("Loading and processing image...");
                File file = fc.getSelectedFile();
                try {
                    Face f = new Face(file);
                    f.load(true);

                    // Check if face was properly loaded
                    if (f.getPicture() == null || f.getPicture().getImage() == null) {
                        showModernMessage("Error: Failed to load the probe image properly.");
                        return;
                    }

                    processFaceRecognition(f);
                    displayResults(startTime);
                } catch (MalformedURLException e) {
                    showModernMessage("Error loading image: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            showModernMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Processes face recognition for the given face 

    private void processFaceRecognition(Face f) {
        try {
            // Ensure face is properly loaded
            if (f.getPicture() == null || f.getPicture().getImage() == null) {
                showModernMessage("Error: Failed to load the probe image properly.");
                classification = "Error";
                return;
            }

            // Extract features
            double[] rslt = eigenFaces.getEigenFaces(f.getPicture(), NUM_EIGEN_VECTORS);

            // Create feature vector
            lastFeatureVector = new FeatureVector();
            lastFeatureVector.setFeatureVector(rslt);
            lastFeatureVector.setFace(f);

            // Perform classification with improved k-NN
            classification = featureSpace.knn(FeatureSpace.EUCLIDEAN_DISTANCE,
                    lastFeatureVector, CLASSIFICATION_THRESHOLD);

            // Handle unknown faces properly
            if (classification == null || "Unknown".equals(classification)) {
                classification = "No Match Found";
                updateStatus("⚠️ No matching face found in database");
                // Don't display face candidate for unmatched faces
            } else {
                // Only show face candidate if there's a match
                displayFaceCandidate(f);
                updateStatus("✅ Match found: " + classification);
            }

            // Always enable the chart button so user can see the feature space
            displayFeatureSpaceButton.setEnabled(true);

        } catch (Exception e) {
            showModernMessage("Recognition error: " + e.getMessage());
            classification = "Error";
            e.printStackTrace();
        }
    }

    //Displays the face candidate in the right panel

    private void displayFaceCandidate(Face f) {
        if (faceCandidate == null) {
            faceCandidate = new FaceItem(f);
            JPanel rightPanel = (JPanel) this.getComponent(1);

            JPanel candidatePanel = new JPanel(new BorderLayout());
            candidatePanel.setBackground(Color.WHITE);
            candidatePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 200, 0), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            candidatePanel.add(faceCandidate, BorderLayout.CENTER);

            rightPanel.add(candidatePanel);
            rightPanel.revalidate();
            rightPanel.repaint();
        } else {
            faceCandidate.setFace(f);
        }
        faceCandidate.setVisible(true);
    }

    //Displays the recognition results in a modern dialog

    private void displayResults(long startTime) {
        long elapsedTime = System.currentTimeMillis() - startTime;

        String message;
        int messageType;
        String title;

        if ("No Match Found".equals(classification) || "Unknown".equals(classification)) {
            message = String.format(
                    "❌ No Match Found!\n\n" +
                            "Time: %.2f seconds\n\n" +
                            "The face is not in the database.\n" +
                            "This person is UNRECOGNIZED.",
                    elapsedTime / 1000.0);
            messageType = JOptionPane.WARNING_MESSAGE;
            title = "No Match - Unknown Face";

        } else if ("Error".equals(classification)) {
            message = "❌ Recognition Error!\n\nPlease try again with a different image.";
            messageType = JOptionPane.ERROR_MESSAGE;
            title = "Recognition Error";

        } else {
            message = String.format(
                    "✅ Recognition Successful!\n\n" +
                            "Time: %.2f seconds\n" +
                            "Match: %s\n\n" +
                            "This person is RECOGNIZED.",
                    elapsedTime / 1000.0, classification);
            messageType = JOptionPane.INFORMATION_MESSAGE;
            title = "Match Found - " + classification;
        }

        JOptionPane.showMessageDialog(this, message, title, messageType);
        updateStatus("Recognition completed in " + elapsedTime + "ms - Result: " + classification);
    }

    //Handles the load image button action to load face images from a folder

    private void loadImage() {
        try {
            faces = new ArrayList<>();
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Select Folder Containing Face Images");

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                setupMainPanel();
                File folder = fc.getSelectedFile();
                try {
                    loadImagesFromFolder(folder);
                    setupFaceBrowser();
                    enableTrainingButtons();
                } catch (MalformedURLException e) {
                    showModernMessage("Error loading images: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            showModernMessage("Error: " + e.getMessage());
        }
    }

    //Sets up the main panel layout with modern design

    private void setupMainPanel() {
        removeAll();

        JPanel headerPanel = createModernHeader();
        add(headerPanel, BorderLayout.NORTH);

        add(main, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        right.setPreferredSize(new Dimension(320, 600));

        // Recreate control panel
        JPanel controlCard = new JPanel();
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));
        controlCard.setBackground(Color.WHITE);
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        controlCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel controlTitle = new JLabel("Face Recognition Controls");
        controlTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        controlTitle.setForeground(Color.BLACK);
        controlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlCard.add(controlTitle);
        controlCard.add(Box.createVerticalStrut(20));

        addModernButtonsToPanel(controlCard);
        right.add(controlCard);

        add(right, BorderLayout.EAST);
        add(createModernStatusBar(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    //Loads images from the selected folder into the system

    private void loadImagesFromFolder(File folder) throws MalformedURLException {
        File[] files = folder
                .listFiles(pathname -> pathname.isFile() && (pathname.getName().toLowerCase().endsWith(".jpg") ||
                        pathname.getName().toLowerCase().endsWith(".png") ||
                        pathname.getName().toLowerCase().endsWith(".jpeg")));

        if (files == null || files.length == 0) {
            showModernMessage("No image files found in selected folder.");
            return;
        }

        trainingSet.clear();
        faceBrowser.empty();

        int count = 0;
        for (File file : files) {
            updateProgress(count, files.length);
            try {
                Face f = new Face(file);
                f.setClassification(folder.getName());
                f.setDescription("Training image - " + folder.getName());
                f.load(true);
                faces.add(f);
                faceBrowser.addFace(f);
                count++;
            } catch (Exception e) {
                System.err.println("Error loading file " + file.getName() + ": " + e.getMessage());
            }
        }

        updateStatus("✅ " + files.length + " files loaded from " + folder.getName());
        resetProgress();
    }

    //Sets up the face browser in the main panel

    private void setupFaceBrowser() {
        faceBrowserScrollPane.setViewportView(faceBrowser);
        faceBrowserScrollPane.setVisible(true);
        main.add(faceBrowserScrollPane, BorderLayout.CENTER);
        main.revalidate();
        main.repaint();
    }

    //Enables the training-related buttons after images are loaded

    private void enableTrainingButtons() {
        trainButton.setEnabled(true);
        cropImageButton.setEnabled(true);
        probeButton.setEnabled(true);
    }

    //Handles the crop button action to crop loaded face images

    private void crop() {
        if (faces.isEmpty()) {
            showModernMessage("No images loaded to crop.");
            return;
        }

        int count = 0;
        for (Face f : faces) {
            updateProgress(count, faces.size());
            try {
                f.load(true);
            } catch (MalformedURLException e) {
                System.err.println("Error cropping image: " + e.getMessage());
            }
            count++;
        }
        resetProgress();
        faceBrowser.refresh();
        updateStatus("✅ Cropping completed for " + faces.size() + " images");
    }

    // Updates the progress bar with the current progress

    private void updateProgress(int count, int total) {
        int val = (count * 100) / total;
        statusBar.setValue(val);
        statusBar.setString(val + "% - Processing...");
        statusBar.repaint();
    }

    // Resets the progress bar to initial state

    private void resetProgress() {
        statusBar.setValue(0);
        statusBar.setString("Ready");
        statusBar.repaint();
    }

    // Updates the status bar with a custom message

    private void updateStatus(String message) {
        statusBar.setString(message);
        statusBar.repaint();
    }

    //Handles the train button action to compute eigen vectors and train the system

    private void train() {
        if (faces.isEmpty()) {
            showModernMessage("No images loaded for training.");
            return;
        }

        // Enhanced validation with better error reporting
        int expectedPixels = faces.get(0).getPicture().getImagePixels().length;
        System.out.println("Expected pixels per image: " + expectedPixels);

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.get(i);

            // Check classification
            if (face.getClassification() == null || face.getClassification().trim().isEmpty()) {
                showModernMessage("Error: Image " + (i + 1) + " has no classification!\n" +
                        "File: " + face.getFile().getName());
                return;
            }

            // Check dimensions
            int actualPixels = face.getPicture().getImagePixels().length;
            System.out.println(
                    "Image " + (i + 1) + " (" + face.getClassification() + "): " + actualPixels + " pixels");

            if (actualPixels != expectedPixels) {
                showModernMessage("Image " + (i + 1) + " has different dimensions.\n" +
                        "Expected: " + expectedPixels + " pixels\n" +
                        "Got: " + actualPixels + " pixels\n" +
                        "File: " + face.getFile().getName() + "\n\n" +
                        "Please ensure all images are the same size after cropping.");
                return;
            }
        }

        final ProgressTracker progress = new ProgressTracker();
        Runnable calc = () -> {
            try {
                // Process training set
                eigenFaces.processTrainingSet(faces.toArray(new Face[0]), progress);

                // Build feature space
                for (Face f : faces) {
                    double[] rslt = eigenFaces.getEigenFaces(f.getPicture(), NUM_EIGEN_VECTORS);
                    featureSpace.insertIntoDatabase(f, rslt);

                    FeatureVector fv = new FeatureVector();
                    fv.setFeatureVector(rslt);
                    fv.setFace(f);
                    trainingSet.add(fv);
                }

                // Display statistics
                String stats = featureSpace.getStatistics();
                System.out.println(stats);

                SwingUtilities.invokeLater(() -> {
                    updateStatus("✅ Training completed. " + trainingSet.size() + " faces in feature space.");
                    showModernMessage("Training Complete!\n\n" + stats);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showModernMessage("Training failed: " + e.getMessage() +
                            "\n\nCheck that all images have the same dimensions.");
                    e.printStackTrace();
                });
            }
        };

        progress.run(this, calc, "Training Face Recognition System");
    }

    //Gets the average face image as a BufferedImage

    public BufferedImage getAverageFaceImage() {
        return CreateImageFromMatrix(eigenFaces.getAverageFace().getRowPackedCopy(), IDEAL_IMAGE_SIZE.width);
    }

    //Creates a BufferedImage from a 1D double array representing grayscale pixel values

    public static BufferedImage CreateImageFromMatrix(double[] img, int width) {
        if (img == null || width == 0) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
        }

        int height = img.length / width;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < img.length; i++) {
            int x = i % width;
            int y = i / width;
            int gray = (int) img[i];
            gray = Math.max(0, Math.min(255, gray));
            bi.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
        }
        return bi;
    }

    //ProgressTracker class to manage progress updates during long operations

    public static class ProgressTracker {
        private ProgressMonitor progressMonitor;
        private Timer timer;
        private String sProgress;
        private boolean bFinished;

        public void advanceProgress(final String message) {
            sProgress = message;
            if (progressMonitor != null) {
                progressMonitor.setProgress(1);
                progressMonitor.setNote(sProgress);
            }
        }

        private class TimerListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (progressMonitor != null) {
                    progressMonitor.setProgress(1);
                    progressMonitor.setNote(sProgress);
                    if (progressMonitor.isCanceled() || bFinished) {
                        timer.stop();
                    }
                }
            }
        }

        public void run(JComponent parent, final Runnable calc, String title) {
            progressMonitor = new ProgressMonitor(parent, title, "", 0, 100);
            timer = new Timer(PROGRESS_TIMER_DELAY, new TimerListener());
            bFinished = false;
            sProgress = "Starting...";

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    calc.run();
                    return null;
                }

                @Override
                protected void done() {
                    bFinished = true;
                    if (progressMonitor != null) {
                        progressMonitor.close();
                    }
                }
            };
            worker.execute();
            timer.start();
        }

        public void finished() {
            bFinished = true;
            if (progressMonitor != null) {
                progressMonitor.close();
            }
        }
    }

    //Main method to launch the application
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("FaceSecure 3D: Intelligent Facial Recognition");
            frame.setDefaultLookAndFeelDecorated(true);

            Main mainPanel = new Main();
            frame.setContentPane(mainPanel);

            frame.setSize(1200, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}