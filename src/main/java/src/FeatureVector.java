package src;

public class FeatureVector {
    private double[] featureVector;
    private int classification;
    private Face face;
    
    //Getting the classification of this feature vector.
    //The classification represents the identity or category assigned to this feature vector.
   
    public int getClassification() {
        return classification;
    }
    
    //Setting the classification of this feature vector.
    //The Classification should be a unique identifier representing the identity or category of the face.
     
    public void setClassification(int classification) {
        this.classification = classification;
    }
    
    //Getting the feature vector array containing the extracted face features.
    //These features are typically eigenface coefficients or other numerical representations of facial characteristics.

    public double[] getFeatureVector() {
        return featureVector;
    }
    
    //Setting the feature vector array containing the extracted face features.
    //The array should contain properly normalized feature values.
    
    public void setFeatureVector(double[] featureVector) {
        this.featureVector = featureVector;
    }
    
    //Getting the associated face object.
    //This provides access to the original face image and its properties.
    public Face getFace() {
        return face;
    }
    
    //Sets the associated face object.
    //Links this feature vector to its source face image.
  
    public void setFace(Face face) {
        this.face = face;
    }
}