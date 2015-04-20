package edu.emory.clir.clearnlp.component;

import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.classification.configuration.AbstractTrainerConfiguration;
import edu.emory.clir.clearnlp.classification.model.AbstractModel;
import edu.emory.clir.clearnlp.classification.model.SparseModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;

public class FuzzyCmeansClusterer extends AbstractClustererOnline {

	@Option(name="-a", usage="the learning rate (default: 0.01)", required=false, metaVar="<double>")
	private double d_alpha = 0.01;
	@Option(name="-r", usage="the initial number of clusters (default: 1)", required=false, metaVar="<int>")
	private int num_clusters   = 1;
	@Option(name="-b", usage="the seed (default: 0.0)", required=false, metaVar="<long>")
	private long seed  = 0;
	
	public FuzzyCmeansClusterer(String[] args) {
		super(args);
	}

	@Override
	protected AbstractTrainerConfiguration createTrainConfiguration() {
		return new FuzzyKmeansTrainerConfiguration(i_vectorType, b_binary, i_labelCutoff, i_featureCutoff, i_numberOfThreads, d_alpha, num_clusters, seed);
	}


	@Override
	protected AbstractTrainer getTrainer(AbstractTrainerConfiguration trainConfiguration, AbstractModel<?, ?> model) {
		FuzzyKmeansTrainerConfiguration c = (FuzzyKmeansTrainerConfiguration) trainConfiguration;
		
		return new FuzzyCmeans((SparseModel)model, c.getLabelCutoff(), c.getFeatureCutoff(), c.getFuzziness(), c.getNum_clusters(), c.getSeed());
	}
	
	public static void main(String args[])
	{
		new FuzzyCmeansClusterer(args);
	}

}
