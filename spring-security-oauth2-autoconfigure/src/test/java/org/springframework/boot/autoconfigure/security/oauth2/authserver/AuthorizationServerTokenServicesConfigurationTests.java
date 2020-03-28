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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AuthorizationServerTokenServicesConfiguration}.
 *
 * @author Harold Li
 * @author Josh Cummings
 * @author Vladimir Tsanev
 * @since 2.1.0
 */
public class AuthorizationServerTokenServicesConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(AuthorizationServerConfiguration.class));

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void configureWhenPrivateKeyIsProvidedThenExposesJwtAccessTokenConverter() throws Exception {
		Path privateKeyPath = new ClassPathResource("key.private", this.getClass()).getFile().toPath();
		String privateKey = Files.readAllLines(privateKeyPath).stream().collect(Collectors.joining("\n"));

		this.contextRunner.withPropertyValues("security.oauth2.authorization.jwt.key-value=" + privateKey)
				.run(context -> {
					assertThat(context).getBean(JwtAccessTokenConverter.class)
							.satisfies(JwtAccessTokenConverter::isPublic);
				});
	}

	@Test
	public void configureWhenKeyStoreIsProvidedThenExposesJwtTokenStore() {
		this.contextRunner.withPropertyValues(
				"security.oauth2.authorization.jwt.key-store=classpath:"
						+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme",
				"security.oauth2.authorization.jwt.key-alias=jwt").run(context -> {
					assertThat(context.getBeansOfType(TokenStore.class)).hasSize(1);
					assertThat(context.getBean(TokenStore.class)).isInstanceOf(JwtTokenStore.class);
				});
	}

	@Test
	public void configureWhenKeyStoreIsProvidedThenExposesJwtAccessTokenConverter() {
		this.contextRunner.withPropertyValues(
				"security.oauth2.authorization.jwt.key-store=classpath:"
						+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme",
				"security.oauth2.authorization.jwt.key-alias=jwt").run(context -> {
					assertThat(context.getBeansOfType(JwtAccessTokenConverter.class)).hasSize(1);
				});
	}

	@Test
	public void configureWhenKeyStoreIsProvidedWithKeyPasswordThenExposesJwtAccessTokenConverter() {
		this.contextRunner.withPropertyValues(
				"security.oauth2.authorization.jwt.key-store=classpath:"
						+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keyhaspassword.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme",
				"security.oauth2.authorization.jwt.key-alias=jwt",
				"security.oauth2.authorization.jwt.key-password=password").run(context -> {
					assertThat(context.getBeansOfType(JwtAccessTokenConverter.class)).hasSize(1);
				});
	}

	@Test
	public void configureWhenKeyStoreIsProvidedButNoAliasThenThrowsException() {
		this.contextRunner.withPropertyValues(
				"security.oauth2.authorization.jwt.key-store=classpath:"
						+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
				"security.oauth2.authorization.jwt.key-store-password=changeme").run(context -> {
					assertThat(context).getFailure().isInstanceOf(UnsatisfiedDependencyException.class);
				});
	}

	@Test
	public void configureWhenKeyStoreIsProvidedButNoPasswordThenThrowsException() {
		this.contextRunner
				.withPropertyValues(
						"security.oauth2.authorization.jwt.key-store=classpath:"
								+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keystore.jks",
						"security.oauth2.authorization.jwt.key-alias=jwt")
				.run(context -> assertThat(context).getFailure().isInstanceOf(UnsatisfiedDependencyException.class));
	}

	@Test
	public void configureWhenPrivateKeyIsProvidedWithCustomJwtAccessTokenConverterThenDefaultBackoff()
			throws Exception {
		Path privateKeyPath = new ClassPathResource("key.private", this.getClass()).getFile().toPath();
		String privateKey = Files.readAllLines(privateKeyPath).stream().collect(Collectors.joining("\n"));

		this.contextRunner.withUserConfiguration(JwtAccessTokenConverterConfiguration.class)
				.withPropertyValues("security.oauth2.authorization.jwt.key-value=" + privateKey).run(context -> {
					JwtAccessTokenConverter converter = context.getBean(JwtAccessTokenConverter.class);
					assertThat(converter.getAccessTokenConverter()).isInstanceOf(CustomAccessTokenConverter.class);
				});
	}

	@Test
	public void configureWhenKeyStoreIsProvidedWithKeyPasswordAndCustomJwtAccessTokenConverterThenDefaultBackoff() {
		this.contextRunner.withUserConfiguration(JwtAccessTokenConverterConfiguration.class)
				.withPropertyValues("security.oauth2.authorization.jwt.key-store=classpath:"
						+ "org/springframework/boot/autoconfigure/security/oauth2/authserver/keyhaspassword.jks",
						"security.oauth2.authorization.jwt.key-store-password=changeme",
						"security.oauth2.authorization.jwt.key-alias=jwt",
						"security.oauth2.authorization.jwt.key-password=password")
				.run(context -> {
					JwtAccessTokenConverter converter = context.getBean(JwtAccessTokenConverter.class);
					assertThat(converter.getAccessTokenConverter()).isInstanceOf(CustomAccessTokenConverter.class);
				});
	}

	@Configuration
	@Import({ AuthorizationServerTokenServicesConfiguration.class })
	@EnableConfigurationProperties(AuthorizationServerProperties.class)
	protected static class AuthorizationServerConfiguration {

	}

	@Configuration
	protected static class JwtAccessTokenConverterConfiguration {

		@Bean
		JwtAccessTokenConverter accessTokenConverter() {
			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			converter.setAccessTokenConverter(new CustomAccessTokenConverter());
			return converter;
		}

	}

	protected static class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

	}

}
