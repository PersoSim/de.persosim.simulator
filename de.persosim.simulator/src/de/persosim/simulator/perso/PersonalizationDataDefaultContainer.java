/**
 * 
 */
package de.persosim.simulator.perso;

/**
 * @author slutters
 *
 */
//FIXME SLS I don't like this construct, this class does not represent any useful entity on its own. maybe replace this with some kind of general initializer, default values in the super class or a factory...
public class PersonalizationDataDefaultContainer extends PersonalizationDataContainer {
	
	public PersonalizationDataDefaultContainer() {
		this.dg1PlainData = "ID";
		this.dg2PlainData = "D";
		this.dg3PlainData = "20201031";
		this.dg6PlainData = "";
		this.dg7PlainData = "";
		this.dg10PlainData = "D";
		this.dg13PlainData = "";
		this.dg19PlainData = "ResPermit1";
		this.dg20PlainData = "ResPermit2";
	}
	
}
