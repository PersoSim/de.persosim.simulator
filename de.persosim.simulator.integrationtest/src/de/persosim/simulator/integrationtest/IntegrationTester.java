package de.persosim.simulator.integrationtest;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import de.persosim.simulator.Simulator;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;

public class IntegrationTester {
	
	private static CountDownLatch dependencyLatch = new CountDownLatch(1);
	static Simulator  simService = null;
	
	public void onServiceUp(Simulator service) {
		simService = service;
	    dependencyLatch.countDown();
	}
	
	@Before
	public void dependencyCheck() {
	  // Wait for OSGi dependencies
	    try {
	      dependencyLatch.await(10, TimeUnit.SECONDS); 
	      // Dependencies fulfilled
		  simService.exitSimulator();
	    } catch (InterruptedException ex)  {
	      fail("OSGi dependencies unfulfilled");
	    }
	}
	
	@Test
	public void testSimulatorStart_twice(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.startSimulator());
	}
	
	@Test
	public void testSimulatorStart(){
		assertTrue(simService.startSimulator());
	}
	
	@Test
	public void testSimulatorStop(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.stopSimulator());
	}
	
	@Test
	public void testSimulatorExit(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.exitSimulator());
	}
	
	@Test
	public void testSimulatorExit_twice(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.exitSimulator());
		assertFalse(simService.exitSimulator());
	}
	
	@Test
	public void testSimulatorStop_twice(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.stopSimulator());
		assertFalse(simService.stopSimulator());
	}
	
	@Test
	public void testSimulatorExitAndStop(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.exitSimulator());
		assertFalse(simService.stopSimulator());
	}
	
	@Test
	public void testSimulatorRestartNotRunning(){
		assertTrue(simService.restartSimulator());
	}
	
	@Test
	public void testSimulatorRestartRunning(){
		assertTrue(simService.startSimulator());
		assertTrue(simService.restartSimulator());
	}
	
	@Test
	public void testGetPersonalization(){
		assertTrue(simService.startSimulator());
		Personalization perso = simService.getPersonalization(); 
		assertNotNull(perso);
		assertTrue(perso instanceof DefaultPersonalization);
	}
}
