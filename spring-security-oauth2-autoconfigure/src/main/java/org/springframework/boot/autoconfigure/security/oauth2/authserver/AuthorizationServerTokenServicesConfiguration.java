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

import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Configuration for {@link AuthorizationServerTokenServices}
 *
 * @author Harold Li
 * @author Josh Cummings
 * @since 2.1.0
 */
@Configuration
public class AuthorizationServerTokenServicesConfiguration {

	/**
	 * Configuration for writing a single-key JWT token-issuing authorization server.
	 *
	 * To use, provide a private or symmetric key via
	 *
	 * {@code security.oauth2.authorization.jwt.key-value}
	 */
	@Configuration
	@Conditional(JwtTokenCondition.class)
	protected static class JwtTokenServicesConfiguration {

		private final AuthorizationServerProperties authorization;

		public JwtTokenServicesConfiguration(AuthorizationServerProperties authorization) {
			this.authorization = authorization;
		}

		@Bean
		@ConditionalOnMissingBean(AuthorizationServerTokenServices.class)
		public DefaultTokenServices jwtTokenServices(TokenStore jwtTokenStore) {
			DefaultTokenServices services = new DefaultTokenServices();
			services.setTokenStore(jwtTokenStore);
			return services;
		}

		@Bean
		@ConditionalOnMissingBean(TokenStore.class)
		public TokenStore jwtTokenStore(JwtAccessTokenConverter jwtTokenEnhancer) {
			return new JwtTokenStore(jwtTokenEnhancer);
		}

		@Bean
		@ConditionalOnMissingBean(JwtAccessTokenConverter.class)
		public JwtAccessTokenConverter jwtTokenEnhancer() {
			String keyValue = this.authorization.getJwt().getKeyValue();
			Assert.notNull(this.authorization.getJwt().getKeyValue(), "keyValue cannot be null");

			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
			if (!keyValue.startsWith("-----BEGIN")) {
				converter.setVerifierKey(keyValue);
			}
			converter.setSigningKey(keyValue);

			return converter;
		}

	}

	/**
	 * Configuration for writing a single-key JWT token-issuing authorization server based
	 * on a key store.
	 *
	 * To use, provide a key store and key alias via
	 *
	 * {@code security.oauth2.authorization.jwt.key-store},
	 * {@code security.oauth2.authorization.jwt.key-store-password}, and
	 * {@code security.oauth2.authorization.jwt.key-alias},
	 */
	@Configuration
	@Conditional(JwtKeyStoreCondition.class)
	protected class JwtKeyStoreConfiguration implements ApplicationContextAware {

		private final AuthorizationServerProperties authorization;

		private ApplicationContext context;

		@Autowired
		public JwtKeyStoreConfiguration(AuthorizationServerProperties authorization) {
			this.authorization = authorization;
		}

		@Override
		public void setApplicationContext(ApplicationContext context) throws BeansException {
			this.context = context;
		}

		@Bean
		@ConditionalOnMissingBean(AuthorizationServerTokenServices.class)
		public DefaultTokenServices jwtTokenServices(TokenStore jwtTokenStore) {
			DefaultTokenServices services = new DefaultTokenServices();
			services.setTokenStore(jwtTokenStore);
			return services;
		}

		@Bean
		@ConditionalOnMissingBean(TokenStore.class)
		public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
			return new JwtTokenStore(accessTokenConverter);
		}

		@Bean
		@ConditionalOnMissingBean(JwtAccessTokenConverter.class)
		public JwtAccessTokenConverter accessTokenConverter() {
			Assert.notNull(this.authorization.getJwt().getKeyStore(), "keyStore cannot be null");
			Assert.notNull(this.authorization.getJwt().getKeyStorePassword(), "keyStorePassword cannot be null");
			Assert.notNull(this.authorization.getJwt().getKeyAlias(), "keyAlias cannot be null");

			JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

			Resource keyStore = this.context.getResource(this.authorization.getJwt().getKeyStore());
			char[] keyStorePassword = this.authorization.getJwt().getKeyStorePassword().toCharArray();
			KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(keyStore, keyStorePassword);

			String keyAlias = this.authorization.getJwt().getKeyAlias();
			char[] keyPassword = Optional.ofNullable(this.authorization.getJwt().getKeyPassword())
					.map(String::toCharArray).orElse(keyStorePassword);
			converter.setKeyPair(keyStoreKeyFactory.getKeyPair(keyAlias, keyPassword));

			return converter;
		}

	}

	private static class JwtTokenCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConditionMessage.Builder message = ConditionMessage.forCondition("OAuth JWT Condition");
			Environment environment = context.getEnvironment();
			String keyValue = environment.getProperty("security.oauth2.authorization.jwt.key-value");
			if (StringUtils.hasText(keyValue)) {
				return ConditionOutcome.match(message.foundExactly("provided private or symmetric key"));
			}
			return ConditionOutcome.noMatch(message.didNotFind("provided private or symmetric key").atAll());
		}

	}

	private static class JwtKeyStoreCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConditionMessage.Builder message = ConditionMessage.forCondition("OAuth JWT KeyStore Condition");
			Environment environment = context.getEnvironment();
			String keyStore = environment.getProperty("security.oauth2.authorization.jwt.key-store");
			if (StringUtils.hasText(keyStore)) {
				return ConditionOutcome.match(message.foundExactly("provided key store location"));
			}
			return ConditionOutcome.noMatch(message.didNotFind("provided key store location").atAll());
		}

	}

}
