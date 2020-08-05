package com.example;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.example.controller.HomeController;

/**
 * ホーム画面のテスト.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class HomeTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private HomeController homeController;

	/**
	 * systemユーザーでログインして/homeを表示.
	 */
	@Test
	@WithUserDetails(value = "system", userDetailsServiceBeanName = "UserDetailsServiceImpl")
	public void homeTestSystem() throws Exception {
		// home画面を表示
		mockMvc.perform(get("/home")).andExpect(content().string(containsString("ユーザーID：<span>system</span>"))) // ホーム画面の値をチェック
				.andExpect(header().exists("Content-Security-Policy")) // CSPが存在するかチェック
				.andExpect(header().string("Content-Security-Policy", "default-src 'self'")); // CSPの中身をチェック
	}

	/**
	 * sample2ユーザーでログインして/homeを表示.
	 */
	@Test
	@WithUserDetails(value = "sample2", userDetailsServiceBeanName = "UserDetailsServiceImpl")
	public void homeTestSample() throws Exception {
		// home画面を表示
		mockMvc.perform(get("/home")).andExpect(content().string(containsString("ユーザーID：<span>sample2</span>"))); // ホーム画面の値をチェック
	}

	/**
	 * systemユーザーでログインして/home2を表示.
	 */
	@Test
	@WithUserDetails(value = "system", userDetailsServiceBeanName = "UserDetailsServiceImpl")
	public void home2TestSystem() throws Exception {
		Model model = null;
		Principal principal = null;

		// アクセスできる(メソッドの中で例外発生)
		Assertions.assertThrows(NullPointerException.class, () -> {
			// home2にアクセス
			homeController.getHome2(model, principal);
		});
	}

	/**
	 * sample2ユーザーでログインして/home2を表示.
	 */
	@Test
	@WithUserDetails(value = "sample2", userDetailsServiceBeanName = "UserDetailsServiceImpl")
	public void home2TestSample() throws Exception {
		Model model = null;
		Principal principal = null;

		// アクセスが拒否される
		Assertions.assertThrows(AccessDeniedException.class, () -> {
			// home2にアクセス
			homeController.getHome2(model, principal);
		});
	}

	/**
	 * systemユーザーでログアウト.
	 */
	@Test
	@WithUserDetails(value = "system", userDetailsServiceBeanName = "UserDetailsServiceImpl")
	public void logoutTestSystem() throws Exception {
		// ログアウト処理を実行
		mockMvc.perform(logout("/logout")).andExpect(status().isFound()).andExpect(redirectedUrl("/login"));
	}
}