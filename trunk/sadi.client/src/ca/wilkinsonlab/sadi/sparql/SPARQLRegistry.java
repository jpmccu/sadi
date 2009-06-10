package ca.wilkinsonlab.sadi.sparql;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;

import ca.wilkinsonlab.sadi.client.Registry;
import ca.wilkinsonlab.sadi.utils.SPARQLStringUtils;
import ca.wilkinsonlab.sadi.utils.HttpUtils.HttpResponseCodeException;
import ca.wilkinsonlab.sadi.vocab.SPARQLRegistryOntology;

/**
 * A registry that holds SPARQL endpoints.
 * 
 * This is a "tagging" interface; it exists only because 
 * I needed to distinguish between SPARQL and non-SPARQL 
 * registries. For example, the RandomQueryGenerator and 
 * PredicateStatsDB classes only work on SPARQL registries.
 * -- BV   
 */

public interface SPARQLRegistry extends Registry 
{

	public long getNumTriples(String endpointURI) throws HttpException,
			HttpResponseCodeException, IOException;
}
