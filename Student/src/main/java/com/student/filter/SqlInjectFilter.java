package com.student.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Servlet Filter implementation class SqlInjectFilter
 */
@WebFilter("/SqlInjectFilter")
public class SqlInjectFilter implements Filter {

	public String sqlbody = "";

	public SqlInjectFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String sql = "";
		sqlbody = "";
		MultiReadHttpServletRequest httprequest = new MultiReadHttpServletRequest((HttpServletRequest) request);
		// Get all request parameter names
		Enumeration<?> params = httprequest.getParameterNames();

		while (params.hasMoreElements()) {
			// Get the parameter name
			String name = params.nextElement().toString();
			// Get the corresponding value of the parameter
			String[] value = null;

			value = httprequest.getParameterValues(name);
			for (int i = 0; i < value.length; i++) {
				sql = sql + value[i];
				if (!value[i].equals("")) {
					sql += " ";
				}
			}

		}

		String reqbody = getBody(httprequest);

		if (!reqbody.equals("") && !reqbody.contains("Content-Disposition: form-data;")) {
			processJson(reqbody);
		}

		sql += sqlbody;
		sql = sql.replace('\n', ' ');
		if (sql.equals("")) {
			chain.doFilter(httprequest, response);
		} else {
			if (!SqlSafeUtil.isSqlInjectionSafe(sql)) {
				abortWithUnauthorized(response);
			} else {
				chain.doFilter(httprequest, response);
			}
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}
	
	private void abortWithUnauthorized(ServletResponse response) {
		((HttpServletResponse) response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		PrintWriter out;
		try {
			out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print("{\"message\": \"Please use a valid character\"}");
			out.flush();
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public static String getBody(MultiReadHttpServletRequest request) throws IOException {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}

	public void processJson(String jsonStr) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode node = objectMapper.readTree(jsonStr);
			processNode(node);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void processNode(JsonNode n) {
		if (n.isContainerNode()) {
			processJsonContainer(n.iterator());
		} else if (n.isNumber()) {
			sqlbody += String.valueOf(n.asDouble()) + " ";
		} else if (n.isBoolean()) {
			sqlbody += String.valueOf(n.asBoolean()) + " ";
		} else if (n.isTextual()) {
			byte ptext[] = n.asText().getBytes();
			String value;
			value = new String(ptext, StandardCharsets.US_ASCII);
			// System.out.println("data: "+ n.asText().toString());
			sqlbody += value + " ";
		}
	}

	private void processJsonContainer(Iterator<JsonNode> iterator) {
		while (iterator.hasNext()) {
			processNode(iterator.next());
		}
		
		
	}
}
