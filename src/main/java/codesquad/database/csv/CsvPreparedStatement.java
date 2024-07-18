package codesquad.database.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvPreparedStatement implements PreparedStatement {

	private final String query;
	private final List<Object> parameters = new ArrayList<>();

	public CsvPreparedStatement(String query) {
		this.query = query;
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		if (!query.startsWith("SELECT")) {
			throw new SQLException("Not a SELECT query.");
		}

		String tableName = extractTableNameFromSelect(query);
		String filePath = System.getProperty("user.home") + "/csv/" + tableName + ".csv";
		List<Map<String, String>> data = readCsv(filePath);

		return new CsvResultSet(data);
	}

	private List<Map<String, String>> readCsv(String filePath) throws SQLException {
		List<Map<String, String>> data = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String header = reader.readLine();
			if (header == null) {
				throw new SQLException("The file is empty.");
			}

			String[] columns = header.split(",");
			String row;
			while ((row = reader.readLine()) != null) {
				String[] values = row.split(",");
				Map<String, String> rowData = new HashMap<>();
				for (int i = 0; i < columns.length; i++) {
					rowData.put(columns[i].trim(), values[i].trim());
				}
				data.add(rowData);
			}
		} catch (IOException e) {
			throw new SQLException("Failed to read the CSV file.", e);
		}

		// WHERE 조건 처리
		if (query.contains("WHERE")) {
			String condition = query.substring(query.indexOf("WHERE") + 5).trim();
			String[] conditionParts = condition.split("=");
			String column = conditionParts[0].trim();
			String value = parameters.get(0).toString();

			data.removeIf(row -> !value.equals(row.get(column)));
		}
		return data;
	}

	private String extractTableNameFromSelect(String sql) throws SQLException {
		Pattern pattern = Pattern.compile("SELECT .* FROM (\\w+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new SQLException("Table name not found in SQL.");
		}
	}

	@Override
	public int executeUpdate() throws SQLException {
		if (query.startsWith("INSERT")) {
			return executeInsert();
		} else if (query.startsWith("UPDATE")) {
			throw new UnsupportedOperationException("UPDATE is not supported yet.");
		} else if (query.startsWith("DELETE")) {
			throw new UnsupportedOperationException("DELETE is not supported yet.");
		}
		throw new SQLException("Unsupported query: " + query);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {

	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {

	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {

	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {

	}

	private String extractTableNameSql(String sql) throws SQLException {
		Pattern pattern = Pattern.compile(
			"(INSERT INTO|UPDATE|DELETE FROM|FROM)\\s+(\\w+)",
			Pattern.CASE_INSENSITIVE
		);
		Matcher matcher = pattern.matcher(sql);
		if (matcher.find()) {
			return matcher.group(2);
		} else {
			throw new SQLException("Table name not found in SQL.");
		}
	}

	private int executeInsert() throws SQLException {
		String tableName = extractTableNameSql(query);
		String filePath = System.getProperty("user.home") + "/csv/" + tableName + ".csv";
		File file = new File(filePath);
		if (!file.exists()) {
			throw new SQLException("Table file does not exist.");
		}

		int nextId = getNextId(filePath);
		if (nextId != -1) {
			parameters.add(0, nextId);
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
			writer.write(String.join(",", parameters.stream().map(Object::toString).toArray(String[]::new)));
			writer.newLine();
		} catch (IOException e) {
			throw new SQLException("Failed to write to CSV file.", e);
		}
		return 1;
	}

	private int getNextId(String filePath) throws SQLException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String header = reader.readLine();
			if (header == null || !header.split(",")[0].trim().equalsIgnoreCase("id")) {
				return -1;
			}

			String lastRow = null;
			String currentRow;
			while ((currentRow = reader.readLine()) != null) {
				lastRow = currentRow;
			}

			if (lastRow == null) {
				return 1;
			}

			String[] lastRowData = lastRow.split(",");
			return Integer.parseInt(lastRowData[0].trim()) + 1;
		} catch (IOException | NumberFormatException e) {
			throw new SQLException("Failed to read the CSV file.", e);
		}
	}

	@Override
	public void setString(int parameterIndex, String x) {
		setParameter(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {

	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {

	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {

	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

	}

	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

	}

	@Override
	public void clearParameters() throws SQLException {

	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {

	}

	@Override
	public boolean execute() throws SQLException {
		return false;
	}

	@Override
	public void addBatch() throws SQLException {

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {

	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {

	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {

	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {

	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return null;
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {

	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return null;
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {

	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {

	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setInt(int parameterIndex, int x) {
		setParameter(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {

	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {

	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {

	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

	}

	private void setParameter(int parameterIndex, Object value) {
		if (parameters.size() < parameterIndex) {
			for (int i = parameters.size(); i < parameterIndex; i++) {
				parameters.add(null);
			}
		}
		parameters.set(parameterIndex - 1, value);
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		return null;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return 0;
	}

	@Override
	public void close() throws SQLException {
		// 리소스 정리
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return 0;
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {

	}

	@Override
	public int getMaxRows() throws SQLException {
		return 0;
	}

	@Override
	public void setMaxRows(int max) throws SQLException {

	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {

	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {

	}

	@Override
	public void cancel() throws SQLException {

	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return null;
	}

	@Override
	public void clearWarnings() throws SQLException {

	}

	@Override
	public void setCursorName(String name) throws SQLException {

	}

	@Override
	public boolean execute(String sql) throws SQLException {
		return false;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return null;
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return 0;
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return false;
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {

	}

	@Override
	public int getFetchDirection() throws SQLException {
		return 0;
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {

	}

	@Override
	public int getFetchSize() throws SQLException {
		return 0;
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return 0;
	}

	@Override
	public int getResultSetType() throws SQLException {
		return 0;
	}

	@Override
	public void addBatch(String sql) throws SQLException {

	}

	@Override
	public void clearBatch() throws SQLException {

	}

	@Override
	public int[] executeBatch() throws SQLException {
		return new int[0];
	}

	@Override
	public Connection getConnection() throws SQLException {
		return null;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return false;
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return null;
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		return 0;
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		return 0;
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		return 0;
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		return false;
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return false;
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		return false;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return 0;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {

	}

	@Override
	public boolean isPoolable() throws SQLException {
		return false;
	}

	@Override
	public void closeOnCompletion() throws SQLException {

	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	// 생략된 메서드
}
