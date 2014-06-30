package de.persosim.simulator.protocols.pin; 
/*
 * AUTOMATICALLY GENERATED CODE - DO NOT EDIT!
 * 
 * (C) 2013 HJP-Consulting GmbH
 */
 @SuppressWarnings("all")//generated code 

/* Command line options: -verbose -p EA -o DefaultPinProtocol -l java -t PIN:pinclass C:\develop\wd\protocol_pin.xml   */
/* This file is generated from protocol_pin.xml - do not edit manually  */
/* Generated on: Mon Jun 30 10:28:54 CEST 2014 / version 3.52beta2 */



public class DefaultPinProtocol extends AbstractPinProtocol
{

	public static final int REGISTER_APDU_ACTIVATE_PIN = 0;
	public static final int DEACTIVATE_PIN_RECEIVED = 1;
	public static final int UNBLOCK_PIN_PROCESSED = 2;
	public static final int PIN_ANNOUNCED = 3;
	public static final int CHANGE_PIN_RECEIVED = 4;
	public static final int REGISTER_APDUS = 5;
	public static final int REGISTER_APDU_CHANGE_PIN = 6;
	public static final int PIN_INIT = 7;
	public static final int ACTIVATE_PIN_PROCESSED = 8;
	public static final int ACTIVATE_PIN_RECEIVED = 9;
	public static final int CHANGE_CAN_PROCESSED = 10;
	public static final int UNBLOCK_PIN_RECEIVED = 11;
	public static final int REGISTER_APDU_UNBLOCK_PIN = 12;
	public static final int REGISTER_APDU_DEACTIVATE_PIN = 13;
	public static final int PIN_COMPLETED = 14;
	public static final int CHANGE_PIN_PROCESSED = 15;
	public static final int CHANGE_CAN_RECEIVED = 16;
	public static final int ERROR_HANDLING = 17;
	public static final int DEACTIVATE_PIN_PROCESSED = 18;
	public static final int REGISTER_APDU_CHANGE_CAN = 19;
	public static final int __UNKNOWN_STATE__ = 20;


	public static final int DEFAULTPINPROTOCOL_NO_MSG = 0;
	


	// flag if initialized
	protected boolean m_initialized=false;

	int  stateVar;
	int  stateVarPIN_ANNOUNCED;
	int  stateVarREGISTER_APDUS;

	// State handler class default ctor
	public DefaultPinProtocol()
	{
	}

	/* Helper(s) to reset history */
	public void resetHistoryPIN_ANNOUNCED(){stateVarPIN_ANNOUNCED= PIN_INIT;}
	public void resetHistoryREGISTER_APDUS(){stateVarREGISTER_APDUS= REGISTER_APDU_ACTIVATE_PIN;}

	/* Helper to get innermost active state id */
	public int getInnermostActiveState() {
		if(isInREGISTER_APDU_CHANGE_CAN()){
			return REGISTER_APDU_CHANGE_CAN;
		}else if(isInDEACTIVATE_PIN_PROCESSED()){
			return DEACTIVATE_PIN_PROCESSED;
		}else if(isInCHANGE_CAN_RECEIVED()){
			return CHANGE_CAN_RECEIVED;
		}else if(isInCHANGE_PIN_PROCESSED()){
			return CHANGE_PIN_PROCESSED;
		}else if(isInREGISTER_APDU_DEACTIVATE_PIN()){
			return REGISTER_APDU_DEACTIVATE_PIN;
		}else if(isInREGISTER_APDU_UNBLOCK_PIN()){
			return REGISTER_APDU_UNBLOCK_PIN;
		}else if(isInUNBLOCK_PIN_RECEIVED()){
			return UNBLOCK_PIN_RECEIVED;
		}else if(isInCHANGE_CAN_PROCESSED()){
			return CHANGE_CAN_PROCESSED;
		}else if(isInACTIVATE_PIN_RECEIVED()){
			return ACTIVATE_PIN_RECEIVED;
		}else if(isInACTIVATE_PIN_PROCESSED()){
			return ACTIVATE_PIN_PROCESSED;
		}else if(isInPIN_INIT()){
			return PIN_INIT;
		}else if(isInREGISTER_APDU_CHANGE_PIN()){
			return REGISTER_APDU_CHANGE_PIN;
		}else if(isInCHANGE_PIN_RECEIVED()){
			return CHANGE_PIN_RECEIVED;
		}else if(isInUNBLOCK_PIN_PROCESSED()){
			return UNBLOCK_PIN_PROCESSED;
		}else if(isInDEACTIVATE_PIN_RECEIVED()){
			return DEACTIVATE_PIN_RECEIVED;
		}else if(isInREGISTER_APDU_ACTIVATE_PIN()){
			return REGISTER_APDU_ACTIVATE_PIN;
		}else if(isInERROR_HANDLING()){
			return ERROR_HANDLING;
		}else if(isInPIN_COMPLETED()){
			return PIN_COMPLETED;
		}else{
			return __UNKNOWN_STATE__;
		}
	}

	// Helper(s) to find out if the machine is in a certain state
	public boolean isInREGISTER_APDU_ACTIVATE_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_ACTIVATE_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInDEACTIVATE_PIN_RECEIVED(){return (((stateVarPIN_ANNOUNCED==  DEACTIVATE_PIN_RECEIVED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInUNBLOCK_PIN_PROCESSED(){return (((stateVarPIN_ANNOUNCED==  UNBLOCK_PIN_PROCESSED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPIN_ANNOUNCED(){return (((stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInCHANGE_PIN_RECEIVED(){return (((stateVarPIN_ANNOUNCED==  CHANGE_PIN_RECEIVED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDUS(){return (((stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_CHANGE_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_CHANGE_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPIN_INIT(){return (((stateVarPIN_ANNOUNCED==  PIN_INIT)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInACTIVATE_PIN_PROCESSED(){return (((stateVarPIN_ANNOUNCED==  ACTIVATE_PIN_PROCESSED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInACTIVATE_PIN_RECEIVED(){return (((stateVarPIN_ANNOUNCED==  ACTIVATE_PIN_RECEIVED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInCHANGE_CAN_PROCESSED(){return (((stateVarPIN_ANNOUNCED==  CHANGE_CAN_PROCESSED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInUNBLOCK_PIN_RECEIVED(){return (((stateVarPIN_ANNOUNCED==  UNBLOCK_PIN_RECEIVED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_UNBLOCK_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_UNBLOCK_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_DEACTIVATE_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_DEACTIVATE_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPIN_COMPLETED(){return (((stateVar==  PIN_COMPLETED)) ? (true) : (false));}
	public boolean isInCHANGE_PIN_PROCESSED(){return (((stateVarPIN_ANNOUNCED==  CHANGE_PIN_PROCESSED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInCHANGE_CAN_RECEIVED(){return (((stateVarPIN_ANNOUNCED==  CHANGE_CAN_RECEIVED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInERROR_HANDLING(){return (((stateVar==  ERROR_HANDLING)) ? (true) : (false));}
	public boolean isInDEACTIVATE_PIN_PROCESSED(){return (((stateVarPIN_ANNOUNCED==  DEACTIVATE_PIN_PROCESSED)&&(stateVar==  PIN_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_CHANGE_CAN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_CHANGE_CAN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}




	// Reinitialize the state machine
	public void reInitialize(){
			m_initialized=false;
			initialize();
	}

	public void initialize(){

		if(m_initialized==false){

			m_initialized=true;
			//call on entry code of default states
			

			// Set state vars to default states
			stateVar =  REGISTER_APDUS; /* set init state of top state */
			stateVarPIN_ANNOUNCED =  PIN_INIT; /* set init state of PIN_ANNOUNCED */
			stateVarREGISTER_APDUS =  REGISTER_APDU_ACTIVATE_PIN; /* set init state of REGISTER_APDUS */

		}

	}

	protected void defaultpinprotocolChangeToState(int  state){
		stateVar=state;
	}

	protected void defaultpinprotocolChangeToStatePIN_ANNOUNCED(int  state){
		stateVarPIN_ANNOUNCED = state;
	}
	
	protected void defaultpinprotocolChangeToStateREGISTER_APDUS(int  state){
		stateVarREGISTER_APDUS = state;
	}
	



	public int processEvent(int msg){

		int evConsumed = 0;

		

		if(m_initialized==false) return 0;

		/* action code */
		this.continueProcessing = true;
		do{
		evConsumed = 0;


		switch (stateVar) {

			case ERROR_HANDLING:
				if(true){
					/* Transition from ERROR_HANDLING to PIN_ANNOUNCED */
					evConsumed=16;

					logs("INIT");
					returnResult();
					stateVar =  PIN_ANNOUNCED;/* Default in entry chain  */
					stateVarPIN_ANNOUNCED =  PIN_INIT;/* Default in entry chain  */

				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case ERROR_HANDLING  */

			case PIN_ANNOUNCED:

				switch (stateVarPIN_ANNOUNCED) {

					case ACTIVATE_PIN_PROCESSED:
						if(true){
							/* Transition from ACTIVATE_PIN_PROCESSED to PIN_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PIN_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PIN_COMPLETED;
							stateVarPIN_ANNOUNCED =  PIN_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ACTIVATE_PIN_PROCESSED  */

					case ACTIVATE_PIN_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from ACTIVATE_PIN_RECEIVED to ACTIVATE_PIN_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state ACTIVATE_PIN_PROCESSED */
							logs("ACTIVATE_PIN_PROCESSED");

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  ACTIVATE_PIN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ACTIVATE_PIN_RECEIVED  */

					case CHANGE_CAN_PROCESSED:
						if(true){
							/* Transition from CHANGE_CAN_PROCESSED to PIN_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PIN_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PIN_COMPLETED;
							stateVarPIN_ANNOUNCED =  PIN_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CHANGE_CAN_PROCESSED  */

					case CHANGE_CAN_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from CHANGE_CAN_RECEIVED to CHANGE_CAN_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_CAN_PROCESSED */
							logs("CHANGE_CAN_PROCESSED");

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  CHANGE_CAN_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from CHANGE_CAN_RECEIVED to CHANGE_CAN_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_CAN_PROCESSED */
							logs("CHANGE_CAN_PROCESSED");

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  CHANGE_CAN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CHANGE_CAN_RECEIVED  */

					case CHANGE_PIN_PROCESSED:
						if(true){
							/* Transition from CHANGE_PIN_PROCESSED to PIN_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PIN_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PIN_COMPLETED;
							stateVarPIN_ANNOUNCED =  PIN_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CHANGE_PIN_PROCESSED  */

					case CHANGE_PIN_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from CHANGE_PIN_RECEIVED to CHANGE_PIN_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_PIN_PROCESSED */
							logs("CHANGE_PIN_PROCESSED");

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  CHANGE_PIN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CHANGE_PIN_RECEIVED  */

					case DEACTIVATE_PIN_PROCESSED:
						if(true){
							/* Transition from DEACTIVATE_PIN_PROCESSED to PIN_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PIN_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PIN_COMPLETED;
							stateVarPIN_ANNOUNCED =  PIN_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case DEACTIVATE_PIN_PROCESSED  */

					case DEACTIVATE_PIN_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from DEACTIVATE_PIN_RECEIVED to DEACTIVATE_PIN_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state DEACTIVATE_PIN_PROCESSED */
							logs("DEACTIVATE_PIN_PROCESSED");

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  DEACTIVATE_PIN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case DEACTIVATE_PIN_RECEIVED  */

					case PIN_INIT:
						if(isAPDU("Activate PIN")){
							/* Transition from PIN_INIT to ACTIVATE_PIN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state ACTIVATE_PIN_RECEIVED */
							logs("ACTIVATE_PIN_RECEIVED");
							processCommandActivatePin();

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  ACTIVATE_PIN_RECEIVED;
						}else if(isAPDU("Change CAN")){
							/* Transition from PIN_INIT to CHANGE_CAN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_CAN_RECEIVED */
							logs("CHANGE_CAN_RECEIVED");
							processCommandChangeCan();

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  CHANGE_CAN_RECEIVED;
						}else if(isAPDU("Change PIN")){
							/* Transition from PIN_INIT to CHANGE_PIN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_PIN_RECEIVED */
							logs("CHANGE_PIN_RECEIVED");
							processCommandChangePin();

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  CHANGE_PIN_RECEIVED;
						}else if(isAPDU("Deactivate PIN")){
							/* Transition from PIN_INIT to DEACTIVATE_PIN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state DEACTIVATE_PIN_RECEIVED */
							logs("DEACTIVATE_PIN_RECEIVED");
							processCommandDeactivatePin();

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  DEACTIVATE_PIN_RECEIVED;
						}else if(isAPDU("Unblock PIN")){
							/* Transition from PIN_INIT to UNBLOCK_PIN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state UNBLOCK_PIN_RECEIVED */
							logs("UNBLOCK_PIN_RECEIVED");
							processCommandUnblockPin();

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  UNBLOCK_PIN_RECEIVED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PIN_INIT  */

					case UNBLOCK_PIN_PROCESSED:
						if(true){
							/* Transition from UNBLOCK_PIN_PROCESSED to PIN_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PIN_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PIN_COMPLETED;
							stateVarPIN_ANNOUNCED =  PIN_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UNBLOCK_PIN_PROCESSED  */

					case UNBLOCK_PIN_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from UNBLOCK_PIN_RECEIVED to UNBLOCK_PIN_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state UNBLOCK_PIN_PROCESSED */
							logs("UNBLOCK_PIN_PROCESSED");

							/* adjust state variables  */
							stateVarPIN_ANNOUNCED =  UNBLOCK_PIN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UNBLOCK_PIN_RECEIVED  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch PIN_ANNOUNCED */

				/* Check if event was already processed  */
				if(evConsumed==0){

					if(true){
						/* Transition from PIN_ANNOUNCED to ERROR_HANDLING */
						evConsumed=16;
						

						/* adjust state variables  */
						stateVar =  ERROR_HANDLING;
					}else{
						/* Intentionally left blank */
					} /*end of event selection */
				}
			break; /* end of case PIN_ANNOUNCED  */

			case PIN_COMPLETED:
				if(true){
					/* Transition from PIN_COMPLETED to PIN_INIT */
					evConsumed=16;


					/* OnEntry code of state PIN_INIT */
					logs("INIT");
					returnResult();

					/* adjust state variables  */
					stateVar =  PIN_ANNOUNCED;
					stateVarPIN_ANNOUNCED =  PIN_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case PIN_COMPLETED  */

			case REGISTER_APDUS:

				switch (stateVarREGISTER_APDUS) {

					case REGISTER_APDU_ACTIVATE_PIN:
						/* action code  */
						createNewApduSpecification("Activate PIN");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_1);
						apduSpecification.setIns(INS_44_ACTIVATE_FILE);
						apduSpecification.setP1((byte) 0x10);
						apduSpecification.setReqP2(P2_03_PIN);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_ACTIVATE_PIN to REGISTER_APDU_DEACTIVATE_PIN */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_DEACTIVATE_PIN;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_ACTIVATE_PIN  */

					case REGISTER_APDU_CHANGE_CAN:
						/* action code  */
						createNewApduSpecification("Change CAN");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_3);
						apduSpecification.setP1(P1_02_CHANGE);
						apduSpecification.setP2(P2_02_CAN);
						apduSpecification.setIns(INS_2C_RESET_RETRY_COUNTER);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_CHANGE_CAN to PIN_ANNOUNCED */
							evConsumed=16;


							logs("INIT");
							returnResult();
							stateVar =  PIN_ANNOUNCED;/* Default in entry chain  */
							stateVarPIN_ANNOUNCED =  PIN_INIT;/* Default in entry chain  */

							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_ACTIVATE_PIN;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_CHANGE_CAN  */

					case REGISTER_APDU_CHANGE_PIN:
						/* action code  */
						createNewApduSpecification("Change PIN");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_3);
						apduSpecification.setP1(P1_02_UNBLOCK_AND_CHANGE);
						apduSpecification.setP2(P2_03_PIN);
						apduSpecification.setIns(INS_2C_RESET_RETRY_COUNTER);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_CHANGE_PIN to REGISTER_APDU_CHANGE_CAN */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_CHANGE_CAN;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_CHANGE_PIN  */

					case REGISTER_APDU_DEACTIVATE_PIN:
						/* action code  */
						createNewApduSpecification("Deactivate PIN");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_1);
						apduSpecification.setIns(INS_04_DEACTIVATE_FILE);
						apduSpecification.setP1((byte) 0x10);
						apduSpecification.setP2(P2_03_PIN);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_DEACTIVATE_PIN to REGISTER_APDU_UNBLOCK_PIN */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_UNBLOCK_PIN;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_DEACTIVATE_PIN  */

					case REGISTER_APDU_UNBLOCK_PIN:
						/* action code  */
						createNewApduSpecification("Unblock PIN");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_1);
						apduSpecification.setIns(INS_2C_RESET_RETRY_COUNTER);
						apduSpecification.setP1(P1_03_UNBLOCK);
						apduSpecification.setP2(P2_03_PIN);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_UNBLOCK_PIN to REGISTER_APDU_CHANGE_PIN */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_CHANGE_PIN;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_UNBLOCK_PIN  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch REGISTER_APDUS */
			break; /* end of case REGISTER_APDUS  */

			default:
				/* Intentionally left blank */
			break;
		} /* end switch stateVar_root */

	/* Post Action Code */
	} while (this.continueProcessing);
		return evConsumed;
	}
}
