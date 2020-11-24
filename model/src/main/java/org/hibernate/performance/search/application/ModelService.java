package org.hibernate.performance.search.application;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public interface ModelService {

	Properties properties(boolean manual);

	void waitForIndexFlush(SessionFactory sessionFactory, Class<?> entityClass);

	<E> List<E> search(Session session, Class<E> entityClass);

	void massIndexing(Session session, Class<?> entityClass) throws InterruptedException;

}
