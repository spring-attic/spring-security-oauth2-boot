/*
 * Copyright 2012-2019 the original author or authors.
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

package sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Series of automated integration tests to verify proper behavior of auto-configured,
 * OAuth2-secured system
 *
 * @author Dave Syer
 * @author Rob Winch
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SampleSecureOAuth2ActuatorApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void homePageSecuredByDefault() throws Exception {
		this.mvc.perform(get("/")).andExpect(status().isUnauthorized())
				.andExpect(header().string("WWW-Authenticate", containsString("Bearer")));
	}

	@Test
	public void healthSecured() throws Exception {
		this.mvc.perform(get("/actuator/health")).andExpect(status().isUnauthorized());
	}

	@Test
	public void healthWithBasicAuthorization() throws Exception {
		MockHttpServletRequestBuilder request = get("/actuator/health").with(userCredentials());
		this.mvc.perform(request).andExpect(status().isOk());
	}

	@Test
	public void envSecured() throws Exception {
		this.mvc.perform(get("/actuator/env")).andExpect(status().isUnauthorized());
	}

	@Test
	public void envWithBasicAuthorization() throws Exception {
		MockHttpServletRequestBuilder request = get("/actuator/env").with(userCredentials());
		this.mvc.perform(request).andExpect(status().isOk());
	}

	private RequestPostProcessor userCredentials() {
		return httpBasic("user", "password");
	}

}
