package org.guideme.guideme.model;

import java.util.ArrayList;

import org.guideme.guideme.settings.ComonFunctions;

public class Metronome {
	private String ifSet;
	private String ifNotSet;
	private String bpm; //beats per minute
	private ComonFunctions comonFunctions = ComonFunctions.getComonFunctions();

	public Metronome(String bpm, String ifSet, String ifNotSet) {
		this.ifSet = ifSet;
		this.ifNotSet = ifNotSet;
		this.bpm = bpm;
	}

	public boolean canShow(ArrayList<String> setList) {
		return comonFunctions.canShow(setList, ifSet, ifNotSet);
	}

	public int getbpm() {
		return comonFunctions.getRandom(bpm);
	}


}
