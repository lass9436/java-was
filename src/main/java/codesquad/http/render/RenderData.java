package codesquad.http.render;

import java.util.HashMap;
import java.util.Map;

public class RenderData {
	private String viewName;
	private Map<String, Object> model;

	public RenderData(String viewName, Map<String, Object> model) {
		this.viewName = viewName;
		this.model = model != null ? model : new HashMap<>();
	}

	public RenderData(String viewName) {
		this.viewName = viewName;
		this.model = new HashMap<>();
	}

	public String getViewName() {
		return viewName;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void addAttribute(String key, Object value) {
		model.put(key, value);
	}

	public Object getAttribute(String key) {
		return model.get(key);
	}
}