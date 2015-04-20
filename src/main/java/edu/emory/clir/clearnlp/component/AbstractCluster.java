package edu.emory.clir.clearnlp.component;

import java.util.List;

import edu.emory.clir.clearnlp.classification.vector.MultiWeightVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import edu.emory.clir.clearnlp.collection.list.FloatArrayList;
import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import net.openhft.koloboke.collect.map.IntDoubleMap;

abstract public class AbstractCluster implements Cluster
{
	protected int id;
	protected long numObservations;
	protected long totalObservations;
	protected SparseFeatureVector centroid;
	protected SparseFeatureVector radius;
	protected IntDoubleMap documentWeights;

	
	protected AbstractCluster(SparseFeatureVector point, int id)
	{
		this.numObservations = 0l;
		this.totalObservations = 0l;
		this.centroid = point;
		this.radius = new SparseFeatureVector(true);
		this.id = id;
	}

	protected SparseFeatureVector toSparseVector(MultiWeightVector point) {
		int i;
		double weight;
		FloatArrayList weights = point.cloneWeights();
		SparseFeatureVector sparseVector = new SparseFeatureVector(true);
		for (i=0;i<point.size();i++)
		{
			weight = weights.get(i);
			if (weight > 0)
				sparseVector.addFeature(i, weight);
		}
		return sparseVector;
	}
	
	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public long getNumObservations() {
		return numObservations;
	}

	public void setNumObservations(long numObservations) {
		this.numObservations = numObservations;
	}

	public long getTotalObservations() {
		return totalObservations;
	}

	public void setTotalObservations(long totalObservations) {
		this.totalObservations = totalObservations;
	}

	public SparseFeatureVector getCenter() {
		return centroid;
	}

	public void setCenter(SparseFeatureVector center) {
		this.centroid = center;
	}

	public SparseFeatureVector getRadius() {
		return radius;
	}

	public void setRadius(SparseFeatureVector radius) {
		this.radius = radius;
	}

	public IntDoubleMap getDocumentWeights() {
		return documentWeights;
	}

	public void setDocumentWeights(IntDoubleMap documentWeights) {
		this.documentWeights = documentWeights;
	}
}
