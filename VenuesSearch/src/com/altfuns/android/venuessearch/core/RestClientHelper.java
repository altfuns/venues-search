package com.altfuns.android.venuessearch.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * 
 * RestClientHelper se encarga de construir las peticiones HTTP.
 * 
 * Created by @author Luis Aguilar on Jun 5, 2012
 * 
 * Copyright (c) 2012 SuiGeneris. All rights reserved.
 */
public class RestClientHelper {

	public enum RequestMethod {
		GET, POST, PUT, DELETE
	}

	private ArrayList<NameValuePair> params;

	private ArrayList<NameValuePair> headers;

	private String url;

	private int responseCode;

	private String message;

	private String response;

	private HttpEntity entity;

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setEntity(HttpEntity entity) {
		this.entity = entity;
	}

	public RestClientHelper(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public synchronized void execute(RequestMethod method) throws IOException {

		switch (method) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			executeRequest(request, url);
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			} else if (entity != null) {
				request.setEntity(entity);
			}

			executeRequest(request, url);
			break;
		}
		default:
			break;
		}
	}

	private int getRequestTimeOut(HttpUriRequest request) {
		int timeOut = 0;
		double requestSize = 0;

		// Get the content length of the request's entity
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest) request;
			HttpEntity entity = entityEnclosingRequest.getEntity();
			if (entity != null) {
				requestSize = entity.getContentLength() / 1024f;
			}
		}

		if (requestSize < 0.5) {
			timeOut = 40000;
		} else if (requestSize > 0.5 && requestSize <= 2.0) {
			timeOut = 50000;
		} else if (requestSize > 2.0 && requestSize <= 5.0) {
			timeOut = 60000;
		} else if (requestSize > 5.0 && requestSize <= 10.0) {
			timeOut = 70000;
		} else if (requestSize > 10.0 && requestSize <= 20.0) {
			timeOut = 80000;
		} else if (requestSize > 20.0 && requestSize <= 30.0) {
			timeOut = 90000;
		} else if (requestSize > 30.0 && requestSize <= 60.0) {
			timeOut = 100000;
		} else if (requestSize > 60.0 && requestSize <= 100.0) {
			timeOut = 140000;
		} else {
			timeOut = 200000;
		}

		return timeOut;
	}

	private void executeRequest(HttpUriRequest request, String url)
			throws IOException {
		// User the ssl client when the url is a http request.
		HttpClient client = new DefaultHttpClient();

		int timeOut = getRequestTimeOut(request);

		client.getParams().setIntParameter("http.socket.timeout", timeOut);

		HttpResponse httpResponse;

		try {
			httpResponse = client.execute(request);
			responseCode = httpResponse.getStatusLine().getStatusCode();
			message = httpResponse.getStatusLine().getReasonPhrase();

			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				response = convertStreamToString(instream);

				// Closing the input stream will trigger connection release
				instream.close();
			}

		} catch (ClientProtocolException e) {
			LogIt.e(this, e, e.getMessage());
			throw e;
		} catch (SocketException e) {
			LogIt.e(this, e, e.getMessage());
			throw e;
		} catch (IOException e) {
			LogIt.e(this, e, e.getMessage());
			throw e;
		} finally {
			client.getConnectionManager().shutdown();
		}
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Get the response from the URL using the GET HTTP method.
	 * 
	 * @param url
	 *            URL of the service end-point.
	 * @return
	 * @throws ExigoSyncException
	 */
	public static String get(String url) throws IOException {
		String result = null;

		RestClientHelper client = new RestClientHelper(url);

		try {
			client.execute(RequestMethod.GET);

			switch (client.getResponseCode()) {
			case HttpStatus.SC_OK:
				result = client.getResponse();
				break;
			}
		} catch (IOException e) {
			LogIt.e(RestClientHelper.class, e, e.getMessage());
			throw e;
		}

		return result;
	}
}
