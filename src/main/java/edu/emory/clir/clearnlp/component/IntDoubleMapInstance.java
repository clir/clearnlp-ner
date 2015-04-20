package edu.emory.clir.clearnlp.component;

import edu.emory.clir.clearnlp.classification.vector.AbstractFeatureVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import net.openhft.koloboke.collect.map.IntDoubleMap;


public class IntDoubleMapInstance
{
	private IntDoubleMap m_labels;
	private SparseFeatureVector f_vector;
	
	public IntDoubleMapInstance(IntDoubleMap weightedLabels, SparseFeatureVector vector)
	{
		set(weightedLabels, vector);
	}
	
	public IntDoubleMap getWeightedLabels()
	{
		return m_labels;
	}
	
	public SparseFeatureVector getFeatureVector()
	{
		return f_vector;
	}
	
	public void set(IntDoubleMap weightedLabels, SparseFeatureVector vector)
	{
		setWeightedLabels(weightedLabels);
		setFeatureVector(vector);
	}
	
	public void setWeightedLabels(IntDoubleMap weightedLabels)
	{
		m_labels = weightedLabels;
	}
	
	public void setFeatureVector(SparseFeatureVector vector)
	{
		f_vector = vector;
	}
	
	public boolean isWeightedLabel(IntDoubleMap weightedLabels)
	{
		return m_labels.equals(weightedLabels);
	}
	
	public String toString()
	{
		return m_labels + AbstractFeatureVector.DELIM_FEATURE + f_vector.toString();
	}
}