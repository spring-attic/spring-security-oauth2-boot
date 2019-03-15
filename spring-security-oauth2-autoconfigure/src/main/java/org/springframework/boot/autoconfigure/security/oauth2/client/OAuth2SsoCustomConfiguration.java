/*
 * Copyright 2012-2016 the original author or authors.
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

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Configuration for OAuth2 Single Sign On (SSO) when there is an existing
 * {@link WebSecurityConfigurerAdapter} provided by the user and annotated with
 * {@code @EnableOAuth2Sso}. The user-provided configuration is enhanced by adding an
 * authentication filter and an authentication entry point.
 *
 * @author Dave Syer
 */
@Configuration
@Conditional(EnableOAuth2SsoCondition.class)
public class OAuth2SsoCustomConfiguration
		implements ImportAware, BeanPostProcessor, ApplicationContextAware {

	private Class<?> configType;

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		this.configType = ClassUtils.resolveClassName(importMetadata.getClassName(),
				null);

	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (this.configType.isAssignableFrom(bean.getClass())
				&& bean instanceof WebSecurityConfigurerAdapter) {
			ProxyFactory factory = new ProxyFactory();
			factory.setTarget(bean);
			factory.addAdvice(new SsoSecurityAdapter(this.applicationContext));
			bean = factory.getProxy();
		}
		return bean;
	}

	private static class SsoSecurityAdapter implements MethodInterceptor {

		private SsoSecurityConfigurer configurer;

		SsoSecurityAdapter(ApplicationContext applicationContext) {
			this.configurer = new SsoSecurityConfigurer(applicationContext);
		}

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			if (invocation.getMethod().getName().equals("init")) {
				Method method = ReflectionUtils
						.findMethod(WebSecurityConfigurerAdapter.class, "getHttp");
				ReflectionUtils.makeAccessible(method);
				HttpSecurity http = (HttpSecurity) ReflectionUtils.invokeMethod(method,
						invocation.getThis());
				this.configurer.configure(http);
			}
			return invocation.proceed();
		}

	}

}
