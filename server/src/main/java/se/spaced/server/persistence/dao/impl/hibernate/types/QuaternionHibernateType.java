package se.spaced.server.persistence.dao.impl.hibernate.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import se.ardortech.math.SpacedRotation;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class QuaternionHibernateType implements CompositeUserType {

	@Override
	public String[] getPropertyNames() {
		return new String[]{"x", "y", "z", "w"};
	}

	@Override
	public Type[] getPropertyTypes() {
		return new Type[]{StandardBasicTypes.DOUBLE, StandardBasicTypes.DOUBLE, StandardBasicTypes.DOUBLE, StandardBasicTypes.DOUBLE};
	}

	@Override
	public Object getPropertyValue(Object component, int property) throws HibernateException {
		SpacedRotation q = (SpacedRotation) component;
		switch (property) {
			case 0:
				return q.getW();
			case 1:
				return q.getX();
			case 2:
				return q.getY();
			case 3:
				return q.getZ();
		}
		return null;

	}

	@Override
	public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
		throw new UnsupportedOperationException("Immutable class SpacedRotation");
	}

	@Override
	public Class returnedClass() {
		return SpacedRotation.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return ((x == null) && (y == null)) || ((x != null) && x.equals(y));
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(
			ResultSet rs,
			String[] names,
			SessionImplementor session,
			Object owner) throws HibernateException, SQLException {
		Double x = StandardBasicTypes.DOUBLE.nullSafeGet(rs, names[0], session);
		Double y = StandardBasicTypes.DOUBLE.nullSafeGet(rs, names[1], session);
		Double z = StandardBasicTypes.DOUBLE.nullSafeGet(rs, names[2], session);
		Double w = StandardBasicTypes.DOUBLE.nullSafeGet(rs, names[3], session);
		if (x == null || y == null || z == null || w == null) {
			return null;
		}

		return new SpacedRotation(x, y, z, w);
	}

	@Override
	public void nullSafeSet(
			PreparedStatement st,
			Object value,
			int index,
			SessionImplementor session) throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.DOUBLE);
			st.setNull(index + 1, Types.DOUBLE);
			st.setNull(index + 2, Types.DOUBLE);
			st.setNull(index + 3, Types.DOUBLE);
			return;
		}


		SpacedRotation rotation = (SpacedRotation) value;
		StandardBasicTypes.DOUBLE.nullSafeSet(st, rotation.getX(), index, session);
		StandardBasicTypes.DOUBLE.nullSafeSet(st, rotation.getY(), index + 1, session);
		StandardBasicTypes.DOUBLE.nullSafeSet(st, rotation.getZ(), index + 2, session);
		StandardBasicTypes.DOUBLE.nullSafeSet(st, rotation.getW(), index + 3, session);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object replace(
			Object original,
			Object target,
			SessionImplementor session,
			Object owner) throws HibernateException {
		return original;
	}
}