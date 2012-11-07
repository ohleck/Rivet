package org.e2k;

public class Trigger {
	
	private String triggerSequence;
	private String triggerDescription;
	private int triggerType=-1;
	
	public String getTriggerSequence() {
		return triggerSequence;
	}
	
	public void setTriggerSequence(String triggerSequence) {
		this.triggerSequence = triggerSequence;
	}
	
	public String getTriggerDescription() {
		return triggerDescription;
	}
	
	public void setTriggerDescription(String triggerDescription) {
		this.triggerDescription = triggerDescription;
	}
		
	public int getTriggerType() {
		return triggerType;
	}
	
	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}
	
	// Return true if this triggers sequence appears in the circularBitSet
	public boolean triggerMatch(CircularBitSet cBitSet)	{
		// Get the last sLength bits from the circular buffer
		String cur=cBitSet.extractSequence(triggerSequence.length());
		// Is this the same as the trigger sequence ?
		if (triggerSequence.equals(cur)) return true;
		else return false;
	}
	
	
}
