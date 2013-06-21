package nl.mbosit.http;

import java.io.IOException;

/**
 * HTTP response handler that simply returns the HTTP status code after closing
 * the response's input stream.
 */
public class StatusCodeHandler
		implements
			HttpClient.HttpResponseHandler<Integer> {

	@Override
	public Integer handleResponse(HttpResponse httpResponse) throws IOException {
		httpResponse.getInputStream().close();
		return httpResponse.getStatusCode();
	}
}
