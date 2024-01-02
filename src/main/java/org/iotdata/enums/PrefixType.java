package org.iotdata.enums;

import java.util.Arrays;

import org.apache.jena.sparql.core.Prologue;

import lombok.Getter;

/**
 * Types of prefixes used across datasets in ASSIST-IoT
 */
@Getter
public enum PrefixType {

	AIOT("aiot", "https://assist-iot.eu/ontologies/aiot#" ),
	AIOT_P2("aiotp2", "https://assist-iot.eu/ontologies/aiot_p2#"),
	MEASURE("msr", "http://www.ontology-of-units-of-measure.org/resource/om-2/" ),
	SOSA("sosa", "http://www.w3.org/ns/sosa/"),
	SCHEMA("schema", "http://schema.org/"),
	RDFS("rdfs", "http://www.w3.org/2000/01/rdf-schema#"),
	RDF("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
	XSD("xsd", "http://www.w3.org/2001/XMLSchema#"),

	// prefixes used in specific dataset types
	CAMERA("camera", "https://assist-iot.eu/pilot2_rdf/sensor/camera/"),

	// common prefix used in custom functions
	FUNC("func", "https://assist-iot.eu/iot-analysis/custom-functions#");

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
