package de.persosim.simulator.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.globaltester.logging.InfoSource;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import de.persosim.simulator.apdu.ResponseApdu;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.processing.ProcessingData;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.ProtocolUpdate;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvValuePlain;

public class TestProtocol implements Protocol {
	
	InfoSource source = new InfoSource() {
		
		@Override
		public String getIDString() {
			return "TestSource";
		}
	};
	
	public List<MethodCall> methodCalls = new ArrayList<>();

	private boolean finishProtocol = false;
	

	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCardStateAccessor(CardStateAccessor cardState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<? extends TlvDataObject> getSecInfos(SecInfoPublicity publicity, MasterFile mf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(ProcessingData processingData) {
		methodCalls.add(new MethodCall("process", processingData));
		TlvValuePlain responseData = new TlvValuePlain(new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08 });
		ResponseApdu resp = new ResponseApdu(responseData, Iso7816.SW_9000_NO_ERROR);
		processingData.updateResponseAPDU(source, "GetNonce processed successfully", resp);
		
		if (finishProtocol) {
			processingData.addUpdatePropagation(source, "test protocol completed", new ProtocolUpdate(true));
		}
	}

	@Override
	public void reset() {
		methodCalls.add(new MethodCall("reset"));
	}

	@Override
	public boolean isMoveToStackRequested() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static Matcher<TestProtocol> wasMethodCalled(final String s) {
		return new BaseMatcher<TestProtocol>() {
			@Override
			public boolean matches(final Object item) {
				final TestProtocol tProtocol = (TestProtocol) item;
				for (MethodCall curMethodCall : tProtocol.methodCalls) {
					if (s.equals(curMethodCall.methodName)) return true;
				}
				return false;
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("method should have been called: ").appendValue(s);
			}
		};
	}
	
	public static Matcher<TestProtocol> methodsWhereCalledInSequence(final boolean strict, final String... methods) {
		return new BaseMatcher<TestProtocol>() {
			@Override
			public boolean matches(final Object item) {
				final TestProtocol tProtocol = (TestProtocol) item;
				int i = 0;
				for (MethodCall curMethodCall : tProtocol.methodCalls) {
					if (methods[i].equals(curMethodCall.methodName)) {
						i++;
					} else if(strict) {
						if (methods[0].equals(curMethodCall.methodName)) {
							i=1;
						} else {
							i=0;
						}	
					}
					if (i >= methods.length) return true;
				}
				return false;
			}

			@Override
			public void describeTo(final Description description) {
				String s = "";
				if (strict) s = "strict ";
				description.appendText("methods should have been called in "+s+"sequence: ").appendValue(methods);
			}
		};
	}

	public void setFinishProtocol(boolean finish) {
		this.finishProtocol = finish;
	}

}
