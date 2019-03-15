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

package org.springframework.boot.autoconfigure.security.oauth2.authserver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link AuthorizationServerTokenServicesConfiguration}.
 *
 * @author Harold Li
 * @author Josh Cummings
 * @since 2.1.0
 */
public class AuthorizationServerTokenServicesConfigurationTests {

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
	public void configureWhenPrivateKeyIsProvidedThenExposesJwtAccessTokenConverter()
			throws Exception {
		Path privateKeyPath = new ClassPathResource("key.private", this.getClass())
				.getFile().toPath();
		String privateKey = Files.readAllLines(privateKeyPath).stream()
				.collect(Collectors.joining("\n"));

		TestPropertyValues.of("security.oauth2.authorization.jwt.key-value=" + privateKey)
				.applyTo(this.environment);
		this.context = new SpringApplicationBuilder(
				AuthorizationServerConfiguration.class).environment(this.environment)
						.web(WebApplicationType.NONE).run();

		JwtAccessTokenConverter converter = this.context
				.getBean(JwtAccessTokenConverter.class);
		assertThat(converter.isPublic()).isTrue();
	}

	@Test
	public void configureWhenKeyStoreIsProvidedThenExposesJwtTokenStore() {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:"
				+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme",
				"security.oauth2.authorization.jwt.key-alias=jwt")
				.applyTo(this.environment);
		this.context = new SpringApplicationBuilder(
				AuthorizationServerConfiguration.class).environment(this.environment)
						.web(WebApplicationType.NONE).run();
		assertThat(this.context.getBeansOfType(TokenStore.class)).hasSize(1);
		assertThat(this.context.getBean(TokenStore.class))
				.isInstanceOf(JwtTokenStore.class);
	}

	@Test
	public void configureWhenKeyStoreIsProvidedThenExposesJwtAccessTokenConverter() {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:"
				+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme",
				"security.oauth2.authorization.jwt.key-alias=jwt")
				.applyTo(this.environment);
		this.context = new SpringApplicationBuilder(
				AuthorizationServerConfiguration.class).environment(this.environment)
						.web(WebApplicationType.NONE).run();
		assertThat(this.context.getBeansOfType(JwtAccessTokenConverter.class)).hasSize(1);
	}

	@Test
	public void configureWhenKeyStoreIsProvidedWithKeyPasswordThenExposesJwtAccessTokenConverter() {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:"
				+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keyhaspassword.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme",
				"security.oauth2.authorization.jwt.key-alias=jwt",
				"security.oauth2.authorization.jwt.key-password=password")
				.applyTo(this.environment);
		this.context = new SpringApplicationBuilder(
				AuthorizationServerConfiguration.class).environment(this.environment)
						.web(WebApplicationType.NONE).run();
		assertThat(this.context.getBeansOfType(JwtAccessTokenConverter.class)).hasSize(1);
	}

	@Test
	public void configureWhenKeyStoreIsProvidedButNoAliasThenThrowsException() {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:"
				+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme")
				.applyTo(this.environment);

		assertThatCode(
				() -> new SpringApplicationBuilder(AuthorizationServerConfiguration.class)
						.environment(this.environment).web(WebApplicationType.NONE).run())
								.isInstanceOf(UnsatisfiedDependencyException.class);
	}

	@Test
	public void configureWhenKeyStoreIsProvidedButNoPasswordThenThrowsException() {
		TestPropertyValues.of("security.oauth2.authorization.jwt.key-store=classpath:"
				+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-alias=jwt")
				.applyTo(this.environment);

		assertThatCode(
				() -> new SpringApplicationBuilder(AuthorizationServerConfiguration.class)
						.environment(this.environment).web(WebApplicationType.NONE).run())
								.isInstanceOf(UnsatisfiedDependencyException.class);
	}

	@Configuration
	@Import({ AuthorizationServerTokenServicesConfiguration.class })
	@EnableConfigurationProperties(AuthorizationServerProperties.class)
	protected static class AuthorizationServerConfiguration {

	}

}