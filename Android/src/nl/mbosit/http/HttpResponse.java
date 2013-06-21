package nl.mbosit.http;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP response.
 */
public class HttpResponse {

	private int mStatusCode;
	private Map<String, List<String>> mHeaderMap;
	private InputStream mInputStream;

	public HttpResponse(int statusCode, Map<String, List<String>> headerMap,
			InputStream inputStream) {

		mStatusCode = statusCode;
		mHeaderMap = headerMap;
		mInputStream = inputStream;
	}

	public int getStatusCode() {
		return mStatusCode;
	}

	/**
	 * Gets the values of the HTTP header with the given name.
	 * 
	 * @param name
	 *            The HTTP header name.
	 * @return The HTTP header values, or null if none exists.
	 */
	public List<String> getHeader(String name) {
		return mHeaderMap.get(name);
	}

	/**
	 * @return A map of all HTTP headers.
	 */
	public Map<String, List<String>> getHeaderListMap() {
		return mHeaderMap;
	}

	/**
	 * @return The input stream for the response entity. Response handlers
	 *         should close this.
	 */
	public InputStream getInputStream() {
		return mInputStream;
	}

	/**
	 * Produces a string like "HTTP/1.1 200".
	 */
	public String toString() {
		return "HTTP/1.1 " + mStatusCode;
	}
}
