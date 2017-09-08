package se.spaced.server.persistence.dao.impl.hibernate.types;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import se.spaced.server.persistence.util.ServerXStreamRegistry;

import java.io.Serializable;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Usage: @Type(type = "se.spaced.server.persistence.dao.impl.hibernate.types.XmlStringType")
 * or @Type(type = "xml")
 */
public class XmlStringType implements UserType {
	private static final int[] TYPES = {Types.CLOB};
	private static final XStream STREAM = new XStream(new DomDriver());

	static {
		ServerXStreamRegistry serverXStreamRegistry = new ServerXStreamRegistry();
		serverXStreamRegistry.registerDefaultsOn(STREAM);
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return null == cached ? null : STREAM.fromXML((String) cached);
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return null == value ? null : STREAM.toXML(value);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return null == value ? null : STREAM.fromXML(STREAM.toXML(value));
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == y) || (!(x == null || y == null) && x.equals(y));
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return null == x ? 0 : x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor sessionImplementor, Object owner)
			throws HibernateException, SQLException {
		Clob clob = StandardBasicTypes.CLOB.nullSafeGet(rs, names[0], sessionImplementor);
		if (clob != null) {
			return STREAM.fromXML(clob.getCharacterStream());
		}
		return null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor sessionImplementor)
			throws HibernateException, SQLException {

		if (value != null) {
			String content = STREAM.toXML(value);
			StandardBasicTypes.CLOB.nullSafeSet(st, StandardBasicTypes.CLOB.fromString(content), index, sessionImplementor);
		} else {
			StandardBasicTypes.CLOB.nullSafeSet(st, null, index, sessionImplementor);
		}

	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return this.deepCopy(original);
	}

	@Override
	public Class returnedClass() {
		return Serializable.class;
	}

	@Override
	public int[] sqlTypes() {
		return TYPES;
	}
}
