package com.socialgreenhouse.android;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.socialgreenhouse.android.network.ServerUrlHelper;

public class TwitterSetupActivity extends Activity implements OnClickListener {

	private static final String PATH = "/settings";

	private RequestToken requestToken = null;
	private Twitter twitter;
	private AccessToken accessToken = null;

	private HttpClient mHttpClient = new DefaultHttpClient();
	private ResponseHandler<String> mResponseHandler = new BasicResponseHandler();

	private Activity self;
	private WebView mWebView;
	private EditText mPinEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter);

		self = this;

		mPinEditText = (EditText) findViewById(R.id.editText_twitter_pin);

		mWebView = (WebView) findViewById(R.id.webView_twitter);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		((Button) findViewById(R.id.button_twitter_authorize))
				.setOnClickListener(this);

		new DownloadSettingsTask().execute();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.button_twitter_authorize :
				if (mPinEditText.getText().length() < 4) {
					((EditText) findViewById(R.id.editText_twitter_pin))
							.setError(getString(R.string.error_pin_invalid));
					return;
				}
				new UploadSettingsTask().execute();
				return;
		}
	}

	private class UploadSettingsTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(self, null,
					getString(R.string.phrase_connecting));
		}

		protected Boolean doInBackground(Void... params) {

			try {
				String input = mPinEditText.getText().toString();

				accessToken = twitter.getOAuthAccessToken(requestToken, input);
				JSONObject object = new JSONObject();
				try {
					object.put("AccessToken", accessToken.getToken());
					object.put("AccessTokenSecret",
							accessToken.getTokenSecret());
				} catch (JSONException e) {
					throw new RuntimeException(e);
				}

				HttpPost post = new HttpPost(
						ServerUrlHelper
								.getFromContext(TwitterSetupActivity.this)
								+ PATH);
				post.setEntity(new StringEntity(object.toString()));
				post.setHeader("Content-Type", "application/json");
				mHttpClient.execute(post, mResponseHandler);
				return true;

			} catch (TwitterException e) {

			} catch (IOException e) {

			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean success) {

			mProgressDialog.dismiss();

			if (success) {
				Toast.makeText(self,
						getString(R.string.phrase_twitter_authorize_success),
						Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			Toast.makeText(self,
					getString(R.string.error_twitter_unautohrized),
					Toast.LENGTH_LONG).show();
		}
	}

	private class DownloadSettingsTask
			extends
				AsyncTask<Void, Void, DownloadSettingsTask.Result> {

		private class Result {
			public final int statusCode;
			public final String authorizationUrl;

			private static final int SUCCESS = 0;
			private static final int FAILURE = 2;

			public Result(int statusCode, String authorizationUrl) {
				this.statusCode = statusCode;
				this.authorizationUrl = authorizationUrl;
			}
		}

		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(self, null,
					getString(R.string.phrase_connecting));
		}

		@Override
		protected Result doInBackground(Void... params) {

			try {
				HttpGet get = new HttpGet(
						ServerUrlHelper
								.getFromContext(TwitterSetupActivity.this)
								+ PATH);
				JSONObject json = new JSONObject(mHttpClient.execute(get,
						mResponseHandler));

				ConfigurationBuilder cb = new ConfigurationBuilder();
				cb.setDebugEnabled(true)
						.setOAuthConsumerKey(json.getString("ConsumerKey"))
						.setOAuthConsumerSecret(
								json.getString("ConsumerSecret"));

				TwitterFactory tf = new TwitterFactory(cb.build());
				twitter = tf.getInstance();
				requestToken = twitter.getOAuthRequestToken();
				return new Result(Result.SUCCESS,
						requestToken.getAuthorizationURL());
			} catch (IOException e) {
				return new Result(Result.FAILURE, null);
			} catch (JSONException e) {
				return new Result(Result.FAILURE, null);
			} catch (TwitterException e) {
				return new Result(Result.FAILURE, null);
			}
		}

		@Override
		protected void onPostExecute(Result result) {

			mProgressDialog.dismiss();

			if (result.statusCode == Result.SUCCESS) {
				mWebView.loadUrl(result.authorizationUrl);
				return;
			}

			Toast.makeText(self, getString(R.string.error_twitter_unreachable),
					Toast.LENGTH_LONG).show();
			finish();
		}
	}
}