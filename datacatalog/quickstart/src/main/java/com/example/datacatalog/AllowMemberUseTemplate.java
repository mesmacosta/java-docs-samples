/*
This application demonstrates how to allow a project member to use a
Template in order to create Tags with the Cloud Data Catalog API.

For more information, see the README.md under /datacatalog and the
documentation at https://cloud.google.com/data-catalog/docs.
*/

package com.example.datacatalog;

import com.google.cloud.datacatalog.v1.DataCatalogClient;
import com.google.cloud.datacatalog.v1.TagTemplateName;
import com.google.iam.v1.Binding;
import com.google.iam.v1.Policy;
import com.google.iam.v1.SetIamPolicyRequest;

public class AllowMemberUseTemplate {

  public static void grantTagTemplateUserRole() {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "my-project";
    String tagTemplateId = "onprem_tag_template";
    String memberId = "user:test-user@gmail.com";
    grantTagTemplateUserRole(projectId, tagTemplateId, memberId);
  }

  /**
   * Grant a project member the Tag Template User role for a given template.
   *
   * @param projectId  The project ID to which the Template belongs, e.g. 'my-project'.
   * @param templateId The template ID to grant access, e.g. 'my_template'.
   * @param memberId   The member ID who access will be granted to, e.g. 'user:test-user@gmail.com'.
   */
  public static void grantTagTemplateUserRole(
      String projectId, String templateId, String memberId) {
    // String projectId = "my-project"
    // String templateId = "my_template"
    // String memberId = "user:test-user@gmail.com"

    // Currently, Data Catalog stores metadata in the us-central1 region.
    String location = "us-central1";

    // Format the Template name.
    String templateName =
        TagTemplateName.newBuilder()
            .setProject(projectId)
            .setLocation(location)
            .setTagTemplate(templateId)
            .build()
            .toString();

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {

      // Create a Binding to add the Tag Template User role and member to the policy.
      Binding binding =
          Binding.newBuilder()
              .setRole("roles/datacatalog.tagTemplateUser")
              .addMembers(memberId)
              .build();

      // Create a Policy object to update Template's IAM policy by adding the new binding.
      Policy policyUpdate =
          Policy.newBuilder()
              .addBindings(binding)
              .build();

      SetIamPolicyRequest request = SetIamPolicyRequest.newBuilder().setPolicy(policyUpdate)
          .setResource(templateName).build();

      // Update Template's policy.
      dataCatalogClient.setIamPolicy(request);

      System.out.println(String.format("Role successfully granted to %s", memberId));

    } catch (Exception e) {
      System.out.print("Error during AllowMemberUseTemplate:\n" + e.toString());
      e.printStackTrace();
    }
  }
}