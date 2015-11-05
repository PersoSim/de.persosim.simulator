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
	public boolean matches(CardObjectIdentifier obj) {
		if (obj instanceof TypeIdentifier) {
			return type.isAssignableFrom(((TypeIdentifier) obj).getType());
		}
		return false;
	}

}
