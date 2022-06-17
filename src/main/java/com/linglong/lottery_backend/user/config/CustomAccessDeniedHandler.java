package com.linglong.lottery_backend.user.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Description
 *
 * @author yixun.xing
 * @since 18 三月 2019
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(final HttpServletRequest request, final HttpServletResponse response, final AccessDeniedException ex) throws IOException, ServletException {
		response.getOutputStream().print("Error Message Goes Here");
		response.setStatus(403);
		// response.sendRedirect("/my-error-page");
	}

}
