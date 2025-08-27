package de.persosim.simulator.controller;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
	@Override
	public void start(BundleContext context) throws Exception
	{
		de.persosim.simulator.Activator persoSimActivator = de.persosim.simulator.Activator.getDefault();
		persoSimActivator.stop(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		// nothing to do
	}
}
