package nl.mbosit.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * A basic HTTP client implemented with {@link HttpURLConnection}.
 */
public class HttpURLConnectionClient extends HttpClient {

	private static final int CONNECT_TIMEOUT = 5 * 1000;
	private static final int READ_TIMEOUT = 5 * 1000;

	private ConnectionListener[] mHttpClientListeners;

	public HttpURLConnectionClient() {
		this(new ConnectionListener[0]);
	}

	public HttpURLConnectionClient(ConnectionListener[] httpClientListeners) {
		mHttpClientListeners = httpClientListeners;
	}

	/**
	 * Executes an {@link HttpRequest} and returns the resulting
	 * {@link HttpResponse}. Notes:
	 * 
	 * <ul>
	 * <li>If {@link HttpRequest#hasContent()} is true and
	 * {@link HttpRequest#getMethod()} does not support output,
	 * HttpURLConnection will implicitly change the method to "POST".</li>
	 * 
	 * <li>The "Content-Lenght" header is set automatically via
	 * {@link HttpURLConnection#setFixedLengthStreamingMode(int)}.</li>
	 * </ul>
	 */
	@Override
	public HttpResponse execute(HttpRequest httpRequest) throws IOException {

		HttpURLConnection httpURLConnection = null;

		try {
			// 'Open' the connection (no actual I/O happens here).
			URL url = new URL(httpRequest.getUrl());
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(READ_TIMEOUT);

			// Let listeners operate on the request.
			for (ConnectionListener httpClientListener : mHttpClientListeners) {
				httpClientListener.onRequest(httpRequest, httpURLConnection);
			}

			// Set the HTTP headers.
			Map<String, String> httpHeaderMap = httpRequest.getHeaderMap();
			for (String headerName : httpHeaderMap.keySet()) {
				httpURLConnection.setRequestProperty(headerName,
						httpHeaderMap.get(headerName));
			}

			// Let listeners operate on the request.
			for (ConnectionListener httpClientListener : mHttpClientListeners) {
				httpClientListener.onRequest(httpRequest, httpURLConnection);
			}

			boolean doOutput = httpRequest.hasContent();

			if (doOutput) {
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setFixedLengthStreamingMode(httpRequest
						.getContent().length);
			}

			httpURLConnection.setRequestMethod(httpRequest.getMethod()
					.toString());

			if (doOutput) {
				// Note: the bytes don't actually get transferred yet.
				httpURLConnection.getOutputStream().write(
						httpRequest.getContent());
			}

			// The below line fires the HTTP request.
			InputStream inputStream = httpURLConnection.getInputStream();

			// Construct the HTTP response.
			HttpResponse httpResponse = new HttpResponse(
					httpURLConnection.getResponseCode(),
					httpURLConnection.getHeaderFields(), inputStream);

			// Let listeners process the response.
			for (ConnectionListener httpClientListener : mHttpClientListeners) {
				httpClientListener.onResponse(httpRequest, httpResponse);
			}

			return httpResponse;

		} catch (Exception e) {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
			throw new IOException("HTTP request failed", e);
		}
	}

	/**
	 * Observer class that may be used to write extensions for this client,
	 * implementing for example SSL or HTTP cookie support.
	 */
	public static abstract class ConnectionListener {

		/**
		 * Called when the request is being constructed. Should not access
		 * {@link HttpURLConnection#getOutputStream()} or
		 * {@link HttpURLConnection#getInputStream()}).
		 */
		public void onRequest(HttpRequest httpRequest,
				HttpURLConnection httpURLConnection) throws IOException {

		}

		/**
		 * Called before the response is processed. Should not access
		 * {@link HttpResponse#getInputStream()}, use a
		 * {@link HttpClient.HttpResponseHandler} instead.
		 */
		public void onResponse(HttpRequest httpRequest,
				HttpResponse httpResponse) throws IOException {

		}
	}
}