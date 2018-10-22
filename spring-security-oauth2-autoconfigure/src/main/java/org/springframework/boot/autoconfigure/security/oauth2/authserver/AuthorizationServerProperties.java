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

package org.springframework.boot.autoconfigure.security.oauth2.authserver;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for OAuth2 Authorization server.
 *
 * @author Dave Syer
 * @since 1.3.0
 */
@ConfigurationProperties(prefix = "security.oauth2.authorization")
public class AuthorizationServerProperties {

	/**
	 * Spring Security access rule for the check token endpoint (e.g. a SpEL expression
	 * like "isAuthenticated()") . Default is empty, which is interpreted as "denyAll()"
	 * (no access).
	 */
	private String checkTokenAccess;

	/**
	 * Spring Security access rule for the token key endpoint (e.g. a SpEL expression like
	 * "isAuthenticated()"). Default is empty, which is interpreted as "denyAll()" (no
	 * access).
	 */
	private String tokenKeyAccess;

	/**
	 * Realm name for client authentication. If an unauthenticated request comes in to the
	 * token endpoint, it will respond with a challenge including this name.
	 */
	private String realm;

	/**
	 * Jwt-specific configuration properties
	 */
	private Jwt jwt = new Jwt();

	public String getCheckTokenAccess() {
		return this.checkTokenAccess;
	}

	public void setCheckTokenAccess(String checkTokenAccess) {
		this.checkTokenAccess = checkTokenAccess;
	}

	public String getTokenKeyAccess() {
		return this.tokenKeyAccess;
	}

	public void setTokenKeyAccess(String tokenKeyAccess) {
		this.tokenKeyAccess = tokenKeyAccess;
	}

	public String getRealm() {
		return this.realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public Jwt getJwt() {
		return jwt;
	}

	public void setJwt(Jwt jwt) {
		this.jwt = jwt;
	}

	/**
	 * Configuration properties for Authorization Server Jwt configuration
	 *
	 * @author Harold Li
	 * @author Josh Cummings
	 * @since 2.1.0
	 */
	public class Jwt {

		/**
		 * The signing key of the JWT token. Can either be a symmetric secret or
		 * PEM-encoded RSA private key.
		 */
		private String keyValue;

		/**
		 * The location of the key store.
		 */
		private String keyStore;

		/**
		 * The key store's password
		 */
		private String keyStorePassword;

		/**
		 * The alias of the key from the key store
		 */
		private String keyAlias;

		/**
		 * The password of the key from the key store
		 */
		private String keyPassword;

		public String getKeyValue() {
			return this.keyValue;
		}

		public void setKeyValue(String keyValue) {
			this.keyValue = keyValue;
		}

		public String getKeyStore() {
			return keyStore;
		}

		public void setKeyStore(String keyStore) {
			this.keyStore = keyStore;
		}

		public String getKeyStorePassword() {
			return keyStorePassword;
		}

		public void setKeyStorePassword(String keyStorePassword) {
			this.keyStorePassword = keyStorePassword;
		}

		public String getKeyAlias() {
			return keyAlias;
		}

		public void setKeyAlias(String keyAlias) {
			this.keyAlias = keyAlias;
		}

		public String getKeyPassword() {
			return keyPassword;
		}

		public void setKeyPassword(String keyPassword) {
			this.keyPassword = keyPassword;
		}
	}

}
