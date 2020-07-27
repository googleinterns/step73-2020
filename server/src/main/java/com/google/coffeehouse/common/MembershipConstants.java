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

/**
* The MembershipConstants class holds constant values that denote membership status.
*/
public class MembershipConstants {
  public static final int MEMBER = 1;
  public static final int OWNER = 2;

  public static final String PERSON_ALREADY_IN_CLUB = "Person is already a member of club.";
  public static final String NO_CLUBS = "No clubs that the person is a \"%s\" of.";
  public static final String OWNER_CAN_NOT_LEAVE_CLUB = "Owner can't leave club they created.";
  public static final String PERSON_NOT_IN_CLUB = "Person is not a member of club.";

  public static final String NO_MEMBERS = "No members in club.";

  public static enum MembershipStatus {
    MEMBER, NOT_MEMBER;
  }
  
  private MembershipConstants(){
  }
}
