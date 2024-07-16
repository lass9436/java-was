package codesquad.http.filter;

import java.util.ArrayList;
import java.util.List;

public class FilterChain {

	private final List<Filter> filters;

	public FilterChain(List<Filter> filters) {
		this.filters = filters;
	}

	public FilterChain() {
		this.filters = new ArrayList<Filter>();
		filters.add(new LoginCheckFilter());
	}

	public void doFilter() {
		for (Filter filter : filters) {
			filter.doFilter();
		}
	}
}
