package nl.mbosit.http;

import java.io.IOException;

/**
 * Base class for HTTP client implementations.
 */
public abstract class HttpClient {

	/**
	 * HTTP response handlers can be used to conveniently process the
	 * {@link HttpResponse}. They're expected to close the input stream found at
	 * {@link HttpResponse#getInputStream()}.
	 */
	public static interface HttpResponseHandler<T> {
		public T handleResponse(HttpResponse httpResponse) throws IOException;
	}

	/**
	 * Executes an {@link HttpRequest} and returns the resulting
	 * {@link HttpResponse}.
	 */
	public abstract HttpResponse execute(HttpRequest httpRequest)
			throws IOException;

	/**
	 * Executes an {@link HttpRequest} and uses the given
	 * {@link HttpResponseHandler} to process the {@link HttpResponse}.
	 * 
	 * @return The value returned by
	 *         {@link HttpResponseHandler#handleResponse(HttpResponse)}.
	 */
	public <T> T execute(HttpRequest httpRequest,
			HttpResponseHandler<T> httpResponseHandler) throws IOException {

		return httpResponseHandler.handleResponse(execute(httpRequest));
	}
}
