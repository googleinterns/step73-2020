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
* The StorageHandler class holds the query functions to get information from the database.
*
* @author  Lina Montes
* @since   2020-07-06 
*/
public class StorageHandler {

  /**
  * Returns a string containing information about a person queried from the database.
  * This method formats a string with a user's ID, email, nickname, and pronouns.
  * If the user has no pronouns (NULL), or the pronouns are empty, then "No pronouns"
  * is printed out instead.
  *
  * @param  dbClient    the database client 
  * @param  userId      the user ID string used to query and get a person's information
  * @return personInfo  the formatted string containing the person information
  */
  public static String getPersonQuery(DatabaseClient dbClient, String userId) {
    String personInfo = "";
    String email = "";
    String nickname = "";
    String pronouns = "";
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
        email = resultSet.getString("email");
        nickname = resultSet.getString("nickname");
        if (resultSet.isNull("pronouns") || resultSet.getString("pronouns").isEmpty()) {
          pronouns = "No pronouns";
        } else {
          pronouns = "Pronouns: " + resultSet.getString("pronouns");
        }
      }
      personInfo = String.format("User ID: %s || Email: %s || Nickname: %s || %s\n",
                                  userId, email, nickname, pronouns);
    }
    return personInfo;
  }

  /**
  * Returns a string containing information about a club queried from the database.
  * This method formats a string with a club's ID, book ID, description, name, and ownerID.
  * @param  dbClient  the database client
  * @param  clubId    the club ID string used to query and get a club's information
  * @return clubInfo  the formatted string containing the club information
  */
  public static String getClubQuery(DatabaseClient dbClient, String clubId) {
    String clubInfo = "";
    String bookId = "";
    String description = "";
    String ownerId = "";
    String name = "";
    Statement statement = 
        Statement.newBuilder(
                "SELECT clubId, bookId, description, name, ownerId "
                  + "FROM Clubs "
                  + "WHERE clubId = @clubId")
            .bind("clubId")
            .to(clubId)
            .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        bookId = resultSet.getString("bookId");
        description = resultSet.getString("description");
        ownerId = resultSet.getString("ownerId");
        name = resultSet.getString("name");
      }
      clubInfo = String.format(
                        "Club ID: %s || Book ID: %s || Description: %s || Name: %s || Owner ID: %s\n",
                        clubId, bookId, description, name, ownerId);
    }
    return clubInfo;
  }

  /**
  * Returns a string containing information about a book queried from the database.
  * This method formats a string with a book's ID, author, ISBN, and title.
  * @param  dbClient  the database client
  * @param  bookId    the book ID string used to query and get a book's information
  * @return bookInfo  the formatted string containing the book information
  */
  public static String getBookQuery(DatabaseClient dbClient, String bookId) {
    String bookInfo = "";
    String author = "";
    long isbn = 0;
    String title = "";
    Statement statement = 
        Statement.newBuilder(
                "SELECT bookId, author, isbn, title "
                  + "FROM Books "
                  + "WHERE bookId = @bookId")
            .bind("bookId")
            .to(bookId)
            .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        author = resultSet.getString("author");
        isbn = resultSet.getLong("isbn");
        title = resultSet.getString("title");
      }
      bookInfo = String.format(
                        "Book ID: %s || Author: %s || ISBN: %d || Title: %s\n",
                        bookId, author, isbn, title);
    }
    return bookInfo;
  }
}
