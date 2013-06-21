package nl.mbosit.http;

/**
 * An enum of HTTP methods as specified in {@link http
 * ://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html}.
 */
public enum HttpMethod {

	OPTIONS("OPTIONS"), HEAD("HEAD"), GET("GET"), POST("POST"), PUT("PUT"), DELETE(
			"DELETE"), TRACE("TRACE");

	private String mName;

	private HttpMethod(String name) {
		mName = name;
	}

	public String toString() {
		return mName;
	}
}
