package src;
public interface DistanceMeasure {
    
    //Calculating the distance between two feature vectors.

    double calculateDistance(FeatureVector vector1, FeatureVector vector2);
}