package edu.emory.clir.clearnlp.component;
import org.kohsuke.args4j.Option;

import edu.emory.clir.clearnlp.classification.configuration.AbstractTrainerConfiguration;
import edu.emory.clir.clearnlp.classification.model.AbstractModel;
import edu.emory.clir.clearnlp.classification.trainer.AbstractTrainer;
import edu.emory.clir.clearnlp.collection.list.FloatArrayList;

abstract public class AbstractClustererOnline extends AbstractClusterer
{
	@Option(name="-developFile", usage="the development file (optional)", required=false, metaVar="<filename>")
	protected String s_developFile;
	
	public AbstractClustererOnline(String[] args)
	{
		new ArgsReader(args, this);
		
		AbstractTrainerConfiguration trainConfiguration = createTrainConfiguration();
		AbstractModel<?,?> model = null;
		
		if (s_trainFile != null)
		{
			model = train(trainConfiguration, s_trainFile, s_developFile);
			if (s_modelFile != null) saveModel(model, s_modelFile);
		}
		else if (s_modelFile != null)
		{
			model = loadModel(s_modelFile, i_vectorType);
		}
		
		if (s_testFile != null)
			evaluate(model, s_testFile);
	}
	
	/** @return a trained model using the specific training file. */
	public AbstractModel<?,?> train(AbstractTrainerConfiguration trainConfiguration, String trainFile, String developFile)
	{
		AbstractModel<?,?> model = createModel(trainConfiguration.getVectorType(), trainConfiguration.isBinary());
		readInstances(model, trainFile);
		
		AbstractTrainer trainer = getTrainer(trainConfiguration, model);
		double prevScore, currScore = 0;
		FloatArrayList weights = null;
		System.out.println(trainer.trainerInfoFull());
		
		while (true)
		{
			trainer.train();
			prevScore = currScore;
			currScore = evaluate(model, developFile);
			
			if (prevScore < currScore)
				weights = model.getWeightVector().cloneWeights(); 
			else
				break;
		}
		
		model.getWeightVector().setWeights(weights);
		return model;
	}
}