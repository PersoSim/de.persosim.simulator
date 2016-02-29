package de.persosim.simulator.statemachine;

/**
 * This interface encapsulates methods required to handle EA state machines
 * created with Sinelabore.
 * <p/>
 * NOTE: A {@link StateMachine} needs to be initialized right after construction
 * and before the first use by calling {@link #init()}.
 * 
 * @author amay
 * 
 */
public interface StateMachine {

	/**
	 * Process the transition in the StateMachine.
	 *  
	 * @param event
	 *            this parameter is used as triggering event within the
	 *            generated code, currently unused, as every event triggers
	 *            processing of the processingData and handles potentially many
	 *            state transitions
	 * @return
	 */
	public abstract int processEvent(int event);

	/**
	 * Initialize the {@link StateMachine}. This method MUST be called by the
	 * creating instance before using the StateMachine (e.g. right after the
	 * constructor). It circumvents initialization problems, when subclass
	 * constructors initialize values after the super class constructor already
	 * used/modified them.
	 * 
	 * After this call finished {@link isInitialized()} should return true;
	 * 
	 */
	public abstract void init();

	/**
	 * Returns true when {@link #init()} was called before, i.e. the {@link StateMachine} has been properly initialized. Can be used within the state
	 * machine to prevent execution of initialization steps that are required
	 * only once and would be executed for every APDU instead.
	 */
	public abstract boolean isInitialized();


	/**
	 * Reset the {@link StateMachine} to it's initial state and configuration.
	 * <p/>
	 * After this method is called the object shall behave exactly like a newly
	 * created and initialized object (e.g. object created via constructor and
	 * {@link #init()} called).
	 */
	public abstract void reset();
	
	// below are methods defined that are implemented by generated code and can be used to interact with it
	
	/**
	 * Automatically generated method that initializes the generated state
	 * machine (e.g. sets initial states etc.). Does nothing when state machine
	 * is already initialized.
	 */
	public abstract void initialize();
	
	/**
	 * Automatically generated method that initializes the generated state
	 * machine (e.g. sets initial states etc.). This method works regardless of
	 * the current initialization state.
	 */
	public abstract void reInitialize();

}
