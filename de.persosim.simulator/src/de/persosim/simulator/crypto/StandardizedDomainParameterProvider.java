package de.persosim.simulator.crypto;

import java.util.Collection;

public interface StandardizedDomainParameterProvider {
	Collection<Integer> getSupportedDomainParameters();
	DomainParameterSet getDomainParameterSet(int id);
	Integer getSimplifiedAlgorithm(String algorithmId);
}
