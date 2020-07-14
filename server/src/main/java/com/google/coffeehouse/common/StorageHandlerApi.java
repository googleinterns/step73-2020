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

package com.google.coffeehouse.common;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Spanner;
import java.util.List;

/**
* The StorageHandlerApi class holds all the wrapper functions that the rest of the code will
* use to interact with the StorageHandler class. The class also instantiates a Spanner and
* Database Client which is then referenced throughout the file.
*/

public class StorageHandlerApi {

  private static final Spanner spanner = StorageHandlerSetup.createSpannerService();
  private static final DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);

  /**
  * Fetches a person from the database and returns a {@link Person}.
  *
  * @param  userId    the user ID string used to query the Persons table from the database
  * @return person    a {@link Person} object containing information from the database
  */
  public static Person fetchPersonFromId(String userId) {
    Person person = StorageHandler.getPersonQuery(dbClient, userId);
    return person;
  }

  /**
  * Fetches a club from the database and returns a {@link Club}.
  *
  * @param  clubId    the club ID string used to query the Clubs table from the database.
  * @return club      a {@link Club} containing information from the database
  */
  public static Club fetchClubFromId(String clubId) {
    Club club = StorageHandler.getClubQuery(dbClient, clubId);
    return club;
  }

  /**
  * Adds a membership to the database.
  *
  * @param  userId      the user ID string used to insert the membership into the table
  * @param  clubId      the club ID string used to insert the membership into the table
  */
  public static void addPersonClubMembership(String userId, String clubId) {
    StorageHandler.addPersonClubMembershipMutation(dbClient, userId, clubId);
  }

  /**
  * Deletes a membership from the database.
  *
  * @param  userId      the user ID string used to query the membership table
  * @param  clubId      the club ID string used to query the membership table
  */
  public static void deletePersonClubMembership(String userId, String clubId) {
    StorageHandler.deletePersonClubMembershipDml(dbClient, userId, clubId);
  }

  /**
  * Creates and returns a list of {@link Club}s depending on the user's membership status.
  *
  * @param  userId            the user ID string used to query and get a list of clubs
  * @param  membershipStatus  the enum specifying whether the user is a member or not
  * @return clubs             the list of {@link Club}s
  */
  public static List<Club> listClubsFromUserId(String userId, MembershipConstants.MembershipStatus membershipStatus) {
    List<Club> clubs = StorageHandler.getListOfClubsQuery(dbClient, userId, membershipStatus);
    return clubs;
  }
}