package codesquad.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonUtilsTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void 단일_값_첫_번째_가져오기() throws Exception {
		String jsonString = "{ \"userId\": \"john_doe\" }";
		JsonNode body = objectMapper.readTree(jsonString);

		String result = JsonUtils.getFirstValueFromBody(body, "userId");
		assertEquals("john_doe", result);
	}

	@Test
	public void 배열_값_첫_번째_가져오기() throws Exception {
		String jsonString = "{ \"userId\": [\"john_doe\", \"jane_doe\"] }";
		JsonNode body = objectMapper.readTree(jsonString);

		String result = JsonUtils.getFirstValueFromBody(body, "userId");
		assertEquals("john_doe", result);
	}

	@Test
	public void 키_없음_가져오기() throws Exception {
		String jsonString = "{ \"name\": \"John Doe\" }";
		JsonNode body = objectMapper.readTree(jsonString);

		String result = JsonUtils.getFirstValueFromBody(body, "userId");
		assertNull(result);
	}

	@Test
	public void 빈_배열_가져오기() throws Exception {
		String jsonString = "{ \"userId\": [] }";
		JsonNode body = objectMapper.readTree(jsonString);

		String result = JsonUtils.getFirstValueFromBody(body, "userId");
		assertNull(result);
	}
}
