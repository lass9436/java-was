package codesquad.http.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import codesquad.http.constants.HttpMethod;
import codesquad.http.constants.HttpVersion;
import codesquad.http.dto.HttpRequest;

public class HttpRequestParser {

	public static HttpRequest parse(InputStream inputStream) throws IOException {
		String requestLine = InputStreamUtils.readLine(inputStream);
		Map<String, String> requestLineMap = RequestLineParser.parseRequestLine(requestLine);

		String url = requestLineMap.get("url");
		String path = RequestLineParser.extractPath(url);
		String queryString = RequestLineParser.extractQueryString(url);

		Map<String, List<String>> queryParams = QueryStringParser.parseQueryParams(queryString);
		Map<String, List<String>> headers = HeaderParser.parseHeaders(inputStream);
		Map<String, List<Object>> body = BodyParser.parseRequestBody(inputStream, headers);

		HttpMethod httpMethod = HttpMethod.valueOf(requestLineMap.get("method"));
		HttpVersion httpVersion = HttpVersion.valueOf(
			requestLineMap.get("version").replace('/', '_').replace('.', '_'));

		return new HttpRequest(
			httpMethod,
			path,
			httpVersion,
			headers,
			queryParams,
			body
		);
	}
}
