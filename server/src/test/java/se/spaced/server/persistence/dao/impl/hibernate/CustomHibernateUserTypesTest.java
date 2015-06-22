package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.spaced.server.persistence.dao.impl.hibernate.types.QuaternionHibernateType;
import se.spaced.server.persistence.dao.impl.hibernate.types.Vector3HibernateType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.junit.Assert.assertEquals;

public class CustomHibernateUserTypesTest extends PersistentTestBase {
	@Test
	public void testUserType() {
		Foo foo = new Foo();
		foo.quat = new SpacedRotation(2, 3, 4, 1);
		foo.vec = new SpacedVector3(5, 6, 7);

		transactionManager.beginTransaction();
		Session session = transactionManager.getCurrentSession();
		session.saveOrUpdate(foo);
		session.flush();
		transactionManager.getCurrentSession().getTransaction().commit();

		transactionManager.beginTransaction();
		session = transactionManager.getCurrentSession();
		Foo f2 = (Foo) session.get(Foo.class, 1);

		assertEquals(foo.quat, f2.quat);
		assertEquals(foo.vec, f2.vec);

		transactionManager.getCurrentSession().getTransaction().commit();

	}

	@Override
	public void annotateClasses(Configuration config) {
		config.addAnnotatedClass(Foo.class);
	}

	@TypeDefs({
			@TypeDef(name = "vector3", typeClass = Vector3HibernateType.class),
			@TypeDef(name = "quaternion", typeClass = QuaternionHibernateType.class)
	})

	@Entity(name = "Foo")
	public static class Foo {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private int id;

		@Type(type = "quaternion")
		@Columns(columns = {
				@Column(name = "qx"),
				@Column(name = "qy"),
				@Column(name = "qz"),
				@Column(name = "qw")}
		)
		private SpacedRotation quat;

		@Type(type = "vector3")
		@Columns(columns = {
				@Column(name = "vx"),
				@Column(name = "vy"),
				@Column(name = "vz")}
		)
		private SpacedVector3 vec;
	}

}
