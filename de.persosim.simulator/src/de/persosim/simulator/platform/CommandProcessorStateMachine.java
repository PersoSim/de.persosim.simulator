package de.persosim.simulator.platform; 
/*
 * AUTOMATICALLY GENERATED CODE - DO NOT EDIT!
 * 
 * (C) 2013 HJP-Consulting GmbH
 */
 @SuppressWarnings("all")//generated code 

/* Command line options: -verbose -p EA -o CommandProcessorStateMachine -l java -t commandProcessor:commandProcessorClass C:\develop\eclipse-persosim\git\de.persosim.models\exported\commandProcessor.xml   */
/* This file is generated from commandProcessor.xml - do not edit manually  */
/* Generated on: Sun Jun 15 21:41:12 CEST 2014 / version 3.52beta2 */



public class CommandProcessorStateMachine extends AbstractCommandProcessor
{

	public static final int PROTOCOL_FROM_LIST_ACTIVE = 0;
	public static final int END_OF_STACK_OR_EMPTY = 1;
	public static final int START_OF_STACK = 2;
	public static final int PROTOCOL_INACTIVE = 3;
	public static final int WAITING_FOR_COMMAND = 4;
	public static final int COMMAND_PROCESSOR = 5;
	public static final int PROTOCOL_UNABLE_TO_PROCESS_APDU = 6;
	public static final int PROTOCOL_PROCESSED = 7;
	public static final int WAITING_FOR_PROTOCOL_TO_PROCESS_APDU = 8;
	public static final int PROTOCOL_FROM_STACK_ACTIVE = 9;
	public static final int INITIALIZATION = 10;
	public static final int __UNKNOWN_STATE__ = 11;


	public static final int COMMANDPROCESSORSTATEMACHINE_NO_MSG = 0;
	


	// flag if initialized
	protected boolean m_initialized=false;

	int  stateVar;
	int  stateVarCOMMAND_PROCESSOR;

	// State handler class default ctor
	public CommandProcessorStateMachine()
	{
	}

	/* Helper(s) to reset history */
	public void resetHistoryCOMMAND_PROCESSOR(){stateVarCOMMAND_PROCESSOR= WAITING_FOR_COMMAND;}

	/* Helper to get innermost active state id */
	public int getInnermostActiveState() {
		if(isInPROTOCOL_FROM_STACK_ACTIVE()){
			return PROTOCOL_FROM_STACK_ACTIVE;
		}else if(isInWAITING_FOR_PROTOCOL_TO_PROCESS_APDU()){
			return WAITING_FOR_PROTOCOL_TO_PROCESS_APDU;
		}else if(isInPROTOCOL_PROCESSED()){
			return PROTOCOL_PROCESSED;
		}else if(isInPROTOCOL_UNABLE_TO_PROCESS_APDU()){
			return PROTOCOL_UNABLE_TO_PROCESS_APDU;
		}else if(isInWAITING_FOR_COMMAND()){
			return WAITING_FOR_COMMAND;
		}else if(isInPROTOCOL_INACTIVE()){
			return PROTOCOL_INACTIVE;
		}else if(isInSTART_OF_STACK()){
			return START_OF_STACK;
		}else if(isInEND_OF_STACK_OR_EMPTY()){
			return END_OF_STACK_OR_EMPTY;
		}else if(isInPROTOCOL_FROM_LIST_ACTIVE()){
			return PROTOCOL_FROM_LIST_ACTIVE;
		}else if(isInINITIALIZATION()){
			return INITIALIZATION;
		}else{
			return __UNKNOWN_STATE__;
		}
	}

	// Helper(s) to find out if the machine is in a certain state
	public boolean isInPROTOCOL_FROM_LIST_ACTIVE(){return (((stateVarCOMMAND_PROCESSOR==  PROTOCOL_FROM_LIST_ACTIVE)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInEND_OF_STACK_OR_EMPTY(){return (((stateVarCOMMAND_PROCESSOR==  END_OF_STACK_OR_EMPTY)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInSTART_OF_STACK(){return (((stateVarCOMMAND_PROCESSOR==  START_OF_STACK)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInPROTOCOL_INACTIVE(){return (((stateVarCOMMAND_PROCESSOR==  PROTOCOL_INACTIVE)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInWAITING_FOR_COMMAND(){return (((stateVarCOMMAND_PROCESSOR==  WAITING_FOR_COMMAND)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInCOMMAND_PROCESSOR(){return (((stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInPROTOCOL_UNABLE_TO_PROCESS_APDU(){return (((stateVarCOMMAND_PROCESSOR==  PROTOCOL_UNABLE_TO_PROCESS_APDU)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInPROTOCOL_PROCESSED(){return (((stateVarCOMMAND_PROCESSOR==  PROTOCOL_PROCESSED)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInWAITING_FOR_PROTOCOL_TO_PROCESS_APDU(){return (((stateVarCOMMAND_PROCESSOR==  WAITING_FOR_PROTOCOL_TO_PROCESS_APDU)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInPROTOCOL_FROM_STACK_ACTIVE(){return (((stateVarCOMMAND_PROCESSOR==  PROTOCOL_FROM_STACK_ACTIVE)&&(stateVar==  COMMAND_PROCESSOR)) ? (true) : (false));}
	public boolean isInINITIALIZATION(){return (((stateVar==  INITIALIZATION)) ? (true) : (false));}




	// Reinitialize the state machine
	public void reInitialize(){
			m_initialized=false;
			initialize();
	}

	public void initialize(){

		if(m_initialized==false){

			m_initialized=true;
			//call on entry code of default states
			initialize();


			// Set state vars to default states
			stateVar =  INITIALIZATION; /* set init state of top state */
			stateVarCOMMAND_PROCESSOR =  WAITING_FOR_COMMAND; /* set init state of COMMAND_PROCESSOR */

		}

	}

	protected void commandprocessorstatemachineChangeToState(int  state){
		stateVar=state;
	}

	protected void commandprocessorstatemachineChangeToStateCOMMAND_PROCESSOR(int  state){
		stateVarCOMMAND_PROCESSOR = state;
	}
	



	public int processEvent(int msg){

		int evConsumed = 0;

		

		if(m_initialized==false) return 0;

		/* action code */
		this.continueProcessing = true;
		do{
		evConsumed = 0;


		switch (stateVar) {

			case COMMAND_PROCESSOR:

				switch (stateVarCOMMAND_PROCESSOR) {

					case END_OF_STACK_OR_EMPTY:
						if(true){
							if(apduHasBeenProcessed()){
								/* Transition from END_OF_STACK_OR_EMPTY to WAITING_FOR_COMMAND */
								evConsumed=16;

								/* Action code for transition  */
								returnResult();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  WAITING_FOR_COMMAND;
							}else{
								/* Transition from END_OF_STACK_OR_EMPTY to WAITING_FOR_PROTOCOL_TO_PROCESS_APDU */
								evConsumed=16;

								/* Action code for transition  */
								setProtocolPointerToFirstElementOfProtocolList();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  WAITING_FOR_PROTOCOL_TO_PROCESS_APDU;
							} /*end of event selection */
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case END_OF_STACK_OR_EMPTY  */

					case PROTOCOL_FROM_LIST_ACTIVE:
						if(true){
							if(apduHasBeenProcessed() || protocolAtPointerWantsToGetOnStack()){ //FIXME this change should be done in the model
								/* Transition from PROTOCOL_FROM_LIST_ACTIVE to PROTOCOL_PROCESSED */
								evConsumed=16;

								/* Action code for transition  */
								addProtocolAtProtocolPointerToStack();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  PROTOCOL_PROCESSED;
							}else{
								/* Transition from PROTOCOL_FROM_LIST_ACTIVE to PROTOCOL_UNABLE_TO_PROCESS_APDU */
								evConsumed=16;


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  PROTOCOL_UNABLE_TO_PROCESS_APDU;
							} /*end of event selection */
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PROTOCOL_FROM_LIST_ACTIVE  */

					case PROTOCOL_FROM_STACK_ACTIVE:
						if(true){
							/* Transition from PROTOCOL_FROM_STACK_ACTIVE to PROTOCOL_PROCESSED */
							evConsumed=16;

							/* Action code for transition  */
							currentProtocolProcess();


							/* adjust state variables  */
							stateVarCOMMAND_PROCESSOR =  PROTOCOL_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PROTOCOL_FROM_STACK_ACTIVE  */

					case PROTOCOL_INACTIVE:
						if(true){
							/* Transition from PROTOCOL_INACTIVE to START_OF_STACK */
							evConsumed=16;


							/* adjust state variables  */
							stateVarCOMMAND_PROCESSOR =  START_OF_STACK;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PROTOCOL_INACTIVE  */

					case PROTOCOL_PROCESSED:
						if(true){
							if(isProtocolFinished()){
								/* Transition from PROTOCOL_PROCESSED to PROTOCOL_INACTIVE */
								evConsumed=16;

								/* Action code for transition  */
								removeCurrentProtocolAndAboveFromStack();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  PROTOCOL_INACTIVE;
							}else{
								/* Transition from PROTOCOL_PROCESSED to PROTOCOL_INACTIVE */
								evConsumed=16;

								/* Action code for transition  */
								incrementStackPointer();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  PROTOCOL_INACTIVE;
							} /*end of event selection */
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PROTOCOL_PROCESSED  */

					case PROTOCOL_UNABLE_TO_PROCESS_APDU:
						if(true){
							if(protocolAtProtocolPointerIsLastElementOfProtocolList()){
								/* Transition from PROTOCOL_UNABLE_TO_PROCESS_APDU to WAITING_FOR_COMMAND */
								evConsumed=16;

								/* Action code for transition  */
								setStatusWordForUnsupportedCommand();
								returnResult();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  WAITING_FOR_COMMAND;
							}else{
								/* Transition from PROTOCOL_UNABLE_TO_PROCESS_APDU to WAITING_FOR_PROTOCOL_TO_PROCESS_APDU */
								evConsumed=16;

								/* Action code for transition  */
								setProtocolPointerToNextElementOfProtocolList();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  WAITING_FOR_PROTOCOL_TO_PROCESS_APDU;
							} /*end of event selection */
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PROTOCOL_UNABLE_TO_PROCESS_APDU  */

					case START_OF_STACK:
						if(true){
							if(stackPointerIsNull()){
								/* Transition from START_OF_STACK to END_OF_STACK_OR_EMPTY */
								evConsumed=16;


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  END_OF_STACK_OR_EMPTY;
							}else{
								/* Transition from START_OF_STACK to PROTOCOL_FROM_STACK_ACTIVE */
								evConsumed=16;

								/* Action code for transition  */
								makeStackPointerCurrentlyActiveProtocol();


								/* adjust state variables  */
								stateVarCOMMAND_PROCESSOR =  PROTOCOL_FROM_STACK_ACTIVE;
							} /*end of event selection */
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case START_OF_STACK  */

					case WAITING_FOR_COMMAND:
						if(true){
							/* Transition from WAITING_FOR_COMMAND to START_OF_STACK */
							evConsumed=16;

							/* Action code for transition  */
							setStackPointerToBottom();


							/* adjust state variables  */
							stateVarCOMMAND_PROCESSOR =  START_OF_STACK;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case WAITING_FOR_COMMAND  */

					case WAITING_FOR_PROTOCOL_TO_PROCESS_APDU:
						if(true){
							/* Transition from WAITING_FOR_PROTOCOL_TO_PROCESS_APDU to PROTOCOL_FROM_LIST_ACTIVE */
							evConsumed=16;

							/* Action code for transition  */
							resetProtocolAtProtocolPointer();
							makeProtocolAtProtocolPointerCurrentlyActiveProtocol();
							currentProtocolProcess();


							/* adjust state variables  */
							stateVarCOMMAND_PROCESSOR =  PROTOCOL_FROM_LIST_ACTIVE;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case WAITING_FOR_PROTOCOL_TO_PROCESS_APDU  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch COMMAND_PROCESSOR */
			break; /* end of case COMMAND_PROCESSOR  */

			case INITIALIZATION:
				if(true){
					/* Transition from INITIALIZATION to COMMAND_PROCESSOR */
					evConsumed=16;

					/* Action code for transition  */
					returnResult();

					stateVar =  COMMAND_PROCESSOR;/* Default in entry chain  */
					stateVarCOMMAND_PROCESSOR =  WAITING_FOR_COMMAND;/* Default in entry chain  */

				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case INITIALIZATION  */

			default:
				/* Intentionally left blank */
			break;
		} /* end switch stateVar_root */

	/* Post Action Code */
	}while (this.continueProcessing);
		return evConsumed;
	}
}
