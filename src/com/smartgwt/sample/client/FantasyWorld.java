package com.smartgwt.sample.client;

import java.util.HashSet;
import java.util.Set;

public class FantasyWorld {

	private String name = "Planet";

	private Set<String> cities = new HashSet<>();

	public FantasyWorld() {
	}

	public String changeName(String name) {
		this.name = name;
		return this.name;
	}

	public String showName() {
		return this.name;
	}

	public String showCities() {
		return String.join(",", cities);
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// Remove all below this line
	// -------------------------------------------------------------------------------------------------------------- //
}
