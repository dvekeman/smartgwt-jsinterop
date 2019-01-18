package com.smartgwt.sample.client;

import java.util.HashSet;
import java.util.Set;

import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace = "fantasy")
public class FantasyWorld {

	@JsProperty(name = "name")
	private String name = "Planet";

	@JsProperty(name = "cities")
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
	public String addAllCities() {
		String[] allCities = new String[]{
				"Midgar", "Kalm", "Fort Condor", "Junon",
				"Costa del Sol", "North Corel", "Gongaga",
				"Cosmo Canyon"
		};
		for (String city : allCities) {
			// This is the native method
			addCity(city);
		}
		return showCities();
	}

	public native void addCity(String city);
}
