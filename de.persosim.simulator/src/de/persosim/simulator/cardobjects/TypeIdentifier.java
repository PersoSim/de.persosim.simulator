package de.persosim.simulator.cardobjects;

import java.util.Arrays;

/**
 * This identifier implementation allows to search for {@link CardObject}
 * implementations based on type. It searches for {@link CardObject}s that are
 * direct instances of the given type or extend from it.
 * 
 * @author mboonk
 *
 */
public class TypeIdentifier extends AbstractCardObjectIdentifier {

	Class<?>[] types;

	@SafeVarargs
	public TypeIdentifier(Class<?>... types) {
		this.types = types;
	}

	@Override
	public boolean matches(CardObject currentObject) {
		for (Class<?> typeToCheck : types) {
			if (typeToCheck.isAssignableFrom(currentObject.getClass())) return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(types);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypeIdentifier other = (TypeIdentifier) obj;
		if (!Arrays.equals(types, other.types))
			return false;
		return true;
	}
	
	

}
