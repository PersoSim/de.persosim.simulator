package de.persosim.simulator.platform;

public class MethodCall {

	public String methodName;
	public MethodCall(String methodName, Object... params) {
		this.methodName = methodName;
	}

}
