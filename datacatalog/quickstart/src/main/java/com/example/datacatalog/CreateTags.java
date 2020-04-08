/*
This application demonstrates how to perform core operations with the
Data Catalog API.

For more information, see the README.md and the official documentation at
https://cloud.google.com/data-catalog/docs.
*/

package com.example.datacatalog;

import com.google.cloud.datacatalog.v1.CreateTagRequest;
import com.google.cloud.datacatalog.v1.CreateTagTemplateRequest;
import com.google.cloud.datacatalog.v1.DataCatalogClient;
import com.google.cloud.datacatalog.v1.DeleteTagTemplateRequest;
import com.google.cloud.datacatalog.v1.Entry;
import com.google.cloud.datacatalog.v1.FieldType;
import com.google.cloud.datacatalog.v1.FieldType.EnumType;
import com.google.cloud.datacatalog.v1.FieldType.EnumType.EnumValue;
import com.google.cloud.datacatalog.v1.FieldType.PrimitiveType;
import com.google.cloud.datacatalog.v1.LocationName;
import com.google.cloud.datacatalog.v1.LookupEntryRequest;
import com.google.cloud.datacatalog.v1.Tag;
import com.google.cloud.datacatalog.v1.TagField;
import com.google.cloud.datacatalog.v1.TagTemplate;
import com.google.cloud.datacatalog.v1.TagTemplateField;
import com.google.cloud.datacatalog.v1.TagTemplateName;

public class CreateTags {

  public static void createTags() {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "my-project";
    String tagTemplateId = "my_tag_template";
    createTags(projectId, tagTemplateId);
  }

  public static void createTags(String projectId, String tagTemplateId) {
    // Currently, Data Catalog stores metadata in the us-central1 region.
    String location = "us-central1";

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {

      // -------------------------------
      // Create a Tag Template.
      // -------------------------------
      TagTemplateField sourceField =
          TagTemplateField.newBuilder()
              .setDisplayName("Source of data asset")
              .setType(FieldType.newBuilder().setPrimitiveType(PrimitiveType.STRING).build())
              .build();

      TagTemplateField numRowsField =
          TagTemplateField.newBuilder()
              .setDisplayName("Number of rows in data asset")
              .setType(FieldType.newBuilder().setPrimitiveType(PrimitiveType.DOUBLE).build())
              .build();

      TagTemplateField hasPiiField =
          TagTemplateField.newBuilder()
              .setDisplayName("Has PII")
              .setType(FieldType.newBuilder().setPrimitiveType(PrimitiveType.BOOL).build())
              .build();

      TagTemplateField piiTypeField =
          TagTemplateField.newBuilder()
              .setDisplayName("PII type")
              .setType(
                  FieldType.newBuilder()
                      .setEnumType(
                          EnumType.newBuilder()
                              .addAllowedValues(
                                  EnumValue.newBuilder().setDisplayName("EMAIL").build())
                              .addAllowedValues(
                                  EnumValue.newBuilder()
                                      .setDisplayName("SOCIAL SECURITY NUMBER")
                                      .build())
                              .addAllowedValues(
                                  EnumValue.newBuilder().setDisplayName("NONE").build())
                              .build())
                      .build())
              .build();

      TagTemplate tagTemplate =
          TagTemplate.newBuilder()
              .setDisplayName("Demo Tag Template")
              .putFields("source", sourceField)
              .putFields("num_rows", numRowsField)
              .putFields("has_pii", hasPiiField)
              .putFields("pii_type", piiTypeField)
              .build();

      CreateTagTemplateRequest createTagTemplateRequest =
          CreateTagTemplateRequest.newBuilder()
              .setParent(
                  LocationName.newBuilder()
                      .setProject(projectId)
                      .setLocation(location)
                      .build()
                      .toString())
              .setTagTemplateId("demo_tag_template")
              .setTagTemplate(tagTemplate)
              .build();

      String expectedTemplateName =
          TagTemplateName.newBuilder()
              .setProject(projectId)
              .setLocation(location)
              .setTagTemplate("demo_tag_template")
              .build()
              .toString();

      // Delete any pre-existing Template with the same name.
      try {
        dataCatalogClient.deleteTagTemplate(
            DeleteTagTemplateRequest.newBuilder()
                .setName(expectedTemplateName)
                .setForce(true)
                .build());

        System.out.println(String.format("Deleted template: %s", expectedTemplateName));
      } catch (Exception e) {
        System.out.println(String.format("Cannot delete template: %s", expectedTemplateName));
      }

      // Create the Tag Template.
      tagTemplate = dataCatalogClient.createTagTemplate(createTagTemplateRequest);
      System.out.println(String.format("Template created with name: %s", tagTemplate.getName()));

      // -------------------------------
      // Lookup Data Catalog's Entry referring to the table.
      // -------------------------------
      String linkedResource =
          String.format(
              "//bigquery.googleapis.com/projects/%s/datasets/demo_dataset/tables/trips",
              projectId);
      LookupEntryRequest lookupEntryRequest =
          LookupEntryRequest.newBuilder().setLinkedResource(linkedResource).build();
      Entry tableEntry = dataCatalogClient.lookupEntry(lookupEntryRequest);

      // -------------------------------
      // Attach a Tag to the table.
      // -------------------------------
      TagField sourceValue =
          TagField.newBuilder().setStringValue("Copied from tlc_yellow_trips_2017").build();
      TagField numRowsValue = TagField.newBuilder().setDoubleValue(113496874).build();
      TagField hasPiiValue = TagField.newBuilder().setBoolValue(false).build();
      TagField piiTypeValue =
          TagField.newBuilder()
              .setEnumValue(TagField.EnumValue.newBuilder().setDisplayName("NONE").build())
              .build();

      Tag tag =
          Tag.newBuilder()
              .setTemplate(tagTemplate.getName())
              .putFields("source", sourceValue)
              .putFields("num_rows", numRowsValue)
              .putFields("has_pii", hasPiiValue)
              .putFields("pii_type", piiTypeValue)
              .build();

      CreateTagRequest createTagRequest =
          CreateTagRequest.newBuilder().setParent(tableEntry.getName()).setTag(tag).build();

      dataCatalogClient.createTag(createTagRequest);

    } catch (Exception e) {
      System.out.print("Error during CreateTags:\n" + e.toString());
    }
  }
}
