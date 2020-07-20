// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.coffeehouse.util;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;

public class AuthenticationConstants {
  /** Client ID for Google Oauth, safe to expose. */
  public static final String CLIENT_ID =
    "893627513276-o7p1m433c9l828svolutrbaibqqvmt8q.apps.googleusercontent.com";
  /** Client secret for Google Oauth, hidden in Secret Manager API. */
  public static final String CLIENT_SECRET = getSecret();

  private static String getSecret() {
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
      SecretVersionName secretVersionName = SecretVersionName.of(
            "coffeehouse-step2020", "GoogleOauthClientSecret", "1");
      AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
      return response.getPayload().getData().toStringUtf8();
    } catch (Exception e) {
      throw new RuntimeException("Unable to get Oauth client secret");
    }
  }
  // Private constructor to enforce that it should not be instantiated.
  private AuthenticationConstants() {};
}
