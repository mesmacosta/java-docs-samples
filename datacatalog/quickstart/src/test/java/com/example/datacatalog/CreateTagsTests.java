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
import static org.junit.Assert.fail;

import com.google.cloud.datacatalog.v1.TagTemplateName;
import com.google.cloud.datacatalog.v1beta1.DataCatalogClient;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Integration (system) tests for {@link CreateTags}.
 */
@RunWith(JUnit4.class)
public class CreateTagsTests {

  private ByteArrayOutputStream bout;

  private static String LOCATION = "us-central1";
  private static String PROJECT_ID = System.getenv().get("GOOGLE_CLOUD_PROJECT");

  private static List<String> tagTemplatesPendingDeletion = new ArrayList<>();


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

  @AfterClass
  public static void tearDownClass() {
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {
      // Must delete Entries before deleting the Entry Group.
      if (tagTemplatesPendingDeletion.isEmpty()) {
        fail("Something went wrong, no entries were generated");
      }

      for (String tagTemplateName : tagTemplatesPendingDeletion) {
        dataCatalogClient.deleteTagTemplate(tagTemplateName, true);
      }
    } catch (Exception e) {
      System.out.println("Error in cleaning up test data:\n" + e.toString());
    }
  }

  @Test
  public void testCreateEntryQuickStart() {
    String tagTemplateId = "demo_tag_template";

    CreateTags.createTags(PROJECT_ID, tagTemplateId);

    String expectedTagTemplateName = TagTemplateName.of(PROJECT_ID, LOCATION, tagTemplateId)
        .toString();
    tagTemplatesPendingDeletion.add(expectedTagTemplateName);

    String output = bout.toString();

    String tagTemplateTemplate = "Template created with name: %s";

    assertThat(
        output,
        CoreMatchers.containsString(String.format(tagTemplateTemplate, expectedTagTemplateName)));
  }
}
