package codesquad.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import codesquad.http.constants.HttpHandleType;
import codesquad.http.constants.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HttpFunction {
	String path();
	HttpMethod method();
	HttpHandleType type();
}
