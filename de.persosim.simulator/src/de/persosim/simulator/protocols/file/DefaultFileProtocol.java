package de.persosim.simulator.protocols.file; 
/*
 * AUTOMATICALLY GENERATED CODE - DO NOT EDIT!
 * 
 * (C) 2013 HJP-Consulting GmbH
 */
 @SuppressWarnings("all")//generated code 

/* Command line options: -verbose -p EA -o DefaultFileProtocol -l java -t FM:fmclass C:\workunits\complete\codegen\modelExports\protocol_file.xml   */
/* This file is generated from protocol_file.xml - do not edit manually  */
/* Generated on: Mon Nov 16 16:59:45 CET 2015 / version 3.6.11 */



public class DefaultFileProtocol extends AbstractFileProtocol
{

	public static final int UPDATE_BINARY_PROCESSED = 0;
	public static final int FM_ANNOUNCED = 1;
	public static final int READ_BINARY_PROCESSED = 2;
	public static final int FM_INIT = 3;
	public static final int ERASE_BINARY_ODD_PROCESSED = 4;
	public static final int REGISTER_APDU_SELECT_FILE = 5;
	public static final int FM_COMPLETED = 6;
	public static final int REGISTER_APDU_READ_BINARY_ODD = 7;
	public static final int REGISTER_APDU_ERASE_BINARY_ODD = 8;
	public static final int SELECT_FILE_PROCESSED = 9;
	public static final int READ_BINARY_ODD_RECEIVED = 10;
	public static final int UPDATE_BINARY_ODD_RECEIVED = 11;
	public static final int ERASE_BINARY_ODD_RECEIVED = 12;
	public static final int SELECT_FILE_RECEIVED = 13;
	public static final int ERASE_BINARY_PROCESSED = 14;
	public static final int REGISTER_APDU_UPDATE_BINARY_ODD = 15;
	public static final int ERROR_HANDLING = 16;
	public static final int REGISTER_APDUS = 17;
	public static final int REGISTER_APDU_UPDATE_BINARY = 18;
	public static final int UPDATE_BINARY_RECEIVED = 19;
	public static final int READ_BINARY_RECEIVED = 20;
	public static final int UPDATE_BINARY_ODD_PROCESSED = 21;
	public static final int READ_BINARY_ODD_PROCESSED = 22;
	public static final int ERASE_BINARY_RECEIVED = 23;
	public static final int REGISTER_APDU_ERASE_BINARY = 24;
	public static final int REGISTER_APDU_READ_BINARY = 25;
	public static final int __UNKNOWN_STATE__ = 26;


	public static final int DEFAULTFILEPROTOCOL_NO_MSG = 0;
	


	// flag if initialized
	protected boolean m_initialized=false;

	int  stateVar;
	int  stateVarFM_ANNOUNCED;
	int  stateVarREGISTER_APDUS;

	// State handler class default ctor
	public DefaultFileProtocol()
	{
	}

	/* Helper(s) to reset history */
	public void resetHistoryFM_ANNOUNCED(){stateVarFM_ANNOUNCED= FM_INIT;}
	public void resetHistoryREGISTER_APDUS(){stateVarREGISTER_APDUS= REGISTER_APDU_SELECT_FILE;}

	/* Helper to get innermost active state id */
	public int getInnermostActiveState() {
		if(isInREGISTER_APDU_READ_BINARY()){
			return REGISTER_APDU_READ_BINARY;
		}else if(isInREGISTER_APDU_ERASE_BINARY()){
			return REGISTER_APDU_ERASE_BINARY;
		}else if(isInERASE_BINARY_RECEIVED()){
			return ERASE_BINARY_RECEIVED;
		}else if(isInREAD_BINARY_ODD_PROCESSED()){
			return READ_BINARY_ODD_PROCESSED;
		}else if(isInUPDATE_BINARY_ODD_PROCESSED()){
			return UPDATE_BINARY_ODD_PROCESSED;
		}else if(isInREAD_BINARY_RECEIVED()){
			return READ_BINARY_RECEIVED;
		}else if(isInUPDATE_BINARY_RECEIVED()){
			return UPDATE_BINARY_RECEIVED;
		}else if(isInREGISTER_APDU_UPDATE_BINARY()){
			return REGISTER_APDU_UPDATE_BINARY;
		}else if(isInREGISTER_APDU_UPDATE_BINARY_ODD()){
			return REGISTER_APDU_UPDATE_BINARY_ODD;
		}else if(isInERASE_BINARY_PROCESSED()){
			return ERASE_BINARY_PROCESSED;
		}else if(isInSELECT_FILE_RECEIVED()){
			return SELECT_FILE_RECEIVED;
		}else if(isInERASE_BINARY_ODD_RECEIVED()){
			return ERASE_BINARY_ODD_RECEIVED;
		}else if(isInUPDATE_BINARY_ODD_RECEIVED()){
			return UPDATE_BINARY_ODD_RECEIVED;
		}else if(isInREAD_BINARY_ODD_RECEIVED()){
			return READ_BINARY_ODD_RECEIVED;
		}else if(isInSELECT_FILE_PROCESSED()){
			return SELECT_FILE_PROCESSED;
		}else if(isInREGISTER_APDU_ERASE_BINARY_ODD()){
			return REGISTER_APDU_ERASE_BINARY_ODD;
		}else if(isInREGISTER_APDU_READ_BINARY_ODD()){
			return REGISTER_APDU_READ_BINARY_ODD;
		}else if(isInREGISTER_APDU_SELECT_FILE()){
			return REGISTER_APDU_SELECT_FILE;
		}else if(isInERASE_BINARY_ODD_PROCESSED()){
			return ERASE_BINARY_ODD_PROCESSED;
		}else if(isInFM_INIT()){
			return FM_INIT;
		}else if(isInREAD_BINARY_PROCESSED()){
			return READ_BINARY_PROCESSED;
		}else if(isInUPDATE_BINARY_PROCESSED()){
			return UPDATE_BINARY_PROCESSED;
		}else if(isInERROR_HANDLING()){
			return ERROR_HANDLING;
		}else if(isInFM_COMPLETED()){
			return FM_COMPLETED;
		}else{
			return __UNKNOWN_STATE__;
		}
	}

	// Helper(s) to find out if the machine is in a certain state
	public boolean isInUPDATE_BINARY_PROCESSED(){return (((stateVarFM_ANNOUNCED==  UPDATE_BINARY_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInFM_ANNOUNCED(){return (((stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREAD_BINARY_PROCESSED(){return (((stateVarFM_ANNOUNCED==  READ_BINARY_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInFM_INIT(){return (((stateVarFM_ANNOUNCED==  FM_INIT)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInERASE_BINARY_ODD_PROCESSED(){return (((stateVarFM_ANNOUNCED==  ERASE_BINARY_ODD_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_SELECT_FILE(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_SELECT_FILE)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInFM_COMPLETED(){return (((stateVar==  FM_COMPLETED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_READ_BINARY_ODD(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_READ_BINARY_ODD)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_ERASE_BINARY_ODD(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_ERASE_BINARY_ODD)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInSELECT_FILE_PROCESSED(){return (((stateVarFM_ANNOUNCED==  SELECT_FILE_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREAD_BINARY_ODD_RECEIVED(){return (((stateVarFM_ANNOUNCED==  READ_BINARY_ODD_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInUPDATE_BINARY_ODD_RECEIVED(){return (((stateVarFM_ANNOUNCED==  UPDATE_BINARY_ODD_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInERASE_BINARY_ODD_RECEIVED(){return (((stateVarFM_ANNOUNCED==  ERASE_BINARY_ODD_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInSELECT_FILE_RECEIVED(){return (((stateVarFM_ANNOUNCED==  SELECT_FILE_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInERASE_BINARY_PROCESSED(){return (((stateVarFM_ANNOUNCED==  ERASE_BINARY_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_UPDATE_BINARY_ODD(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_UPDATE_BINARY_ODD)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInERROR_HANDLING(){return (((stateVar==  ERROR_HANDLING)) ? (true) : (false));}
	public boolean isInREGISTER_APDUS(){return (((stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_UPDATE_BINARY(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_UPDATE_BINARY)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInUPDATE_BINARY_RECEIVED(){return (((stateVarFM_ANNOUNCED==  UPDATE_BINARY_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREAD_BINARY_RECEIVED(){return (((stateVarFM_ANNOUNCED==  READ_BINARY_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInUPDATE_BINARY_ODD_PROCESSED(){return (((stateVarFM_ANNOUNCED==  UPDATE_BINARY_ODD_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREAD_BINARY_ODD_PROCESSED(){return (((stateVarFM_ANNOUNCED==  READ_BINARY_ODD_PROCESSED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInERASE_BINARY_RECEIVED(){return (((stateVarFM_ANNOUNCED==  ERASE_BINARY_RECEIVED)&&(stateVar==  FM_ANNOUNCED)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_ERASE_BINARY(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_ERASE_BINARY)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}
	public boolean isInREGISTER_APDU_READ_BINARY(){return (((stateVarREGISTER_APDUS==  REGISTER_APDU_READ_BINARY)&&(stateVar==  REGISTER_APDUS)) ? (true) : (false));}




	// Reinitialize the state machine
	public void reInitialize(){
			m_initialized=false;
			initialize();
	}

	public void initialize(){

		if(m_initialized==false){

			m_initialized=true;

			// Set state vars to default states
			stateVar =  REGISTER_APDUS; /* set init state of top state */
			stateVarFM_ANNOUNCED =  FM_INIT; /* set init state of FM_ANNOUNCED */
			stateVarREGISTER_APDUS =  REGISTER_APDU_SELECT_FILE; /* set init state of REGISTER_APDUS */


			//call on entry code of default states
			

		}

	}

	protected void defaultfileprotocolChangeToState(int  state){
		stateVar=state;
	}

	protected void defaultfileprotocolChangeToStateFM_ANNOUNCED(int  state){
		stateVarFM_ANNOUNCED = state;
	}
	
	protected void defaultfileprotocolChangeToStateREGISTER_APDUS(int  state){
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
					/* Transition from ERROR_HANDLING to FM_ANNOUNCED */
					evConsumed=16;

					logs("INIT");
					returnResult();
					stateVar =  FM_ANNOUNCED;/* Default in entry chain  */
					stateVarFM_ANNOUNCED =  FM_INIT;/* Default in entry chain  */

				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case ERROR_HANDLING  */

			case FM_ANNOUNCED:

				switch (stateVarFM_ANNOUNCED) {

					case ERASE_BINARY_ODD_PROCESSED:
						if(true){
							/* Transition from ERASE_BINARY_ODD_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ERASE_BINARY_ODD_PROCESSED  */

					case ERASE_BINARY_ODD_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from ERASE_BINARY_ODD_RECEIVED to ERASE_BINARY_ODD_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  ERASE_BINARY_ODD_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from ERASE_BINARY_ODD_RECEIVED to ERASE_BINARY_ODD_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  ERASE_BINARY_ODD_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ERASE_BINARY_ODD_RECEIVED  */

					case ERASE_BINARY_PROCESSED:
						if(true){
							/* Transition from ERASE_BINARY_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ERASE_BINARY_PROCESSED  */

					case ERASE_BINARY_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from ERASE_BINARY_RECEIVED to ERASE_BINARY_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  ERASE_BINARY_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from ERASE_BINARY_RECEIVED to ERASE_BINARY_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  ERASE_BINARY_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case ERASE_BINARY_RECEIVED  */

					case FM_INIT:
						if(isAPDU("Erase Binary Odd")){
							/* Transition from FM_INIT to ERASE_BINARY_ODD_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state ERASE_BINARY_ODD_RECEIVED */
							processCommandEraseBinaryOdd();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  ERASE_BINARY_ODD_RECEIVED;
						}else if(isAPDU("Erase Binary")){
							/* Transition from FM_INIT to ERASE_BINARY_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state ERASE_BINARY_RECEIVED */
							processCommandEraseBinary();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  ERASE_BINARY_RECEIVED;
						}else if(isAPDU("Read Binary Odd")){
							/* Transition from FM_INIT to READ_BINARY_ODD_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state READ_BINARY_ODD_RECEIVED */
							processCommandReadBinary();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  READ_BINARY_ODD_RECEIVED;
						}else if(isAPDU("Read Binary")){
							/* Transition from FM_INIT to READ_BINARY_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state READ_BINARY_RECEIVED */
							logs("READ_BINARY_RECEIVED");
							processCommandReadBinary();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  READ_BINARY_RECEIVED;
						}else if(isAPDU("Select File")){
							/* Transition from FM_INIT to SELECT_FILE_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state SELECT_FILE_RECEIVED */
							processCommandSelectFile();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  SELECT_FILE_RECEIVED;
						}else if(isAPDU("Update Binary Odd")){
							/* Transition from FM_INIT to UPDATE_BINARY_ODD_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state UPDATE_BINARY_ODD_RECEIVED */
							processCommandUpdateBinary();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  UPDATE_BINARY_ODD_RECEIVED;
						}else if(isAPDU("Update Binary")){
							/* Transition from FM_INIT to UPDATE_BINARY_RECEIVED */
							evConsumed=16;

							/* OnEntry code of state UPDATE_BINARY_RECEIVED */
							logs("UPDATE BINARY RECEIVED");
							processCommandUpdateBinary();

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  UPDATE_BINARY_RECEIVED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case FM_INIT  */

					case READ_BINARY_ODD_PROCESSED:
						if(true){
							/* Transition from READ_BINARY_ODD_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case READ_BINARY_ODD_PROCESSED  */

					case READ_BINARY_ODD_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from READ_BINARY_ODD_RECEIVED to READ_BINARY_ODD_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  READ_BINARY_ODD_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from READ_BINARY_ODD_RECEIVED to READ_BINARY_ODD_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  READ_BINARY_ODD_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case READ_BINARY_ODD_RECEIVED  */

					case READ_BINARY_PROCESSED:
						if(true){
							/* Transition from READ_BINARY_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case READ_BINARY_PROCESSED  */

					case READ_BINARY_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from READ_BINARY_RECEIVED to READ_BINARY_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state READ_BINARY_PROCESSED */
							logs("READ_BINARY_PROCESSED");

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  READ_BINARY_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from READ_BINARY_RECEIVED to READ_BINARY_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state READ_BINARY_PROCESSED */
							logs("READ_BINARY_PROCESSED");

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  READ_BINARY_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case READ_BINARY_RECEIVED  */

					case SELECT_FILE_PROCESSED:
						if(true){
							/* Transition from SELECT_FILE_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case SELECT_FILE_PROCESSED  */

					case SELECT_FILE_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from SELECT_FILE_RECEIVED to SELECT_FILE_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  SELECT_FILE_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case SELECT_FILE_RECEIVED  */

					case UPDATE_BINARY_ODD_PROCESSED:
						if(true){
							/* Transition from UPDATE_BINARY_ODD_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UPDATE_BINARY_ODD_PROCESSED  */

					case UPDATE_BINARY_ODD_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from UPDATE_BINARY_ODD_RECEIVED to UPDATE_BINARY_ODD_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  UPDATE_BINARY_ODD_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from UPDATE_BINARY_ODD_RECEIVED to UPDATE_BINARY_ODD_PROCESSED */
							evConsumed=16;


							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  UPDATE_BINARY_ODD_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UPDATE_BINARY_ODD_RECEIVED  */

					case UPDATE_BINARY_PROCESSED:
						if(true){
							/* Transition from UPDATE_BINARY_PROCESSED to FM_COMPLETED */
							evConsumed=16;


							/* OnEntry code of state FM_COMPLETED */
							returnResult();

							/* adjust state variables  */
							stateVar =  FM_COMPLETED;
							stateVarFM_ANNOUNCED =  FM_INIT;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UPDATE_BINARY_PROCESSED  */

					case UPDATE_BINARY_RECEIVED:
						if(isStatusWord(SW_9000_NO_ERROR)){
							/* Transition from UPDATE_BINARY_RECEIVED to UPDATE_BINARY_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state UPDATE_BINARY_PROCESSED */
							logs("UPDATE BINARY PROCESSED");

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  UPDATE_BINARY_PROCESSED;
						}else if(isStatusWord_63CX_Counter()){
							/* Transition from UPDATE_BINARY_RECEIVED to UPDATE_BINARY_PROCESSED */
							evConsumed=16;

							/* OnEntry code of state UPDATE_BINARY_PROCESSED */
							logs("UPDATE BINARY PROCESSED");

							/* adjust state variables  */
							stateVarFM_ANNOUNCED =  UPDATE_BINARY_PROCESSED;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case UPDATE_BINARY_RECEIVED  */

					default:
						/* Intentionally left blank */
					break;
				} /* end switch FM_ANNOUNCED */

				/* Check if event was already processed  */
				if(evConsumed==0){

					if(true){
						/* Transition from FM_ANNOUNCED to ERROR_HANDLING */
						evConsumed=16;
						

						/* adjust state variables  */
						stateVar =  ERROR_HANDLING;
					}else{
						/* Intentionally left blank */
					} /*end of event selection */
				}
			break; /* end of case FM_ANNOUNCED  */

			case FM_COMPLETED:
				if(true){
					/* Transition from FM_COMPLETED to FM_INIT */
					evConsumed=16;


					/* OnEntry code of state FM_INIT */
					logs("INIT");
					returnResult();

					/* adjust state variables  */
					stateVar =  FM_ANNOUNCED;
					stateVarFM_ANNOUNCED =  FM_INIT;
				}else{
					/* Intentionally left blank */
				} /*end of event selection */
			break; /* end of case FM_COMPLETED  */

			case REGISTER_APDUS:

				switch (stateVarREGISTER_APDUS) {

					case REGISTER_APDU_ERASE_BINARY:
						/* action code  */
						createNewApduSpecification("Erase Binary");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_0E_ERASE_BINARY);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_ERASE_BINARY to REGISTER_APDU_ERASE_BINARY_ODD */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_ERASE_BINARY_ODD;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_ERASE_BINARY  */

					case REGISTER_APDU_ERASE_BINARY_ODD:
						/* action code  */
						createNewApduSpecification("Erase Binary Odd");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_0F_ERASE_BINARY);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_ERASE_BINARY_ODD to FM_ANNOUNCED */
							evConsumed=16;


							logs("INIT");
							returnResult();
							stateVar =  FM_ANNOUNCED;/* Default in entry chain  */
							stateVarFM_ANNOUNCED =  FM_INIT;/* Default in entry chain  */

							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_SELECT_FILE;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_ERASE_BINARY_ODD  */

					case REGISTER_APDU_READ_BINARY:
						/* action code  */
						createNewApduSpecification("Read Binary");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_B0_READ_BINARY);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_READ_BINARY to REGISTER_APDU_READ_BINARY_ODD */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_READ_BINARY_ODD;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_READ_BINARY  */

					case REGISTER_APDU_READ_BINARY_ODD:
						/* action code  */
						createNewApduSpecification("Read Binary Odd");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_B1_READ_BINARY);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_READ_BINARY_ODD to REGISTER_APDU_UPDATE_BINARY */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_UPDATE_BINARY;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_READ_BINARY_ODD  */

					case REGISTER_APDU_SELECT_FILE:
						/* action code  */
						createNewApduSpecification("Select File");
						apduSpecification.setInitialAPDU(true);
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_A4_SELECT);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_SELECT_FILE to REGISTER_APDU_READ_BINARY */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_READ_BINARY;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_SELECT_FILE  */

					case REGISTER_APDU_UPDATE_BINARY:
						/* action code  */
						createNewApduSpecification("Update Binary");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_D6_UPDATE_BINARY);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_UPDATE_BINARY to REGISTER_APDU_UPDATE_BINARY_ODD */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_UPDATE_BINARY_ODD;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_UPDATE_BINARY  */

					case REGISTER_APDU_UPDATE_BINARY_ODD:
						/* action code  */
						createNewApduSpecification("Update Binary Odd");
						apduSpecification.setIsoFormat(ISO_FORMAT_FIRSTINTERINDUSTRY);
						apduSpecification.setReqIsoCase(REQ_OPTIONAL);
						apduSpecification.setReqP1(REQ_OPTIONAL);
						apduSpecification.setReqP2(REQ_OPTIONAL);
						apduSpecification.setIns(INS_D7_UPDATE_BINARY);
						registerApduSpecification(apduSpecification);


						if(true){
							/* Transition from REGISTER_APDU_UPDATE_BINARY_ODD to REGISTER_APDU_ERASE_BINARY */
							evConsumed=16;


							/* adjust state variables  */
							stateVarREGISTER_APDUS =  REGISTER_APDU_ERASE_BINARY;
						}else{
							/* Intentionally left blank */
						} /*end of event selection */
					break; /* end of case REGISTER_APDU_UPDATE_BINARY_ODD  */

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
