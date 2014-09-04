package com.baoyuan.weixin.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;

import com.baoyuan.weixin.exception.HttpException;

public final class HttpUtils {

	protected static final HttpClient httpClient;

	static {
		SSLContext sslContext = SSLContexts.createDefault();
		try {
			sslContext = SSLContexts.custom()
					.loadTrustMaterial(null, new TrustStrategy() {
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		httpClient = HttpClientBuilder.create().setSslcontext(sslContext)
				.build();
	}

	public static HttpClient getInstance(){
		return httpClient;
	}
	
	public static String get(String uri, List<NameValuePair> params)
			throws HttpException {
		String url = uri;
		if (params != null) {
			String param = StringUtils.join(params, "&");
			if (url.contains("?")) {
				url = url + "&" + param;
			} else {
				url = url + "?" + param;
			}
		}

		return get(url);
	}

	public static String get(String uri, Header... headers)
			throws HttpException {
		HttpGet request = new HttpGet(uri);
		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
		return execute(request);
	}

	public static String post(String uri, HttpEntity postBody,
			Header... headers) throws HttpException {
		HttpPost request = new HttpPost(uri);
		if (postBody != null) {
			request.setEntity(postBody);
		}

		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
		return execute(request);
	}

	public static String post(String uri, List<NameValuePair> params,
			String charset, Header... headers) throws HttpException {
		HttpPost request = new HttpPost(uri);
		if (params != null) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			parameters.addAll(params);
			try {
				HttpEntity entity = new UrlEncodedFormEntity(parameters,
						charset);
				request.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				throw new HttpException(e);
			}
		}

		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
		return execute(request);
	}

	public static String post(String uri) throws HttpException {
		return post(uri, (HttpEntity) null);
	}

	private static String execute(HttpUriRequest request) throws HttpException {
		try {
			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = IOUtils.toString(entity.getContent());
				return result;
			} else {
				throw new HttpException("No response entity.");
			}
		} catch (ClientProtocolException e) {
			throw new HttpException(e);
		} catch (IOException e) {
			throw new HttpException(e);
		}
	}
}
