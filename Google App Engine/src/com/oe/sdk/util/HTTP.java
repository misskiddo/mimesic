package com.oe.sdk.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HTTP {
	public enum HTTP_METHOD { POST,GET };

	public static String POST(final String url, final Map<String,String> params) throws HTTPException {
		return doRequest(url,params,HTTP_METHOD.POST);
	}
	public static String POST(final String url, final Map<String,String> params, final String proxy, final int proxyport) throws HTTPException {
		return doRequest(url,params,HTTP_METHOD.POST, proxy, proxyport);
	}
	public static String doRequest(final String url, final Map<String,String> params, HTTP_METHOD method) throws HTTPException {
		return doRequest(url, params, method, null, 0);
	}
	public static String doRequest(final String url, final Map<String,String> params, HTTP_METHOD method, final String proxy, final int proxyport) throws HTTPException {
		String response = null;
		final boolean hasparams = (params != null) && (params.size()>0);
		try {
			StringBuilder data = null;
			if (hasparams) {
				data = new StringBuilder();
				data.append("_d=x");
				for (final Map.Entry<String,String> entry : params.entrySet()) {
					data.append('&').append(URLEncoder.encode(entry.getKey(),"UTF-8")).append('=').append(URLEncoder.encode(entry.getValue(),"UTF-8"));
				}
			} else {
				method = HTTP_METHOD.GET;
			}
			HttpURLConnection con = null;
			URL urlnew;
			String surl;
			if (method==HTTP_METHOD.POST) {
				surl = url;
			} else {
				surl = hasparams ? (url+"?"+data.toString()) : url;
			}
			urlnew = new URL(surl);
			if (proxy == null) {
				con = (HttpURLConnection)urlnew.openConnection();
			}
			else {
				Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy, proxyport));
				con = (HttpURLConnection)urlnew.openConnection(p); 
			}
					
			con.setRequestMethod(method==HTTP_METHOD.POST?"POST":"GET");

			con.setDoInput(true);
			con.setUseCaches(false);
			con.setDoOutput(true);
			if (method==HTTP_METHOD.POST) {
				con.setRequestProperty("Content-type","application/x-www-form-urlencoded");
				con.setRequestProperty("Content-length",""+data.length());
				final OutputStream os = con.getOutputStream();
				os.write(data.toString().getBytes("UTF-8"));
				os.flush();
				os.close();
			}

			final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			final StringBuilder sb = new StringBuilder();
			final char[] cbuf = new char[512];
			int br;
			do {
				br = in.read(cbuf);
				if (br > 0) {
					sb.append(cbuf,0,br);
				}
			} while (br > -1);
			in.close();
			response = sb.toString();
		} catch (final Exception e) {
			throw new HTTPException(e);
		}
		return response;
	}

	public static class HTTPException extends Exception {
		private static final long serialVersionUID = 2856591348833647661L;
		public HTTPException(final Exception cause) {
			super(cause);
		}
	}
}