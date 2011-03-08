package ca.wilkinsonlab.sadi.service.example;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.wilkinsonlab.sadi.service.AsynchronousServiceServlet;
import ca.wilkinsonlab.sadi.utils.ServiceUtils;
import ca.wilkinsonlab.sadi.vocab.LSRN;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

@SuppressWarnings("serial")
public class EntrezGene2KeggServiceServlet extends AsynchronousServiceServlet
{
	private static final Log log = LogFactory.getLog(EntrezGene2KeggServiceServlet.class);
	
	@Override
	public int getInputBatchSize()
	{
		// override input batch size to 100, the maximum for the KEGG API...
		return 100;
	}
	
	@Override
	protected InputProcessingTask getInputProcessingTask(Model inputModel, Collection<Resource> inputNodes)
	{
		return new UniProtInputProcessingTask(inputModel, inputNodes);
	}
	
	private class UniProtInputProcessingTask extends InputProcessingTask
	{	
		public UniProtInputProcessingTask(Model inputModel, Collection<Resource> inputNodes)
		{
			super(inputModel, inputNodes);
		}

		public void run()
		{
			// build map: Entrez Gene IDs => output RDF nodes
			
			Map<String, Resource> idToOutputNode = new HashMap<String, Resource>(inputNodes.size());
			
			for (Resource inputNode: inputNodes) {
				String id = ServiceUtils.getDatabaseId(inputNode, LSRN.Entrez.Gene);
				id = String.format("ncbi-geneid:%s", id);
				idToOutputNode.put(id, outputModel.getResource(inputNode.getURI()));
			}

			// retrieve the id mappings
			
			String[] idMappings;
			try {
				
				KEGGPortType keggService = new KEGGLocator().getKEGGPort();
				String idList = StringUtils.join(idToOutputNode.keySet(), " ");
				idMappings = keggService.bconv(idList).split("\\r?\\n");
				
			} catch(ServiceException e) {
				throw new RuntimeException("error initializing KEGG API", e);
			} catch(RemoteException e) {
				throw new RuntimeException("error contacting KEGG service", e);
			}
			
			// encode the mappings in RDF
			
			for(String idMapping : idMappings) {
			
				String[] fields = idMapping.split("\\s+");
				
				if(fields.length < 2) {
					log.warn(String.format("skipping line with unexpected number of fields: %s", idMapping));
					continue;
				}
				
				String entrezGeneId = fields[0];
				String keggGeneId = fields[1];
				
				Resource keggGeneNode = ServiceUtils.createLSRNRecordNode(outputModel, LSRN.KEGG.Gene, keggGeneId);
				idToOutputNode.get(entrezGeneId).addProperty(OWL.sameAs, keggGeneNode);
			
			}
			
			success();
		}
	}
	
	
}