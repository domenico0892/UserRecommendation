package org.rm3umf.framework.buildmodel.extractor;


import java.util.List;
import java.util.Set;

import org.rm3umf.domain.Concept;
import org.rm3umf.domain.Message;
import org.rm3umf.domain.PseudoFragment;
import org.rm3umf.domain.SignalComponent;



public interface StrategyExtraction {
	
	public void exploreResource(boolean exploreResource);
	
	public List<SignalComponent> extract(PseudoFragment pseudo) throws ExtractorException;

	public List<Concept> extract(Message m) throws ExtractorException;
}
