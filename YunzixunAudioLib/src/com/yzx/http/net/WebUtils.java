package com.yzx.http.net;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.text.TextUtils;

import com.yzx.preference.UserData;
import com.yzx.tools.CustomLog;
import com.yzx.tools.Util;
import com.yzx.tools.SignTools;


public abstract class WebUtils {
	public static final String DEFAULT_CHARSET = "UTF-8";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";

	private WebUtils() {

	}


	public static String doPost(Context context, String url,
			Map<String, String> params, int connectTimeout, int readTimeout,
			boolean responseError) throws IOException {
		return doPost(context, url, params, DEFAULT_CHARSET, connectTimeout,
				readTimeout, responseError);
	}

	
	public static String doPost(Context context, String url,
			Map<String, String> params, String charset, int connectTimeout,
			int readTimeout, boolean responseError) throws IOException {
		String ctype = "application/x-www-form-urlencoded;charset=" + charset;
		String query = buildQuery(params, charset);

		byte[] content = {};
		if (query != null) {
			content = query.getBytes(charset);
		}
		return doPost(context, url, ctype, content, connectTimeout,
				readTimeout, responseError);
	}

	
	public static String doPost(Context context, String url, String ctype,
			byte[] content, int connectTimeout, int readTimeout,
			boolean responseError) throws IOException {
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				conn = getConnection(context, new URL(url), METHOD_POST, ctype);
				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {
				throw e;
			}
			try {
				out = conn.getOutputStream();
				out.write(content);
				rsp = getResponseAsString(conn, responseError);
			} catch (IOException e) {
				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	
	public static String doPost(Context context, String url,
			Map<String, String> params, Map<String, FileItem> fileParams,
			int connectTimeout, int readTimeout) throws IOException {
		if (fileParams == null || fileParams.isEmpty()) {
			return doPost(context, url, params, DEFAULT_CHARSET,
					connectTimeout, readTimeout, false);
		} else {
			return doPost(context, url, params, fileParams, DEFAULT_CHARSET,
					connectTimeout, readTimeout);
		}
	}

	
	public static String doPost(Context context, String url,
			Map<String, String> params, Map<String, FileItem> fileParams,
			String charset, int connectTimeout, int readTimeout)
			throws IOException {
		String boundary = System.currentTimeMillis() + ""; 
		HttpURLConnection conn = null;
		OutputStream out = null;
		String rsp = null;
		try {
			try {
				String ctype = "multipart/form-data;charset=" + charset
						+ ";boundary=" + boundary;
				conn = getConnection(context, new URL(url), METHOD_POST, ctype);

				conn.setConnectTimeout(connectTimeout);
				conn.setReadTimeout(readTimeout);
			} catch (IOException e) {

				throw e;
			}

			try {
				out = conn.getOutputStream();

				byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n")
						.getBytes(charset);

				
				Set<Entry<String, String>> textEntrySet = params.entrySet();
				for (Entry<String, String> textEntry : textEntrySet) {
					byte[] textBytes = getTextEntry(textEntry.getKey(),
							textEntry.getValue(), charset);
					out.write(entryBoundaryBytes);
					out.write(textBytes);
				}
				Set<Entry<String, FileItem>> fileEntrySet = fileParams
						.entrySet();
				for (Entry<String, FileItem> fileEntry : fileEntrySet) {
					FileItem fileItem = fileEntry.getValue();
					byte[] fileBytes = getFileEntry(fileEntry.getKey(),
							fileItem.getFileName(), fileItem.getMimeType(),
							charset);
					out.write(entryBoundaryBytes);
					out.write(fileBytes);
					out.write(fileItem.getContent());
				}
				
				byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n")
						.getBytes(charset);
				out.write(endBoundaryBytes);
				rsp = getResponseAsString(conn, false);
			} catch (IOException e) {

				throw e;
			}

		} finally {
			if (out != null) {
				out.close();
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static byte[] getTextEntry(String fieldName, String fieldValue,
			String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
		entry.append(fieldValue);
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName,
			String mimeType, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\";filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type:");
		entry.append(mimeType);
		entry.append("\r\n\r\n");
		return entry.toString().getBytes(charset);
	}

	
	public static String doGet(Context context, String url,
			Map<String, String> params) throws IOException {
		return doGet(context, url, params, DEFAULT_CHARSET);
	}

	
	public static String doGet(Context context, String url,
			Map<String, String> params, String charset) throws IOException {
		HttpURLConnection conn = null;
		String rsp = null;
		 //CustomLog.v("doGet1:"+url);
		try {
			String ctype = "application/x-www-form-urlencoded;charset="
					+ charset;
			String query = buildQuery(params, charset);
			String sn = params.get("sn");
			try {
				conn = getConnection(context, buildGetUrl(url,query,sn),
						METHOD_GET, ctype); 
				//CustomLog.v("doGet2:"+url);
			} catch (IOException e) {
				 //CustomLog.v("###IOException###");
				throw e;
			}

			try {
				rsp = getResponseAsString(conn, true);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}

		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		return rsp;
	}

	private static HttpURLConnection getConnection(Context context, URL url, String method, String ctype)
			throws IOException {
		
		HttpURLConnection conn = null;
		//CustomLog.v("getConnection---begin");
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		conn.setDoInput(true);
		//conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
		//conn.setRequestProperty("Content-Type", ctype);
		conn.setRequestProperty("Accept-Charset", "utf-8");
		conn.setRequestProperty("Connection", "close");
		conn.setRequestProperty("SecurityFlag", Util.setSecurityFlag());
		
		//CustomLog.v("getConnection---over");
		return conn;
	}

	public static URL buildGetUrl(String url, Map<String, String> params,
			String charset) throws IOException {
		String queryString = buildQuery(params, charset);
		String sn = params.get("sn");
		return buildGetUrl(url, queryString, sn);
	}

	private static URL buildGetUrl(String strUrl, String query, String sn)
			throws IOException {
		URL url=new URL(strUrl);
		String sign;

		//this need to sqlit strurl, get sn value
		//CustomLog.v("strUrl:"+strUrl);
		//sn = Util.getUrlOptionValue(strUrl, "sn=");
		if(sn == null){
			sn = SignTools.getSn();
		}
		//CustomLog.v("sn:"+sn);
		sign = sn+InterfaceConst.getMd5Key;
		//CustomLog.v("sign:"+sign);
		if (TextUtils.isEmpty(query)) {
			 sign = SignTools.getSign2(sign,SignTools.Encryption.MD5);
			return new URL(strUrl+"&sign="+sign);
		}

		if (TextUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}
		
		sign = SignTools.getSign2(sign,SignTools.Encryption.MD5);
	    //CustomLog.v("请求地址buildGetUrl:"+strUrl+"&sign="+sign);
		return new URL(strUrl+"&sign="+sign);
	}

	public static String buildQuery(Map<String, String> params, String charset)
			throws UnsupportedEncodingException {
		if (params == null || params.isEmpty()) {
		}
		if (TextUtils.isEmpty(charset)) {
			charset = DEFAULT_CHARSET;
		}

		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=")
						.append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

	/**
	 *
	 * 
	 * @param conn
	 * @param responseError
	 * @return
	 * @throws IOException
	 */
	protected static String getResponseAsString(HttpURLConnection conn,
			boolean responseError) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		String header = conn.getHeaderField("Content-Encoding");
		boolean isGzip = false;
		if (header != null && header.toLowerCase().contains("gzip")) {
			isGzip = true;
		}
		//CustomLog.v("应答消息:"+conn.getResponseMessage());
		InputStream es = conn.getErrorStream();
		if (es == null) {
			InputStream input = conn.getInputStream();
			if (isGzip) {
				input = new GZIPInputStream(input);
			}
			return getStreamAsString(input, charset);
		} else {
			if (isGzip) {
				es = new GZIPInputStream(es);
			}
			String msg = getStreamAsString(es, charset);
			if (TextUtils.isEmpty(msg)) {
				throw new IOException(conn.getResponseCode() + ":"
						+ conn.getResponseMessage());
			} else if (responseError) {
				return msg;
			} else {
				throw new IOException(msg);
			}
		}
	}

	private static String getStreamAsString(InputStream stream, String charset)
			throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream, charset));
			StringWriter writer = new StringWriter();

			char[] chars = new char[256];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!TextUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!TextUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

	/**
	 *  
	 * @param value
	 *            */
	public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}

	/**
	 * 
	 * 
	 * @param value
	 *            
	 */
	public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}

	/**
	 *  
	 * @param value
	 * * @param charset
	 * * @return */
	public static String decode(String value, String charset) {
		String result = null;
		if (!TextUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 *  
	 * @param value
	 * @param charset
	 *  @return
	 */
	public static String encode(String value, String charset) {
		String result = null;
		if (!TextUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

}
