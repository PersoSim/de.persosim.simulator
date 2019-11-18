package de.persosim.simulator.platform;

public class MethodCall {

	private String methodName;
	private String[] params;

	public MethodCall(String methodName, String... params) {
		this.methodName = methodName;
		this.params = params;
	}

}
