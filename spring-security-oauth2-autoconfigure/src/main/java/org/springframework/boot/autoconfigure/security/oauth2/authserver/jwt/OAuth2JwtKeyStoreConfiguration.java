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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.StringUtils;

/**
 * Configuration for a Spring Security OAuth2 authorization server with JWT keystore.
 * @author Harold Li
 * @since 2.0.1
 */
@Configuration
@ConditionalOnClass(EnableAuthorizationServer.class)
@Conditional(OAuth2JwtKeyStoreConfiguration.JwtKeyStoreCondition.class)
@EnableConfigurationProperties(AuthorizationServerJwtProperties.class)
public class OAuth2JwtKeyStoreConfiguration {

	private final ApplicationContext context;
	private final AuthorizationServerJwtProperties properties;

	@Autowired
	public OAuth2JwtKeyStoreConfiguration(AuthorizationServerJwtProperties properties, ApplicationContext context) {
		this.context = context;
		this.properties = properties;
	}

	@Bean
	@ConditionalOnMissingBean(TokenStore.class)
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	@ConditionalOnMissingBean(AccessTokenConverter.class)
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(context.getResource(properties.getKeyStore()),
				properties.getPassword().toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair(properties.getAlias()));
		return converter;
	}

	public static class JwtKeyStoreCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConditionMessage.Builder message = ConditionMessage.forCondition("OAuth JWT keystore Condition");
			AuthorizationServerJwtProperties jwt = Binder.get(context.getEnvironment())
					.bind("security.oauth2.authorization.jwt", AuthorizationServerJwtProperties.class).orElse(null);
			if (jwt != null) {
				String keyStore = jwt.getKeyStore();
				String password = jwt.getPassword();
				String alias = jwt.getAlias();
				if (StringUtils.hasText(keyStore) && StringUtils.hasText(password) && StringUtils.hasText(alias)) {
					return ConditionOutcome.match(message.foundExactly("provided jwt key store"));
				}
			}
			return ConditionOutcome.noMatch(message.didNotFind("provided jwt key store").atAll());
		}
	}

}
