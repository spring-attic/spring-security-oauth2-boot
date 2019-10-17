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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Series of automated integration tests to verify proper behavior of auto-configured,
 * OAuth2-secured system
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @author Rob Winch
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SampleSecureOAuth2ResourceApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void homePageAvailable() throws Exception {
		this.mvc.perform(get("/").accept(MediaTypes.HAL_JSON)).andExpect(status().isOk());
	}

	@Test
	public void flightsSecuredByDefault() throws Exception {
		this.mvc.perform(get("/flights").accept(MediaTypes.HAL_JSON)).andExpect(status().isUnauthorized());
		this.mvc.perform(get("/flights/1").accept(MediaTypes.HAL_JSON)).andExpect(status().isUnauthorized());
	}

	@Test
	public void profileAvailable() throws Exception {
		this.mvc.perform(get("/profile").accept(MediaTypes.HAL_JSON)).andExpect(status().isOk());
	}

}
