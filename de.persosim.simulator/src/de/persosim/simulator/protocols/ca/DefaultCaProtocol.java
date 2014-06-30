package de.persosim.simulator.protocols.ca; 
/*
 * AUTOMATICALLY GENERATED CODE - DO NOT EDIT!
 * 
 * (C) 2013 HJP-Consulting GmbH
 */
 @SuppressWarnings("all")//generated code 

/* Command line options: -verbose -p EA -o DefaultCaProtocol -l java -t CA:caclass C:\develop\eclipse-persosim\git\de.persosim.models\exported\protocol_ca.xml   */
/* This file is generated from protocol_ca.xml - do not edit manually  */
/* Generated on: Sun Jun 15 21:41:14 CEST 2014 / version 3.52beta2 */



public class DefaultCaProtocol extends AbstractCaProtocol
{

	public static final int CA_GENERAL_AUTHENTICATE_RECEIVED = 0;
	public static final int REGISTER_APDU_SET_AT = 1;
	public static final int CA_SET_AT_RECEIVED = 2;
	public static final int DUMMY_INITIAL = 3;
	public static final int CA_INIT = 4;
	public static final int RESET = 5;
	public static final int CA_IN_PROGRESS = 6;
	public static final int PROCESSING_ERROR = 7;
	public static final int REGISTER_APDUS = 8;
	public static final int CA_COMPLETED = 9;
	public static final int CA_GENERAL_AUTHENTICATE_PROCESSED = 10;
	public static final int REGISTER_APDU_MAP_NONCE = 11;
	public static final int CA_SET_AT_PROCESSED = 12;
	public static final int __UNKNOWN_STATE__ = 13;


	public static final int DEFAULTCAPROTOCOL_NO_MSG = 0;
	


	// flag if initialized
	protected boolean m_initialized=false;

	int  stateVar;
	int  stateVarREGISTER_APDUS;
	int  stateVarCA_IN_PROGRESS;

	// State handler class default ctor
	public DefaultCaProtocol()
	{
	}

	/* Helper(s) to reset history */
	public void resetHistoryREGISTER_APDUS(){stateVarREGISTER_APDUS= REGISTER_APDU_SET_AT;}
	public void resetHistoryCA_IN_PROGRESS(){stateVarCA_IN_PROGRESS= CA_INIT;}

	/* Helper to get innermost active state id */
	public int getInnermostActiveState() {
		if(isInCA_SET_AT_PROCESSED()){
			return CA_SET_AT_PROCESSED;
		}else if(isInREGISTER_APDU_MAP_NONCE()){
			return REGISTER_APDU_MAP_NONCE;
		}else if(isInCA_GENERAL_AUTHENTICATE_PROCESSED()){
			return CA_GENERAL_AUTHENTICATE_PROCESSED;
		}else if(isInCA_INIT()){
			return CA_INIT;
		}else if(isInCA_SET_AT_RECEIVED()){
			return CA_SET_AT_RECEIVED;
		}else if(isInREGISTER_APDU_SET_AT()){
			return REGISTER_APDU_SET_AT;
		}else if(isInCA_GENERAL_AUTHENTICATE_RECEIVED()){
			return CA_GENERAL_AUTHENTICATE_RECEIVED;
		}else if(isInCA_COMPLETED()){
			return CA_COMPLETED;
		}else if(isInPROCESSING_ERROR()){
			return PROCESSING_ERROR;
		}else if(isInRESET()){
			return RESET;
		}else if(isInDUMMY_INITIAL()){
			return DUMMY_INITIAL;
		}else{
			return __UNKNOWN_STATE__;
		}
	}

	// Helper(s) to find out if the machine is in a certain state
	public boolean isInCA_GENERAL_AUTHENTICATE_RECEIVED(){return (((stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_RECEIVED)&&(stateVar==  CA_IN_PROGRESS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_SET_AT(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_SET_AT)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInCA_SET_AT_RECEIVED(){return (((stateVarCA_IN_PROGRESS==  CA_SET_AT_RECEIVED)&&(stateVar==  CA_IN_PROGRESS)) ? (true) : (false));}
	public boolean isInDUMMY_INITIAL(){return (((stateVar==  DUMMY_INITIAL)) ? (true) : (false));}
	public boolean isInCA_INIT(){return (((stateVarCA_IN_PROGRESS==  CA_INIT)&&(stateVar==  CA_IN_PROGRESS)) ? (true) : (false));}
	public boolean isInRESET(){return (((stateVar==  RESET)) ? (true) : (false));}
	public boolean isInCA_IN_PROGRESS(){return (((stateVar==  CA_IN_PROGRESS)) ? (true) : (false));}
	public boolean isInPROCESSING_ERROR(){return (((stateVar==  PROCESSING_ERROR)) ? (true) : (false));}
	public boolean isInREGISTER_APDUS(){return (((stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInCA_COMPLETED(){return (((stateVar==  CA_COMPLETED)) ? (true) : (false));}
	public boolean isInCA_GENERAL_AUTHENTICATE_PROCESSED(){return (((stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_PROCESSED)&&(stateVar==  CA_IN_PROGRESS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_MAP_NONCE(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_MAP_NONCE)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInCA_SET_AT_PROCESSED(){return (((stateVarCA_IN_PROGRESS==  CA_SET_AT_PROCESSED)&&(stateVar==  CA_IN_PROGRESS)) ? (true) : (false));}




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
			stateVar =  DUMMY_INITIAL; /* set init state of top state */
			stateVarREGISTER_APDUS =  REGISTER_APDU_SET_AT; /* set init state of REGISTER_APDUS */
			stateVarCA_IN_PROGRESS =  CA_INIT; /* set init state of CA_IN_PROGRESS */

		}

	}

	protected void defaultcaprotocolChangeToState(int  state){
		stateVar=state;
	}

	protected void defaultcaprotocolChangeToStateREGISTER_APDUS(int  state){
		stateVarREGISTER_APDUS = state;
	}
	
	protected void defaultcaprotocolChangeToStateCA_IN_PROGRESS(int  state){
		stateVarCA_IN_PROGRESS = state;
	}
	



	public int processEvent(int msg){

		int evConsumed = 0;

		

		if(m_initialized==false) return 0;

		/* action code */
		this.continueProcessing = true;
		do{
		evConsumed = 0;


		switch (stateVar) {

			case CA_COMPLETED:
				if(true){
					/* Transition from CA_COMPLETED to CA_INIT */
					evConsumed=16;

					/* OnEntry code of state CA_IN_PROGRESS */
					logs("CA_ANNOUNCED");

					/* OnEntry code of state CA_INIT */
					logs("INIT");
					processCommandInitialize();
					returnResult();

					/* adjust state variables  */
					stateVar =  CA_IN_PROGRESS;
					stateVarCA_IN_PROGRESS =  CA_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case CA_COMPLETED  */

			case DUMMY_INITIAL:
				if(true){
					if(isInitialized()){
						/* Transition from DUMMY_INITIAL to CA_IN_PROGRESS */
						evConsumed=16;

						/* OnEntry code of state CA_IN_PROGRESS */
						logs("CA_ANNOUNCED");
						stateVar =  CA_IN_PROGRESS;/* entry chain  */
						if(stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_PROCESSED){
							logs("GENERAL_AUTHENTICATE_PROCESSED");
							processCommandFinalize();

						}else if(stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_RECEIVED){
							logs("GENERAL_AUTHENTICATE_RECEIVED");
							processCommandGeneralAuthenticate();

						}else if(stateVarCA_IN_PROGRESS==  CA_INIT){
							logs("INIT");
							processCommandInitialize();
							returnResult();

						}else if(stateVarCA_IN_PROGRESS==  CA_SET_AT_PROCESSED){
							logs("SET_AT_PROCESSED");
							returnResult();

						}else if(stateVarCA_IN_PROGRESS==  CA_SET_AT_RECEIVED){
							logs("SET_AT_RECEIVED");
							processCommandSetAT();

						}

					}else{
						/* Transition from DUMMY_INITIAL to REGISTER_APDUS */
						evConsumed=16;

						stateVar =  REGISTER_APDUS;/* Default in entry chain  */
						stateVarREGISTER_APDUS =  REGISTER_APDU_SET_AT;/* Default in entry chain  */

					} /*end of event selection */
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case DUMMY_INITIAL  */

			case PROCESSING_ERROR:
				if(true){
					/* Transition from PROCESSING_ERROR to RESET */
					evConsumed=16;


					/* adjust state variables  */
					stateVar =  RESET;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case PROCESSING_ERROR  */

			case REGISTER_APDUS:

				switch (stateVarREGISTER_APDUS) {

					case REGISTER_APDU_MAP_NONCE:
						/* action code  */
						createNewApduSpecification("General Authenticate");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_4);
						apduSpecification.setChaining(false);
						apduSpecification.setIns(INS_86_GENERAL_AUTHENTICATE);
						apduSpecification.setP1((byte) 0x00);
						apduSpecification.setP2((byte) 0x00);
						createNewTagSpecification((byte) 0x7C);
						apduSpecification.addTag(tagSpecification);
						createNewPath();
						path.add((byte) 0x7C);
						createNewTagSpecification((byte) 0x80);
						apduSpecification.addTag(path, tagSpecification);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_MAP_NONCE to CA_IN_PROGRESS */
							evConsumed=16;


							/* OnEntry code of state CA_IN_PROGRESS */
							logs("CA_ANNOUNCED");
							stateVar =  CA_IN_PROGRESS;/* entry chain  */
							if(stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_PROCESSED){
								logs("GENERAL_AUTHENTICATE_PROCESSED");
								processCommandFinalize();

							}else if(stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_RECEIVED){
								logs("GENERAL_AUTHENTICATE_RECEIVED");
								processCommandGeneralAuthenticate();

							}else if(stateVarCA_IN_PROGRESS==  CA_INIT){
								logs("INIT");
								processCommandInitialize();
								returnResult();

							}else if(stateVarCA_IN_PROGRESS==  CA_SET_AT_PROCESSED){
								logs("SET_AT_PROCESSED");
								returnResult();

							}else if(stateVarCA_IN_PROGRESS==  CA_SET_AT_RECEIVED){
								logs("SET_AT_RECEIVED");
								processCommandSetAT();

							}

							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_SET_AT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_MAP_NONCE  */

					case REGISTER_APDU_SET_AT:
						/* action code  */
						createNewApduSpecification("Set AT");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_3);
						apduSpecification.setChaining(false);
						apduSpecification.setIns(INS_22_MANAGE_SECURITY_ENVIRONMENT);
						apduSpecification.setP1((byte) 0x41);
						apduSpecification.setP2((byte) 0xA4);
						createNewTagSpecification((byte) 0x80);
						apduSpecification.addTag(tagSpecification);
						createNewTagSpecification((byte) 0x84);
						tagSpecification.setRequired(REQ_OPTIONAL);
						apduSpecification.addTag(tagSpecification);
						apduSpecification.setInitialApdu();
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_SET_AT to REGISTER_APDU_MAP_NONCE */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_MAP_NONCE;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_SET_AT  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch REGISTER_APDUS */
			break; /* end of case REGISTER_APDUS  */

			case RESET:
				/* action code  */
				logs("RESET");
				processCommandReset();


				if(true){
					/* Transition from RESET to CA_INIT */
					evConsumed=16;

					/* OnEntry code of state CA_IN_PROGRESS */
					logs("CA_ANNOUNCED");

					/* OnEntry code of state CA_INIT */
					logs("INIT");
					processCommandInitialize();
					returnResult();

					/* adjust state variables  */
					stateVar =  CA_IN_PROGRESS;
					stateVarCA_IN_PROGRESS =  CA_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case RESET  */

			case CA_IN_PROGRESS:

				switch (stateVarCA_IN_PROGRESS) {

					case CA_GENERAL_AUTHENTICATE_PROCESSED:
						if(true){
							/* Transition from CA_GENERAL_AUTHENTICATE_PROCESSED to CA_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state CA_COMPLETED */
							logs("COMPLETED");
							returnResult();

							/* adjust state variables  */
							stateVar =  CA_COMPLETED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CA_GENERAL_AUTHENTICATE_PROCESSED  */

					case CA_GENERAL_AUTHENTICATE_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from CA_GENERAL_AUTHENTICATE_RECEIVED to CA_GENERAL_AUTHENTICATE_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state CA_GENERAL_AUTHENTICATE_PROCESSED */
							logs("GENERAL_AUTHENTICATE_PROCESSED");
							processCommandFinalize();

							/* adjust state variables  */
							stateVarCA_IN_PROGRESS =  CA_GENERAL_AUTHENTICATE_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CA_GENERAL_AUTHENTICATE_RECEIVED  */

					case CA_INIT:
						if(isAPDU("Set AT")){
							/* Transition from CA_INIT to CA_SET_AT_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state CA_SET_AT_RECEIVED */
							logs("SET_AT_RECEIVED");
							processCommandSetAT();

							/* adjust state variables  */
							stateVarCA_IN_PROGRESS =  CA_SET_AT_RECEIVED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CA_INIT  */

					case CA_SET_AT_PROCESSED:
						if(isAPDU("General Authenticate")){
							/* Transition from CA_SET_AT_PROCESSED to CA_GENERAL_AUTHENTICATE_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state CA_GENERAL_AUTHENTICATE_RECEIVED */
							logs("GENERAL_AUTHENTICATE_RECEIVED");
							processCommandGeneralAuthenticate();

							/* adjust state variables  */
							stateVarCA_IN_PROGRESS =  CA_GENERAL_AUTHENTICATE_RECEIVED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CA_SET_AT_PROCESSED  */

					case CA_SET_AT_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from CA_SET_AT_RECEIVED to CA_SET_AT_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state CA_SET_AT_PROCESSED */
							logs("SET_AT_PROCESSED");
							returnResult();

							/* adjust state variables  */
							stateVarCA_IN_PROGRESS =  CA_SET_AT_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case CA_SET_AT_RECEIVED  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch CA_IN_PROGRESS */

				/* Check if event was already processed  */
				if(evConsumed==0){

					if(true){
						if(warningOrErrorOccurredDuringProcessing()){
							/* Transition from CA_IN_PROGRESS to PROCESSING_ERROR */
							evConsumed=16;
						
							/* Action code for transition  */
							logs("error occurred during processing");


							/* adjust state variables  */
							stateVar =  PROCESSING_ERROR;
						}else{
							/* Transition from CA_IN_PROGRESS to CA_IN_PROGRESS */
							evConsumed=16;
						
							/* Action code for transition  */
							logs("unable to process APDU - returning to previous state");
							/* OnEntry code of state CA_IN_PROGRESS */
							logs("CA_ANNOUNCED");
							stateVar =  CA_IN_PROGRESS;/* entry chain  */
							if(stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_PROCESSED){
								logs("GENERAL_AUTHENTICATE_PROCESSED");
								processCommandFinalize();

							}else if(stateVarCA_IN_PROGRESS==  CA_GENERAL_AUTHENTICATE_RECEIVED){
								logs("GENERAL_AUTHENTICATE_RECEIVED");
								processCommandGeneralAuthenticate();

							}else if(stateVarCA_IN_PROGRESS==  CA_INIT){
								logs("INIT");
								processCommandInitialize();
								returnResult();

							}else if(stateVarCA_IN_PROGRESS==  CA_SET_AT_PROCESSED){
								logs("SET_AT_PROCESSED");
								returnResult();

							}else if(stateVarCA_IN_PROGRESS==  CA_SET_AT_RECEIVED){
								logs("SET_AT_RECEIVED");
								processCommandSetAT();

							}

						} /*end of event selection */
					}else{
						/* Intentionally left blank */
					} /*end of event selection */
				}
			break; /* end of case CA_IN_PROGRESS  */

			default:
				/* Intentionally left blank */
			break;
		} /* end switch stateVar_root */

	/* Post Action Code */
	}while (this.continueProcessing);
		return evConsumed;
	}
}
