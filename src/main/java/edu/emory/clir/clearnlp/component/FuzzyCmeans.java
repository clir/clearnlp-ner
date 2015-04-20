package edu.emory.clir.clearnlp.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.display.ClustersFilterTest;
import org.apache.mahout.clustering.fuzzykmeans.SoftCluster;

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
import edu.emory.clir.clearnlp.collection.list.IntArrayList;
import edu.emory.clir.clearnlp.collection.pair.DoubleIntPair;
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

	public FuzzyCmeans(SparseModel model, int labelCutoff, int featureCutoff, double fuzziness, int num_clusters, long seed, List<KCluster> prior) {
		super(model, fuzziness, num_clusters, seed);
		m = fuzziness;
		initClusters(prior);
	}

	private void initClusters()
	{
		clusters = initRandomClusters(num_clusters, documents);
		double distance;
		while(checkAllConverged())
		{
		    List<Double> distances = new ArrayList<>();
		    for (KCluster cluster : prior) {
		      clusters.add(cluster);
		      distances.add(cluster.distance(cluster, cluster.centroid));
		    }
			updateWeights(clusters, clusterDistances);
		}
		
	}

	private List<KCluster> initRandomClusters(int num_clusters, List<SparseFeatureVector> points) {
		clusters = IntStream.range(0, num_clusters).mapToObj(i -> new KCluster(i)).collect(Collectors.toList());
		Random rand = new Random(1);
		for (int i=0; i<points.size(); i++)
			clusters.get(rand.nextInt(num_clusters)).addDocument(i,0);
		initCentroids(points, clusters);
		reweighDocuments(points);
		return clusters;
	}
	
	private void reweighDocuments(List<SparseFeatureVector> points)
	{
		for (KCluster cluster : clusters)
		{
			cluster.reweighDocuments(points);
		}
	}

	private void initCentroids(List<SparseFeatureVector> points, List<KCluster> clusters) {
		for (KCluster cluster : clusters)
		{
			centroids.add(cluster.updateCentroid(points, 1));
		}
	}
	
	protected boolean update(IntDoubleMapInstance instance, int averageCount, List<KCluster> prior) {
		updateClusters(instance, instance.getWeightedLabels());
		updateWeights();
		return false;
	}

	private void updateClusters(IntDoubleMapInstance instance, IntDoubleMap intDoubleMap) 
	{
		SparseFeatureVector vector = instance.getFeatureVector();
		int i, vectorIndex, len = vector.size();
		double vectorWeight;
		
		for (i=0; i<len; i++)
		{
			vectorIndex = vector.getIndex(i);
			vectorWeight = MathUtils.sq(vector.getWeight(i));
		}
		while (!checkAllConverged())
		{
			
		}
	}

	private void updateWeights(List<KCluster> clusters, List<Double> clusterDistances) {
		int i;
		double weight;
		for (i=0; i < clusters.size(); i++) {
			weight = updateWeights(clusterDistances.get(i), clusterDistances);
			clusters.get(i).getDocumentWeights().put(i, weight);
		}
	}
	
	private double updateWeights(double clusterDistance, List<Double> clusterDistances) {
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
