package codesquad.database.csv;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class CsvDriver implements Driver {

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if (!url.startsWith("jdbc:csv:")) {
			return null;
		}
		return new CsvConnection();
	}

	@Override
	public boolean acceptsURL(String url) {
		return url != null && url.startsWith("jdbc:csv:");
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return new DriverPropertyInfo[0];
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}

