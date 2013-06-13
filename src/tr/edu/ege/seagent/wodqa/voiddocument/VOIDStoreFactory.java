package tr.edu.ege.seagent.wodqa.voiddocument;

import tr.edu.ege.seagent.triplestoremanager.DBConnectionDescription;
import tr.edu.ege.seagent.triplestoremanager.DBUtil;
import tr.edu.ege.seagent.triplestoremanager.SDBHandler;
import tr.edu.ege.seagent.triplestoremanager.exception.UnsupportedDatabaseTypeException;
import tr.edu.ege.seagent.wodqa.exception.VOIDStoreCreationException;

import com.hp.hpl.jena.sdb.store.DatabaseType;

public class VOIDStoreFactory {

	public static final String JDBC_MYSQL_LOCALHOST_3306 = "jdbc:mysql://localhost:3306/";

	public static final DatabaseType MYSQL_DB_TYPE = DatabaseType.MySQL;

	public static final String PASSWORD = "toor";

	public static final String USER_NAME = "root";

	/**
	 * SDB connection instance of void store handler.
	 */
	private DBConnectionDescription connection;

	/**
	 * Database name of the void store.
	 */
	private String dbName;

	/**
	 * Void store handler that holds VOID ontologies.
	 */
	private SDBHandler voidStoreHandler;

	public VOIDStoreFactory(String dbName) throws VOIDStoreCreationException {
		this(dbName, null);
	}

	public VOIDStoreFactory(String dbName,
			DBConnectionDescription connectionDescription)
			throws VOIDStoreCreationException {
		validateDBName(dbName);
		this.dbName = dbName;

		if (connectionDescription != null) {
			connection = connectionDescription;
		} else {
			// create a connection
			connection = new DBConnectionDescription(
					VOIDStoreFactory.JDBC_MYSQL_LOCALHOST_3306, dbName,
					VOIDStoreFactory.USER_NAME, VOIDStoreFactory.PASSWORD,
					VOIDStoreFactory.MYSQL_DB_TYPE);
		}

		// create database if does not exist...
		DBUtil dbUtil;
		try {
			boolean firstCreation = false;
			dbUtil = new DBUtil(connection);
			if (!dbUtil.isExist()) {
				dbUtil.createDatabase();
				firstCreation = true;
			}
			// create SDB Handler...
			voidStoreHandler = dbUtil.createSDBHandler();
			// if there is a database before, do not delete it.
			if (firstCreation)
				voidStoreHandler.formatStore();
		} catch (UnsupportedDatabaseTypeException e) {
			throw new VOIDStoreCreationException(e.getMessage());
		}
	}

	/**
	 * Validates that the given database name is not null.
	 * 
	 * @param dbName
	 *            database name to be validated.
	 * @throws VOIDStoreCreationException
	 */
	private void validateDBName(String dbName)
			throws VOIDStoreCreationException {
		if (dbName == null) {
			throw new VOIDStoreCreationException(
					"Database name for VOID store must not be null.");
		}
	}

	public void disposeVOIDStoreHandler() {
		if (voidStoreHandler != null) {
			voidStoreHandler.dispose();
			voidStoreHandler = null;
		}
	}

	/**
	 * If there is no void store it throws an exception.
	 * 
	 * @return
	 * @throws VOIDStoreCreationException
	 */
	public String getDbName() throws VOIDStoreCreationException {
		if (voidStoreHandler == null)
			throw new VOIDStoreCreationException(
					VOIDStoreCreationException.NO_VOID_STORE);
		return dbName;
	}

	/**
	 * Creates a void store handler if there is no.
	 * 
	 * @return
	 * @throws VOIDStoreCreationException
	 */
	public SDBHandler getVOIDStoreHandler() {
		return voidStoreHandler;
	}
}
