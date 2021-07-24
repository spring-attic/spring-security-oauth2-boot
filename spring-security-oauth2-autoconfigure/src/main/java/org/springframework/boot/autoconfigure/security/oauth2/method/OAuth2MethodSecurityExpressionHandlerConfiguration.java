/*
 * Copyright 2012-2021 the original author or authors.
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

package org.springframework.boot.autoconfigure.security.oauth2.method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Auto-configure an expression handler for method-level security (if the application is
 * already annotated with {@code @EnableGlobalMethodSecurity}).
 *
 * @author Josh Cummings
 * @since 2.6
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ OAuth2AccessToken.class })
@ConditionalOnBean(GlobalMethodSecurityConfiguration.class)
public class OAuth2MethodSecurityExpressionHandlerConfiguration {

	@Bean
	@ConditionalOnMissingBean(MethodSecurityExpressionHandler.class)
	MethodSecurityExpressionHandler methodSecurityExpressionHandler(@Autowired ApplicationContext context,
			@Autowired(required = false) PermissionEvaluator permissionEvaluator,
			@Autowired(required = false) RoleHierarchy roleHierarchy,
			@Autowired(required = false) AuthenticationTrustResolver trustResolver,
			@Autowired(required = false) GrantedAuthorityDefaults grantedAuthorityDefaults) {
		OAuth2MethodSecurityExpressionHandler expressionHandler = new OAuth2MethodSecurityExpressionHandler();
		expressionHandler.setApplicationContext(context);
		if (permissionEvaluator != null) {
			expressionHandler.setPermissionEvaluator(permissionEvaluator);
		}
		if (roleHierarchy != null) {
			expressionHandler.setRoleHierarchy(roleHierarchy);
		}
		if (trustResolver != null) {
			expressionHandler.setTrustResolver(trustResolver);
		}
		if (grantedAuthorityDefaults != null) {
			expressionHandler.setDefaultRolePrefix(grantedAuthorityDefaults.getRolePrefix());
		}
		return expressionHandler;
	}

}
