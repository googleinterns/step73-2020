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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.gson.GsonFactory;

/**
 * A class that verifies an ID token and returns its subject.
 */
public class TokenVerifier {
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  
  /**
   * Gets the subject of an ID token.
   * @param token a string representation of the ID token
   * @return the subject of the token if it is valid, otherwise null
   */
  public String getSubject(String token) {
    try {
      GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, token);
      if (idToken.verifySignature() == null) {
        return null;
      }
      return idToken.getPayload().getSubject();
    } catch (Exception e) {
      return null;
    }
  }
}
