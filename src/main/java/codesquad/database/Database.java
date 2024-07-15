package codesquad.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static final String JDBC_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1";
	private static final String JDBC_USER = "sa";
	private static final String JDBC_PASSWORD = "password";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
	}

	public static void initializeDatabase() {
		try (Connection connection = getConnection();
			 Statement statement = connection.createStatement()) {

			// schema.sql 파일 읽기
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				Database.class.getClassLoader().getResourceAsStream("schema.sql")))) {
				String line;
				StringBuilder sql = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					sql.append(line).append("\n");
				}
				statement.execute(sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
