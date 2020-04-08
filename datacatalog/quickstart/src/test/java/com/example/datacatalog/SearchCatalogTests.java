/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.datacatalog;

import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Integration (system) tests for {@link CreateFilesetEntry}.
 */
@RunWith(JUnit4.class)
public class SearchCatalogTests {

  private ByteArrayOutputStream bout;

  private static String ORG_ID = System.getenv().get("GOOGLE_CLOUD_ORG");

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bout));
  }

  @After
  public void tearDown() {
    System.setOut(null);
    bout.reset();
  }

  @Test
  public void testSearchCatalog() {
    SearchCatalog.searchCatalog(ORG_ID, "type=dataset");
    String output = bout.toString();

    assertThat(
        output,
        CoreMatchers.containsString("relative_resource_name"));
  }

  private static String getUuid8Chars() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
