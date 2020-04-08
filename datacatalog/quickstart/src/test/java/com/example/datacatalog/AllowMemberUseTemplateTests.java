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

import com.google.cloud.datacatalog.v1.CreateTagTemplateRequest;
import com.google.cloud.datacatalog.v1.FieldType;
import com.google.cloud.datacatalog.v1.LocationName;
import com.google.cloud.datacatalog.v1.TagTemplate;
import com.google.cloud.datacatalog.v1.TagTemplateField;
import com.google.cloud.datacatalog.v1.TagTemplateName;
import com.google.cloud.datacatalog.v1.DataCatalogClient;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Integration (system) tests for {@link AllowMemberUseTemplate}. */
@RunWith(JUnit4.class)
public class AllowMemberUseTemplateTests {

  private ByteArrayOutputStream bout;

  private static String LOCATION = "us-central1";
  private static String PROJECT_ID = System.getenv().get("GOOGLE_CLOUD_PROJECT");
  private static String PROJECT_MEMBER = System.getenv().get("GOOGLE_CLOUD_PROJECT_MEMBER");
  private static String TAG_TEMPLATE_ID = "grant_member_role_tag_template_id_" + getUuid8Chars();
  private static List<String> tagTemplatesPendingDeletion = new ArrayList<>();

  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(bout));
    createTemplateForTest();
  }

  @After
  public void tearDown() {
    System.setOut(null);
    bout.reset();
  }

  @AfterClass
  public static void tearDownClass() {
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {
      if (tagTemplatesPendingDeletion.isEmpty()) {
        fail("Something went wrong, no templates were generated");
      }

      for (String tagTemplateName : tagTemplatesPendingDeletion) {
        dataCatalogClient.deleteTagTemplate(tagTemplateName, true);
      }
    } catch (Exception e) {
      System.out.println("Error in cleaning up test data:\n" + e.toString());
    }
  }

  @Test
  public void testGrantTagTemplateUserRole() {
    AllowMemberUseTemplate.grantTagTemplateUserRole(PROJECT_ID, TAG_TEMPLATE_ID, PROJECT_MEMBER);
    String output = bout.toString();

    String grantRoleTemplate = "Role successfully granted to %s";
    assertThat(
        output,
        CoreMatchers.containsString(String.format(grantRoleTemplate, PROJECT_MEMBER)));
  }

  private static String getUuid8Chars() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  private void createTemplateForTest() {
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {

      String expectedTagTemplateName = TagTemplateName.of(PROJECT_ID, LOCATION, TAG_TEMPLATE_ID)
          .toString();
      tagTemplatesPendingDeletion.add(expectedTagTemplateName);

      TagTemplateField stringField =
          TagTemplateField.newBuilder()
              .setDisplayName("String field")
              .setType(FieldType.newBuilder().setPrimitiveType(
                  FieldType.PrimitiveType.STRING).build())
              .build();

      TagTemplate tagTemplate =
          TagTemplate.newBuilder()
              .setDisplayName("Tag Template to test IAM grant role")
              .putFields("string_field", stringField)
              .build();

      CreateTagTemplateRequest createTagTemplateRequest =
          CreateTagTemplateRequest.newBuilder()
              .setParent(
                  LocationName.newBuilder()
                      .setProject(PROJECT_ID)
                      .setLocation(LOCATION)
                      .build()
                      .toString())
              .setTagTemplateId(TAG_TEMPLATE_ID)
              .setTagTemplate(tagTemplate)
              .build();

      TagTemplate createdTagTemplate = dataCatalogClient
          .createTagTemplate(createTagTemplateRequest);
    } catch (Exception e) {
      System.out.println("Error creating test data:\n" + e.toString());
    }
  }
}
