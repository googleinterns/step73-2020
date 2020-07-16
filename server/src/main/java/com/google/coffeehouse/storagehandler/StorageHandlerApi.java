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
  * @return           a {@link Person} object containing information from the database
  */
  public static Person fetchPersonFromId(String userId) {
    return StorageHandler.getPerson(dbClient, userId);
  }

  /**
  * Fetches a club by ID from the database and returns a {@link Club}.
  *
  * @param  clubId    the club ID string used to query the Clubs table from the database.
  * @return           a {@link Club} containing information from the database
  */
  public static Club fetchClubFromId(String clubId) {
    return StorageHandler.getClub(dbClient, clubId);
  }

  /**
  * Fetches members by club ID from the database and returns a list of {@link Persons}s.
  *
  * @param  clubId    the club ID used to retrieve a list of members
  * @return           a list of {@link Persons} that are members of a club
  */
  public static List<Person> fetchMembersByClubId(String clubId) {
    return StorageHandler.getListOfMembers(dbClient, clubId);
  }

  /**
  * Adds a membership to the database.
  * This method calls a transacation that adds a membership to the table.
  *
  * @param  userId      the user ID string specifying the person who is being added as a member
  * @param  clubId      the club ID string specifying the club a person is being added to
  */
  public static void addMembership(String userId, String clubId) {
    StorageHandler.runAddMembershipTransaction(dbClient, userId, clubId);
  }

  /**
  * Deletes a membership from the database.
  * This method calls a transacation that deletes a membership to the table.
  *
  * @param  userId      the user ID string used to perform the transaction
  * @param  clubId      the club ID string used to perform the transaction
  */
  public static void deleteMembership(String userId, String clubId) {
    StorageHandler.runDeleteMembershipTransaction(dbClient, userId, clubId);
  }

  /**
  * Creates and returns a list of {@link Club}s depending on the user's membership status.
  *
  * @param  userId            the user ID string used to query and get a list of clubs
  * @param  membershipStatus  the enum specifying whether the user is a member or not
  * @return                   the list of {@link Club}s
  */
  public static List<Club> listClubsFromUserId(String userId, MembershipConstants.MembershipStatus membershipStatus) {
    return StorageHandler.getListOfClubs(dbClient, userId, membershipStatus);
  }
}
