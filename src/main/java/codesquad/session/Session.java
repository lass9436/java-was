package codesquad.session;

import java.time.LocalDateTime;
import java.util.Map;

public record Session(
	Map<String, Object> attributes,
	LocalDateTime creationTime,
	LocalDateTime expirationTime
) {
}
