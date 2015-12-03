package de.persosim.simulator.cardobjects;

import java.util.Date;


/**
 * This {@link CardObject} stores a date and time.
 * 
 * @author mboonk
 *
 */
public class DateTimeCardObject extends AbstractCardObject {

	Date currentDate;

	public DateTimeCardObject(Date date) {
		currentDate = new Date(date.getTime());
	}
	
	/**
	 * @return the stored date
	 */
	public Date getDate(){
		return new Date(currentDate.getTime());
	}
	
	/**
	 * @param the new date to store
	 */
	public void update(Date date){
		// XXX MBK check update access rights here
		currentDate = new Date(date.getTime());
	}

}
