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

package com.google.coffeehouse.storagehandler;

import com.google.coffeehouse.common.MembershipConstants;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.ReadContext;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import java.util.Arrays;
import java.util.List;

/**
* The StorageHandlerHelper class holds helper query functions that get the number of results
* matching the primary keys of different Cloud Spanner tables.
*/
public class StorageHandlerHelper {

  /**
  * Returns a Boolean that indicates whether or not a person is a member of a club.
  * This method creates a Struct that holds a single row from a database read row transacation.
  * It returns false if the Struct is null, indicating the membership does not exist.
  * It returns true if the Struct is not null, indicating the membership does exist.
  *
  * @param  readContext  the context for an attempt to perform a transaction
  * @param  userId       the user ID string of the user we are checking is in a club
  * @param  clubId       the club ID string of the club we are checking the user is in
  * @return              the Boolean true or false representing if the membership exists or not
  */
  public static Boolean checkMembership(ReadContext readContext, String userId, String clubId) {
    Struct row =
          readContext
            .readRow(
              "Memberships",
              Key.of(userId, clubId),
              Arrays.asList("userId"));
    return (row != null) ? true : false;
  }

  /**
  * Returns a long that is the number of members in a club, including the owner.
  *
  * @param  readContext  the context for an attempt to perform a transaction
  * @param  userId       the club ID string used to get number of members
  * @return              the long representing the number of members in the club
  */
  public static long getMemberCount(ReadContext readContext, String clubId) {
    long count = 0;
    Statement statement = 
        Statement.newBuilder(
                "SELECT COUNT(*) as count "
                  + "FROM Memberships "
                  + "WHERE clubId = @clubId")
              .bind("clubId")
              .to(clubId)
              .build();
    try (ResultSet resultSet = readContext.executeQuery(statement)) {
      while (resultSet.next()) {
        count = resultSet.getLong("count");
      }
    }
    return count;
  }
}
