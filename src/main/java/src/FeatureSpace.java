package src;

import java.util.ArrayList;
import java.util.Arrays;

public class FeatureSpace {

    //Euclidean distance measure implementation
     
    public static final DistanceMeasure EUCLIDEAN_DISTANCE = new DistanceMeasure() {
        @Override
        public double calculateDistance(FeatureVector fv1, FeatureVector fv2) {
            double sum = 0;
            for (int i = 0; i < fv1.getFeatureVector().length; i++) {
                double diff = fv1.getFeatureVector()[i] - fv2.getFeatureVector()[i];
                sum += diff * diff;
            }
            return Math.sqrt(sum);
        }
    };

    private final ArrayList<FeatureVector> featureSpace;
    private final ArrayList<String> classifications;
    
    // Statistics for dynamic thresholding

    private double minIntraClassDistance = Double.MAX_VALUE;
    private double maxIntraClassDistance = 0;
    private double avgIntraClassDistance = 0;

    public FeatureSpace() {
        featureSpace = new ArrayList<>();
        classifications = new ArrayList<>();
    }

    //Inserting a face and its feature vector into the database
     
    public void insertIntoDatabase(Face face, double[] featureVector) {
        if (face.getClassification() == null) {
            System.out.println("WARNING: Skipping face with null classification: " + face.getFile().getName());
            return;
        }

        if (!classifications.contains(face.getClassification())) {
            classifications.add(face.getClassification());
        }
        int clas = classifications.indexOf(face.getClassification());

        FeatureVector obj = new FeatureVector();
        obj.setClassification(clas);
        obj.setFace(face);
        obj.setFeatureVector(featureVector);

        featureSpace.add(obj);
        
        // Update statistics after adding

        updateDistanceStatistics();
    }

    // Calculating intra-class distances to establish dynamic thresholds
     
    private void updateDistanceStatistics() {
        if (featureSpace.size() < 2) return;
        
        double totalDistance = 0;
        int count = 0;
        minIntraClassDistance = Double.MAX_VALUE;
        maxIntraClassDistance = 0;
        
        // Calculating distances between faces of the SAME class

        for (int i = 0; i < featureSpace.size(); i++) {
            for (int j = i + 1; j < featureSpace.size(); j++) {
                if (featureSpace.get(i).getClassification() == 
                    featureSpace.get(j).getClassification()) {
                    
                    double dist = EUCLIDEAN_DISTANCE.calculateDistance(
                        featureSpace.get(i), featureSpace.get(j));
                    
                    totalDistance += dist;
                    count++;
                    
                    if (dist < minIntraClassDistance) minIntraClassDistance = dist;
                    if (dist > maxIntraClassDistance) maxIntraClassDistance = dist;
                }
            }
        }
        
        if (count > 0) {
            avgIntraClassDistance = totalDistance / count;
            System.out.println("=== Distance Statistics ===");
            System.out.println("Min intra-class distance: " + minIntraClassDistance);
            System.out.println("Max intra-class distance: " + maxIntraClassDistance);
            System.out.println("Avg intra-class distance: " + avgIntraClassDistance);
        }
    }

    //k-nearest neighbor classification with dynamic thresholding
     
    public String knn(DistanceMeasure measure, FeatureVector fv, int k) {
        FaceDistancePair[] distances = orderByDistance(measure, fv);
        if (distances.length == 0) {
            return "Unknown";
        }

        // Adjust k if needed

        if (k > distances.length) {
            k = distances.length;
        }

        // CRITICAL FIX: Dynamic threshold based on training data statistics

        double distanceThreshold;
        
        if (avgIntraClassDistance > 0) {

            // Use 2x the average intra-class distance as threshold
            // This is more robust than a fixed value

            distanceThreshold = avgIntraClassDistance * 2.5;
            
            // Ensure minimum threshold

            distanceThreshold = Math.max(distanceThreshold, 1500.0);
        } else {

            // Fallback for insufficient training data

            distanceThreshold = 2500.0;
        }

        double closestDistance = distances[0].getDist();
        
        System.out.println("=== Recognition Analysis ===");
        System.out.println("Closest match distance: " + closestDistance);
        System.out.println("Threshold: " + distanceThreshold);
        System.out.println("Classification: " + distances[0].getFace().getClassification());
        
        // Show top 3 matches for debugging

        System.out.println("Top 3 matches:");
        for (int i = 0; i < Math.min(3, distances.length); i++) {
            System.out.println("  " + (i+1) + ". " + 
                distances[i].getFace().getClassification() + 
                " - Distance: " + distances[i].getDist());
        }

        // STRICT THRESHOLD CHECK - Must pass to be recognized

        if (closestDistance > distanceThreshold) {
            System.out.println("REJECTED: Distance exceeds threshold");
            return "Unknown";
        }

        // Count votes among k nearest neighbors

        java.util.HashMap<String, Integer> voteCount = new java.util.HashMap<>();
        java.util.HashMap<String, Double> voteDistances = new java.util.HashMap<>();
        
        for (int i = 0; i < k; i++) {
            String classification = distances[i].getFace().getClassification();
            double distance = distances[i].getDist();
            
            voteCount.put(classification, voteCount.getOrDefault(classification, 0) + 1);
            
            // Track minimum distance for each class

            if (!voteDistances.containsKey(classification) || 
                distance < voteDistances.get(classification)) {
                voteDistances.put(classification, distance);
            }
        }

        // Find classification with most votes

        String bestClassification = null;
        int maxVotes = 0;
        double bestDistance = Double.MAX_VALUE;
        
        for (java.util.Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > maxVotes || 
                (entry.getValue() == maxVotes && 
                 voteDistances.get(entry.getKey()) < bestDistance)) {
                maxVotes = entry.getValue();
                bestClassification = entry.getKey();
                bestDistance = voteDistances.get(entry.getKey());
            }
        }

        // MAJORITY VOTE REQUIREMENT - More than half must agree

        if (maxVotes <= k / 2) {
            System.out.println("REJECTED: No clear majority in k-NN votes");
            return "Unknown";
        }

        System.out.println("ACCEPTED: " + bestClassification + 
                         " with " + maxVotes + "/" + k + " votes");
        return bestClassification != null ? bestClassification : "Unknown";
    }

    //Orders all faces by their distance to a probe feature vector
     
    public FaceDistancePair[] orderByDistance(DistanceMeasure measure, FeatureVector fv) {
        FaceDistancePair[] distances = new FaceDistancePair[featureSpace.size()];
        for (int i = 0; i < featureSpace.size(); i++) {
            distances[i] = new FaceDistancePair();
            distances[i].setFace(featureSpace.get(i).getFace());
            distances[i].setDist(measure.calculateDistance(fv, featureSpace.get(i)));
        }
        Arrays.sort(distances, (a, b) -> Double.compare(a.getDist(), b.getDist()));
        return distances;
    }

    //Inner class representing a face and its distance to a probe vector
     
    public static class FaceDistancePair {
        private Face face;
        private double dist;

        public Face getFace() {
            return face;
        }

        public void setFace(Face face) {
            this.face = face;
        }

        public double getDist() {
            return dist;
        }

        public void setDist(double dist) {
            this.dist = dist;
        }
    }

    public double[][] get3dFeatureSpace() {
        if (featureSpace.isEmpty()) {
            return new double[0][3];
        }

        double[][] features = new double[featureSpace.size()][3];
        for (int i = 0; i < featureSpace.size(); i++) {
            double[] featureVector = featureSpace.get(i).getFeatureVector();
            features[i][0] = featureVector.length > 0 ? featureVector[0] : 0;
            features[i][1] = featureVector.length > 1 ? featureVector[1] : 0;
            features[i][2] = featureVector.length > 2 ? featureVector[2] : 0;
        }

        return normalizeFeatures(features);
    }

    public double[][] get3dFeatureSpace(FeatureVector probe) {
        if (probe == null) {
            return get3dFeatureSpace();
        }

        double[][] features = new double[featureSpace.size() + 1][3];

        for (int i = 0; i < featureSpace.size(); i++) {
            double[] featureVector = featureSpace.get(i).getFeatureVector();
            features[i][0] = featureVector.length > 0 ? featureVector[0] : 0;
            features[i][1] = featureVector.length > 1 ? featureVector[1] : 0;
            features[i][2] = featureVector.length > 2 ? featureVector[2] : 0;
        }

        double[] probeVector = probe.getFeatureVector();
        features[featureSpace.size()][0] = probeVector.length > 0 ? probeVector[0] : 0;
        features[featureSpace.size()][1] = probeVector.length > 1 ? probeVector[1] : 0;
        features[featureSpace.size()][2] = probeVector.length > 2 ? probeVector[2] : 0;

        return normalizeFeatures(features);
    }

    private double[][] normalizeFeatures(double[][] features) {
        if (features.length == 0) return features;

        double max0 = features[0][0], max1 = features[0][1], max2 = features[0][2];
        double min0 = features[0][0], min1 = features[0][1], min2 = features[0][2];

        for (int i = 1; i < features.length; i++) {
            if (features[i][0] > max0) max0 = features[i][0];
            if (features[i][0] < min0) min0 = features[i][0];
            if (features[i][1] > max1) max1 = features[i][1];
            if (features[i][1] < min1) min1 = features[i][1];
            if (features[i][2] > max2) max2 = features[i][2];
            if (features[i][2] < min2) min2 = features[i][2];
        }

        double range0 = max0 - min0;
        double range1 = max1 - min1;
        double range2 = max2 - min2;

        for (int i = 0; i < features.length; i++) {
            if (range0 > 0) features[i][0] = ((features[i][0] - min0) / range0) * 100;
            if (range1 > 0) features[i][1] = ((features[i][1] - min1) / range1) * 100;
            if (range2 > 0) features[i][2] = ((features[i][2] - min2) / range2) * 100;
        }

        return features;
    }

    public int getFeatureSpaceSize() {
        return featureSpace.size();
    }

    public void clear() {
        featureSpace.clear();
        classifications.clear();
        minIntraClassDistance = Double.MAX_VALUE;
        maxIntraClassDistance = 0;
        avgIntraClassDistance = 0;
    }
    
    //Get statistics for debugging

    public String getStatistics() {
        return String.format(
            "Feature Space: %d faces, %d classes\n" +
            "Min Distance: %.2f, Max Distance: %.2f, Avg Distance: %.2f",
            featureSpace.size(), classifications.size(),
            minIntraClassDistance, maxIntraClassDistance, avgIntraClassDistance
        );
    }
}