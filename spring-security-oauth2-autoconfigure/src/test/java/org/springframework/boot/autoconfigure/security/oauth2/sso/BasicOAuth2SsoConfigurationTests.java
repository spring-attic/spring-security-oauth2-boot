/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.security.oauth2.sso;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link OAuth2AutoConfiguration} with basic configuration.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
@TestPropertySource(properties = { "security.oauth2.client.clientId=client",
		"security.oauth2.client.clientSecret=secret",
		"security.oauth2.client.userAuthorizationUri=http://example.com/oauth/authorize",
		"security.oauth2.client.accessTokenUri=http://example.com/oauth/token",
		"security.oauth2.resource.jwt.keyValue=SSSSHHH" })
public class BasicOAuth2SsoConfigurationTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	@Qualifier("springSecurityFilterChain")
	private Filter filter;

	private MockMvc mvc;

	@Before
	public void init() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilters(this.filter).build();
	}

	@Test
	public void homePageIsSecure() throws Exception {
		this.mvc.perform(get("/")).andExpect(status().isFound())
				.andExpect(header().string("location", "http://localhost/login"));
	}

	@Test
	public void homePageSends401ToXhr() throws Exception {
		this.mvc.perform(get("/").header("X-Requested-With", "XMLHttpRequest"))
				.andExpect(status().isUnauthorized());
	}

	@Configuration
	@Import(OAuth2AutoConfiguration.class)
	@EnableOAuth2Sso
	@MinimalSecureWebConfiguration
	protected static class TestConfiguration {

	}

}
