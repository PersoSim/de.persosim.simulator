package de.persosim.simulator.test.globaltester;

/**
 * This class describes a GT Testsuite (or GT Testcase) that can be run via
 * ServerMode. Simply consists of a project name and a suite name as provided
 * via ServerMode.
 * 
 * @author amay
 * 
 */
public class GtSuiteDescriptor extends JobDescriptor {

	private String projectName;
	private String suiteName;

	public GtSuiteDescriptor(String projectName, String suiteName) {
		super();
		this.projectName = projectName;
		this.suiteName = suiteName;
	}

	public String getProject() {
		return projectName;
	}

	public String getTestSuiteName() {
		return suiteName;
	}

}
