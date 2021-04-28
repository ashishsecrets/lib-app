package com.ucsf.config;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucsf.common.ErrorCodes;
import com.ucsf.payload.response.ErrorResponse;

import org.json.JSONObject;

@Component
public class UcsfAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		System.out.println(" ================== YOU  _+++++++++++++++++++=");
		ObjectMapper mapper = new ObjectMapper();
		JSONObject responsejson = new JSONObject();
		responsejson.put("error", new ErrorResponse(ErrorCodes.INVALID_CREDENTIALS.code(), authException.getMessage()));
		mapper.writeValue(response.getWriter(), responsejson.toMap());
		response.setStatus(401);
		// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
	}
}
