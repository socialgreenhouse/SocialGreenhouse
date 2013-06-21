package nl.mbosit.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Implementation of {@link HttpClient.HttpResponseHandler} that reads and
 * returns the response entity as a string.
 */
public class StringHandler
		implements
			HttpClient.HttpResponseHandler<String> {

	private Charset mCharset;

	public StringHandler(String charsetName) {
		this(Charset.forName(charsetName));
	}

	public StringHandler(Charset charset) {
		mCharset = charset;
	}

	@Override
	public String handleResponse(HttpResponse httpResponse) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(httpResponse.getInputStream(), mCharset));

		StringBuilder stringBuilder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		bufferedReader.close();
		return stringBuilder.toString();
	}
}
