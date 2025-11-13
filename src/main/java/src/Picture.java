package src;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import javax.swing.JComponent;

public class Picture extends JComponent {
    
    private static final long serialVersionUID = 1L;
    private static final double COLOR_CHANNEL_WEIGHT = 3.0;
    
    private BufferedImage image;

    // Constructor to create a Picture with a given image

    public Picture(BufferedImage image) {
        this.image = image;
    }
    
    // Gets the current image

    public BufferedImage getImage() {
        return image;
    }
    
    // Sets a new image

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }
    
    @Override
    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }
    
    //  Gets the width of the image.

    public int getWidth() {
        return image != null ? image.getWidth() : 0;
    }
    
    // Gets the height of the image.

    public int getHeight() {
        return image != null ? image.getHeight() : 0;
    }
    
    // Gets the grayscale pixel values of the image.

    public double[] getImagePixels() {
        if (image == null) {
            return new double[0];
        }
        
        int width = image.getWidth(this);
        int height = image.getHeight(this);
        int[] pixels = new int[width * height];
        
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for pixels: " + e.getMessage());
            return new double[0];
        }
        
        if ((pixelGrabber.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("Image fetch aborted or errored");
            return new double[0];
        }

        double[] grayscalePixels = new double[width * height];
        ColorModel colorModel = pixelGrabber.getColorModel();
        
        for (int i = 0; i < grayscalePixels.length; i++) {
            int red = colorModel.getRed(pixels[i]);
            int green = colorModel.getGreen(pixels[i]);
            int blue = colorModel.getBlue(pixels[i]);
            grayscalePixels[i] = (red + green + blue) / COLOR_CHANNEL_WEIGHT;
        }
        
        return grayscalePixels;
    }
    
    // Gets the color pixel values of the image.

    public double[] getImageColorPixels() {
        if (image == null) {
            return new double[0];
        }
        
        int width = image.getWidth(this);
        int height = image.getHeight(this);
        int[] pixels = new int[width * height];
        
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for pixels: " + e.getMessage());
            return new double[0];
        }
        
        if ((pixelGrabber.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("Image fetch aborted or errored");
            return new double[0];
        }

        double[] colorPixels = new double[width * height];
        
        for (int i = 0; i < colorPixels.length; i++) {
            colorPixels[i] = pixels[i];
        }
        
        return colorPixels;
    }
    
    // Crops the image to the specified boundaries and displays it.

    public void cropAndDisplay(int[] resultPixels, int width, int height,
                             int leftCrop, int rightCrop, int topCrop, int bottomCrop) {
        if (resultPixels == null || width <= 0 || height <= 0) {
            return;
        }
        
        // Ensure crop boundaries are valid
        leftCrop = Math.max(0, leftCrop);
        topCrop = Math.max(0, topCrop);
        rightCrop = Math.min(width, rightCrop);
        bottomCrop = Math.min(height, bottomCrop);
        
        int cropWidth = rightCrop - leftCrop;
        int cropHeight = bottomCrop - topCrop;
        
        if (cropWidth <= 0 || cropHeight <= 0) {
            return;
        }
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, resultPixels, 0, width);
        
        // Only crop if we have valid dimensions
        if (cropWidth > 0 && cropHeight > 0) {
            image = image.getSubimage(leftCrop, topCrop, cropWidth, cropHeight);
        }
        
        repaint();
    }

    // Displays the image from the given pixel array.
    
    public void display(int[] resultPixels, int width, int height) {
        if (resultPixels == null || width <= 0 || height <= 0) {
            return;
        }
        
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, resultPixels, 0, width);
        repaint();
    }
}