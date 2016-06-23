package se.spaced.server.persistence.dao.impl.hibernate.types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import se.fearless.common.stats.ModStat;
import se.fearless.common.stats.Operator;
import se.spaced.shared.model.stats.SpacedStatType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModStatUserType implements UserType {
	@Override
	public int[] sqlTypes() {
		return new int[]{StandardBasicTypes.DOUBLE.sqlType(), StandardBasicTypes.STRING.sqlType(), StandardBasicTypes.STRING.sqlType()};
	}

	@Override
	public Class returnedClass() {
		return ModStat.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x == y || !(x == null || y == null) && x.equals(y);
	}

	@Override
	public int hashCode(Object o) throws HibernateException {
		return o.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
		double amount = resultSet.getDouble(names[0]);
		String statTypeName = resultSet.getString(names[1]);
		String operatorName = resultSet.getString(names[2]);
		if (resultSet.wasNull()) {
			return null;
		}
		return new ModStat(amount, SpacedStatType.valueOf(statTypeName), Operator.valueOf(operatorName));
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement, Object value, int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
		if (value == null) {
			preparedStatement.setNull(index, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index + 1, StandardBasicTypes.STRING.sqlType());
			preparedStatement.setNull(index + 2, StandardBasicTypes.STRING.sqlType());
		} else {
			ModStat stat = (ModStat) value;
			preparedStatement.setDouble(index, stat.getValue());
			SpacedStatType statType = (SpacedStatType) stat.getStatType();
			preparedStatement.setString(index + 1, statType.name());
			preparedStatement.setString(index + 2, stat.getOperator().name());
		}

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
		return serializable;
	}

	@Override
	public Object replace(Object original, Object o1, Object o2) throws HibernateException {
		return original;
	}
}
