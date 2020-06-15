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
package org.springframework.boot.autoconfigure.security.oauth2;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.base.Predicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Tests regarding the overall architecture of the module
 *
 * @author Josh Cummings
 */
public class ArchitectureTests {

	@Test
	public void freeOfCycles() {
		String pkg = "org.springframework.boot.autoconfigure.security.oauth2";
		JavaClasses classes = new ClassFileImporter().importPackages(pkg);
		slices().matching("(**)").should().beFreeOfCycles().ignoreDependency(isATestClass(), alwaysTrue())
				.check(classes);
	}

	private DescribedPredicate<JavaClass> isATestClass() {
		return when("full name containing 'Tests'", input -> input.getFullName().contains("Tests"));
	}

	private DescribedPredicate<JavaClass> when(String description, Predicate<JavaClass> test) {
		return new DescribedPredicate<JavaClass>(description) {
			@Override
			public boolean apply(JavaClass input) {
				return test.apply(input);
			}
		};
	}

}
