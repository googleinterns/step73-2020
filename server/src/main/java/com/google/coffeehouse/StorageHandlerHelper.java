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

package com.google.coffeehouse;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;

/**
* The StorageHandlerHelper class holds helper query functions that get the number of results
* matching the primary keys of different Cloud Spanner tables.
*/
public class StorageHandlerHelper {
  /**
  * Returns a long that is the number of results matching a specific user ID
  * @param  dbClient  the database client
  * @param  userId    the user ID string used to query and get how many users have that key
  * @return count     the long representing the number of results from the query
  */
  public static long getPersonCountQuery(DatabaseClient dbClient, String userId) {
    long count = 0;
    Statement statement = 
        Statement.newBuilder(
                "SELECT COUNT(*) as count "
                  + "FROM Persons "
                  + "WHERE userId = @userId")
              .bind("userId")
              .to(userId)
              .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        count = resultSet.getLong("count");
      }
    }
    return count;
  }

  /**
  *Returns a long that is the number of results matching a specific club ID
  * @param  dbClient  the database client
  * @param  clubId    the club ID string used to query and get how many clubs have that key
  * @return count     the long representing the number of results from the query
  */
  public static long getClubCountQuery(DatabaseClient dbClient, String clubId) {
    long count = 0;
    Statement statement = 
        Statement.newBuilder(
                "SELECT COUNT(*) as count "
                  + "FROM Clubs "
                  + "WHERE clubId = @clubId")
              .bind("clubId")
              .to(clubId)
              .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        count = resultSet.getLong("count");
      }
    }
    return count;
  }

  /**
  * Returns a long that is the number of results matching a specific book ID
  * @param  dbClient  the database client
  * @param  bookId    the book ID string used to query and get how many books have that key
  * @return count     the long representing the number of results from the query
  */
  public static long getBookCountQuery(DatabaseClient dbClient, String bookId) {
    long count = 0;
    Statement statement = 
        Statement.newBuilder(
                "SELECT COUNT(*) as count "
                  + "FROM Books "
                  + "WHERE bookId = @bookId")
              .bind("bookId")
              .to(bookId)
              .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        count = resultSet.getLong("count");
      }
    }
    return count;
  }
}
