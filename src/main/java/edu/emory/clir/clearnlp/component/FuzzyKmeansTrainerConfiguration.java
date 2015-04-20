package edu.emory.clir.clearnlp.component;

import edu.emory.clir.clearnlp.classification.configuration.DefaultTrainerConfiguration;

public class FuzzyKmeansTrainerConfiguration extends DefaultTrainerConfiguration {
	private double fuzziness;
	private int num_clusters;
	private long seed;

	public FuzzyKmeansTrainerConfiguration(byte vectorType, boolean binary, int labelCutoff, int featureCutoff, int numberOfThreads, double fuzziness, int num_clusters, long seed) {
		super(vectorType, binary, labelCutoff, featureCutoff, numberOfThreads);
		this.fuzziness = fuzziness;
		this.num_clusters = num_clusters;
		this.seed = seed;
	}

	public double getFuzziness() {
		return fuzziness;
	}

	public void setFuzziness(double fuzziness) {
		this.fuzziness = fuzziness;
	}

	public int getNum_clusters() {
		return num_clusters;
	}

	public void setNum_clusters(int num_clusters) {
		this.num_clusters = num_clusters;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}
}
