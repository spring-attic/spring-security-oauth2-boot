/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.boot.autoconfigure.security.oauth2;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2RestOperationsConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.method.OAuth2MethodSecurityConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Security OAuth2.
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @since 1.3.0
 */
@Configuration
@ConditionalOnClass({ OAuth2AccessToken.class, WebMvcConfigurer.class })
@Import({ OAuth2AuthorizationServerConfiguration.class,
		OAuth2MethodSecurityConfiguration.class, OAuth2ResourceServerConfiguration.class,
		OAuth2RestOperationsConfiguration.class })
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(OAuth2ClientProperties.class)
public class OAuth2AutoConfiguration {

	private final OAuth2ClientProperties credentials;

	public OAuth2AutoConfiguration(OAuth2ClientProperties credentials) {
		this.credentials = credentials;
	}

	@Bean
	public ResourceServerProperties resourceServerProperties() {
		return new ResourceServerProperties(this.credentials.getClientId(),
				this.credentials.getClientSecret());
	}

}
