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

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OAuth2JwtKeyStoreConfiguration}.
 *
 * @author Harold Li
 * @since 2.0.1
 */
public class OAuth2JwtKeyStoreConfigurationTests {

	private ConfigurableApplicationContext context;

	private ConfigurableEnvironment environment = new StandardEnvironment();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void tokenStoreIsConfiguredWhenKeystoreIsProvided() throws Exception {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:jwt.jks",
				"security.oauth2.authorization.jwt.password=changeme",
				"security.oauth2.authorization.jwt.alias=jwt")
				.applyTo(this.environment);
		this.context = new SpringApplicationBuilder(OAuth2JwtKeyStoreConfiguration.class, AuthorizationServerConfiguration.class).environment(this.environment)
				.web(WebApplicationType.NONE).run();
		assertThat(this.context.getBeansOfType(TokenStore.class)).hasSize(1);
		assertThat(this.context.getBean(TokenStore.class)).isInstanceOf(JwtTokenStore.class);
	}

	@Test
	public void JwtAccessTokenConverterIsConfiguredWhenKeystoreIsProvided() throws Exception {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:jwt.jks",
				"security.oauth2.authorization.jwt.password=changeme",
				"security.oauth2.authorization.jwt.alias=jwt")
				.applyTo(this.environment);
		this.context = new SpringApplicationBuilder(OAuth2JwtKeyStoreConfiguration.class, AuthorizationServerConfiguration.class)
				.environment(this.environment)
				.web(WebApplicationType.NONE).run();
		assertThat(this.context.getBeansOfType(JwtAccessTokenConverter.class)).hasSize(1);
	}

	@Configuration
	@EnableWebSecurity
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	}

}