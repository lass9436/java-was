package codesquad.http.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("QueryStringParser 테스트")
class QueryStringParserTest {

	@Test
	void 빈_쿼리_문자열() {
		String queryString = "";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertTrue(result.isEmpty());
	}

	@Test
	void 단일_쿼리_파라미터() {
		String queryString = "key=value";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(1, result.size());
		assertEquals("value", result.get("key").get(0));
	}

	@Test
	void 여러_쿼리_파라미터() {
		String queryString = "key1=value1&key2=value2";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(2, result.size());
		assertEquals("value1", result.get("key1").get(0));
		assertEquals("value2", result.get("key2").get(0));
	}

	@Test
	void 동일한_키_여러_값() {
		String queryString = "key=value1&key=value2";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(1, result.size());
		assertEquals(2, result.get("key").size());
		assertEquals("value1", result.get("key").get(0));
		assertEquals("value2", result.get("key").get(1));
	}

	@Test
	void 값이_없는_키() {
		String queryString = "key=";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(1, result.size());
		assertEquals("", result.get("key").get(0));
	}

	@Test
	void 인코딩된_쿼리_파라미터() {
		String queryString = "key1=value%201&key2=value%202";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(2, result.size());
		assertEquals("value 1", result.get("key1").get(0));
		assertEquals("value 2", result.get("key2").get(0));
	}

	@Test
	void 값이_없는_키_여러개() {
		String queryString = "key1=&key2=";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(2, result.size());
		assertEquals("", result.get("key1").get(0));
		assertEquals("", result.get("key2").get(0));
	}

	@Test
	void 값이_없는_마지막_키() {
		String queryString = "key1=value1&key2=";
		Map<String, List<String>> result = QueryStringParser.parseQueryParams(queryString);
		assertEquals(2, result.size());
		assertEquals("value1", result.get("key1").get(0));
		assertEquals("", result.get("key2").get(0));
	}

}
