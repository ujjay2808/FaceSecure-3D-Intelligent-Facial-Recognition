package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.ImageIcon;

public class Face {
    private static final int WINDOW_SIZE = 5;
    private static final double SKIN_COLOR_HUE_MIN = 70.0;
    private static final double SKIN_COLOR_HUE_MAX = 150.0;
    private static final int BLACK_COLOR_THRESHOLD = 50;
    private static final int SCALE_FACTOR = 320;
    private static final int BORDER_MARGIN = 2;
    private static final Dimension IDEAL_IMAGE_SIZE = new Dimension(48, 64);
    private static final long serialVersionUID = 1L;

    private File file;
    private Picture picture;
    private String classification;
    private String description;

    // Creating a new Face instance from a file

    public Face(File file) throws MalformedURLException {
        this.classification = null;
        this.description = "";
        this.file = file;
        load(false);
    }

    // Getting the file associated with this face

    public File getFile() {
        return file;
    }

    // Getting the picture associated with this face

    public Picture getPicture() {
        return picture;
    }

    // Getting the classification of this face

    public String getClassification() {
        return classification;
    }

    // Setting the classification of this face

    public void setClassification(String classification) {
        this.classification = classification;
    }

    // Getting the description of this face

    public String getDescription() {
        return description;
    }

    // Setting the description of this face

    public void setDescription(String description) {
        this.description = description;
    }

    // It loads the face image from the file

    public void load(boolean crop) throws MalformedURLException {
        Image image = new ImageIcon(file.toURI().toURL()).getImage();
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        if (crop) {
            bufferedImage = recognize(recognize(bufferedImage));
        }

        // Add image normalization for consistent processing

        bufferedImage = normalizeImage(bufferedImage);

        BufferedImage resizedImage = resizeImage(bufferedImage, IDEAL_IMAGE_SIZE, true);
        picture = new Picture(resizedImage);
    }

    private BufferedImage normalizeImage(BufferedImage image) {
        BufferedImage normalized = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = normalized.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // Converting to grayscale for consistent feature extraction

        BufferedImage grayscale = new BufferedImage(
                normalized.getWidth(), normalized.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D gGray = grayscale.createGraphics();
        gGray.drawImage(normalized, 0, 0, null);
        gGray.dispose();

        return grayscale;
    }

    // It recognizes and extracts the face region from the image

    public BufferedImage recognize(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        int scale = ((height + width) / SCALE_FACTOR);
        int leftCrop = width / 2;
        int rightCrop = width / 2;
        int topCrop = height / 2;
        int bottomCrop = height / 2;

        picture = new Picture(image);

        double[] originalPixels = picture.getImageColorPixels().clone();
        int[] originalPixelsInt = new int[originalPixels.length];
        for (int i = 0; i < originalPixels.length; i++) {
            originalPixelsInt[i] = (int) originalPixels[i];
        }

        double[] pixels = picture.getImageColorPixels().clone();
        double[] processedPixels = pixels.clone();

        processSkinColorDetection(width, height, pixels, processedPixels);
        detectFaceBoundaries(width, height, processedPixels, leftCrop, rightCrop, topCrop, bottomCrop);

        picture.cropAndDisplay(originalPixelsInt, width, height, leftCrop, rightCrop, topCrop, bottomCrop);

        return picture.getImage();
    }

    // Processing the image to detect skin color regions

    private void processSkinColorDetection(int width, int height, double[] pixels, double[] processedPixels) {
        for (int i = BORDER_MARGIN; i < width - BORDER_MARGIN; i++) {
            for (int j = BORDER_MARGIN; j < height - BORDER_MARGIN; j++) {
                Color color = getColor((int) pixels[i + j * width]);
                float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

                double intensity = (Math.log(color.getRed()) + Math.log(color.getGreen()) + Math.log(color.getBlue()))
                        / 3;
                double redGreen = Math.log(color.getRed()) - Math.log(color.getGreen());
                double blueYellow = Math.log(color.getBlue())
                        - (Math.log(color.getBlue()) + Math.log(color.getRed())) / 2;

                double hue = Math.atan2(redGreen, blueYellow) * (180 / Math.PI);

                if ((hue >= SKIN_COLOR_HUE_MIN && hue <= SKIN_COLOR_HUE_MAX)) {
                    processedPixels[i + width * j] = color.getRGB();
                } else {
                    clearNeighborhood(processedPixels, width, i, j);
                }
            }
        }
    }

    // Clearing the neighborhood of a pixel by setting it to black

    private void clearNeighborhood(double[] pixels, int width, int x, int y) {
        for (int i = x - BORDER_MARGIN; i <= x + BORDER_MARGIN; i++) {
            for (int j = y - BORDER_MARGIN; j <= y + BORDER_MARGIN; j++) {
                pixels[i + width * j] = Color.BLACK.getRGB();
            }
        }
    }

    // Detecting the boundaries of the face in the image

    private void detectFaceBoundaries(int width, int height, double[] pixels,
            int leftCrop, int rightCrop, int topCrop, int bottomCrop) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color = getColor((int) pixels[i + width * j]);
                if (color.getRGB() > (Color.BLACK.getRGB() + BLACK_COLOR_THRESHOLD)) {
                    if (i < leftCrop)
                        leftCrop = i;
                    if (i > rightCrop)
                        rightCrop = i;
                    if (j < topCrop)
                        topCrop = j;
                    if (j > bottomCrop)
                        bottomCrop = j;
                }
            }
        }
    }

    // Applying a median filter to the image

    public void medianFilter(double[] pixels, int windowSize) {
        int height = picture.getHeight();
        int width = picture.getWidth();

        for (int i = windowSize / 2; i < width - windowSize / 2; i++) {
            for (int j = windowSize / 2; j < height - windowSize / 2; j++) {
                ArrayList<Integer> numbers = new ArrayList<>();
                for (int l = -windowSize / 2; l <= windowSize / 2; l++) {
                    for (int k = -windowSize / 2; k <= windowSize / 2; k++) {
                        numbers.add((int) pixels[(i + k) + width * (j + l)]);
                    }
                }
                Collections.sort(numbers);
                pixels[i + width * j] = numbers.get(numbers.size() / 2);
            }
        }
    }

    // Extracting RGB components from an integer color values

    public Color getColor(int rgb) {
        int red = (rgb & (255 << 16)) >> 16;
        int green = (rgb & (255 << 8)) >> 8;
        int blue = (rgb & 255);
        return new Color(red, green, blue);
    }

    // Resizing an image to fit within the specified dimensions

    private BufferedImage resizeImage(Image original, Dimension box, boolean fitOutside) {
        BufferedImage scaledImage = new BufferedImage(box.width, box.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = scaledImage.createGraphics();
        graphics.setBackground(Color.BLACK);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        float xRatio = (float) box.width / (float) original.getWidth(null);
        float yRatio = (float) box.height / (float) original.getHeight(null);
        float ratio = fitOutside ? Math.max(xRatio, yRatio) : Math.min(xRatio, yRatio);

        int newWidth = Math.round(original.getWidth(null) * ratio);
        int newHeight = Math.round(original.getHeight(null) * ratio);

        graphics.drawImage(original, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return scaledImage;
    }
}