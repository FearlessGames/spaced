package se.spaced.server.persistence.dao.impl.hibernate.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SpacedUUIDHibernateType implements UserType {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final int[] SQL_TYPES = {Types.VARCHAR};

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	@Override
	public Class<UUID> returnedClass() {
		return UUID.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == null) ? (y == null) : x.equals(y);
	}

	@Override
	public int hashCode(Object o) throws HibernateException {
		if (o == null) {
			return 0;
		}
		return o.hashCode();
	}

	@Override
	public Object deepCopy(Object o) throws HibernateException {
		return o;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object o) throws HibernateException {
		return (Serializable) o;
	}

	@Override
	public Object assemble(Serializable serializable, Object o) throws HibernateException {
		return o;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
		String uuidString = StandardBasicTypes.STRING.nullSafeGet(resultSet, names[0], sessionImplementor);

		if (uuidString == null) {
			return null;
		}

		return UUID.fromString(uuidString);
	}

	@Override
	public void nullSafeSet(
			PreparedStatement statement,
			Object value,
			int index,
			SessionImplementor sessionImplementor) throws HibernateException, SQLException {

		if (value == null) {
			statement.setNull(index, Types.VARCHAR);
		} else {
			UUID uuid = (UUID) value;
			StandardBasicTypes.STRING.set(statement, uuid.toString(), index, sessionImplementor);
		}
	}


}