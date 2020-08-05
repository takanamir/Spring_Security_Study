package com.example;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

/**
 * ログイン画面のテスト.
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
	@Autowired
	private MockMvc mockMvc;

	/**
	 * systemユーザーでログイン処理を実行.
	 */
	@Test
	public void loginTestSystem() throws Exception {
		// ログイン処理を実行
		mockMvc.perform(post("https://sample.localhost.com:8443/login").with(csrf()).param("userId", "system")
				.param("password", "password").param("tenantId", "tenant")).andExpect(status().isFound()) // HTTPレスポンスが302かどうか
				.andExpect(authenticated().withUsername("system").withRoles("ADMIN", "GENERAL")) // 認証されたか
				.andExpect(redirectedUrl("/home")); // リダイレクト先
	}

	/**
	 * sample1ユーザーでログイン処理を実行.
	 */
	@Test
	public void loginTestSample1() throws Exception {
		// ログイン処理を実行
		mockMvc.perform(post("https://sample.localhost.com:8443/login").with(csrf()).param("userId", "sample1")
				.param("password", "password").param("tenantId", "tenant")).andExpect(status().isFound()) // HTTPレスポンスが302かどうか
				.andExpect(authenticated().withUsername("sample1").withRoles("GENERAL")) // 認証されたか
				.andExpect(redirectedUrl("/password/change")); // リダイレクト先
	}

	/**
	 * sample2ユーザーでログイン処理を実行.
	 */
	@Test
	public void loginTestSample2() throws Exception {
		// ログイン処理を実行
		mockMvc.perform(post("https://sample.localhost.com:8443/login").with(csrf()).param("userId", "sample2")
				.param("password", "password").param("tenantId", "tenant")).andExpect(status().isFound()) // HTTPレスポンスが302かどうか
				.andExpect(unauthenticated()) // 認証拒否されたか
				.andExpect(redirectedUrl("/login?error")); // リダイレクト先
	}
}