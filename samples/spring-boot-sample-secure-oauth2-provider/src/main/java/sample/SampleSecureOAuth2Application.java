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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * After you launch the app, you can retrieve a bearer token like this:
 *
 * <pre>
 * curl first-client:noonewilleverguess@localhost:8080/oauth/token -dgrant_type=client_credentials -dscope=any
 * </pre>
 *
 * The response should be similar to:
 * <code>{"access_token":"533de99b-5a0f-4175-8afd-1a64feb952d5","token_type":"bearer","expires_in":43199,"scope":"any"}</code>
 *
 * Try using this with a resource server sample for more fun!
 *
 * @author Josh Cummings
 */
@EnableAuthorizationServer
@SpringBootApplication
public class SampleSecureOAuth2Application {

	public static void main(String[] args) {
		SpringApplication.run(SampleSecureOAuth2Application.class, args);
	}

}
