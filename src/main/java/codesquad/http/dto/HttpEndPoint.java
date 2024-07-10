package codesquad.http.dto;

import codesquad.http.constants.HttpMethod;

public record HttpEndPoint(String path, HttpMethod method) {
}
