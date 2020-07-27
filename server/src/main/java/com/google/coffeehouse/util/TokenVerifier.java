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

import com.google.api.client.auth.openidconnect.IdToken;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.coffeehouse.util.AuthenticationHelper;
import java.util.Arrays;
import java.util.List;

/**
 * A class that verifies an ID token and returns its subject.
 */
public class TokenVerifier {
  private static final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
  private static final HttpTransport transport = new NetHttpTransport();
  private static final GooglePublicKeysManager keyManager =
      new GooglePublicKeysManager(transport, jsonFactory);
  public static final List<String> acceptableIssuers =
      Arrays.asList("https://accounts.google.com", "accounts.google.com");
  public static final List<String> acceptableIds = Arrays.asList(AuthenticationHelper.CLIENT_ID);
  
  /**
   * Gets the subject of an ID token.
   * @param token a string representation of the ID token
   * @return the subject of the token if it is valid, otherwise null
   */
  public String getSubject(String token) {
    try {
      IdToken idToken = IdToken.parse(jsonFactory, token);
      boolean isValidSignature =
          keyManager.getPublicKeys()
                    .stream()
                    .anyMatch(key -> {
                      try {
                        return idToken.verifySignature(key);
                      } catch (Exception e) {
                        return false;
                      }
                    });
      if (!isValidSignature ||
          !idToken.verifyIssuer(acceptableIssuers) ||
          !idToken.verifyAudience(acceptableIds)) {
        return null;
      }
      return idToken.getPayload().getSubject();
    } catch (Exception e) {
      return null;
    }
  }
}
