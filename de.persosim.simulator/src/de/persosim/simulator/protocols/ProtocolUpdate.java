package de.persosim.simulator.protocols;

import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.processing.UpdatePropagation;

/**
 * This UpdatePropagation stores and provides update information about currently
 * active protocols. The information stored herein is common for all protocols
 * and does not relate to any protocol specifics. To provide protocol specific
 * information provide a own UpdatePropagation.
 * 
 * It's main use case is to carry this information between the {@link CommandProcessor} and
 * the protocols.
 * 
 * @author amay
 * 
 */
public class ProtocolUpdate implements UpdatePropagation {

	private boolean isFinished;

	public ProtocolUpdate(boolean isFinished) {
		super();
		this.isFinished = isFinished;
	}

	/**
	 * If this method returns true the {@link CommandProcessor} will remove the
	 * {@link Protocol} from the stack.
	 */
	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public Class<? extends UpdatePropagation> getKey() {
		return getClass();
	}

}
