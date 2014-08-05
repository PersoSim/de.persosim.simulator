package de.persosim.simulator.protocols.pin; 
/*
 * AUTOMATICALLY GENERATED CODE - DO NOT EDIT!
 * 
 * (C) 2013 HJP-Consulting GmbH
 */
 @SuppressWarnings("all")//generated code 

/* Command line options: -verbose -p EA -o DefaultPinProtocol -l java -t PIN:pinclass C:\develop\wd\protocol_pin.xml   */
/* This file is generated from protocol_pin.xml - do not edit manually  */
/* Generated on: Tue Aug 05 09:48:51 CEST 2014 / version 3.52beta2 */



public class DefaultPinProtocol extends AbstractPinProtocol
{

	public static final int REGISTER_APDU_ACTIVATE_PIN = 0;
	public static final int DEACTIVATE_PIN_RECEIVED = 1;
	public static final int UNBLOCK_PIN_PROCESSED = 2;
	public static final int CHANGE_PASSWORD_RECEIVED = 3;
	public static final int PASSWORD_ANNOUNCED = 4;
	public static final int REGISTER_APDUS = 5;
	public static final int REGISTER_APDU_CHANGE_PASSWORD = 6;
	public static final int PIN_INIT = 7;
	public static final int ACTIVATE_PIN_PROCESSED = 8;
	public static final int ACTIVATE_PIN_RECEIVED = 9;
	public static final int UNBLOCK_PIN_RECEIVED = 10;
	public static final int REGISTER_APDU_UNBLOCK_PIN = 11;
	public static final int REGISTER_APDU_DEACTIVATE_PIN = 12;
	public static final int PASSWORD_COMPLETED = 13;
	public static final int CHANGE_PASSWORD_PROCESSED = 14;
	public static final int ERROR_HANDLING = 15;
	public static final int DEACTIVATE_PIN_PROCESSED = 16;
	public static final int __UNKNOWN_STATE__ = 17;


	public static final int DEFAULTPINPROTOCOL_NO_MSG = 0;
	


	// flag if initialized
	protected boolean m_initialized=false;

	int  stateVar;
	int  stateVarPASSWORD_ANNOUNCED;
	int  stateVarREGISTER_APDUS;

	// State handler class default ctor
	public DefaultPinProtocol()
	{
	}

	/* Helper(s) to reset history */
	public void resetHistoryPASSWORD_ANNOUNCED(){stateVarPASSWORD_ANNOUNCED= PIN_INIT;}
	public void resetHistoryREGISTER_APDUS(){stateVarREGISTER_APDUS= REGISTER_APDU_ACTIVATE_PIN;}

	/* Helper to get innermost active state id */
	public int getInnermostActiveState() {
		if(isInDEACTIVATE_PIN_PROCESSED()){
			return DEACTIVATE_PIN_PROCESSED;
		}else if(isInCHANGE_PASSWORD_PROCESSED()){
			return CHANGE_PASSWORD_PROCESSED;
		}else if(isInREGISTER_APDU_DEACTIVATE_PIN()){
			return REGISTER_APDU_DEACTIVATE_PIN;
		}else if(isInREGISTER_APDU_UNBLOCK_PIN()){
			return REGISTER_APDU_UNBLOCK_PIN;
		}else if(isInUNBLOCK_PIN_RECEIVED()){
			return UNBLOCK_PIN_RECEIVED;
		}else if(isInACTIVATE_PIN_RECEIVED()){
			return ACTIVATE_PIN_RECEIVED;
		}else if(isInACTIVATE_PIN_PROCESSED()){
			return ACTIVATE_PIN_PROCESSED;
		}else if(isInPIN_INIT()){
			return PIN_INIT;
		}else if(isInREGISTER_APDU_CHANGE_PASSWORD()){
			return REGISTER_APDU_CHANGE_PASSWORD;
		}else if(isInCHANGE_PASSWORD_RECEIVED()){
			return CHANGE_PASSWORD_RECEIVED;
		}else if(isInUNBLOCK_PIN_PROCESSED()){
			return UNBLOCK_PIN_PROCESSED;
		}else if(isInDEACTIVATE_PIN_RECEIVED()){
			return DEACTIVATE_PIN_RECEIVED;
		}else if(isInREGISTER_APDU_ACTIVATE_PIN()){
			return REGISTER_APDU_ACTIVATE_PIN;
		}else if(isInERROR_HANDLING()){
			return ERROR_HANDLING;
		}else if(isInPASSWORD_COMPLETED()){
			return PASSWORD_COMPLETED;
		}else{
			return __UNKNOWN_STATE__;
		}
	}

	// Helper(s) to find out if the machine is in a certain state
	public boolean isInREGISTER_APDU_ACTIVATE_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_ACTIVATE_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInDEACTIVATE_PIN_RECEIVED(){return (((stateVarPASSWORD_ANNOUNCED==  DEACTIVATE_PIN_RECEIVED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInUNBLOCK_PIN_PROCESSED(){return (((stateVarPASSWORD_ANNOUNCED==  UNBLOCK_PIN_PROCESSED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInCHANGE_PASSWORD_RECEIVED(){return (((stateVarPASSWORD_ANNOUNCED==  CHANGE_PASSWORD_RECEIVED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPASSWORD_ANNOUNCED(){return (((stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDUS(){return (((stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_CHANGE_PASSWORD(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_CHANGE_PASSWORD)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPIN_INIT(){return (((stateVarPASSWORD_ANNOUNCED==  PIN_INIT)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInACTIVATE_PIN_PROCESSED(){return (((stateVarPASSWORD_ANNOUNCED==  ACTIVATE_PIN_PROCESSED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInACTIVATE_PIN_RECEIVED(){return (((stateVarPASSWORD_ANNOUNCED==  ACTIVATE_PIN_RECEIVED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInUNBLOCK_PIN_RECEIVED(){return (((stateVarPASSWORD_ANNOUNCED==  UNBLOCK_PIN_RECEIVED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_UNBLOCK_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_UNBLOCK_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_DEACTIVATE_PIN(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_DEACTIVATE_PIN)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPASSWORD_COMPLETED(){return (((stateVar==  PASSWORD_COMPLETED)) ? (true) : (false));}
	public boolean isInCHANGE_PASSWORD_PROCESSED(){return (((stateVarPASSWORD_ANNOUNCED==  CHANGE_PASSWORD_PROCESSED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}
	public boolean isInERROR_HANDLING(){return (((stateVar==  ERROR_HANDLING)) ? (true) : (false));}
	public boolean isInDEACTIVATE_PIN_PROCESSED(){return (((stateVarPASSWORD_ANNOUNCED==  DEACTIVATE_PIN_PROCESSED)&&(stateVar==  PASSWORD_ANNOUNCED)) ? (true) : (false));}




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
			stateVarPASSWORD_ANNOUNCED =  PIN_INIT; /* set init state of PASSWORD_ANNOUNCED */
			stateVarREGISTER_APDUS =  REGISTER_APDU_ACTIVATE_PIN; /* set init state of REGISTER_APDUS */

		}

	}

	protected void defaultpinprotocolChangeToState(int  state){
		stateVar=state;
	}

	protected void defaultpinprotocolChangeToStatePASSWORD_ANNOUNCED(int  state){
		stateVarPASSWORD_ANNOUNCED = state;
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
					/* Transition from ERROR_HANDLING to PASSWORD_ANNOUNCED */
					evConsumed=16;

					logs("INIT");
					returnResult();
					stateVar =  PASSWORD_ANNOUNCED;/* Default in entry chain  */
					stateVarPASSWORD_ANNOUNCED =  PIN_INIT;/* Default in entry chain  */

				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case ERROR_HANDLING  */

			case PASSWORD_ANNOUNCED:

				switch (stateVarPASSWORD_ANNOUNCED) {

					case ACTIVATE_PIN_PROCESSED:
						if(true){
							/* Transition from ACTIVATE_PIN_PROCESSED to PASSWORD_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PASSWORD_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PASSWORD_COMPLETED;
							stateVarPASSWORD_ANNOUNCED =  PIN_INIT;
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
							stateVarPASSWORD_ANNOUNCED =  ACTIVATE_PIN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ACTIVATE_PIN_RECEIVED  */

					case CHANGE_PASSWORD_PROCESSED:
						if(true){
							/* Transition from CHANGE_PASSWORD_PROCESSED to PASSWORD_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PASSWORD_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PASSWORD_COMPLETED;
							stateVarPASSWORD_ANNOUNCED =  PIN_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CHANGE_PASSWORD_PROCESSED  */

					case CHANGE_PASSWORD_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from CHANGE_PASSWORD_RECEIVED to CHANGE_PASSWORD_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_PASSWORD_PROCESSED */
							logs("CHANGE_PASSWORD_PROCESSED");

							/* adjust state variables  */
							stateVarPASSWORD_ANNOUNCED =  CHANGE_PASSWORD_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CHANGE_PASSWORD_RECEIVED  */

					case DEACTIVATE_PIN_PROCESSED:
						if(true){
							/* Transition from DEACTIVATE_PIN_PROCESSED to PASSWORD_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PASSWORD_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PASSWORD_COMPLETED;
							stateVarPASSWORD_ANNOUNCED =  PIN_INIT;
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
							stateVarPASSWORD_ANNOUNCED =  DEACTIVATE_PIN_PROCESSED;
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
							stateVarPASSWORD_ANNOUNCED =  ACTIVATE_PIN_RECEIVED;
						}else if(isAPDU("Change password")){
							/* Transition from PIN_INIT to CHANGE_PASSWORD_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state CHANGE_PASSWORD_RECEIVED */
							logs("CHANGE_PASSWORD_RECEIVED");
							processCommandChangePassword();

							/* adjust state variables  */
							stateVarPASSWORD_ANNOUNCED =  CHANGE_PASSWORD_RECEIVED;
						}else if(isAPDU("Deactivate PIN")){
							/* Transition from PIN_INIT to DEACTIVATE_PIN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state DEACTIVATE_PIN_RECEIVED */
							logs("DEACTIVATE_PIN_RECEIVED");
							processCommandDeactivatePin();

							/* adjust state variables  */
							stateVarPASSWORD_ANNOUNCED =  DEACTIVATE_PIN_RECEIVED;
						}else if(isAPDU("Unblock PIN")){
							/* Transition from PIN_INIT to UNBLOCK_PIN_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state UNBLOCK_PIN_RECEIVED */
							logs("UNBLOCK_PIN_RECEIVED");
							processCommandUnblockPin();

							/* adjust state variables  */
							stateVarPASSWORD_ANNOUNCED =  UNBLOCK_PIN_RECEIVED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PIN_INIT  */

					case UNBLOCK_PIN_PROCESSED:
						if(true){
							/* Transition from UNBLOCK_PIN_PROCESSED to PASSWORD_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PASSWORD_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  PASSWORD_COMPLETED;
							stateVarPASSWORD_ANNOUNCED =  PIN_INIT;
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
							stateVarPASSWORD_ANNOUNCED =  UNBLOCK_PIN_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UNBLOCK_PIN_RECEIVED  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch PASSWORD_ANNOUNCED */

				/* Check if event was already processed  */
				if(evConsumed==0){

					if(true){
						/* Transition from PASSWORD_ANNOUNCED to ERROR_HANDLING */
						evConsumed=16;
						

						/* adjust state variables  */
						stateVar =  ERROR_HANDLING;
					}else{
						/* Intentionally left blank */
					} /*end of event selection */
				}
			break; /* end of case PASSWORD_ANNOUNCED  */

			case PASSWORD_COMPLETED:
				if(true){
					/* Transition from PASSWORD_COMPLETED to PIN_INIT */
					evConsumed=16;


					/* OnEntry code of state PIN_INIT */
					logs("INIT");
					returnResult();

					/* adjust state variables  */
					stateVar =  PASSWORD_ANNOUNCED;
					stateVarPASSWORD_ANNOUNCED =  PIN_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case PASSWORD_COMPLETED  */

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
						apduSpecification.setP2(ID_PIN);
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

					case REGISTER_APDU_CHANGE_PASSWORD:
						/* action code  */
						createNewApduSpecification("Change password");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_3);
						apduSpecification.setP1(P1_RESET_RETRY_COUNTER_UNBLOCK_AND_CHANGE);
						apduSpecification.setIns(INS_2C_RESET_RETRY_COUNTER);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_CHANGE_PASSWORD to PASSWORD_ANNOUNCED */
							evConsumed=16;


							logs("INIT");
							returnResult();
							stateVar =  PASSWORD_ANNOUNCED;/* Default in entry chain  */
							stateVarPASSWORD_ANNOUNCED =  PIN_INIT;/* Default in entry chain  */

							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_ACTIVATE_PIN;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_CHANGE_PASSWORD  */

					case REGISTER_APDU_DEACTIVATE_PIN:
						/* action code  */
						createNewApduSpecification("Deactivate PIN");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_1);
						apduSpecification.setIns(INS_04_DEACTIVATE_FILE);
						apduSpecification.setP1((byte) 0x10);
						apduSpecification.setP2(ID_PIN);
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
						apduSpecification.setP1(P1_RESET_RETRY_COUNTER_UNBLOCK);
						apduSpecification.setP2(ID_PIN);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_UNBLOCK_PIN to REGISTER_APDU_CHANGE_PASSWORD */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_CHANGE_PASSWORD;
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
