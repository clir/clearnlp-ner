package edu.emory.clir.clearnlp.component;

import java.util.List;
import java.util.Map.Entry;

import edu.emory.clir.clearnlp.classification.vector.MultiWeightVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import net.openhft.koloboke.collect.map.IntDoubleMap;

public class KCluster extends AbstractCluster {

	private boolean converged;
	protected IntDoubleMap documentWeights;
	
	public KCluster(SparseFeatureVector center, int id)
	{
		super(center, id);
	}
	
	public KCluster(int id)
	{
		super(id);
	}

	public SparseFeatureVector updateCentroid(List<SparseFeatureVector> points, double convergenceDelta) {
		SparseFeatureVector centroid = updateCentroid(points);
		this.converged = distance(centroid, getCenter()) <= convergenceDelta;
		this.centroid = centroid;
		return centroid;
	}

	private SparseFeatureVector updateCentroid(List<SparseFeatureVector> points) {
		MultiWeightVector centroid = new MultiWeightVector();
		int i;
		float weight;
		for (Entry<Integer, Double> indexWeightPair : documentWeights.entrySet())
		{
			SparseFeatureVector point = points.get(indexWeightPair.getKey());
			for (i=0; i<point.size();i++)
			{
				weight = (float) (point.getWeight(i)*indexWeightPair.getValue());
				centroid.add(point.getIndex(i), weight);
			}
		}
		for(i=0;i<centroid.size();i++)
		{
			centroid.multiply(i, 1/points.size()); // or documentWeights.size()?
		}
		return toSparseVector(centroid);
	}
	
	private double distance(SparseFeatureVector v1, SparseFeatureVector v2)
	{
		double dotProduct=0, mag1=0,mag2=0;
		int i,j,i1,i2, diff;
		for (i=0,j=0;i<v1.size()&&j<v2.size();i++,j++)
		{
			i1 = v1.getIndex(i);
			i2 = v2.getIndex(j);
			diff = i1-i2;
			if (diff == 0)
			{
				dotProduct += v1.getWeight(i)*v2.getWeight(j);
			}
			if (diff > 0)
			{
				i--;
			}
			else if (diff < 0)
			{
				j--;
			}
		}
		for (i=0;i<v1.size();i++)
		{
			mag1+=Math.pow(v1.getWeight(i),2d);
		}
		for (j=0;i<v2.size();j++)
		{
			mag2+=Math.pow(v2.getWeight(j),2d);
		}
		mag1=Math.pow(mag1, .5);
		mag2=Math.pow(mag2, .5);
		
		return dotProduct/(mag1*mag2);
	}
	
	public void addDocument(int document, double weight)
	{
		documentWeights.put(document, weight);
		totalObservations++;
	}
	
	@Override
	public boolean isConverged() {
		return converged;
	}

	public IntDoubleMap getDocumentWeights() {
		return documentWeights;
	}

	public void setDocumentWeights(IntDoubleMap documentWeights)
	{
		this.documentWeights = documentWeights;
	}

	public void reweighDocuments(List<SparseFeatureVector> points)
	{
		double weight;
		for (Entry<Integer, Double> indexWeightPair : documentWeights.entrySet())
		{
			SparseFeatureVector point = points.get(indexWeightPair.getKey());
			weight = 1/distance(point, centroid);
			documentWeights.put(indexWeightPair.getKey().intValue(), weight);
		}
	}

}
