package ca.wilkinsonlab.sadi.utils.blast;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ca.wilkinsonlab.sadi.utils.SIOUtils;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class AbstractBLASTParser
{
	private static final Logger log = Logger.getLogger(AbstractBLASTParser.class);
	
	public void parseBLAST(Model model, Document blastXML)
	{
		// TODO create blastProcessNode...
		Resource blastProcess = model.createResource("#process");
		
        for (Node iteration: evaluateXPath(blastXML.getDocumentElement(), "BlastOutput_iterations/Iteration")) {
        	parseIteration(blastProcess, iteration);
        }
	}
	
	protected void parseIteration(Resource blastProcess, Node iteration)
	{
		Resource querySequence = getQuerySequence(blastProcess.getModel(), iteration);
		int i=0;
		for (Node hit: evaluateXPath(iteration, "Iteration_hits/Hit")) {
			String blastHitURI = String.format("#%06d", ++i);
			Resource blastHit = createBlastHit(blastHitURI, querySequence, hit);
			blastHit.addProperty(Vocab.is_output_of, blastProcess);
		}
	}

	protected Resource createBlastHit(String blastHitURI, Resource querySequence, Node hit)
	{
		Model model = querySequence.getModel();
		Resource hitSequence = getHitSequence(model, hit);
		Resource blastHit = model.createResource(blastHitURI, Vocab.blast_hit);
		int i=0;
		for (Node hsp: evaluateXPath(hit, "Hit_hsps/Hsp")) {
			String alignmentURI = String.format("%s-%02d", blastHitURI, ++i);
			Resource alignment = createAlignment(alignmentURI, querySequence, hitSequence, hsp);
			blastHit.addProperty(Vocab.has_part, alignment);
			alignment.addProperty(Vocab.is_part_of, blastHit);
        }
		return blastHit;
	}

	protected Resource createAlignment(String alignmentURI, Resource querySequence, Resource hitSequence, Node hsp)
	{
		Resource querySubsequence = createSubsequence(querySequence,
				Integer.valueOf(getSingleValue(hsp, "Hsp_query-from")),
				Integer.valueOf(getSingleValue(hsp, "Hsp_query-to")),
				getSingleValue(hsp, "Hsp_qseq"));
		
		Resource hitSubsequence = createSubsequence(hitSequence,
				Integer.valueOf(getSingleValue(hsp, "Hsp_hit-from")),
				Integer.valueOf(getSingleValue(hsp, "Hsp_hit-to")),
				getSingleValue(hsp, "Hsp_hseq"));
		
		Resource alignmentNode = querySequence.getModel().createResource(alignmentURI, Vocab.sequence_alignment);
		Literal e = ResourceFactory.createTypedLiteral(getSingleValue(hsp, "Hsp_evalue"), XSDDatatype.XSDdecimal);
		SIOUtils.createAttribute(alignmentNode, Vocab.expectation, e);
		SIOUtils.createAttribute(alignmentNode, Vocab.identity, 
				Double.valueOf(getSingleValue(hsp, "Hsp_identity")) /
				Double.valueOf(getSingleValue(hsp, "Hsp_align-len")));
		SIOUtils.createAttribute(alignmentNode, Vocab.bits, Double.valueOf(getSingleValue(hsp, "Hsp_bit-score")));
		SIOUtils.createAttribute(alignmentNode, Vocab.score, Integer.valueOf(getSingleValue(hsp, "Hsp_score")));
		SIOUtils.createAttribute(alignmentNode, Vocab.consensus_sequence, getSingleValue(hsp, "Hsp_midline"));
		alignmentNode.addProperty(Vocab.has_part, querySubsequence);
		alignmentNode.addProperty(Vocab.has_part, hitSubsequence);
		return alignmentNode;
	}

	private Resource createSubsequence(Resource sequence, int start, int stop, String subsequenceString)
	{
		Resource subsequence = sequence.getModel().createResource(getSubsequenceURI(sequence.getURI()), SIOUtils.getSequenceType(sequence));
		sequence.addProperty(Vocab.has_part, subsequence);
		subsequence.addProperty(Vocab.is_part_of, sequence);
		SIOUtils.createAttribute(subsequence, Vocab.sequence_start_position, start);
		SIOUtils.createAttribute(subsequence, Vocab.sequence_stop_position, stop);
		subsequence.addProperty(Vocab.has_value, subsequenceString);
		return subsequence;
	}

	protected String getSubsequenceURI(String sequenceURI)
	{
		return null;
	}
	
	protected abstract Resource getQuerySequence(Model model, Node iteration);
	
	protected abstract Resource getHitSequence(Model model, Node iteration);
	
	public void parseBLAST(Model model, InputStream blastXML)
	{
		Document doc = null;
		try {
			doc = parseInputStream(blastXML);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		if (doc != null) {
			doc.getDocumentElement().normalize();
			parseBLAST(model, doc);
		}
	}

	public static Document parseInputStream(InputStream xmlStream) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(xmlStream);
		return doc;
	}
	
	public static List<Node> evaluateXPath(Object root, String xpathExpression)
	{
		XPath xpath = XPathFactory.newInstance().newXPath();
        try {
			NodeList nodeList = (NodeList)xpath.evaluate(xpathExpression, root, XPathConstants.NODESET);
			List<Node> nodes = new ArrayList<Node>(nodeList.getLength());
			for (int i=0; i<nodeList.getLength(); ++i)
				nodes.add(nodeList.item(i));
			return nodes;
		} catch (XPathExpressionException e) {
			log.error(e.toString(), e);
			return Collections.emptyList();
		}
	}
	
	public static String getSingleValue(Object root, String xpathExpression)
	{
		List<Node> nodes = evaluateXPath(root, xpathExpression);
		if (nodes.isEmpty())
			return null;
		else
			return nodes.get(1).getTextContent();
	}
	
	/**
	 * Returns a UUID-based URI.
	 * @return a UUID-based URI
	 */
	public static String createUniqueURI()
	{
		return String.format("urn:uuid:%s", UUID.randomUUID());
	}
	
	public static class Vocab
	{
		private static Model m_model = ModelFactory.createDefaultModel();

		public static final String SIO_NS = "http://semanticscience.org/resource/";
		public static final Property has_part = m_model.createProperty( SIO_NS, "SIO_000028" );
		public static final Property is_part_of = m_model.createProperty( SIO_NS, "SIO_000068" );
		public static final Property is_output_of = m_model.createProperty(SIO_NS, "SIO_000232");
		public static final Property has_value = m_model.createProperty( SIO_NS, "SIO_000300" );
	    public static final Resource sequence_start_position = m_model.createResource( SIO_NS + "SIO_000791" );
	    public static final Resource sequence_stop_position = m_model.createResource( SIO_NS + "SIO_000792" );
		
		public static final String BLAST_NS = "http://sadiframework.org/ontologies/blast.owl";
		public static final Resource blast_hit = m_model.createResource(BLAST_NS + "BlastHit");
		public static final Resource sequence_alignment = m_model.createResource(BLAST_NS + "SequenceAlignment");
		public static final Resource consensus_sequence = m_model.createResource(BLAST_NS + "Consensus");
		public static final Resource expectation = m_model.createResource(BLAST_NS + "expectation");
		public static final Resource identity = m_model.createResource(BLAST_NS + "identity");
		public static final Resource bits = m_model.createResource(BLAST_NS + "bits");
		public static final Resource score = m_model.createResource(BLAST_NS + "score");
	}
	
	public static void main(String args[])
	{
		for (String arg: args) {
			Model model = ModelFactory.createDefaultModel();
			try {
				new AbstractBLASTParser() {
					@Override
					protected Resource getQuerySequence(Model model, Node iteration)
					{
						return model.createResource();
					}
					@Override
					protected Resource getHitSequence(Model model, Node iteration) {
						return model.createResource();
					}
				}.parseBLAST(model, new FileInputStream(arg));
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.write(System.out, "N3");
		}
	}
}
