package org.iotdata.enums;

import java.util.Arrays;

import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.Prologue;

import lombok.Getter;

/**
 * Types of prefixes used across datasets in ASSIST-IoT
 */
@Getter
public enum PrefixType {

	SOSA("sosa", "http://www.w3.org/ns/sosa/"),
	IOT_ONTO("iot", "https://assist-iot.eu/ontologies/aiot_p2#"),
	SCHEMA("schema", "http://schema.org/"),
	RDFS("rdfs", "http://www.w3.org/2000/01/rdf-schema#"),
	RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),

	// prefixes used in specific dataset types
	CAMERA("camera", "https://assist-iot.eu/pilot2_rdf/sensor/camera/");

	private final String prefix;
	private final String uri;

	PrefixType(final String prefix, final String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}

	/**
	 * Method creates prologue from the given set of prefixes
	 */
	public static Prologue createPrefixPrologue(PrefixType... prefixes) {
		Prologue prologue = new Prologue();
		Arrays.stream(prefixes).forEach(prefix -> prologue.setPrefix(prefix.getPrefix(), prefix.getUri()));
		return prologue;
	}

}
