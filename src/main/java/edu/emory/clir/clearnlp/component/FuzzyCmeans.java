package edu.emory.clir.clearnlp.component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.mahout.clustering.display.ClustersFilterTest;

import edu.emory.clir.clearnlp.classification.configuration.AbstractTrainerConfiguration;
import edu.emory.clir.clearnlp.classification.instance.IntInstance;
import edu.emory.clir.clearnlp.classification.model.AbstractModel;
import edu.emory.clir.clearnlp.classification.model.SparseModel;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;
import edu.emory.clir.clearnlp.classification.vector.MultiWeightVector;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;
import edu.emory.clir.clearnlp.classification.vector.StringFeatureVector;
import edu.emory.clir.clearnlp.collection.list.DoubleArrayList;
import edu.emory.clir.clearnlp.component.evaluation.AbstractEval;
import edu.emory.clir.clearnlp.component.state.AbstractState;
import edu.emory.clir.clearnlp.feature.AbstractFeatureExtractor;
import edu.emory.clir.clearnlp.util.MathUtils;
import net.openhft.koloboke.collect.map.DoubleIntMap;
import net.openhft.koloboke.collect.map.IntDoubleMap;

public class FuzzyCmeans extends AbstractFuzzyCmeans
{
	private static final double MINIMAL_VALUE = 0.0000000001;
	private double m = 2.0;

//	public FuzzyCmeans(SparseModel model, int labelCutoff, int featureCutoff, double fuzziness, int num_clusters, long seed) {
//		super(model, labelCutoff, featureCutoff, fuzziness, num_clusters, seed);
//		m = fuzziness;
//	}
	public FuzzyCmeans(SparseModel model, int labelCutoff, int featureCutoff, double fuzziness, int num_clusters, long seed) {
		super(model, fuzziness, num_clusters, seed);
		m = fuzziness;
		initRandomClusters();
	}

	private void initRandomClusters()
	{
		clusters = initClusters(points, num_clusters);
		List<KCluster> prevCentroids, currCentroids = 
		double distance;
		int iter = 1;
		do
		{
			clusters = maximize(points, centroids, num_clusters);
		}
		updateClusters();
		
	}

	private List<KCluster> maximize(List<SparseFeatureVector> points, List<SparseFeatureVector> centroids, int num_clusters) {
		
		return null;
	}

	private List<KCluster> initCentroids(List<SparseFeatureVector> points, int num_clusters) {
		// TODO Auto-generated method stub
		return null;
	}

	protected boolean update(IntDoubleMapInstance instance, int averageCount) {
		updateClusters(instance, instance.getWeightedLabels());
		return false;
	}

	private void updateClusters(IntDoubleMapInstance instance, IntDoubleMap intDoubleMap) {
		
		while (!checkAllConverged())
		{
			
		}
		SparseFeatureVector vector = instance.getFeatureVector();
		int i, vectorIndex, len = vector.size();
		double vectorWeight;
		
		for (i=0; i<len; i++)
		{
			vectorIndex = vector.getIndex(i);
			vectorWeight = MathUtils.sq(vector.getWeight(i));
			updateClusters();
		}
	}

	private void computeProbabilities(List<KCluster> clusters, List<Double> clusterDistances) {
		int i;
		double weight;
		for (i=0; i < clusters.size(); i++) {
			weight = computeProbabilities(clusterDistances.get(i), clusterDistances);
			clusters.get(i).getDocumentWeights().put(i, weight);
		}
	}
	
	
	private double computeProbabilities(double clusterDistance, List<Double> clusterDistances) {
		if (clusterDistance == 0)
			clusterDistance = MINIMAL_VALUE;
		
		double denom = 0d;
		for (double distance : clusterDistances)
		{
			if (distance == 0);
				distance = MINIMAL_VALUE;
				denom += Math.pow(clusterDistance/distance, 2.0/(m-1));
		}
		return 1d/denom;
	}

	@Override
	public String trainerInfo() {
		return getTrainerInfo("FuzzyK");
	}

	@Override
	protected boolean update(IntInstance instance, int averageCount) {
		// not applicable
		return false;
	}
}
