package ca.wilkinsonlab.sadi.service.example;

import ca.wilkinsonlab.sadi.utils.UniProtUtils;

import com.hp.hpl.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.Collections;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

@SuppressWarnings("serial")
public class BlastUniProtByIdServiceServlet extends BlastUniProtServiceServlet 
{
	@Override
	protected Collection<String> getSequences(Resource input) 
	{
		// TODO: This service can be made more efficient by
		// retrieving the UniProt entries for all of the inputs
		// in batch, as is done in UniProtServiceServlet.

		String uniprotId = UniProtUtils.getUniProtId(input);
		UniProtEntry uniprotEntry = UniProtUtils.getUniProtEntries(Collections.singleton(uniprotId)).get(uniprotId);
		String sequence = uniprotEntry.getSequence().getValue();
		return Collections.singleton(sequence);
	}
}
