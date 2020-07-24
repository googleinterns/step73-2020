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
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Value;

/**
* The StorageHandler class holds the functions that add mutations in order to either
* insert or delete informationg from the database.
*/
public class StorageHandlerCommonMutations {
  /**
  * Returns a single Mutation that can add a membership or ownership to the database.
  *
  * @param  userId               the user ID string used to insert the membership into the table
  * @param  clubId               the club ID string used to insert the membership into the table
  * @param  membershipLevel      the integer representing membership level (member or owner)
  * @return                      the single mutation to add a membership
  */
  public static Mutation addAnyMembershipTypeMutation(String userId, String clubId,
                                                          int membershipLevel) {
    return Mutation.newInsertBuilder("Memberships")
                   .set("userId")
                   .to(userId)
                   .set("clubId")
                   .to(clubId)
                   .set("membershipType")
                   .to(membershipLevel)
                   .set("timestamp")
                   .to(Value.COMMIT_TIMESTAMP)
                   .build();
  }

  /**
  * Returns a single Mutation that can delete a membership (ow ownership) from the database.
  *
  * @param  userId      the user ID string used to query the membership table
  * @param  clubId      the club ID string used to query the membership table
  * @return             the single mutation to delete a membership
  */
  public static Mutation deleteMembershipMutation(String userId, String clubId) {
    return Mutation.delete("Memberships",
                            KeySet.newBuilder()
                                  .addKey(Key.of(userId, clubId)).build());
  }
}
