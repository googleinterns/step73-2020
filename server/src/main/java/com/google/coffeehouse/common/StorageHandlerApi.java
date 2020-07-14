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
* use to interact with the StorageHandler class. Each function will instantiate a Spanner and
* Database Client.
*/

public class StorageHandlerApi {

  public static Person fetchPersonFromId(String userId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    Person person = StorageHandler.getPersonQuery(dbClient, userId);
    return person;
  }

  public static Club fetchClubFromId(String clubId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    Club club = StorageHandler.getClubQuery(dbClient, clubId);
    return club;
  }

  public static void deletePersonClubMembership(String userId, String clubId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    StorageHandler.deletePersonClubMembershipDml(dbClient, userId, clubId);
  }

  public static void addPersonClubMembership(String userId, String clubId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    StorageHandler.addPersonClubMembershipMutation(dbClient, userId, clubId);
  }

  public static List<Club> listClubsFromUserId(String userId, MembershipConstants.MembershipStatus membershipStatus) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    List<Club> clubs = StorageHandler.getListOfClubsQuery(dbClient, userId, membershipStatus);
    return clubs;
  }
}