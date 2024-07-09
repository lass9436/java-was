package codesquad.utils;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonUtils {

	public static String getFirstValueFromBody(JsonNode body, String key) {
		JsonNode valueNode = body.get(key);
		if (valueNode != null) {
			if (valueNode.isArray() && !valueNode.isEmpty()) {
				return valueNode.get(0).asText();
			} else {
				return valueNode.asText();
			}
		}
		return null;
	}
}
