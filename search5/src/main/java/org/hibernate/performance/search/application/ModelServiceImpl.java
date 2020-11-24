package org.hibernate.performance.search.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.backend.FlushLuceneWork;
import org.hibernate.search.cfg.Environment;
import org.hibernate.search.engine.integration.impl.ExtendedSearchIntegrator;
import org.hibernate.search.hcore.util.impl.ContextHelper;
import org.hibernate.search.indexes.spi.IndexManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.spi.impl.PojoIndexedTypeIdentifier;

public class ModelServiceImpl implements ModelService {

	@Override
	public Properties properties(boolean manual) {
		Properties properties = new Properties();
		properties.put( Environment.MODEL_MAPPING, SearchProgrammaticMapping.create() );
		properties.put( "hibernate.search.default.directory_provider", "local-heap" );

		if ( manual ) {
			properties.put( "hibernate.search.indexing_strategy", "manual" );
		}

		return properties;
	}

	@Override
	public void waitForIndexFlush(SessionFactory sessionFactory, Class<?> entityClass) {
		ExtendedSearchIntegrator integrator = ContextHelper.getSearchIntegratorBySF( sessionFactory );
		PojoIndexedTypeIdentifier identifier = new PojoIndexedTypeIdentifier( entityClass );

		// Ensure that we'll block until all works have been performed
		for ( IndexManager indexManager : integrator.getIndexBinding( identifier ).getIndexManagerSelector().all() ) {
			indexManager.performStreamOperation( new FlushLuceneWork( null, identifier ), null, false );
		}
	}

	@Override
	public <E> List<E> search(Session session, Class<E> entityClass) {
		FullTextSession fullTextSession = Search.getFullTextSession( session );

		QueryBuilder b = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity( entityClass ).get();

		org.apache.lucene.search.Query luceneQuery = b.all().createQuery();

		@SuppressWarnings("deprecation")
		org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery);
		return fullTextQuery.list(); //return a list of managed objects
	}

	@Override
	public void massIndexing(Session session, Class<?> entityClass) throws InterruptedException {
		FullTextSession fullTextSession = Search.getFullTextSession( session );
		fullTextSession.createIndexer( entityClass ).startAndWait();
	}
}
