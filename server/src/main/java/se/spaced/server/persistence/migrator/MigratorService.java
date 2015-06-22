package se.spaced.server.persistence.migrator;

public class MigratorService {

	public void runMigrators(Iterable<Migrator> migrators) {
		for (Migrator migrator : migrators) {
			migrator.execute();
		}
	}
}
