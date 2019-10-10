/*
 * Copyright 2019 LLC
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

package com.google.cloud.translate.automl;

// [START automl_translate_list_model]
import com.google.cloud.automl.v1.AutoMlClient;
import com.google.cloud.automl.v1.ListModelsRequest;
import com.google.cloud.automl.v1.LocationName;
import com.google.cloud.automl.v1.Model;

import java.io.IOException;

class ListModels {

  // List models
  static void listModels(String projectId) {
    // String projectId = "YOUR_PROJECT_ID";

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (AutoMlClient client = AutoMlClient.create()) {
      // A resource that represents Google Cloud Platform location.
      LocationName projectLocation = LocationName.of(projectId, "us-central1");

      // Create list models request.
      ListModelsRequest listModlesRequest =
              ListModelsRequest.newBuilder()
                      .setParent(projectLocation.toString())
                      .setFilter("")
                      .build();

      // List all the models available in the region by applying filter.
      System.out.println("List of models:");
      for (Model model : client.listModels(listModlesRequest).iterateAll()) {
        // Display the model information.
        System.out.format("Model name: %s\n", model.getName());
        System.out.format(
                "Model id: %s\n",
                model.getName().split("/")[model.getName().split("/").length - 1]);
        System.out.format("Model display name: %s\n", model.getDisplayName());
        System.out.println("Model create time:");
        System.out.format("\tseconds: %s\n", model.getCreateTime().getSeconds());
        System.out.format("\tnanos: %s\n", model.getCreateTime().getNanos());
        System.out.format("Model deployment state: %s\n", model.getDeploymentState());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
// [END automl_translate_list_model]
