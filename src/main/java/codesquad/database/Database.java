package codesquad.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import codesquad.database.csv.CsvDriver;

public class Database {

	private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1";
	private static final String JDBC_USER = "sa";
	private static final String JDBC_PASSWORD = "password";

	private static final String JDBC_URL_CSV = "jdbc:csv:";

	public static Connection getConnection() throws SQLException {
		DriverManager.registerDriver(new CsvDriver());
		//return DriverManager.getConnection(JDBC_URL_CSV);
		return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
	}

	public static void initializeDatabase() {
		try (Connection connection = getConnection()) {
			executeSqlFile(connection, "schema.sql");
			executeSqlFile(connection, "data.sql");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void executeSqlFile(Connection connection, String fileName) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
			Database.class.getClassLoader().getResourceAsStream(fileName)))) {
			String line;
			StringBuilder sql = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sql.append(line.trim());
				if (line.trim().endsWith(";")) {
					try (Statement statement = connection.createStatement()) {
						statement.execute(sql.toString());
						sql.setLength(0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
