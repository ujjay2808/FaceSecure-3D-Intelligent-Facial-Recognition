package src;

import java.util.Arrays;
import src.Main.ProgressTracker;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class TSCD {
    private static final double EIGENVALUE_THRESHOLD = 0.0001;

    private Matrix averageFace;        // Stores the average face useful when probing the database
    private Matrix eigenVectors;       // Stores all the sorted eigen vectors from the training set
    private Matrix eigenValues;        // Stores all the sorted eigen Values from the training set
    private boolean trained;           // Has a training set been provided yet?
    private int numEigenVectors;       // Number of eigen vectors available

    // Processes the training set of faces to compute eigenfaces.

    public void processTrainingSet(Face[] faces, ProgressTracker progress) {
        if (faces == null || faces.length == 0) {
            throw new IllegalArgumentException("No faces provided for training");
        }

        try {
            progress.advanceProgress("Constructing matrix...");
            double[][] dpix = constructImageMatrix(faces);

            progress.advanceProgress("Calculating averages...");
            computeAverageFace(dpix);

            progress.advanceProgress("Computing covariance matrix...");
            Matrix diffMatrix = computeDifferenceMatrix(dpix);

            progress.advanceProgress("Calculating eigenvectors...");
            computeEigenvectors(diffMatrix);

            progress.advanceProgress("Sorting eigenvectors...");
            sortEigenvectors();

            progress.advanceProgress("Extracting eigenvalues...");
            extractEigenvalues(diffMatrix);

            progress.advanceProgress("Normalising eigenvectors...");
            normalizeEigenvectors();

            trained = true;
            progress.advanceProgress("Training completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error in training process: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Training failed: " + e.getMessage(), e);
        } finally {
            progress.finished();
        }
    }

    //  Constructs the image matrix from the training faces.

    private double[][] constructImageMatrix(Face[] faces) {
        if (faces == null || faces.length == 0) {
            throw new IllegalArgumentException("No faces provided for training");
        }
        
        // Get the dimensions from the first face
        double[] firstPixels = faces[0].getPicture().getImagePixels();
        int pixelCount = firstPixels.length;
        int faceCount = faces.length;
        
        System.out.println("Constructing matrix with " + pixelCount + " pixels and " + faceCount + " faces");

        // Verify all images have the same dimensions
        for (int i = 1; i < faces.length; i++) {
            double[] pixels = faces[i].getPicture().getImagePixels();
            if (pixels.length != pixelCount) {
                throw new IllegalArgumentException(
                    "All images must have the same dimensions. " +
                    "Image " + i + " has " + pixels.length + " pixels, expected " + pixelCount
                );
            }
        }
        
        // Create matrix: each column is a face, each row is a pixel
        double[][] dpix = new double[pixelCount][faceCount];
        
        for (int faceIdx = 0; faceIdx < faceCount; faceIdx++) {
            double[] pixels = faces[faceIdx].getPicture().getImagePixels();
            for (int pixelIdx = 0; pixelIdx < pixelCount; pixelIdx++) {
                dpix[pixelIdx][faceIdx] = pixels[pixelIdx];
            }
        }
        
        return dpix;
    }

    // Computes the average face from the training set.

    private void computeAverageFace(double[][] dpix) {
        int pixelCount = dpix.length;
        int faceCount = dpix[0].length;
        
        averageFace = new Matrix(pixelCount, 1);
        
        for (int i = 0; i < pixelCount; i++) {
            double sum = 0;
            for (int j = 0; j < faceCount; j++) {
                sum += dpix[i][j];
            }
            averageFace.set(i, 0, sum / faceCount);
        }
        
        System.out.println("Average face computed with " + pixelCount + " pixels");
    }

    //  Computes the difference matrix by subtracting the average face from each face.

    private Matrix computeDifferenceMatrix(double[][] dpix) {
        Matrix matrix = new Matrix(dpix); // pixels × faces
        int pixelCount = matrix.getRowDimension();
        int faceCount = matrix.getColumnDimension();
        
        Matrix diffMatrix = new Matrix(pixelCount, faceCount);
        
        // Subtract average face from each face
        for (int faceIdx = 0; faceIdx < faceCount; faceIdx++) {
            for (int pixelIdx = 0; pixelIdx < pixelCount; pixelIdx++) {
                double diff = matrix.get(pixelIdx, faceIdx) - averageFace.get(pixelIdx, 0);
                diffMatrix.set(pixelIdx, faceIdx, diff);
            }
        }
        
        System.out.println("Difference matrix computed: " + pixelCount + " × " + faceCount);
        return diffMatrix;
    }

    // Computes the eigenvectors of the covariance matrix.

    private void computeEigenvectors(Matrix A) {
        try {
            int pixelCount = A.getRowDimension();
            int faceCount = A.getColumnDimension();
            
            System.out.println("Computing eigenvectors for matrix: " + pixelCount + " × " + faceCount);
            
            // Use the efficient approach: compute eigenvectors of A^T * A (faces × faces)
            // This is much smaller than A * A^T (pixels × pixels)
            Matrix At = A.transpose(); // faces × pixels
            Matrix L = At.times(A);    // faces × faces
            
            System.out.println("Computing eigen decomposition of " + L.getRowDimension() + " × " + L.getColumnDimension() + " matrix");
            
            EigenvalueDecomposition eigen = L.eig();
            eigenValues = eigen.getD();
            eigenVectors = eigen.getV();
            
            // Convert to eigenvectors of A * A^T using: u_i = A * v_i
            Matrix actualEigenVectors = A.times(eigenVectors); // pixels × faces
            
            // Normalize the eigenvectors
            for (int i = 0; i < actualEigenVectors.getColumnDimension(); i++) {
                Matrix eigenVector = actualEigenVectors.getMatrix(0, actualEigenVectors.getRowDimension() - 1, i, i);
                double norm = eigenVector.normF();
                if (norm > EIGENVALUE_THRESHOLD) {
                    eigenVector.timesEquals(1.0 / norm);
                } else {
                    // Set small eigenvectors to zero
                    eigenVector.timesEquals(0.0);
                }
            }
            
            eigenVectors = actualEigenVectors;
            System.out.println("Eigenvectors computed: " + eigenVectors.getRowDimension() + " × " + eigenVectors.getColumnDimension());
            
        } catch (Exception e) {
            System.err.println("Error in eigen decomposition: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Eigen computation failed: " + e.getMessage(), e);
        }
    }

    // Sorts eigenvectors and eigenvalues in descending order.

    private void sortEigenvectors() {
        if (eigenValues == null || eigenVectors == null) {
            throw new IllegalStateException("Eigenvalues or eigenvectors not computed");
        }
        
        double[] values = diag(eigenValues);
        EigenPair[] pairs = new EigenPair[values.length];
        
        for (int i = 0; i < pairs.length; i++) {
            pairs[i] = new EigenPair();
            pairs[i].index = i;
            pairs[i].value = values[i];
        }

        // Sort in descending order
        Arrays.sort(pairs, (a, b) -> Double.compare(b.value, a.value));

        Matrix sortedEigenvalues = new Matrix(eigenValues.getRowDimension(), eigenValues.getColumnDimension());
        Matrix sortedEigenvectors = new Matrix(eigenVectors.getRowDimension(), eigenVectors.getColumnDimension());

        for (int i = 0; i < pairs.length; i++) {
            sortedEigenvalues.set(i, i, pairs[i].value);
            sortedEigenvectors.setMatrix(0, sortedEigenvectors.getRowDimension() - 1, i, i,
                eigenVectors.getMatrix(0, eigenVectors.getRowDimension() - 1, pairs[i].index, pairs[i].index));
        }

        eigenValues = sortedEigenvalues;
        eigenVectors = sortedEigenvectors;
        
        System.out.println("Eigenvectors sorted. Top eigenvalue: " + (pairs.length > 0 ? pairs[0].value : "N/A"));
    }

    // Extracts and normalizes eigenvalues.

    private void extractEigenvalues(Matrix A) {
        if (eigenValues == null) {
            throw new IllegalStateException("Eigenvalues not computed");
        }
        
        double[] values = diag(eigenValues);
        int faceCount = A.getColumnDimension();
        
        // Normalize eigenvalues
        for (int i = 0; i < values.length; i++) {
            values[i] = Math.max(0, values[i] / (faceCount - 1));
        }
        
        System.out.println("Eigenvalues extracted and normalized. Max value: " + 
                         (values.length > 0 ? values[0] : "N/A"));
    }

    // Normalizes eigenvectors and retains only significant ones.

    private void normalizeEigenvectors() {
        if (eigenVectors == null || eigenValues == null) {
            throw new IllegalStateException("Eigenvectors or eigenvalues not computed");
        }
        
        double[] values = diag(eigenValues);
        numEigenVectors = 0;
        
        // Count significant eigenvectors
        for (int i = 0; i < values.length; i++) {
            if (values[i] >= EIGENVALUE_THRESHOLD) {
                numEigenVectors++;
            }
        }
        
        System.out.println("Found " + numEigenVectors + " significant eigenvectors out of " + values.length);
        
        // Keep only the significant eigenvectors
        if (numEigenVectors > 0) {
            eigenVectors = eigenVectors.getMatrix(0, eigenVectors.getRowDimension() - 1, 0, numEigenVectors - 1);
            eigenValues = eigenValues.getMatrix(0, numEigenVectors - 1, 0, numEigenVectors - 1);
        } else {
            // Keep at least one eigenvector if all are below threshold
            numEigenVectors = 1;
            eigenVectors = eigenVectors.getMatrix(0, eigenVectors.getRowDimension() - 1, 0, 0);
            eigenValues = eigenValues.getMatrix(0, 0, 0, 0);
            System.out.println("No significant eigenvectors found, keeping first eigenvector");
        }
    }

    //  Extracts the eigenface coefficients for a given picture.

    public double[] getEigenFaces(Picture pic, int number) {
        if (!trained) {
            throw new IllegalStateException("System not trained yet");
        }
        
        if (eigenVectors == null || numEigenVectors == 0) {
            throw new IllegalStateException("No eigenvectors available");
        }
        
        if (number > numEigenVectors) {
            number = numEigenVectors;
            System.out.println("Reduced number of eigenvectors to " + number);
        }
        
        if (number <= 0) {
            return new double[0];
        }

        try {
            double[] pixels = pic.getImagePixels();
            
            // Verify the input image has correct dimensions
            if (pixels.length != averageFace.getRowDimension()) {
                throw new IllegalArgumentException(
                    "Input image has " + pixels.length + " pixels, but expected " + 
                    averageFace.getRowDimension() + " pixels. Image dimensions don't match training set."
                );
            }
            
            // Create face vector and subtract average face
            Matrix faceVector = new Matrix(pixels, pixels.length);
            Matrix diff = faceVector.minus(averageFace);
            
            // Project onto eigenface space
            Matrix eigenSubset = eigenVectors.getMatrix(0, eigenVectors.getRowDimension() - 1, 0, number - 1);
            Matrix coefficients = eigenSubset.transpose().times(diff);

            double[] result = new double[number];
            for (int i = 0; i < number; i++) {
                result[i] = coefficients.get(i, 0);
            }
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Error in getEigenFaces: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Feature extraction failed: " + e.getMessage(), e);
        }
    }

    // Helper method to extract diagonal elements from a matrix.

    private double[] diag(Matrix matrix) {
        int minDim = Math.min(matrix.getRowDimension(), matrix.getColumnDimension());
        double[] diagonal = new double[minDim];
        for (int i = 0; i < minDim; i++) {
            diagonal[i] = matrix.get(i, i);
        }
        return diagonal;
    }

    // Helper class to pair eigenvalues with their indices for sorting.

    private static class EigenPair {
        double value;
        int index;
    }

    //  Checks if the system has been trained.

    public boolean isTrained() {
        return trained;
    }

    // Gets the number of significant eigen vectors.

    public int getNumEigenVectors() {
        return numEigenVectors;
    }

    //  Gets the average face matrix.

    public Matrix getAverageFace() {
        return averageFace;
    }

    //  Gets the eigen vectors matrix.
    public Matrix getEigenVectors() {
        return eigenVectors;
    }

    // Gets the eigen values matrix.

    public Matrix getEigenValues() {
        return eigenValues;
    }

    // Resets the training state.
    
    public void reset() {
        averageFace = null;
        eigenVectors = null;
        eigenValues = null;
        trained = false;
        numEigenVectors = 0;
    }
}