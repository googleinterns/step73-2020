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
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;

/**
* The StorageHandler class holds the query functions to get information from the database.
*/
public class StorageHandler {
  public static final String NO_PRONOUNS = "No pronouns";
  public static final String PERSON_DOES_NOT_EXIST = "This person does not exist in the database.";
  public static final String ERROR_MORE_THAN_ONE_PERSON = "More than one person per user ID.";

  public static final String CLUB_DOES_NOT_EXIST = "This club does not exist in the database.";
  public static final String ERROR_MORE_THAN_ONE_CLUB = "More than one club per club ID.";

  public static final String NO_AUTHOR = "No author";
  public static final String NO_ISBN = "No ISBN";
  public static final String BOOK_DOES_NOT_EXIST = "This book does not exist in the database.";
  public static final String ERROR_MORE_THAN_ONE_BOOK = "More than one book per book ID.";
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
  public static Person getPersonQuery(DatabaseClient dbClient, String userId) {
    String email = "";
    String nickname = "";
    String pronouns = "";
    // TODO: implement setting user ID if it already exists @linamontes10
    long resultCount = StorageHandlerHelper.getPersonCountQuery(dbClient, userId);
    Statement statement = 
        Statement.newBuilder(
                "SELECT userId, email, nickname, pronouns "
                  + "FROM Persons "
                  + "WHERE userId = @userId")
              .bind("userId")
              .to(userId)
              .build();
    if (resultCount == 1) {
      try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          email = resultSet.getString("email");
          nickname = resultSet.getString("nickname");
          if (!resultSet.isNull("pronouns") || !resultSet.getString("pronouns").isEmpty()) {
            pronouns = resultSet.getString("pronouns");
          }
        }
        Person.Builder personBuilder = Person.newBuilder(email, nickname);
        if (!pronouns.isEmpty()) {
          personBuilder.setPronouns(pronouns);
        }
        Person person = personBuilder.build();
      }
    } else if (resultCount > 1) {
      throw new IllegalArgumentException(ERROR_MORE_THAN_ONE_PERSON);
    } else {
      throw new IllegalArgumentException(PERSON_DOES_NOT_EXIST);
    }
    return person;
  }

  /**
  * Returns a string containing information about a club queried from the database.
  * This method formats a string with a club's ID, book ID, description, name, and ownerID.
  * @param  dbClient  the database client
  * @param  clubId    the club ID string used to query and get a club's information
  * @return clubInfo  the formatted string containing the club information
  */
  public static Club getClubQuery(DatabaseClient dbClient, String clubId) {
    String bookId = "";
    String description = "";
    String ownerId = "";
    String name = "";
    long resultCount = StorageHandlerHelper.getClubCountQuery(dbClient, clubId);
    Statement statement = 
        Statement.newBuilder(
                "SELECT clubId, bookId, description, name, ownerId "
                  + "FROM Clubs "
                  + "WHERE clubId = @clubId")
            .bind("clubId")
            .to(clubId)
            .build();
    if (resultCount == 1) {
      try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          bookId = resultSet.getString("bookId");
          name = resultSet.getString("name");
          description = resultSet.getString("description");
          ownerId = resultSet.getString("ownerId");
        }
        Club.Builder clubBuilder = Club.newBuilder(name, bookId);
        Club club = clubBuilder.build();
      }
    } else if (resultCount > 1) {
      throw new IllegalArgumentException(ERROR_MORE_THAN_ONE_CLUB);
    } else {
      throw new IllegalArgumentException(clubInfo = CLUB_DOES_NOT_EXIST);
    }
    return club;
  }

  /**
  * Returns a string containing information about a book queried from the database.
  * This method formats a string with a book's ID, author, ISBN, and title.
  * @param  dbClient  the database client
  * @param  bookId    the book ID string used to query and get a book's information
  * @return bookInfo  the formatted string containing the book information
  */
  public static Book getBookQuery(DatabaseClient dbClient, String bookId) {
    String author = "";
    String isbn = "";
    String title = "";
    long resultCount = StorageHandlerHelper.getBookCountQuery(dbClient, bookId);
    Statement statement = 
        Statement.newBuilder(
                "SELECT bookId, author, isbn, title "
                  + "FROM Books "
                  + "WHERE bookId = @bookId")
            .bind("bookId")
            .to(bookId)
            .build();
    if (resultCount == 1) {
      try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          title = resultSet.getString("title");
          if (!resultSet.isNull("author") || !resultSet.getString("author").isEmpty()) {
            author = resultSet.getString("author");
          }
          if (!resultSet.isNull("isbn") || !resultSet.getString("isbn").isEmpty()) {
            isbn = resultSet.getString("isbn");
          }
        }
        Book.Builder bookBuilder = Book.newBuilder(email, nickname);
        if (!author.isEmpty()) {
          bookBuilder.setAuthor(author);
        }
        if (!isbn.isEmpty()) {
          bookBuilder.setAuthor(isbn);
        }
        Book book = bookBuilder.build();
      }
    } else if (resultCount > 1) {
      throw new IllegalArgumentException(ERROR_MORE_THAN_ONE_BOOK);
    } else {
      throw new IllegalArgumentException(BOOK_DOES_NOT_EXIST);
    }
    return book;
  }
}
