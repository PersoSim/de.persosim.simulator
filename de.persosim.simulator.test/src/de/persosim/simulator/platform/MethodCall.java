package de.persosim.simulator.platform;

public class MethodCall {

	public String methodName;
	private Object[] params;

	public MethodCall(String methodName, Object... params) {
		this.methodName = methodName;
		this.params = params;
	}

}
