package de.persosim.simulator.protocols.pace; 
/*
 * AUTOMATICALLY GENERATED CODE - DO NOT EDIT!
 * 
 * (C) 2013 HJP-Consulting GmbH
 */
 @SuppressWarnings("all")//generated code 

/* Command line options: -verbose -p EA -o DefaultPaceProtocol -l java -t PACE:paceclass C:\develop\wd\protocol_pace.xml   */
/* This file is generated from protocol_pace.xml - do not edit manually  */
/* Generated on: Thu Jul 03 14:56:23 CEST 2014 / version 3.52beta2 */



public class DefaultPaceProtocol extends AbstractPaceProtocol
{

	public static final int PACE_GET_NONCE_RECEIVED = 0;
	public static final int REGISTER_APDU_MUTUAL_AUTHENTICATE = 1;
	public static final int PACE_SET_AT_RECEIVED = 2;
	public static final int PACE_MUTUAL_AUTHENTICATE_RECEIVED = 3;
	public static final int DUMMY_INITIAL = 4;
	public static final int PACE_MAP_NONCE_RECEIVED = 5;
	public static final int REGISTER_APDU_GET_NONCE = 6;
	public static final int PACE_PERFORM_KEY_AGREEMENT_RECEIVED = 7;
	public static final int PACE_INIT = 8;
	public static final int RESET = 9;
	public static final int PACE_ANNOUNCED = 10;
	public static final int PROCESSING_ERROR = 11;
	public static final int PACE_PERFORM_KEY_AGREEMENT_PROCESSED = 12;
	public static final int REGISTER_APDU_PERFORM_KEY_AGREEMENT = 13;
	public static final int PACE_GET_NONCE_PROCESSED = 14;
	public static final int PACE_MAP_NONCE_PROCESSED = 15;
	public static final int REGISTER_APDU_SET_AT = 16;
	public static final int CHAINING_INTERRUPTED = 17;
	public static final int REGISTER_APDUS = 18;
	public static final int PACE_COMPLETED = 19;
	public static final int PACE_IN_PROGRESS = 20;
	public static final int PACE_MUTUAL_AUTHENTICATE_PROCESSED = 21;
	public static final int GENERAL_AUTHENTICATE_CHAINING = 22;
	public static final int REGISTER_APDU_MAP_NONCE = 23;
	public static final int PACE_SET_AT_PROCESSED = 24;
	public static final int __UNKNOWN_STATE__ = 25;


	public static final int DEFAULTPACEPROTOCOL_NO_MSG = 0;
	


	// flag if initialized
	protected boolean m_initialized=false;

	int  stateVar;
	int  stateVarREGISTER_APDUS;
	int  stateVarPACE_ANNOUNCED;
	int  stateVarPACE_IN_PROGRESS;
	int  stateVarGENERAL_AUTHENTICATE_CHAINING;

	// State handler class default ctor
	public DefaultPaceProtocol()
	{
	}

	/* Helper(s) to reset history */
	public void resetHistoryREGISTER_APDUS(){stateVarREGISTER_APDUS= REGISTER_APDU_SET_AT;}
	public void resetHistoryPACE_ANNOUNCED(){stateVarPACE_ANNOUNCED= PACE_INIT;}
	public void resetHistoryPACE_IN_PROGRESS(){stateVarPACE_IN_PROGRESS= PACE_SET_AT_RECEIVED;}
	public void resetHistoryGENERAL_AUTHENTICATE_CHAINING(){stateVarGENERAL_AUTHENTICATE_CHAINING= PACE_GET_NONCE_PROCESSED;}

	/* Helper to get innermost active state id */
	public int getInnermostActiveState() {
		if(isInPACE_MAP_NONCE_PROCESSED()){
			return PACE_MAP_NONCE_PROCESSED;
		}else if(isInPACE_GET_NONCE_PROCESSED()){
			return PACE_GET_NONCE_PROCESSED;
		}else if(isInPACE_PERFORM_KEY_AGREEMENT_PROCESSED()){
			return PACE_PERFORM_KEY_AGREEMENT_PROCESSED;
		}else if(isInPACE_PERFORM_KEY_AGREEMENT_RECEIVED()){
			return PACE_PERFORM_KEY_AGREEMENT_RECEIVED;
		}else if(isInPACE_MAP_NONCE_RECEIVED()){
			return PACE_MAP_NONCE_RECEIVED;
		}else if(isInPACE_MUTUAL_AUTHENTICATE_RECEIVED()){
			return PACE_MUTUAL_AUTHENTICATE_RECEIVED;
		}else if(isInPACE_SET_AT_PROCESSED()){
			return PACE_SET_AT_PROCESSED;
		}else if(isInPACE_SET_AT_RECEIVED()){
			return PACE_SET_AT_RECEIVED;
		}else if(isInPACE_GET_NONCE_RECEIVED()){
			return PACE_GET_NONCE_RECEIVED;
		}else if(isInREGISTER_APDU_MAP_NONCE()){
			return REGISTER_APDU_MAP_NONCE;
		}else if(isInPACE_MUTUAL_AUTHENTICATE_PROCESSED()){
			return PACE_MUTUAL_AUTHENTICATE_PROCESSED;
		}else if(isInREGISTER_APDU_SET_AT()){
			return REGISTER_APDU_SET_AT;
		}else if(isInREGISTER_APDU_PERFORM_KEY_AGREEMENT()){
			return REGISTER_APDU_PERFORM_KEY_AGREEMENT;
		}else if(isInPACE_INIT()){
			return PACE_INIT;
		}else if(isInREGISTER_APDU_GET_NONCE()){
			return REGISTER_APDU_GET_NONCE;
		}else if(isInREGISTER_APDU_MUTUAL_AUTHENTICATE()){
			return REGISTER_APDU_MUTUAL_AUTHENTICATE;
		}else if(isInPACE_COMPLETED()){
			return PACE_COMPLETED;
		}else if(isInCHAINING_INTERRUPTED()){
			return CHAINING_INTERRUPTED;
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
	public boolean isInPACE_GET_NONCE_RECEIVED(){return (((stateVarPACE_IN_PROGRESS==  PACE_GET_NONCE_RECEIVED)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_MUTUAL_AUTHENTICATE(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_MUTUAL_AUTHENTICATE)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPACE_SET_AT_RECEIVED(){return (((stateVarPACE_IN_PROGRESS==  PACE_SET_AT_RECEIVED)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPACE_MUTUAL_AUTHENTICATE_RECEIVED(){return (((stateVarGENERAL_AUTHENTICATE_CHAINING==  PACE_MUTUAL_AUTHENTICATE_RECEIVED)&&(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInDUMMY_INITIAL(){return (((stateVar==  DUMMY_INITIAL)) ? (true) : (false));}
	public boolean isInPACE_MAP_NONCE_RECEIVED(){return (((stateVarGENERAL_AUTHENTICATE_CHAINING==  PACE_MAP_NONCE_RECEIVED)&&(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_GET_NONCE(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_GET_NONCE)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPACE_PERFORM_KEY_AGREEMENT_RECEIVED(){return (((stateVarGENERAL_AUTHENTICATE_CHAINING==  PACE_PERFORM_KEY_AGREEMENT_RECEIVED)&&(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPACE_INIT(){return (((stateVarPACE_ANNOUNCED==  PACE_INIT)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInRESET(){return (((stateVar==  RESET)) ? (true) : (false));}
	public boolean isInPACE_ANNOUNCED(){return (((stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPROCESSING_ERROR(){return (((stateVar==  PROCESSING_ERROR)) ? (true) : (false));}
	public boolean isInPACE_PERFORM_KEY_AGREEMENT_PROCESSED(){return (((stateVarGENERAL_AUTHENTICATE_CHAINING==  PACE_PERFORM_KEY_AGREEMENT_PROCESSED)&&(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_PERFORM_KEY_AGREEMENT(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_PERFORM_KEY_AGREEMENT)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPACE_GET_NONCE_PROCESSED(){return (((stateVarGENERAL_AUTHENTICATE_CHAINING==  PACE_GET_NONCE_PROCESSED)&&(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPACE_MAP_NONCE_PROCESSED(){return (((stateVarGENERAL_AUTHENTICATE_CHAINING==  PACE_MAP_NONCE_PROCESSED)&&(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_SET_AT(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_SET_AT)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInCHAINING_INTERRUPTED(){return (((stateVar==  CHAINING_INTERRUPTED)) ? (true) : (false));}
	public boolean isInREGISTER_APDUS(){return (((stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPACE_COMPLETED(){return (((stateVar==  PACE_COMPLETED)) ? (true) : (false));}
	public boolean isInPACE_IN_PROGRESS(){return (((stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInPACE_MUTUAL_AUTHENTICATE_PROCESSED(){return (((stateVarPACE_ANNOUNCED==  PACE_MUTUAL_AUTHENTICATE_PROCESSED)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInGENERAL_AUTHENTICATE_CHAINING(){return (((stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_MAP_NONCE(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_MAP_NONCE)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInPACE_SET_AT_PROCESSED(){return (((stateVarPACE_IN_PROGRESS==  PACE_SET_AT_PROCESSED)&&(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS)&&(stateVar==  PACE_ANNOUNCED)) ? (true) : (false));}




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
			stateVarPACE_ANNOUNCED =  PACE_INIT; /* set init state of PACE_ANNOUNCED */
			stateVarPACE_IN_PROGRESS =  PACE_SET_AT_RECEIVED; /* set init state of PACE_IN_PROGRESS */
			stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED; /* set init state of GENERAL_AUTHENTICATE_CHAINING */

		}

	}

	protected void defaultpaceprotocolChangeToState(int  state){
		stateVar=state;
	}

	protected void defaultpaceprotocolChangeToStateREGISTER_APDUS(int  state){
		stateVarREGISTER_APDUS = state;
	}
	
	protected void defaultpaceprotocolChangeToStatePACE_ANNOUNCED(int  state){
		stateVarPACE_ANNOUNCED = state;
	}
	
	protected void defaultpaceprotocolChangeToStatePACE_IN_PROGRESS(int  state){
		stateVarPACE_IN_PROGRESS = state;
	}
	
	protected void defaultpaceprotocolChangeToStateGENERAL_AUTHENTICATE_CHAINING(int  state){
		stateVarGENERAL_AUTHENTICATE_CHAINING = state;
	}
	



	public int processEvent(int msg){

		int evConsumed = 0;

		

		if(m_initialized==false) return 0;

		/* action code */
		this.continueProcessing = true;
		do{
		evConsumed = 0;


		switch (stateVar) {

			case CHAINING_INTERRUPTED:
				/* action code  */
				processChainingInterrupted();


				if(true){
					/* Transition from CHAINING_INTERRUPTED to RESET */
					evConsumed=16;


					/* adjust state variables  */
					stateVar =  RESET;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case CHAINING_INTERRUPTED  */

			case DUMMY_INITIAL:
				if(true){
					if(isInitialized()){
						/* Transition from DUMMY_INITIAL to PACE_ANNOUNCED */
						evConsumed=16;

						/* OnEntry code of state PACE_ANNOUNCED */
						logs("PACE_ANNOUNCED");
						stateVar =  PACE_ANNOUNCED;/* entry chain  */
						if(stateVarPACE_ANNOUNCED==  PACE_INIT){
							logs("INIT");
							processCommandInitialize();
							returnResult();

						}else if(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS){
							logs("PACE_IN_PROGRESS");

							stateVarPACE_ANNOUNCED =  PACE_IN_PROGRESS;/* entry chain  */
							if(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING){
								
								logs("GET_NONCE_PROCESSED");
								returnResult();
								stateVarPACE_IN_PROGRESS =  GENERAL_AUTHENTICATE_CHAINING;/* Default in entry chain  */
								stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED;/* Default in entry chain  */
							}else if(stateVarPACE_IN_PROGRESS==  PACE_GET_NONCE_RECEIVED){
								logs("GET_NONCE_RECEIVED");
								processCommandGetNonce();

							}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_PROCESSED){
								logs("SET_AT_PROCESSED");
								returnResult();

							}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_RECEIVED){
								logs("SET_AT_RECEIVED");
								processCommandSetAT();

							}
						}else if(stateVarPACE_ANNOUNCED==  PACE_MUTUAL_AUTHENTICATE_PROCESSED){
							logs("MA_PROCESSED");
							processCommandFinalize();

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

			case PACE_COMPLETED:
				if(true){
					/* Transition from PACE_COMPLETED to PACE_INIT */
					evConsumed=16;

					/* OnEntry code of state PACE_ANNOUNCED */
					logs("PACE_ANNOUNCED");

					/* OnEntry code of state PACE_INIT */
					logs("INIT");
					processCommandInitialize();
					returnResult();

					/* adjust state variables  */
					stateVar =  PACE_ANNOUNCED;
					stateVarPACE_ANNOUNCED =  PACE_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case PACE_COMPLETED  */

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

					case REGISTER_APDU_GET_NONCE:
						/* action code  */
						createNewApduSpecification("Get Nonce");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_4);
						apduSpecification.setChaining(true);
						apduSpecification.setIns(INS_86_GENERAL_AUTHENTICATE);
						apduSpecification.setP1((byte) 0x00);
						apduSpecification.setP2((byte) 0x00);
						apduSpecification.addTag(TAG_7C);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_GET_NONCE to REGISTER_APDU_MAP_NONCE */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_MAP_NONCE;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_GET_NONCE  */

					case REGISTER_APDU_MAP_NONCE:
						/* action code  */
						createNewApduSpecification("Map Nonce");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_4);
						apduSpecification.setChaining(true);
						apduSpecification.setIns(INS_86_GENERAL_AUTHENTICATE);
						apduSpecification.setP1((byte) 0x00);
						apduSpecification.setP2((byte) 0x00);
						createNewTagSpecification(TAG_7C);
						apduSpecification.addTag(tagSpecification);
						createNewPath();
						path.add(TAG_7C);
						createNewTagSpecification(TAG_81);
						apduSpecification.addTag(path, tagSpecification);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_MAP_NONCE to REGISTER_APDU_PERFORM_KEY_AGREEMENT */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_PERFORM_KEY_AGREEMENT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_MAP_NONCE  */

					case REGISTER_APDU_MUTUAL_AUTHENTICATE:
						/* action code  */
						createNewApduSpecification("Mutual Authenticate");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_4);
						apduSpecification.setChaining(false);
						apduSpecification.setIns(INS_86_GENERAL_AUTHENTICATE);
						apduSpecification.setP1((byte) 0x00);
						apduSpecification.setP2((byte) 0x00);
						createNewTagSpecification(TAG_7C);
						apduSpecification.addTag(tagSpecification);
						createNewPath();
						path.add(TAG_7C);
						createNewTagSpecification(TAG_85);
						apduSpecification.addTag(path, tagSpecification);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_MUTUAL_AUTHENTICATE to PACE_ANNOUNCED */
							evConsumed=16;


							/* OnEntry code of state PACE_ANNOUNCED */
							logs("PACE_ANNOUNCED");
							stateVar =  PACE_ANNOUNCED;/* entry chain  */
							if(stateVarPACE_ANNOUNCED==  PACE_INIT){
								logs("INIT");
								processCommandInitialize();
								returnResult();

							}else if(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS){
								logs("PACE_IN_PROGRESS");

								stateVarPACE_ANNOUNCED =  PACE_IN_PROGRESS;/* entry chain  */
								if(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING){
									
									logs("GET_NONCE_PROCESSED");
									returnResult();
									stateVarPACE_IN_PROGRESS =  GENERAL_AUTHENTICATE_CHAINING;/* Default in entry chain  */
									stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED;/* Default in entry chain  */
								}else if(stateVarPACE_IN_PROGRESS==  PACE_GET_NONCE_RECEIVED){
									logs("GET_NONCE_RECEIVED");
									processCommandGetNonce();

								}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_PROCESSED){
									logs("SET_AT_PROCESSED");
									returnResult();

								}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_RECEIVED){
									logs("SET_AT_RECEIVED");
									processCommandSetAT();

								}
							}else if(stateVarPACE_ANNOUNCED==  PACE_MUTUAL_AUTHENTICATE_PROCESSED){
								logs("MA_PROCESSED");
								processCommandFinalize();

							}

							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_SET_AT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_MUTUAL_AUTHENTICATE  */

					case REGISTER_APDU_PERFORM_KEY_AGREEMENT:
						/* action code  */
						createNewApduSpecification("Perform Key Agreement");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_4);
						apduSpecification.setChaining(true);
						apduSpecification.setIns(INS_86_GENERAL_AUTHENTICATE);
						apduSpecification.setP1((byte) 0x00);
						apduSpecification.setP2((byte) 0x00);
						createNewTagSpecification(TAG_7C);
						apduSpecification.addTag(tagSpecification);
						createNewPath();
						path.add(TAG_7C);
						createNewTagSpecification(TAG_83);
						apduSpecification.addTag(path, tagSpecification);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_PERFORM_KEY_AGREEMENT to REGISTER_APDU_MUTUAL_AUTHENTICATE */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_MUTUAL_AUTHENTICATE;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_PERFORM_KEY_AGREEMENT  */

					case REGISTER_APDU_SET_AT:
						/* action code  */
						createNewApduSpecification("Set AT");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setIsoCase(ISO_CASE_3);
						apduSpecification.setChaining(false);
						apduSpecification.setIns(INS_22_MANAGE_SECURITY_ENVIRONMENT);
						apduSpecification.setP1((byte) 0xC1);
						apduSpecification.setP2((byte) 0xA4);
						createNewTagSpecification(TAG_80);
						apduSpecification.addTag(tagSpecification);
						createNewTagSpecification(TAG_83);
						apduSpecification.addTag(tagSpecification);
						createNewTagSpecification(TAG_7F4C);
						tagSpecification.setRequired(REQ_OPTIONAL);
						tagSpecification.setAllowUnspecifiedSubTags(true);
						apduSpecification.addTag(tagSpecification);
						createNewTagSpecification(TAG_84);
						tagSpecification.setRequired(REQ_OPTIONAL);
						apduSpecification.addTag(tagSpecification);
						apduSpecification.setInitialApdu();
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_SET_AT to REGISTER_APDU_GET_NONCE */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_GET_NONCE;
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
					/* Transition from RESET to PACE_INIT */
					evConsumed=16;

					/* OnEntry code of state PACE_ANNOUNCED */
					logs("PACE_ANNOUNCED");

					/* OnEntry code of state PACE_INIT */
					logs("INIT");
					processCommandInitialize();
					returnResult();

					/* adjust state variables  */
					stateVar =  PACE_ANNOUNCED;
					stateVarPACE_ANNOUNCED =  PACE_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case RESET  */

			case PACE_ANNOUNCED:

				switch (stateVarPACE_ANNOUNCED) {

					case PACE_INIT:
						if(isAPDU("Set AT")){
							/* Transition from PACE_INIT to PACE_IN_PROGRESS */
							evConsumed=16;

							/* OnEntry code of state PACE_IN_PROGRESS */
							logs("PACE_IN_PROGRESS");
							stateVarPACE_ANNOUNCED =  PACE_IN_PROGRESS;/* entry chain  */
							if(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING){
								
								logs("GET_NONCE_PROCESSED");
								returnResult();
								stateVarPACE_IN_PROGRESS =  GENERAL_AUTHENTICATE_CHAINING;/* Default in entry chain  */
								stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED;/* Default in entry chain  */
							}else if(stateVarPACE_IN_PROGRESS==  PACE_GET_NONCE_RECEIVED){
								logs("GET_NONCE_RECEIVED");
								processCommandGetNonce();

							}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_PROCESSED){
								logs("SET_AT_PROCESSED");
								returnResult();

							}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_RECEIVED){
								logs("SET_AT_RECEIVED");
								processCommandSetAT();

							}

						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PACE_INIT  */

					case PACE_IN_PROGRESS:

						switch (stateVarPACE_IN_PROGRESS) {

							case GENERAL_AUTHENTICATE_CHAINING:

								switch (stateVarGENERAL_AUTHENTICATE_CHAINING) {

									case PACE_GET_NONCE_PROCESSED:
										if(isAPDU("Map Nonce")){
											/* Transition from PACE_GET_NONCE_PROCESSED to PACE_MAP_NONCE_RECEIVED */
											evConsumed=16;

											/* OnEntry code of state PACE_MAP_NONCE_RECEIVED */
											logs("MAP_NONCE_RECEIVED");
											processCommandMapNonce();

											/* adjust state variables  */
											stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_MAP_NONCE_RECEIVED;
										}else{
											/* Intentionally left blank */
										} /*end of event selection */
									break; /* end of case PACE_GET_NONCE_PROCESSED  */

									case PACE_MAP_NONCE_PROCESSED:
										if(isAPDU("Perform Key Agreement")){
											/* Transition from PACE_MAP_NONCE_PROCESSED to PACE_PERFORM_KEY_AGREEMENT_RECEIVED */
											evConsumed=16;

											/* OnEntry code of state PACE_PERFORM_KEY_AGREEMENT_RECEIVED */
											logs("PKA_RECEIVED");
											processCommandPerformKeyAgreement();

											/* adjust state variables  */
											stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_PERFORM_KEY_AGREEMENT_RECEIVED;
										}else{
											/* Intentionally left blank */
										} /*end of event selection */
									break; /* end of case PACE_MAP_NONCE_PROCESSED  */

									case PACE_MAP_NONCE_RECEIVED:
										if(isStatusWord(SW_9000_NO_ERROR)){
											/* Transition from PACE_MAP_NONCE_RECEIVED to PACE_MAP_NONCE_PROCESSED */
											evConsumed=16;

											/* OnEntry code of state PACE_MAP_NONCE_PROCESSED */
											logs("MAP_NONCE_PROCESSED");
											returnResult();

											/* adjust state variables  */
											stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_MAP_NONCE_PROCESSED;
										}else{
											/* Intentionally left blank */
										} /*end of event selection */
									break; /* end of case PACE_MAP_NONCE_RECEIVED  */

									case PACE_PERFORM_KEY_AGREEMENT_PROCESSED:
										if(isAPDU("Mutual Authenticate")){
											/* Transition from PACE_PERFORM_KEY_AGREEMENT_PROCESSED to PACE_MUTUAL_AUTHENTICATE_RECEIVED */
											evConsumed=16;

											/* OnEntry code of state PACE_MUTUAL_AUTHENTICATE_RECEIVED */
											logs("MA_RECEIVED");
											processCommandMutualAuthenticate();

											/* adjust state variables  */
											stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_MUTUAL_AUTHENTICATE_RECEIVED;
										}else{
											/* Intentionally left blank */
										} /*end of event selection */
									break; /* end of case PACE_PERFORM_KEY_AGREEMENT_PROCESSED  */

									case PACE_PERFORM_KEY_AGREEMENT_RECEIVED:
										if(isStatusWord(SW_9000_NO_ERROR)){
											/* Transition from PACE_PERFORM_KEY_AGREEMENT_RECEIVED to PACE_PERFORM_KEY_AGREEMENT_PROCESSED */
											evConsumed=16;

											/* OnEntry code of state PACE_PERFORM_KEY_AGREEMENT_PROCESSED */
											logs("PKA_PROCESSED");
											returnResult();

											/* adjust state variables  */
											stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_PERFORM_KEY_AGREEMENT_PROCESSED;
										}else{
											/* Intentionally left blank */
										} /*end of event selection */
									break; /* end of case PACE_PERFORM_KEY_AGREEMENT_RECEIVED  */

									case PACE_MUTUAL_AUTHENTICATE_RECEIVED:
										if(isStatusWord(SW_9000_NO_ERROR)){
											/* Transition from PACE_MUTUAL_AUTHENTICATE_RECEIVED to PACE_MUTUAL_AUTHENTICATE_PROCESSED */
											evConsumed=16;



											/* OnEntry code of state PACE_MUTUAL_AUTHENTICATE_PROCESSED */
											logs("MA_PROCESSED");
											processCommandFinalize();

											/* adjust state variables  */
											stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED;
											stateVarPACE_ANNOUNCED =  PACE_MUTUAL_AUTHENTICATE_PROCESSED;
										}else{
											/* Intentionally left blank */
										} /*end of event selection */
									break; /* end of case PACE_MUTUAL_AUTHENTICATE_RECEIVED  */

									default:
										/* Intentionally left blank */
									break;
								} /* end switch GENERAL_AUTHENTICATE_CHAINING */

								/* Check if event was already processed  */
								if(evConsumed==0){

									if(true){
										if(warningOrErrorOccurredDuringProcessing()){
											/* Transition from GENERAL_AUTHENTICATE_CHAINING to PROCESSING_ERROR */
											evConsumed=16;
										



											/* adjust state variables  */
											stateVar =  PROCESSING_ERROR;
										}else{
											/* Transition from GENERAL_AUTHENTICATE_CHAINING to CHAINING_INTERRUPTED */
											evConsumed=16;
										



											/* adjust state variables  */
											stateVar =  CHAINING_INTERRUPTED;
										} /*end of event selection */
									}else{
										/* Intentionally left blank */
									} /*end of event selection */
								}
							break; /* end of case GENERAL_AUTHENTICATE_CHAINING  */

							case PACE_GET_NONCE_RECEIVED:
								if(isStatusWord(SW_9000_NO_ERROR)){
									/* Transition from PACE_GET_NONCE_RECEIVED to GENERAL_AUTHENTICATE_CHAINING */
									evConsumed=16;

									logs("GET_NONCE_PROCESSED");
									returnResult();
									stateVarPACE_IN_PROGRESS =  GENERAL_AUTHENTICATE_CHAINING;/* Default in entry chain  */
									stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED;/* Default in entry chain  */

								}else{
									/* Intentionally left blank */
								} /*end of event selection */
							break; /* end of case PACE_GET_NONCE_RECEIVED  */

							case PACE_SET_AT_PROCESSED:
								if(isAPDU("Get Nonce")){
									/* Transition from PACE_SET_AT_PROCESSED to PACE_GET_NONCE_RECEIVED */
									evConsumed=16;

									/* OnEntry code of state PACE_GET_NONCE_RECEIVED */
									logs("GET_NONCE_RECEIVED");
									processCommandGetNonce();

									/* adjust state variables  */
									stateVarPACE_IN_PROGRESS =  PACE_GET_NONCE_RECEIVED;
								}else{
									/* Intentionally left blank */
								} /*end of event selection */
							break; /* end of case PACE_SET_AT_PROCESSED  */

							case PACE_SET_AT_RECEIVED:
								if(isStatusWord(SW_9000_NO_ERROR)){
									/* Transition from PACE_SET_AT_RECEIVED to PACE_SET_AT_PROCESSED */
									evConsumed=16;

									/* OnEntry code of state PACE_SET_AT_PROCESSED */
									logs("SET_AT_PROCESSED");
									returnResult();

									/* adjust state variables  */
									stateVarPACE_IN_PROGRESS =  PACE_SET_AT_PROCESSED;
								}else if(isStatusWord_63CX_Counter()){
									/* Transition from PACE_SET_AT_RECEIVED to PACE_SET_AT_PROCESSED */
									evConsumed=16;

									/* OnEntry code of state PACE_SET_AT_PROCESSED */
									logs("SET_AT_PROCESSED");
									returnResult();

									/* adjust state variables  */
									stateVarPACE_IN_PROGRESS =  PACE_SET_AT_PROCESSED;
								}else{
									/* Intentionally left blank */
								} /*end of event selection */
							break; /* end of case PACE_SET_AT_RECEIVED  */

							default:
								/* Intentionally left blank */
							break;
						} /* end switch PACE_IN_PROGRESS */
					break; /* end of case PACE_IN_PROGRESS  */

					case PACE_MUTUAL_AUTHENTICATE_PROCESSED:
						if(true){
							/* Transition from PACE_MUTUAL_AUTHENTICATE_PROCESSED to PACE_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state PACE_COMPLETED */
							logs("COMPLETED");
							returnResult();

							/* adjust state variables  */
							stateVar =  PACE_COMPLETED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case PACE_MUTUAL_AUTHENTICATE_PROCESSED  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch PACE_ANNOUNCED */

				/* Check if event was already processed  */
				if(evConsumed==0){

					if(true){
						if(warningOrErrorOccurredDuringProcessing()){
							/* Transition from PACE_ANNOUNCED to RESET */
							evConsumed=16;
						
							if(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS){

								if(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING){
									
								}
								
							}

							/* Action code for transition  */
							logs("error occurred during processing");


							/* adjust state variables  */
							stateVar =  RESET;
						}else{
							/* Transition from PACE_ANNOUNCED to PACE_ANNOUNCED */
							evConsumed=16;
						
							if(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS){

								if(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING){
									
								}
								
							}

							/* Action code for transition  */
							logs("unable to process APDU - returning to previous state");

							/* OnEntry code of state PACE_ANNOUNCED */
							logs("PACE_ANNOUNCED");
							stateVar =  PACE_ANNOUNCED;/* entry chain  */
							if(stateVarPACE_ANNOUNCED==  PACE_INIT){
								logs("INIT");
								processCommandInitialize();
								returnResult();

							}else if(stateVarPACE_ANNOUNCED==  PACE_IN_PROGRESS){
								logs("PACE_IN_PROGRESS");

								stateVarPACE_ANNOUNCED =  PACE_IN_PROGRESS;/* entry chain  */
								if(stateVarPACE_IN_PROGRESS==  GENERAL_AUTHENTICATE_CHAINING){
									
									logs("GET_NONCE_PROCESSED");
									returnResult();
									stateVarPACE_IN_PROGRESS =  GENERAL_AUTHENTICATE_CHAINING;/* Default in entry chain  */
									stateVarGENERAL_AUTHENTICATE_CHAINING =  PACE_GET_NONCE_PROCESSED;/* Default in entry chain  */
								}else if(stateVarPACE_IN_PROGRESS==  PACE_GET_NONCE_RECEIVED){
									logs("GET_NONCE_RECEIVED");
									processCommandGetNonce();

								}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_PROCESSED){
									logs("SET_AT_PROCESSED");
									returnResult();

								}else if(stateVarPACE_IN_PROGRESS==  PACE_SET_AT_RECEIVED){
									logs("SET_AT_RECEIVED");
									processCommandSetAT();

								}
							}else if(stateVarPACE_ANNOUNCED==  PACE_MUTUAL_AUTHENTICATE_PROCESSED){
								logs("MA_PROCESSED");
								processCommandFinalize();

							}

						} /*end of event selection */
					}else{
						/* Intentionally left blank */
					} /*end of event selection */
				}
			break; /* end of case PACE_ANNOUNCED  */

			default:
				/* Intentionally left blank */
			break;
		} /* end switch stateVar_root */

	/* Post Action Code */
	}while (this.continueProcessing);
		return evConsumed;
	}
}
