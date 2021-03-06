/*-
 * #%L
 * che-starter
 * %%
 * Copyright (C) 2017 Red Hat, Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package io.fabric8.che.starter.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import io.fabric8.che.starter.TestBase;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(properties = { "OPENSHIFT_TOKEN_URL=http://localhost:33333/keycloak/token/openshift",
		"GITHUB_TOKEN_URL=http://localhost:33333/keycloak/token/github" })
public class StackControllerTest extends TestBase {

	private static final String STACK_OSO_ENDPOINT = "/stack/oso";
	private static final String STACK_ENDPOINT = "/stack";

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void testGetStacksOSO() throws Exception {
		mockMvc.perform(get(STACK_OSO_ENDPOINT).header("Authorization", OPENSHIFT_TOKEN)
				.param("masterUrl", VERTX_SERVER).param("namespace", NAMESPACE)).andExpect(status().isOk())
				.andExpect(jsonPath("$[1].id", is("java-default"))).andExpect(jsonPath("$[0].id", is("vert.x")));
	}

	@Test
	public void testGetStacksOSOWithWrongToken() throws Exception {
		mockMvc.perform(get(STACK_OSO_ENDPOINT).header("Authorization", "badtoken").param("masterUrl", VERTX_SERVER)
				.param("namespace", NAMESPACE)).andExpect(status().is(401));
	}

	@Test
	public void testGetStacksOSOWithWrongNamespace() throws Exception {
		mockMvc.perform(get(STACK_OSO_ENDPOINT).header("Authorization", OPENSHIFT_TOKEN)
				.param("masterUrl", VERTX_SERVER).param("namespace", "noexisting")).andExpect(status().is(401));
	}

	@Test
	public void testGetStacksOSOWithWrongMasterURL() throws Exception {
		mockMvc.perform(get(STACK_OSO_ENDPOINT).header("Authorization", OPENSHIFT_TOKEN)
				.param("masterUrl", "http://i.do.not.exist").param("namespace", NAMESPACE)).andExpect(status().is(401));
	}

	@Test
	public void testGetStacks() throws Exception {
		mockMvc.perform(get(STACK_ENDPOINT).header("Authorization", KEYCLOAK_TOKEN).param("masterUrl", VERTX_SERVER)
				.param("namespace", NAMESPACE)).andExpect(status().isOk())
				.andExpect(jsonPath("$[1].id", is("java-default"))).andExpect(jsonPath("$[0].id", is("vert.x")));
	}

	@Test
	public void testGetStacksWithWrongToken() throws Exception {
		mockMvc.perform(get(STACK_ENDPOINT).header("Authorization", "badtoken").param("masterUrl", VERTX_SERVER)
				.param("namespace", NAMESPACE)).andExpect(status().is(401));
	}

	@Test
	public void testGetStacksWithWrongNamespace() throws Exception {
		mockMvc.perform(get(STACK_ENDPOINT).header("Authorization", KEYCLOAK_TOKEN).param("masterUrl", VERTX_SERVER)
				.param("namespace", "noexisting")).andExpect(status().is(401));
	}

	@Test
	public void testGetStacksWithWrongMasterURL() throws Exception {
		mockMvc.perform(get(STACK_ENDPOINT).header("Authorization", KEYCLOAK_TOKEN)
				.param("masterUrl", "http://i.do.not.exist").param("namespace", NAMESPACE)).andExpect(status().is(401));
	}
}
