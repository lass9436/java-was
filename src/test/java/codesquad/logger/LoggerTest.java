package codesquad.logger;

import static org.junit.jupiter.api.Assertions.*;
import static uk.org.lidalia.slf4jtest.LoggingEvent.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@DisplayName("Logger 클래스")
public class LoggerTest {

	private static final TestLogger logger = TestLoggerFactory.getTestLogger(LoggerTest.class);

	@Test
	public void 로그_테스트() {
		// set
		logger.setEnabledLevels(Level.DEBUG, Level.INFO);

		// Act
		logger.info("Application started successfully.");
		logger.debug("This is a debug message.");

		// Assert
		List<LoggingEvent> logEvents = logger.getLoggingEvents();
		assertEquals(2, logEvents.size());
		assertEquals(info("Application started successfully."), logEvents.get(0));
		assertEquals(debug("This is a debug message."), logEvents.get(1));
	}
}
