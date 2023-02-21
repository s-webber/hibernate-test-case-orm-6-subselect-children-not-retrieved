package org.hibernate.bugs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hibernate.model.Child;
import org.hibernate.model.Parent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		Parent parent = new Parent();
		entityManager.persist(parent);
		entityManager.persist(new Child(parent, "b"));
		entityManager.persist(new Child(parent, "a"));
		entityManager.persist(new Child(parent, "c"));

		entityManager.getTransaction().commit();
		entityManager.close();

		entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Parent> query = criteriaBuilder.createQuery(Parent.class);
		Root<?> root = query.from(Parent.class);
		root.join("grandParent", JoinType.LEFT);
		query.where(criteriaBuilder.isNull(root.get("grandParent")));
		query.select(query.from(Parent.class));
		// expect the 3 Child entities to be returned but get none
		assertEquals(3, entityManager.createQuery(query).getResultList().get(0).getChildren().size());

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
