/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2018, The THYMELEAF team (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package org.thymeleaf.extras.springsecurity4.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.expression.IExpressionObjects;
import org.thymeleaf.extras.springsecurity4.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.1
 *
 */
final class SpringVersionSpecificUtils {

    // TODO This infrastructure should evolve towards having support for different versions of thymeleaf-spring
    // TODO (not the same as different versions of Spring), and merge with ThymeleafSpringUtils (probably)

    private static final Logger LOG = LoggerFactory.getLogger(SpringVersionSpecificUtils.class);

    private static final String PACKAGE_NAME = SpringVersionSpecificUtils.class.getPackage().getName();
    private static final String SPRING3_DELEGATE_CLASS = PACKAGE_NAME + ".Spring3VersionSpecificUtility";
    private static final String SPRING4_DELEGATE_CLASS = PACKAGE_NAME + ".Spring4VersionSpecificUtility";
    private static final String SPRING5_DELEGATE_CLASS = PACKAGE_NAME + ".Spring5VersionSpecificUtility";


    private static final ISpringVersionSpecificUtility spring3Delegate;
    private static final ISpringVersionSpecificUtility spring4Delegate;
    private static final ISpringVersionSpecificUtility spring5Delegate;



    static {

        if (SpringVersionUtils.isSpring50AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 5.0+ found on classpath. Initializing auth utility for Spring 5");

            try {
                final Class<?> implClass = ClassLoaderUtils.loadClass(SPRING5_DELEGATE_CLASS);
                spring5Delegate = (ISpringVersionSpecificUtility) implClass.newInstance();
                spring4Delegate = null;
                spring3Delegate = null;
            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        new ConfigurationException(
                            "Environment has been detected to be at least Spring 5, but thymeleaf could not initialize a " +
                            "delegate of class \"" + SPRING5_DELEGATE_CLASS + "\"", e));
            }

        } else if (SpringVersionUtils.isSpring40AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 4.0+ found on classpath. Initializing auth utility for Spring 4");

            try {
                final Class<?> implClass = ClassLoaderUtils.loadClass(SPRING4_DELEGATE_CLASS);
                spring5Delegate = null;
                spring4Delegate = (ISpringVersionSpecificUtility) implClass.newInstance();
                spring3Delegate = null;
            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        new ConfigurationException(
                            "Environment has been detected to be at least Spring 4, but thymeleaf could not initialize a " +
                            "delegate of class \"" + SPRING4_DELEGATE_CLASS + "\"", e));
            }

        } else if (SpringVersionUtils.isSpring30AtLeast()) {

            LOG.trace("[THYMELEAF][TESTING] Spring 3.x found on classpath. Initializing auth utility for Spring 3");

            try {
                final Class<?> implClass = ClassLoaderUtils.loadClass(SPRING3_DELEGATE_CLASS);
                spring5Delegate = null;
                spring4Delegate = null;
                spring3Delegate = (ISpringVersionSpecificUtility) implClass.newInstance();
            } catch (final Exception e) {
                throw new ExceptionInInitializerError(
                        new ConfigurationException(
                            "Environment has been detected to be Spring 3.x, but thymeleaf could not initialize a " +
                            "delegate of class \"" + SPRING3_DELEGATE_CLASS + "\"", e));
            }

        } else {

            throw new ExceptionInInitializerError(
                    new ConfigurationException(
                        "The auth infrastructure could not create utility for the specific version of Spring being" +
                        "used. Currently only Spring 3.x, 4.x and 5.x are supported."));

        }

    }




    static EvaluationContext wrapEvaluationContext(
            final EvaluationContext evaluationContext, final IExpressionObjects expresionObjects) {

        if (spring5Delegate != null) {
            return spring5Delegate.wrapEvaluationContext(evaluationContext, expresionObjects);
        }
        if (spring4Delegate != null) {
            return spring4Delegate.wrapEvaluationContext(evaluationContext, expresionObjects);
        }
        if (spring3Delegate != null) {
            return spring3Delegate.wrapEvaluationContext(evaluationContext, expresionObjects);
        }

        throw new ConfigurationException(
                "The authorization infrastructure could not create initializer for the specific version of Spring being" +
                "used. Currently only Spring 3.x, 4.x and 5.x are supported.");

    }




    private SpringVersionSpecificUtils() {
        super();
    }


    
}
