package edu.emory.clir.clearnlp.component;

import edu.emory.clir.clearnlp.classification.vector.AbstractFeatureVector;

public interface Cluster {
	int getID();
	AbstractFeatureVector getCenter();
	AbstractFeatureVector getRadius();
	String toString();
	boolean isConverged();
}
