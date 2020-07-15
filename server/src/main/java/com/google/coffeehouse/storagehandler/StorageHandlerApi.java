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

import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.MembershipConstants;
import com.google.coffeehouse.common.Person;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Spanner;
import java.util.List;

/**
* The StorageHandlerApi class holds all the wrapper functions that the rest of the code will
* use to interact with the StorageHandler class. This class also instantiates a Spanner and
* Database Client which is then referenced throughout the file.
*/
public class StorageHandlerApi {

  private static final Spanner spanner = StorageHandlerSetup.createSpannerService();
  private static final DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);

  /**
  * Fetches a person by ID from the database and returns a {@link Person}.
  *
  * @param  userId    the user ID string used to query the Persons table from the database
  * @return person    a {@link Person} object containing information from the database
  */
  public static Person fetchPersonFromId(String userId) {
    return StorageHandler.getPerson(dbClient, userId);
  }

  /**
  * Fetches a club by ID from the database and returns a {@link Club}.
  *
  * @param  clubId    the club ID string used to query the Clubs table from the database.
  * @return club      a {@link Club} containing information from the database
  */
  public static Club fetchClubFromId(String clubId) {
    return StorageHandler.getClub(dbClient, clubId);
  }

  /**
  * Performs a transaction that adds a membership to the database.
  * This method checks if a person is already a member of a club. If the person is
  * already a member, it will throw an exception. Otherwise, it adds the row containing the
  * user ID and club ID to the Memberships table.
  *
  * @param  userId      the user ID string used to perform the transaction
  * @param  clubId      the club ID string used to perform the transaction
  */
  public static void performAddMembershipTransaction(String userId, String clubId) {
    StorageHandler.getAddMembershipTransaction(dbClient, userId, clubId);
  }

  /**
  * Performs a transaction that deletes a membership from the database.
  * This method checks if a person is already a member of a club. If the person is
  * not a member, it will throw an exception. Otherwise, it deletes the row
  * containing the user ID and club ID from the Memberships table.
  *
  * @param  userId      the user ID string used to perform the transaction
  * @param  clubId      the club ID string used to perform the transaction
  */
  public static void performDeleteMembershipTransaction(String userId, String clubId) {
    StorageHandler.getDeleteMembershipTransaction(dbClient, userId, clubId);
  }

  /**
  * Creates and returns a list of {@link Club}s depending on the user's membership status.
  *
  * @param  userId            the user ID string used to query and get a list of clubs
  * @param  membershipStatus  the enum specifying whether the user is a member or not
  * @return clubs             the list of {@link Club}s
  */
  public static List<Club> listClubsFromUserId(String userId, MembershipConstants.MembershipStatus membershipStatus) {
    return StorageHandler.getListOfClubs(dbClient, userId, membershipStatus);
  }
}
