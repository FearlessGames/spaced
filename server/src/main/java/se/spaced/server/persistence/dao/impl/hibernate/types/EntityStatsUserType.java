package se.spaced.server.persistence.dao.impl.hibernate.types;

import com.google.inject.Inject;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import se.fearless.common.time.TimeProvider;
import se.spaced.shared.model.stats.EntityStats;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityStatsUserType implements UserType {
	@Inject
	private static TimeProvider timeProvider;

	@Override
	public int[] sqlTypes() {
		return new int[]{
				StandardBasicTypes.DOUBLE.sqlType(), StandardBasicTypes.DOUBLE.sqlType(), StandardBasicTypes.DOUBLE.sqlType(),
				StandardBasicTypes.DOUBLE.sqlType(), StandardBasicTypes.DOUBLE.sqlType(), StandardBasicTypes.DOUBLE.sqlType(),
				StandardBasicTypes.DOUBLE.sqlType()};
	}

	@Override
	public Class returnedClass() {
		return EntityStats.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == y) || (!(x == null || y == null) && x.equals(y));
	}

	@Override
	public int hashCode(Object o) throws HibernateException {
		return o.hashCode();
	}

	@Override
	public Object nullSafeGet(
			ResultSet resultSet,
			String[] names, SessionImplementor sessionImplementor,
			Object owner) throws HibernateException, SQLException {
		EntityStats stats = new EntityStats(timeProvider);


		int i = 0;
		double stamina = resultSet.getDouble(names[i++]);
		double currentHealth = resultSet.getDouble(names[i++]);
		double currentHeat = resultSet.getDouble(names[i++]);
		double shieldStrength = resultSet.getDouble(names[i++]);
		double coolRate = resultSet.getDouble(names[i++]);
		double recoveryRate = resultSet.getDouble(names[i++]);
		double attackRating = resultSet.getDouble(names[i++]);

		if (resultSet.wasNull()) {
			return null;
		}

		stats.getStamina().changeValue((int) stamina);
		stats.getCurrentHealth().changeValue((int) currentHealth);
		stats.getHeat().setValue(currentHeat);
		stats.getShieldStrength().changeValue(shieldStrength);
		stats.getBaseCoolRate().changeValue(coolRate);
		stats.getBaseShieldRecovery().changeValue(recoveryRate);
		stats.getBaseAttackRating().changeValue(attackRating);

		return stats;
	}

	@Override
	public void nullSafeSet(
			PreparedStatement preparedStatement,
			Object value,
			int index, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
		if (value == null) {
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
			preparedStatement.setNull(index++, StandardBasicTypes.DOUBLE.sqlType());
		} else {
			EntityStats stats = (EntityStats) value;
			preparedStatement.setDouble(index++, stats.getBaseStamina().getValue());
			preparedStatement.setDouble(index++, stats.getCurrentHealth().getValue());
			preparedStatement.setDouble(index++, stats.getHeat().getValue());
			preparedStatement.setDouble(index++, stats.getShieldStrength().getValue());
			preparedStatement.setDouble(index++, stats.getBaseCoolRate().getValue());
			preparedStatement.setDouble(index++, stats.getBaseShieldRecovery().getValue());
			preparedStatement.setDouble(index++, stats.getBaseAttackRating().getValue());
		}
	}

	@Override
	public Object deepCopy(Object o) throws HibernateException {
		EntityStats stats = (EntityStats) o;
		return new EntityStats(stats);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object o) throws HibernateException {
		return (Serializable) o;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		EntityStats stats = (EntityStats) original;
		EntityStats newStats = (EntityStats) target;
		stats.update(newStats);
		return stats;
	}
}
