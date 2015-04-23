/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.clir.clearnlp.bin;

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.component.mode.ner.DefaultNERecognizer;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class NERDecode
{
	static public void main(String[] args)
	{
		List<String> dsw = new ArrayList<>();
		dsw.add("brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt.xz");
		dsw.add("model-2030000000.LEARNING_RATE=1e-09.EMBEDDING_LEARNING_RATE=1e-06.EMBEDDING_SIZE=100.txt.xz");
		dsw.add("hlbl_reps_clean_2.50d.rcv1.clean.tokenized-CoNLL03.case-intact.txt.xz");
		GlobalLexica.initDistributionalSemanticsWords(dsw);
		
		new DefaultNERecognizer(NLPUtils.getObjectInputStream("general-en-ner.xz"));
	}
}
