/*
 * Copyright 2012-2017 the original author or authors.
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

package org.springframework.boot.autoconfigure.security.oauth2.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OAuth2 Single Sign On (SSO).
 *
 * @author Dave Syer
 * @since 1.3.0
 */
@ConfigurationProperties(prefix = "security.oauth2.sso")
public class OAuth2SsoProperties {

	public static final String DEFAULT_LOGIN_PATH = "/login";

	/**
	 * Path to the login page, i.e. the one that triggers the redirect to the OAuth2
	 * Authorization Server.
	 */
	private String loginPath = DEFAULT_LOGIN_PATH;

	public String getLoginPath() {
		return this.loginPath;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

}
