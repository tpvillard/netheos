/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.biffbangpow.faq.app;

import com.biffbangpow.faq.auth.AuthService;
import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.db.FaqDAO;
import com.biffbangpow.faq.filter.AuthenticationFilter;
import com.biffbangpow.faq.resources.FaqDAOExceptionMapper;
import com.biffbangpow.faq.resources.FaqsResource;
import com.biffbangpow.faq.resources.SearchResource;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.message.MessageProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;

/**
 * Jersey configuration.
 */
@ApplicationPath("/faq-api")
public class AppWithAuthentication extends ResourceConfig {

    public AppWithAuthentication(Configuration config, FaqDAO dao, AuthService authService) {
        registerClasses(FaqsResource.class);
        registerClasses(SearchResource.class);
        registerClasses(FaqDAOExceptionMapper.class);
        register(AuthenticationFilter.class);
        register(RolesAllowedDynamicFeature.class);
        property(MessageProperties.XML_FORMAT_OUTPUT, true);
        register(createMoxyJsonResolver());
        register(new AppBinder(dao, config, authService));
    }

    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig()
                .setFormattedOutput(true)
                .setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }

    /**
     * Dependency injection configuration
     */
    private static class AppBinder extends AbstractBinder {

        private final FaqDAO dao;
        private final Configuration config;
        private final AuthService authService;

        AppBinder(FaqDAO dao, Configuration config, AuthService authService) {
            this.dao = dao;
            this.config = config;
            this.authService = authService;
        }

        @Override
        protected void configure() {
            bind(config).to(Configuration.class);
            bind(dao).to(FaqDAO.class);
            bind(authService).to(AuthService.class);
        }
    }
}
