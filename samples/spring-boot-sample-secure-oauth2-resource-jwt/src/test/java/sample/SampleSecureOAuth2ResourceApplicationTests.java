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
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Series of automated integration tests to verify proper behavior of JWT-encoded Bearer
 * Token-secured Resource Server
 *
 * @author Josh Cummings
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SampleSecureOAuth2ResourceApplicationTests {

	private static final String VALID_JWT = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImV4cCI6MjE0NDA4NjQ0MCwiYXV0aG9yaXRpZXMiOlsiUk9MRV9VU0VSIl0sInVzZXJfbmFtZSI6InRvbSIsImp0aSI6ImM4N2Q5NTNjLTZlZDAtNGRlMy1hZTJlLTMwZTcwOTYyNjExNyIsImNsaWVudF9pZCI6ImZvbyJ9.vOx3WIajVeaPelFuYttvSjvOSXw5POwzQiZPxQmH6eSQTVR_YCHHzd0vh2a00g3spZ0-S7fZfkiFuNF-QJogGS-GER-B8p4c6mMrazN0x-wytMVM6xZrQbner0Uqu_uuK1vQs-gm2-2BFpydQtq-ZYicss21RSJTLK7fuH5DzHQ";

	private static final String EXPIRED_JWT = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImV4cCI6MTQ0MDg2NDQwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwidXNlcl9uYW1lIjoidG9tIiwianRpIjoiYzg3ZDk1M2MtNmVkMC00ZGUzLWFlMmUtMzBlNzA5NjI2MTE3IiwiY2xpZW50X2lkIjoiZm9vIn0.T4aZBcheibokhIj5ugfbKSKbR83_SKm_ElDpJ1G24XFpGX6EayNofjGTfc59HR9GGnI3-Eo8Lo6zZRMQycvz8BWhX3L5zNcB9wwcK4m3fsEep9fhwz1VZxecqdRr6wLuUXSFGfty2xBsIJ84C2n6tS7_5y_2LVE1pvDmQRnbYhg";

	@Autowired
	MockMvc mvc;

	@Test
	public void homePageAvailable() throws Exception {
		this.mvc.perform(get("/").accept(MediaTypes.HAL_JSON)).andExpect(status().isOk());
	}

	@Test
	public void profileAvailable() throws Exception {
		this.mvc.perform(get("/profile").accept(MediaTypes.HAL_JSON)).andExpect(status().isOk());
	}

	@Test
	public void flightsWhenUsingValidJwtThenOk() throws Exception {
		this.mvc.perform(get("/flights").with(bearerToken(VALID_JWT))).andExpect(status().isOk());
		this.mvc.perform(get("/flights/1").with(bearerToken(VALID_JWT))).andExpect(status().isOk());
	}

	@Test
	public void flightsWhenUsingExpiredJwtThenUnauthorized() throws Exception {
		this.mvc.perform(get("/flights").with(bearerToken(EXPIRED_JWT))).andExpect(status().isUnauthorized());
		this.mvc.perform(get("/flights/1").with(bearerToken(EXPIRED_JWT))).andExpect(status().isUnauthorized());
	}

	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {

		private String token;

		public BearerTokenRequestPostProcessor(String token) {
			this.token = token;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader("Authorization", "Bearer " + this.token);
			return request;
		}

	}

	private static BearerTokenRequestPostProcessor bearerToken(String token) {
		return new BearerTokenRequestPostProcessor(token);
	}

}
