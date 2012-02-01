package ca.wilkinsonlab.sadi.rdfpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.wilkinsonlab.sadi.rdfpath.RDFPath.PropertyValueIterator;
import ca.wilkinsonlab.sadi.utils.LSRNUtils;
import ca.wilkinsonlab.sadi.utils.RdfUtils;
import ca.wilkinsonlab.sadi.vocab.MyGrid;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * @author Luke McCarthy
 */
public class RDFPathTest
{
	private static final Logger log = Logger.getLogger(RDFPathTest.class);
	private static final String NS = "http://sadi.wilkinsonlab.ca/RDFPathTest.rdf#";
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	@Test
	public void testConstructors()
	{
		RDFPath path1 = new RDFPath(FOAF.name, RDF.value);
		RDFPath path2 = new RDFPath(FOAF.name.getURI(), RDF.value.getURI());
		RDFPath path3 = new RDFPath(String.format("%s, %s", FOAF.name, RDF.value));
		assertEquals(path1, path2);
		assertEquals(path2, path3);
	}
	
	@Test
	public void testSetMembership()
	{
		Set<RDFPath> paths = new HashSet<RDFPath>();
		RDFPath path1 = new RDFPath(FOAF.name, RDF.value);
		RDFPath path2 = new RDFPath(FOAF.name.getURI(), RDF.value.getURI());
		RDFPath path3 = new RDFPath(String.format("%s, %s", FOAF.name, RDF.value));
		paths.add(path1);
		paths.add(path2);
		paths.add(path3);
		assertEquals(1, paths.size());
	}
	
	/**
	 * Test method for {@link ca.wilkinsonlab.sadi.rdfpath.RDFPath#collectNodesOfType(java.util.Iterator, com.hp.hpl.jena.rdf.model.Resource, java.util.Collection)}.
	 */
	@Test
	public void testCollectNodesOfType()
	{
		Model model = ModelFactory.createDefaultModel();
		Resource c1 = model.createResource(NS + "c1");
		Resource c2 = model.createResource(NS + "c2");
		Resource c1instance = model.createResource(NS + "c1instance", c1);
		Resource c2instance = model.createResource(NS + "c2instance", c2);
		Resource root = model.createResource(NS + "root");
		Property p = model.createProperty(NS + "p");
		root.addProperty(p, c1instance);
		root.addProperty(p, c2instance);
		Collection<RDFNode> matches = new ArrayList<RDFNode>();
		RDFPath.collectNodesOfType(new PropertyValueIterator(root.listProperties(p)), c1, matches);
		assertCollectionContains(matches, c1instance);
		RDFPath.collectNodesOfType(new PropertyValueIterator(root.listProperties(p)), c2, matches);
		assertCollectionContains(matches, c2instance);
		
	}

	/**
	 * Test method for {@link ca.wilkinsonlab.sadi.rdfpath.RDFPath#accumulateValuesRootedAt(java.util.Iterator, java.util.Collection, boolean)}.
	 */
	@Test
	public void testAccumulateValuesRootedAt()
	{
		Model model = ModelFactory.createDefaultModel();
		Property p = model.createProperty(NS + "p");
		Property q = model.createProperty(NS + "q");
		Resource type = model.createResource(NS + "type");
		Resource root = model.createResource(NS + "root");
		Resource instance = model.createResource(NS + "instance", type);
		
		root.addProperty(p, instance);
		RDFPath path = new RDFPath();
		path.add(p);
		path.add(type);
		Collection<RDFNode> values = new ArrayList<RDFNode>();
		path.accumulateValuesRootedAt(Collections.singleton(root).iterator(), values, true);
		assertCollectionContains(values, instance);
		
		Literal literal = model.createLiteral("literal");
		instance.addProperty(q, literal);
		path.add(q);
		path.add(null);
		path.accumulateValuesRootedAt(Collections.singleton(root).iterator(), values, true);
		assertCollectionContains(values, literal);
	}
	
	@Test
	public void testAddValuesRootedAt() throws Exception
	{
		RDFPath path = new RDFPath(new String[]{
				"http://sadiframework.org/ontologies/properties.owl#hasParticipant",
					"http://purl.oclc.org/SADI/LSRN/KEGG_DRUG_Record",
				"http://semanticscience.org/resource/SIO_000008",
					"http://purl.oclc.org/SADI/LSRN/KEGG_DRUG_Identifier",
				"http://semanticscience.org/resource/SIO_000300",
					"*"
		});
		path.reuseExistingNodes = false;
		Model model = ModelFactory.createDefaultModel();
		Resource KEGG_Pathway_Record = model.createResource("http://purl.oclc.org/SADI/LSRN/KEGG_Pathway_Record");
		Resource hsa00232 = LSRNUtils.createInstance(model, KEGG_Pathway_Record, "hsa00232");
		path.addValueRootedAt(hsa00232, model.createTypedLiteral("D02261"));
		path.addValueRootedAt(hsa00232, model.createTypedLiteral("D02262"));
		RdfUtils.addNamespacePrefixes(model);
//		model.getWriter("N3").write(model, new FileOutputStream("/tmp/out.n3"), "");
	}
	
	@Test
	public void testCreateValuesRootedAt() throws Exception
	{
		Model model = ModelFactory.createDefaultModel();
		Resource root = model.createResource(); 
		RDFPath path1 = new RDFPath(
			"http://semanticscience.org/resource/SIO_000552, " +
				"http://unbsj.biordf.net/fishtox/BLAST-sadi-service-ontology.owl#E_Value, " +
			"http://semanticscience.org/resource/SIO_000300, " +
				"http://www.w3.org/2001/XMLSchema#double"
		);
		path1.createLiteralRootedAt(root, "0.0001");
		RDFPath path2 = new RDFPath(
			"http://semanticscience.org/resource/SIO_000552, " +
				"http://unbsj.biordf.net/fishtox/BLAST-sadi-service-ontology.owl#BitScore, " +
			"http://semanticscience.org/resource/SIO_000300, " +
				"http://www.w3.org/2001/XMLSchema#double"
		);
		path2.createLiteralRootedAt(root, "25");
//		model.getWriter("N3").write(model, System.out, "");
	}
	
	@Test
	public void testCreateLiteralRootedAt() throws Exception
	{
		RDFPath path = new RDFPath(new String[]{ 
				MyGrid.authoritative.getURI(), 
					XSDDatatype.XSDboolean.getURI()
		});
		Model model = ModelFactory.createDefaultModel();
		Resource root = model.createResource();
		path.createLiteralRootedAt(root, "true");
		assertCollectionContains(path.getValuesRootedAt(root), ResourceFactory.createTypedLiteral(true));
		root.addLiteral(MyGrid.authoritative, "not a boolean");
		assertCollectionDoesNotContain(path.getValuesRootedAt(root), ResourceFactory.createTypedLiteral("not a boolean"));
	}
	
	private static void assertCollectionContains(Collection<?> collection, Object item)
	{
		String collectionString = String.format("[%s]", StringUtils.join(collection, ", "));
		log.debug(String.format("looking for %s in %s", item, collectionString));
		if (!collection.contains(item))
			fail(String.format("missing %s from %s", item, collectionString));
	}
	
	private static void assertCollectionDoesNotContain(Collection<?> collection, Object item)
	{
		String collectionString = String.format("[%s]", StringUtils.join(collection, ", "));
		log.debug(String.format("looking for %s in %s", item, collectionString));
		if (collection.contains(item))
			fail(String.format("unexpected %s in %s", item, collectionString));
	}
}