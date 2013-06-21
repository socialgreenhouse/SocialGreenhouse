package nl.mbosit.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Describes an HTTP request.
 */
public class HttpRequest {

	private HttpMethod mMethod = HttpMethod.GET;
	private String mUrl;

	private Map<String, String> mHttpHeaderMap = new HashMap<String, String>();
	private byte[] mContent = null;

	public HttpRequest(String url) {
		mUrl = url;
	}

	public String getUrl() {
		return mUrl;
	}

	/**
	 * @param method
	 * @return The HttpRequest itself, for chainability.
	 */
	public HttpRequest setMethod(HttpMethod method) {
		mMethod = method;
		return this;
	}

	public HttpMethod getMethod() {
		return mMethod;
	}

	/**
	 * Sets an HTTP header, replacing it if it already exists.
	 * 
	 * @return The HttpRequest itself, for chainability.
	 */
	public HttpRequest setHeader(String name, String value) {
		mHttpHeaderMap.put(name, value);
		return this;
	}

	/**
	 * Sets the current header map (any headers set through
	 * {@link #setHeader(String, String)} will be replaced).
	 * 
	 * @return The HttpRequest itself, for chainability.
	 */
	public HttpRequest setHeaderMap(Map<String, String> httpHeaderMap) {
		mHttpHeaderMap = httpHeaderMap;
		return this;
	}

	public Map<String, String> getHeaderMap() {
		return mHttpHeaderMap;
	}

	/**
	 * @return True if the request has an entitify; false otherwise.
	 */
	public boolean hasContent() {
		return mContent != null;
	}

	/**
	 * Set the content. By default, the content is <code>null</code>, which
	 * flags the request as not having output.
	 * 
	 * @return The HttpRequest itself, for chainability.
	 */
	public HttpRequest setContent(byte[] bytes) {
		mContent = bytes;
		return this;
	}

	public byte[] getContent() {
		return mContent;
	}

	/**
	 * Produces something like "GET http://www.for.example/"
	 */
	public String toString() {
		return mMethod + " " + mUrl;
	}
}
