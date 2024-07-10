package codesquad.session;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

	private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
	private static final long SESSION_TIMEOUT_MINUTES = 30; // 예: 세션 타임아웃 30분
	private static final ThreadLocal<String> threadLocalSID = new ThreadLocal<>();

	private static String createSession(String key, Object value) {
		String sessionId = UUID.randomUUID().toString();
		LocalDateTime creationTime = LocalDateTime.now();
		LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(SESSION_TIMEOUT_MINUTES);
		Map<String, Object> attributes = new HashMap<>();
		attributes.put(key, value);
		Session session = new Session(attributes, creationTime, expirationTime);
		sessions.put(sessionId, session);
		return sessionId;
	}

	public static String putSession(String key, Object value) {
		String sid = getThreadLocalSID();
		if (sid != null) {
			Session session = sessions.get(sid);
			if (isExpired(session)) {
				setThreadLocalSID(createSession(key, value));
				return getThreadLocalSID();
			}
			session.attributes().put(key, value);
			return getThreadLocalSID();
		}
		setThreadLocalSID(createSession(key, value));
		return getThreadLocalSID();
	}

	public static Object getSession(String key) {
		String sid = getThreadLocalSID();
		if (sid != null) {
			Session session = sessions.get(sid);
			if (isExpired(session)) {
				return null;
			}
			return session.attributes().get(key);
		}
		return null;
	}

	private static boolean isExpired(Session session) {
		return session == null || (session != null && session.expirationTime().isBefore(LocalDateTime.now()));
	}

	private static void removeSession(String sessionId) {
		sessions.remove(sessionId);
	}

	public static void setThreadLocalSID(String sid) {
		threadLocalSID.set(sid);
	}

	public static String getThreadLocalSID() {
		return threadLocalSID.get();
	}

	public static void removeThreadLocalSID() {
		threadLocalSID.remove();
	}
}
