/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.security.oauth2.authserver.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

/**
 * Configuration properties for OAuth2 Authorization server with JWT keystore.
 * @author Harold Li
 * @since 2.0.1
 */
@ConfigurationProperties(prefix = "security.oauth2.authorization.jwt")
public class AuthorizationServerJwtProperties {

	/**
	 * The {@link Resource} location of the keystore.
	 */
	private String keyStore;

	/**
	 * The password of the JWT keystore.
	 */
	private String password;

	/**
	 * The alias of the JWT keystore.
	 */
	private String alias;

	public String getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
