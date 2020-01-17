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

/**
 * This sample is intended to demonstrate that
 * {@link org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer}
 * only needs an {@link org.springframework.security.authentication.AuthenticationManager}
 * when the {@code password} grant type is configured.
 *
 * After you launch the app, you can retrieve a bearer token like this:
 *
 * <pre>
 * curl ifusespasswordgranttype:thenneedsauthenticationmanager@localhost:8080/oauth/token -dgrant_type=password -dusername=enduser -dpassword=password -dscope=any
 * </pre>
 *
 * The response should be similar to:
 *
 * <pre>
 *	{
 *		"access_token":"6b79fdbc-734c-4e33-b625-1930e1b74719"",
 *		"token_type":"bearer",
 *		"expires_in":599999999,
 *		"scope":"message:read",
 *		"jti":"58d86787-af16-421d-831d-13bd16306d6b"
 *	}
 * </pre>
 *
 * Try using this with a resource server sample for more fun!
 *
 * @author Josh Cummings
 */
@SpringBootApplication
public class SampleSecureOAuth2Application {

	public static void main(String[] args) {
		SpringApplication.run(SampleSecureOAuth2Application.class, args);
	}

}
