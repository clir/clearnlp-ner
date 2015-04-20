package edu.emory.clir.clearnlp.component;

import java.util.List;

import edu.emory.clir.clearnlp.classification.model.SparseModel;
import edu.emory.clir.clearnlp.classification.model.StringModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractOnlineTrainer;
import edu.emory.clir.clearnlp.classification.vector.SparseFeatureVector;

abstract public class AbstractFuzzyCmeans extends AbstractOnlineTrainer
{
	protected List<SparseFeatureVector> points;
	protected double fuzziness;
	protected int num_clusters;
	protected long seed;
	
	public AbstractFuzzyCmeans(SparseModel model, double fuzziness, int num_clusters, long seed) {
		super(model, false);
		init(fuzziness, num_clusters, seed);
	}

	public AbstractFuzzyCmeans(StringModel model, int labelCutoff, int featureCutoff, double fuzziness, int num_clusters, long seed) {
		super(model, labelCutoff, featureCutoff, false);
		init(fuzziness, num_clusters, seed);
	}

	private void init(double fuzziness, int num_clusters, long seed) {
		this.fuzziness = fuzziness;
		this.num_clusters = num_clusters;
		this.seed = seed;
	}

	protected String getTrainerInfo(String type) {
		return String.format("type-%s: num_clusters = %4.3f, fuzziness = %4.3f, seed = %4.3f", type, fuzziness, num_clusters, seed);
	}

	public List<SparseFeatureVector> getPoints() {
		return points;
	}

	public void setPoints(List<SparseFeatureVector> points) {
		this.points = points;
	}
}
