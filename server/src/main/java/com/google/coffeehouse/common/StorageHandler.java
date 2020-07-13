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

import static com.google.cloud.spanner.TransactionRunner.TransactionCallable;

import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.TransactionContext;
import com.google.cloud.spanner.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  
  public static Person createPersonFromId(String userId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    Person person = StorageHandler.getPersonQuery(dbClient, userId);
    return person;
  }

  public static Club createClubFromId(String clubId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    Club club = StorageHandler.getClubQuery(dbClient, clubId);
    return club;
  }
  
  public static void addPersonToClub(String userId, String clubId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    List<Mutation> mutations = new ArrayList<>();
    try {
      mutations.add(
      Mutation.newInsertBuilder("Memberships")
        .set("userId")
        .to(userId)
        .set("clubId")
        .to(clubId)
        .set("membershipType")
        .to(Membership.MEMBER)
        .set("timestamp")
        .to(Value.COMMIT_TIMESTAMP)
        .build());
    dbClient.write(mutations);
    } catch (Exception e) {
      throw new IllegalArgumentException(Membership.PERSON_ALREADY_IN_CLUB);
    }
  }

  public static void deletePersonFromClub(String userId, String clubId) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    if (StorageHandlerHelper.checkPersonInClubQuery(dbClient, userId, clubId)) {
      dbClient
        .readWriteTransaction()
        .run(
            new TransactionCallable<Void>() {
              @Override
              public Void run(TransactionContext transaction) throws Exception {
                String sql = "DELETE from Memberships WHERE userId = '"
                      + userId + "' AND clubId = '" + clubId + "'";
                long rowCount = transaction.executeUpdate(Statement.of(sql));
                System.out.printf("%d record deleted.\n", rowCount);
                return null;
              }
            });
    } else {
      throw new IllegalArgumentException(Membership.PERSON_NOT_IN_CLUB);
    }
  }

  /**
  * Creates and returns a {@link Person} with the info about a person queried from the database.
  * This method builds a {@link Person} with a user's ID, email, nickname, and pronouns.
  * If the user has no pronouns (NULL), or the pronouns are empty, then "No pronouns"
  * is printed out instead.
  *
  * @param  dbClient    the database client 
  * @param  userId      the user ID string used to query and get a person's information
  * @return person      the Person object built containing the person information
  */
  public static Person getPersonQuery(DatabaseClient dbClient, String userId) {
    Person person;
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
      }
      Person.Builder personBuilder = Person.newBuilder(email, nickname);
      if (!pronouns.isEmpty()) {
        personBuilder.setPronouns(pronouns);
      }
      person = personBuilder.build();
    } else if (resultCount > 1) {
      throw new IllegalArgumentException(ERROR_MORE_THAN_ONE_PERSON);
    } else {
      throw new IllegalArgumentException(PERSON_DOES_NOT_EXIST);
    }
    return person;
  }

  /**
  * Creates and returns a {@link Book} with the info about a book queried from the database.
  * This method builds a {@link Book} with a book's ID, author, ISBN, and title.
  * @param  dbClient  the database client
  * @param  bookId    the book ID string used to query and get a book's information
  * @return book      the Book object built containing the book information
  */
  public static Book getBookQuery(DatabaseClient dbClient, String bookId) {
    Book book;
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
      }
      Book.Builder bookBuilder = Book.newBuilder(title);
      if (!author.isEmpty()) {
        bookBuilder.setAuthor(author);
      }
      if (!isbn.isEmpty()) {
        bookBuilder.setAuthor(isbn);
      }
      book = bookBuilder.build();
    } else if (resultCount > 1) {
      throw new IllegalArgumentException(ERROR_MORE_THAN_ONE_BOOK);
    } else {
      throw new IllegalArgumentException(BOOK_DOES_NOT_EXIST);
    }
    return book;
  }

  /**
  * Creates and returns a {@link Club} with the info about a club queried from the database.
  * This method builds a {@link Club} with a club's ID, book ID, description, name, and ownerID.
  * @param  dbClient  the database client
  * @param  clubId    the club ID string used to query and get a club's information
  * @return club      the Club object built containing the club information
  */
  public static Club getClubQuery(DatabaseClient dbClient, String clubId) {
    Club club;
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
      }
      Book book = getBookQuery(dbClient, bookId);
      Club.Builder clubBuilder = Club.newBuilder(name, book);
      club = clubBuilder
                        .setDescription(description)
                        .build();
    } else if (resultCount > 1) {
      throw new IllegalArgumentException(ERROR_MORE_THAN_ONE_CLUB);
    } else {
      throw new IllegalArgumentException(CLUB_DOES_NOT_EXIST);
    }
    return club;
  }

  /**
  * Creates and returns a list of {@link Persons}s that are a member of a club.
  * This method builds a {@link Person} for each person who is a member of the club
  * specified by club ID. Each {@link Person} is added to a list that gets returned.
  * @param  dbClient    the database client
  * @param  clubId      the club ID string used to query
  * @return persons     the list of Person objects
  */
  public static List<Person> getListOfMembersQuery(DatabaseClient dbClient, String clubId) {
    List<Person> persons = new ArrayList<>();
    long resultCount = StorageHandlerHelper.getMemberCountQuery(dbClient, clubId);
    Statement statement = 
        Statement.newBuilder(
                "SELECT userId, clubId, membershipType "
                  + "FROM Memberships "
                  + "WHERE clubId = @clubId AND membershipType = " + Membership.MEMBER)
            .bind("clubId")
            .to(clubId)
            .build();
    if (resultCount > 0) {
      try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          String userId = resultSet.getString("userId");
          persons.add(getPersonQuery(dbClient, userId));
        }
      }
    } else {
      throw new IllegalArgumentException(Membership.NO_MEMBERS);
    }
    return persons;
  }

  /**
  * Creates and returns a list of {@link Club}s depending on the user's membership status.
  * This method builds a {@link Club} with a club's ID, book ID, description, name, and ownerID
  * for each club that a user is either a member of or not a member of, and appends each club
  * to a list of clubs that gets returned.
  * @param  dbClient    the database client
  * @param  userId      the user ID string used to query and get a list of clubs
  * @return clubs       the list of Club objects
  */
  public static List<Club> getListOfClubsQuery(DatabaseClient dbClient, String userId, Membership.MembershipStatus membershipStatus) {
    List<Club> clubs = new ArrayList<>();
    if (membershipStatus == Membership.MembershipStatus.MEMBER) {
      Statement statement = 
        Statement.newBuilder(
                "SELECT userId, clubId, membershipType "
                  + "FROM Memberships "
                  + "WHERE userId = @userId AND "
                  + "membershipType = " + Membership.MEMBER)
            .bind("userId")
            .to(userId)
            .build();
      try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          String clubId = resultSet.getString("clubId");
          clubs.add(getClubQuery(dbClient, clubId));
        }
      }
    } else if (membershipStatus == Membership.MembershipStatus.NOT_MEMBER) {
      Statement statement = 
        Statement.newBuilder(
                "SELECT clubId, userId "
                  + "FROM Clubs "
                  + "WHERE NOT EXISTS ("
                  + "SELECT userId, clubId, membershipType "
                  + "FROM Memberships "
                  + "WHERE userId = @userId"
                  + "membershipType = " + Membership.MEMBER + ")")
            .bind("userId")
            .to(userId)
            .build();
      try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
        while (resultSet.next()) {
          String clubId = resultSet.getString("clubId");
          clubs.add(getClubQuery(dbClient, clubId));
        }
      }
    }
    return clubs;
  }

  public static List<Club> listClubsFromUserId(String userId, Membership.MembershipStatus membershipStatus) {
    Spanner spanner = StorageHandlerSetup.createSpannerService();
    DatabaseClient dbClient = StorageHandlerSetup.createDbClient(spanner);
    List<Club> clubs = getListOfClubsQuery(dbClient, userId, membershipStatus);
    return clubs;
  }
}
