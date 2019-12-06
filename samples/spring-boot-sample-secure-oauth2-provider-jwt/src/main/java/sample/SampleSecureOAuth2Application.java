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
 *
 * <pre>
 * 	{
 * 		"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbnkiXSwiZXhwIjoxNTQ0MDkyMDY0LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiZTJmMDE3ODItNTczMC00OWI5LWI2Y2ItNTI1NjQ2NjVkMmYxIiwiY2xpZW50X2lkIjoiZmlyc3QtY2xpZW50In0.MaNc5R7ViGzX2d9ldCE-pbaaoRAULqyIbmURqY8_IZBRJCkEwGBlySI4UhMUK271Yd8KyE0MnSWQ6CDFZpryP_IsFsdICpklJQE7vLnAFqAS9TQupbEPpnDY6Ceb4bEHaYyyRYjsgZeyLyBP8E41VmuFNqydWBg0jqaqYu66YJw",
 * 		"token_type":"bearer",
 * 		"expires_in":43199,
 * 		"scope":"any"
 * 	}
 * </pre>
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
