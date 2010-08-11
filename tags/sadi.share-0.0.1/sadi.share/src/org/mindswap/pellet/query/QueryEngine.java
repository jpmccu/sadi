// The MIT License
//
// Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

/*
 * Created on Jul 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.mindswap.pellet.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.query.impl.ARQParser;
import org.mindswap.pellet.query.impl.DistVarsQueryExec;
import org.mindswap.pellet.query.impl.MultiQueryResults;
import org.mindswap.pellet.query.impl.NoDistVarsQueryExec;
import org.mindswap.pellet.query.impl.OptimizedQueryExec;
import org.mindswap.pellet.query.impl.QueryImpl;
import org.mindswap.pellet.query.impl.QueryResultBindingImpl;
import org.mindswap.pellet.query.impl.QueryResultsImpl;
import org.mindswap.pellet.query.impl.SimpleQueryExec;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.DisjointSet;
import org.mindswap.pellet.utils.PermutationGenerator;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.SizeEstimate;
import org.mindswap.pellet.utils.Timer;

import aterm.ATermAppl;

import ca.wilkinsonlab.sadi.optimizer.PelletOptimizer;
import ca.wilkinsonlab.sadi.pellet.SADIQueryExec;

import com.hp.hpl.jena.query.Syntax;


/**
 * @author Evren Sirin
 */
@SuppressWarnings("unchecked")
public class QueryEngine {

	public final static Log log = LogFactory.getLog( QueryEngine.class );

    public final static Syntax DEFAULT_SYNTAX = Syntax.syntaxSPARQL;
    
    private static DistVarsQueryExec distVars = new DistVarsQueryExec();
    private static OptimizedQueryExec optimized = new OptimizedQueryExec();
    private static SimpleQueryExec simple = new SimpleQueryExec();
    private static NoDistVarsQueryExec noVars = new NoDistVarsQueryExec();
    
    /** 
     * PATCH: Added for SADI query optimization. -- BV
     */
    private static SADIQueryExec sadiQueryExec = new SADIQueryExec();
    
    private static QueryExec[] queryExecs = 
    	{sadiQueryExec, noVars, distVars, optimized, simple};
    
//    private static QuerySplitter splitter = new QuerySplitter();

    public static QueryParser createParser() {
        return createParser( DEFAULT_SYNTAX );
    }
    
    public static QueryParser createParser( Syntax syntax ) {
        ARQParser parser = new ARQParser();   
        parser.setSyntax( syntax );

        return parser;
    }

    public static QueryResults exec( String queryStr, KnowledgeBase kb ) {    
	    return exec( queryStr, kb, DEFAULT_SYNTAX );	    
	}
    
    public static QueryResults execRDQL( String queryStr, KnowledgeBase kb ) {
	    return exec( queryStr, kb, Syntax.syntaxRDQL );	    
	}
    
    public static QueryResults execSPARQL( String queryStr, KnowledgeBase kb ) {
	    return exec( queryStr, kb, Syntax.syntaxSPARQL );	    
	}

    public static Query parse( String queryStr, KnowledgeBase kb ) {
        return parse( queryStr, kb, DEFAULT_SYNTAX );       
    }
    
    public static Query parse( String queryStr, KnowledgeBase kb, Syntax syntax ) {    
        QueryParser parser = createParser( syntax );
        Query query = parser.parse( queryStr, kb );
        
        return query;
    }
    
    public static QueryResults exec( String queryStr, KnowledgeBase kb, Syntax syntax ) {    
	    Query query = parse( queryStr, kb, syntax );
	    
	    return exec( query );
	}

    public static QueryResults exec( Query query, KnowledgeBase kb ) {
        KnowledgeBase origKB = query.getKB();
        query.setKB( kb );
        QueryResults results = exec( query );
        query.setKB( origKB );
        
        return results;
    }
    
    public static QueryResults exec( Query query ) {
        if( query.getQueryPatterns().isEmpty() ) {
    		QueryResultsImpl results = new QueryResultsImpl( query );	
   		    results.add(new QueryResultBindingImpl());
                
            return results;             
        }
        
        /* 
         * PATCH: We don't want the special case of
         * "no variables in the query" to bypass the
         * DynamicKnowledgeBase. --BV
         */
        
        /*
        else if( query.isGround() ) {
            return noVars.exec( query );
        }
        */
        
        if( PelletOptions.SIMPLIFY_QUERY ) {
            if( log.isInfoEnabled() )
                log.info( "Simplifying:\n" + query );
            
            simplify( query );
        }

        if( log.isInfoEnabled() )
            log.info( "Split:\n" + query );
        
        List queries = split( query );

        if( queries.isEmpty() ) {
    	    throw new InternalReasonerException( "Splitting query returned no results!" );
        }
        else if( queries.size() == 1 ) {
            return execSingleQuery( (Query) queries.get( 0 ) );
        }
        else {    
            QueryResults[] results = new QueryResults[ queries.size() ];
            for( int i = 0; i < queries.size(); i++ ) {
                Query qry = (Query) queries.get( i );
                results[i] = execSingleQuery( qry );
            }
            
            return new MultiQueryResults( query, results );
        }
	}
    
    private static QueryResults execSingleQuery( Query query ) {
        query.prepare();
        
        if( query.hasUndefinedPredicate() )
            return new QueryResultsImpl( query ); 
        
        if( PelletOptions.SAMPLING_RATIO > 0 ) {
            if( log.isInfoEnabled() )
                log.info( "Reorder\n" + query );
            
            query = reorder( query );
        }
        
        if( log.isInfoEnabled() )
            log.info( "Execute\n" + query );
        
        for( int i = 0; i < queryExecs.length; i++ ) {
            if( queryExecs[ i ].supports( query ) ) {               
                Timer timer = query.getKB().timers.startTimer( "Query" );
                QueryExec queryExec = queryExecs[ i ];
                QueryResults results = queryExec.exec( query );
                timer.stop();
                
                return results;
            }
        }
        
        // this should never happen
	    throw new InternalReasonerException( "Cannot determine which query engine to use" );
    }
    
	/**
	 * Reorder the triple patterns of the query, prior to query execution.
	 * 
	 * PATCH: This method was altered to ensure a SADI-resolvable
	 * ordering of triple patterns.  And also to incorporate
	 * SADI query optimization. 
	 */
    public static Query reorder( Query query ) {
    	
    	// Keeping this here in case original reordering has side effects. -- BV
    	Query q = originalReorder( query );

    	PelletOptimizer optimizer = new PelletOptimizer(query.getKB());
    	q = optimizer.optimize(query);
    	
    	return q;
    }
    
    private static Query originalReorder( Query query ) {        
        double minCost = Double.POSITIVE_INFINITY;
        
        KnowledgeBase kb = query.getKB();
        
        if( kb.getIndividuals().size() <= 100 )
            return query;
        
        computeSizeEstimates( query );
        
        QueryCost queryCost = new QueryCost( query.getKB() );
        
        List patterns = query.getQueryPatterns();
        List bestOrder = null;
        int n = patterns.size();
        
        PermutationGenerator gen = new PermutationGenerator( n );
        for(int i = 0; gen.hasMore(); i++) {
            int[] perm = gen.getNext();
            List newPatterns = new ArrayList();
            for(int j = 0; j < n; j++) 
                newPatterns.add( patterns.get( perm[j] ) );

            double cost = queryCost.estimateCost( newPatterns );
            if( cost < minCost ) {
                minCost = cost;
                bestOrder = newPatterns;
            }
        }
        
        if( bestOrder == patterns )
            return query;
        
        Query newQuery = new QueryImpl( kb );
        for(int j = 0; j < n; j++) {
            newQuery.addPattern( (QueryPattern) bestOrder.get( j ) );
        }
        for( Iterator j = query.getResultVars().iterator(); j.hasNext(); ) {
            ATermAppl var = (ATermAppl) j.next();
            newQuery.addResultVar( var );
        }
        for( Iterator j = query.getDistVars().iterator(); j.hasNext(); ) {
            ATermAppl var = (ATermAppl) j.next();
            newQuery.addDistVar( var );
        }        
        return newQuery;
    }
    
    /**
     * If a query has disconnected components such as C(x), D(y) then it should be
     * answered as two separate queries. The answers to each query should be 
     * combined at the end by taking Cartesian product.(we combine results on a tuple 
     * basis as results are iterated. This way we avoid generating the full Cartesian 
     * product. Splitting the query ensures the correctness of the answer, e.g. 
     * rolling-up technique becomes applicable.
     * 
     * @param query Query to be split
     * @return List of queries (contains the initial query if the initial query is connected)
     */
    public static List split( Query query ) {        
        try {
            Set distVars = query.getDistVars();
            Set resultVars = new HashSet( query.getResultVars() );
            
            DisjointSet disjointSet = new DisjointSet();
            
            List patterns = query.getQueryPatterns();
            for( Iterator i = patterns.iterator(); i.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) i.next();
                
                ATermAppl subj = pattern.getSubject();
                ATermAppl obj = pattern.getObject();
                
                if( pattern.isTypePattern() )
                    disjointSet.add( subj );
                else {
                    disjointSet.add( subj );
                    disjointSet.add( obj );
                    
                    disjointSet.union( subj, obj );
                }
            }
            
            Collection equivalenceSets = disjointSet.getEquivalanceSets();
            if( equivalenceSets.size() == 1 )
                return  Collections.singletonList( query );
            
            Map queries = new HashMap();
            for( Iterator i = patterns.iterator(); i.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) i.next();
                
                ATermAppl subj = pattern.getSubject();
                ATermAppl pred = pattern.getPredicate();
                ATermAppl obj = pattern.getObject();
                
                Object representative = disjointSet.find( subj );
                Query newQuery = (Query) queries.get( representative );
                if( newQuery == null ) {
                    newQuery = new QueryImpl( query.getKB() );
                    queries.put( representative, newQuery );
                }
                    
                if( resultVars.contains( subj ) )
                    newQuery.addResultVar( subj );
                else if( distVars.contains( subj ) )
                    newQuery.addDistVar( subj );
                
                if( pattern.isTypePattern() )
                    newQuery.addTypePattern( subj, obj );
                else {
                    newQuery.addEdgePattern( subj, pred, obj );
                    
                    if( resultVars.contains( obj ) )
                        newQuery.addResultVar( obj );
                    else if( distVars.contains( obj ) )
                        newQuery.addDistVar( obj );
                }
            }             

            return new ArrayList( queries.values() );
        } catch(RuntimeException e) {
            log.warn( "Query split failed, continuing with query execution.");
            e.printStackTrace();
            return Collections.singletonList( query );
        }
    }
    
    public static void simplify( Query query ) {
        Map allInferredTypes = new HashMap();
        
        KnowledgeBase kb = query.getKB();
        Set vars = query.getObjVars();
        
        for( Iterator i = vars.iterator(); i.hasNext(); ) {
            ATermAppl var = (ATermAppl) i.next();

            Set inferredTypes = new HashSet();            

            List outList = query.findPatterns( var, null, null );
            for( Iterator j = outList.iterator(); j.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) j.next();
                ATermAppl pred = pattern.getPredicate();

                inferredTypes.addAll( kb.getDomains( pred ) );
            }

            List inList = query.findPatterns( null, null, var );
            for( Iterator j = inList.iterator(); j.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) j.next();
                ATermAppl pred = pattern.getPredicate();

                inferredTypes.addAll( kb.getRanges( pred ) );
            }
            
            if( !inferredTypes.isEmpty() )
                allInferredTypes.put( var, inferredTypes );
        }

        List patterns = new ArrayList( query.getQueryPatterns() );
        for( Iterator i = patterns.iterator(); i.hasNext(); ) {
            QueryPattern pattern = (QueryPattern) i.next();
            
            if( pattern.isEdgePattern() ) continue;
            
            ATermAppl var = pattern.getSubject();
            Set inferredTypes = (Set) allInferredTypes.get( var );
            
            if( inferredTypes == null ) continue;
            
            ATermAppl c = pattern.getObject();
            
            if( inferredTypes.contains( c ) )
                query.removePattern( pattern );
            else if( kb.isClassified() ) {
                Set subs = kb.getTaxonomy().getSubSupers( c, false, Taxonomy.SUB, true );
                Set eqs = kb.getAllEquivalentClasses( c );
                if( SetUtils.intersects( inferredTypes, subs )
                    || SetUtils.intersects( inferredTypes, eqs ) )
                    query.removePattern( pattern );
            }
        }      
    }
    
    /**
     * @deprecated Renamed to {@link #computeSizeEstimates(KnowledgeBase)}
     */
    public static void prepare( KnowledgeBase kb ) {
    	computeSizeEstimates( kb );
    }
    
    public static void computeSizeEstimates( KnowledgeBase kb ) {
        kb.getSizeEstimate().computeAll();
    }
    
    /**
     * @deprecated Renamed to {@link #computeSizeEstimates(Query...)}
     */
    public static void prepare( Query... queries ) {
    	computeSizeEstimates( queries );
    }
    
    public static void computeSizeEstimates( Query... queries ) {
    	if( queries == null || queries.length == 0 )
    		throw new IllegalArgumentException( "No query specified!" );
    	
        SizeEstimate sizeEstimate = queries[0].getKB().getSizeEstimate();
        
        Set<ATermAppl> concepts = new HashSet<ATermAppl>();
        Set<ATermAppl> properties = new HashSet<ATermAppl>();        
        for(int j = 0; j < queries.length; j++) {
            Query query = queries[j];
            List patterns = query.getQueryPatterns();
            for( int i = 0; i < patterns.size(); i++ ) {
                QueryPattern pattern = (QueryPattern) patterns.get( i );

                if( pattern.isTypePattern() ) {
					if( !sizeEstimate.isComputed( pattern.getObject() ) )
						concepts.add( pattern.getObject() );
				}
				else {
					if( !sizeEstimate.isComputed( pattern.getPredicate() ) )
						properties.add( pattern.getPredicate() );
				}
            }
        }
        
        sizeEstimate.compute( concepts, properties );
    }
    
    public static boolean execBoolean( Query query ) {
	    return noVars.execBoolean( query );
	}

    public static boolean isEquivalent( Query q1, Query q2 ) {
        return isSubsumedBy( q1, q2 ) && isSubsumedBy( q2, q1 );
    }
    
    /**
     * @deprecated Use {@link #isSubsumed(Query, Query)}
     */
    public static boolean isSubsumed( Query sub, Query sup ) {
    	return isSubsumedBy( sub, sup );
    }
    
    public static boolean isSubsumedBy( Query sub, Query sup ) {
        return !getSubsumptionMappings( sub, sup ).isEmpty();
    }

    /**
     * @deprecated Use {@link #isSubsumed(Query, Query, KnowledgeBase)}
     */
    public static boolean isSubsumed( Query sub, Query sup, KnowledgeBase backgroundKB ) {
        return isSubsumedBy( sub, sup, backgroundKB );
    }

    public static boolean isSubsumedBy( Query sub, Query sup, KnowledgeBase backgroundKB ) {
        return !getSubsumptionMappings( sub, sup, backgroundKB ).isEmpty();
    }

    public static QueryResults getSubsumptionMappings( Query sub, Query sup ) {
        return getSubsumptionMappings( sub, sup, sub.getKB() );
    }
    
    public static QueryResults getSubsumptionMappings( Query sub, Query sup, KnowledgeBase backgroundKB ) {
        KnowledgeBase kb = backgroundKB.copy( true );
        
        List patterns = sub.getQueryPatterns();
        for( Iterator i = patterns.iterator(); i.hasNext(); ) {
            QueryPattern pattern = (QueryPattern) i.next();
            
            ATermAppl subj = pattern.getSubject();
            ATermAppl pred = pattern.getPredicate();
            ATermAppl obj = pattern.getObject();
            
            subj = (ATermAppl) (ATermUtils.isVar(subj) ? subj.getArgument(0) : subj);
            obj = (ATermAppl) (ATermUtils.isVar(obj) ? obj.getArgument(0) : obj);
            
            kb.addIndividual(subj);
            
            if( pattern.isTypePattern() )
                kb.addType( subj, obj );
            else {
                kb.addIndividual(obj);
                kb.addPropertyValue( pred, subj, obj );
            }
        }
        
        kb.isConsistent();
        
        sup.setKB( kb );
        QueryResults results = QueryEngine.exec( sup );
        sup.setKB( backgroundKB );
        
        return results;
    }

}
