// Copyright 2019 Google LLC
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

package com.google.coffeehouse;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerOptions;
import com.google.cloud.spanner.Statement;

public class StorageHandler {

  static void getPersonQuery(DatabaseClient dbClient, String userId) {
    Statement statement = 
        Statement.newBuilder(
                "SELECT userId, email, nickname, pronouns "
                  + "FROM Persons "
                  + "WHERE userId = @userId")
            .bind("userId")
            .to(userId)
            .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        System.out.printf(
          "User ID: %s || Email: %s || Nickname: %s || ",
          resultSet.getString("userId"),
          resultSet.getString("email"),
          resultSet.getString("nickname"));
        try {
          if (!resultSet.getString("pronouns").isEmpty()) {
            System.out.printf("Pronouns: %s\n", resultSet.getString("pronouns"));
          } else {
            System.out.printf("No pronouns\n");
          }
        } catch(Exception e) {
          System.out.printf("No pronouns\n");
        }
      }
    }
  }


  public static void main(String... args) throws Exception {
    if (args.length != 1) {
      System.err.println("Must specify userId.");
    }
    // Instantiates a client
    SpannerOptions options = SpannerOptions.newBuilder().build();
    Spanner spanner = options.getService();
    String instanceId = "coffeehouse-instance";
    String databaseId = "coffeehouse-db";
    String userId = args[0];
    try {
      // Creates a database client
      DatabaseId db = DatabaseId.of(options.getProjectId(), instanceId, databaseId);
      DatabaseClient dbClient = spanner.getDatabaseClient(db);
      // Queries the database
      getPersonQuery(dbClient, userId);
    } finally {
      // Closes the client which will free up the resources used
      spanner.close();
    }
  }
}
