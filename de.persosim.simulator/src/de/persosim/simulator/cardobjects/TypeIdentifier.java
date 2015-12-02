package de.persosim.simulator.cardobjects;

/**
 * This identifier implementation allows to search for {@link CardObject}
 * implementations based on type. It searches for {@link CardObject}s that are
 * direct instances of the given type or extend from it.
 * 
 * @author mboonk
 *
 */
public class TypeIdentifier extends AbstractCardObjectIdentifier {

	Class<?> type;

	public TypeIdentifier(Class<? extends AbstractCardObject> type) {
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean matches(CardObject currentObject) {
		return type.isAssignableFrom(currentObject.getClass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
