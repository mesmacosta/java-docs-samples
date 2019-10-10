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

// [START automl_translate_create_dataset]
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.automl.v1.AutoMlClient;
import com.google.cloud.automl.v1.Dataset;
import com.google.cloud.automl.v1.LocationName;
import com.google.cloud.automl.v1.OperationMetadata;
import com.google.cloud.automl.v1.TranslationDatasetMetadata;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

class CreateDataset {

  // Create a dataset
  static void createDataset(String projectId, String displayName) {
    // String projectId = "YOUR_PROJECT_ID";
    // String displayName = "YOUR_DATASET_NAME";

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (AutoMlClient client = AutoMlClient.create()) {
      // A resource that represents Google Cloud Platform location.
      LocationName projectLocation = LocationName.of(projectId, "us-central1");

      // Specify the source and target language.
      TranslationDatasetMetadata translationDatasetMetadata =
              TranslationDatasetMetadata.newBuilder()
                      .setSourceLanguageCode("en")
                      .setTargetLanguageCode("ja")
                      .build();
      Dataset dataset =
              Dataset.newBuilder()
                      .setDisplayName(displayName)
                      .setTranslationDatasetMetadata(translationDatasetMetadata)
                      .build();
      OperationFuture<Dataset, OperationMetadata> future = client.createDataset(projectLocation, dataset);

      Dataset createdDataset = future.get();

      // Display the dataset information.
      System.out.format("Dataset name: %s\n", createdDataset.getName());
      System.out.format(
              "Dataset id: %s\n",
              createdDataset.getName().split("/")[createdDataset.getName().split("/").length - 1]);
      System.out.format("Dataset display name: %s\n", createdDataset.getDisplayName());
      System.out.println("Translation dataset Metadata:");
      System.out.format(
              "\tSource language code: %s\n",
              createdDataset.getTranslationDatasetMetadata().getSourceLanguageCode());
      System.out.format(
              "\tTarget language code: %s\n",
              createdDataset.getTranslationDatasetMetadata().getTargetLanguageCode());
      System.out.println("Dataset create time:");
      System.out.format("\tseconds: %s\n", createdDataset.getCreateTime().getSeconds());
      System.out.format("\tnanos: %s\n", createdDataset.getCreateTime().getNanos());
    } catch (IOException | InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
  }
}
// [END automl_translate_create_dataset]
