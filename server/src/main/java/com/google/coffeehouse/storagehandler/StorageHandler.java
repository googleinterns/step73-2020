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

import static com.google.cloud.spanner.TransactionRunner.TransactionCallable;

import com.google.coffeehouse.common.Book;
import com.google.coffeehouse.common.Club;
import com.google.coffeehouse.common.MembershipConstants;
import com.google.coffeehouse.common.Person;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
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

  public static final String CLUB_DOES_NOT_EXIST = "This club does not exist in the database.";

  public static final String NO_AUTHOR = "No author";
  public static final String NO_ISBN = "No ISBN";
  public static final String BOOK_DOES_NOT_EXIST = "This book does not exist in the database.";

  /**
  * Creates and returns a {@link Person} with the result of a strong read from the database.
  * This method builds a {@link Person} with a user's ID, email, nickname, and pronouns.
  *
  * @param  dbClient    the database client 
  * @param  userId      the user ID string used to query and get a person's information
  * @return person      the Person object built containing the person information
  */
  public static Person getPerson(DatabaseClient dbClient, String userId) {
    String email = "";
    String nickname = "";
    String pronouns = "";
    Struct row = 
        dbClient
            .singleUse()
            .readRow(
              "Persons",
              Key.of(userId),
              Arrays.asList("email", "nickname", "pronouns"));
    if (row != null) {
      Person.Builder personBuilder = Person.newBuilder(row.getString(/*emailIndex=*/0),
                                                       row.getString(/*nicknameIndex=*/1));
      // TODO: implement setting the userId field @JosephBushagour
      if (!row.isNull(/*index=*/2) || !row.getString(/*index=*/2).isEmpty()) {
        personBuilder.setPronouns(row.getString(/*pronounsIndex=*/2));
      }
      return personBuilder.build();
    } else {
      throw new IllegalArgumentException(PERSON_DOES_NOT_EXIST);
    }
  }

  /**
  * Creates and returns a {@link Book} with the result of a strong read from the database.
  * This method builds a {@link Book} with a book's ID, author, ISBN, and title.
  *
  * @param  dbClient  the database client
  * @param  bookId    the book ID string used to query and get a book's information
  * @return book      the Book object built containing the book information
  */
  public static Book getBook(DatabaseClient dbClient, String bookId) {
    String author = "";
    String isbn = "";
    String title = "";
    Struct row = 
        dbClient
            .singleUse()
            .readRow(
              "Books",
              Key.of(bookId),
              Arrays.asList("author", "isbn", "title"));
    if (row != null) {
      if (!row.isNull(/*index=*/0) || !row.getString(/*index=*/0).isEmpty()) {
        author = row.getString(/*index=*/0);
      }
      if (!row.isNull(/*index=*/1) || !row.getString(/*index=*/1).isEmpty()) {
        isbn = row.getString(/*index=*/1);
      }
      title = row.getString(/*index=*/2);
    } else {
      throw new IllegalArgumentException(BOOK_DOES_NOT_EXIST);
    }
    Book.Builder bookBuilder = Book.newBuilder(title);
    // TODO: implement setting the bookId field @JosephBushagour
    if (!author.isEmpty()) {
      bookBuilder.setAuthor(author);
    }
    if (!isbn.isEmpty()) {
      bookBuilder.setIsbn(isbn);
    }
    return bookBuilder.build();
  }

  /**
  * Creates and returns a {@link Club} with the result of a strong read from the database.
  * This method builds a {@link Club} with a club's ID, book ID, description, name, and ownerID.
  *
  * @param  dbClient  the database client
  * @param  clubId    the club ID string used to query and get a club's information
  * @return club      the Club object built containing the club information
  */
  public static Club getClub(DatabaseClient dbClient, String clubId) {
    String bookId = "";
    String description = "";
    String ownerId = "";
    String name = "";
    Struct row = 
      dbClient
          .singleUse()
          .readRow(
            "Books",
            Key.of(bookId),
            Arrays.asList("bookId", "description", "name", "ownerId"));
    if (row != null) {
      bookId = row.getString(/*index=*/0);
      description = row.getString(/*index=*/1);
      name = row.getString(/*index=*/2);
      ownerId = row.getString(/*index=*/3);
    } else {
      throw new IllegalArgumentException(CLUB_DOES_NOT_EXIST);
    }
    Book book = getBook(dbClient, bookId);
    Club.Builder clubBuilder = Club.newBuilder(name, book);
    // TODO: implement setting the clubId field @JosephBushagour
    // TODO: implement setting the ownerId field @JosephBushagour
    clubBuilder.setDescription(description);
    return clubBuilder.build();
  }

  /**
  * Creates and returns a list of {@link Persons}s that are a member of a club.
  * This method builds a {@link Person} for each person who is a member of the club
  * specified by club ID. Each {@link Person} is added to a list that gets returned.
  *
  * @param  dbClient    the database client
  * @param  clubId      the club ID string used to query
  * @return persons     the list of Person objects
  */
  public static List<Person> getListOfMembers(DatabaseClient dbClient, String clubId) {
    List<Person> persons = new ArrayList<>();
    long resultCount = StorageHandlerHelper.getMemberCount(dbClient, clubId);
    if (resultCount < 0) {
      throw new IllegalArgumentException(MembershipConstants.NO_MEMBERS);
    }
    Statement statement = 
        Statement.newBuilder(
                "SELECT userId, clubId"
                  + "FROM Memberships "
                  + "WHERE clubId = @clubId")
            .bind("clubId")
            .to(clubId)
            .build();
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        String userId = resultSet.getString("userId");
        persons.add(getPerson(dbClient, userId));
      }
    }
    return persons;
  }

  /**
  * Creates and returns a list of {@link Club}s depending on the user's membership status.
  * This method builds a {@link Club} with a club's ID, book ID, description, name, and ownerID
  * for each club that a user is either a member of or not a member of, and appends each club
  * to a list of clubs that gets returned.
  *
  * @param  dbClient          the database client
  * @param  userId            the user ID string used to query and get a list of clubs
  * @param  membershipStatus  the enum specifying whether the user is a member or not
  * @return clubs             the list of {@link Club}s
  */
  public static List<Club> getListOfClubs(DatabaseClient dbClient, String userId, MembershipConstants.MembershipStatus membershipStatus) {
    Statement statement;
    List<Club> clubs = new ArrayList<>();
    if (membershipStatus == MembershipConstants.MembershipStatus.MEMBER) {
      statement = 
        Statement.newBuilder(
                "SELECT userId, clubId "
                  + "FROM Memberships "
                  + "WHERE userId = @userId")
            .bind("userId")
            .to(userId)
            .build();
    } else {
      statement = 
        Statement.newBuilder(
                "SELECT clubId "
                  + "FROM Clubs "
                  + "WHERE NOT EXISTS ("
                  + "SELECT userId, clubId "
                  + "FROM Memberships "
                  + "WHERE userId = @userId)")
            .bind("userId")
            .to(userId)
            .build();
    }
    try (ResultSet resultSet = dbClient.singleUse().executeQuery(statement)) {
      while (resultSet.next()) {
        String clubId = resultSet.getString("clubId");
        clubs.add(getClub(dbClient, clubId));
      }
    }
    return clubs;
  }

  /**
  * Performs a transaction that adds a membership to the database.
  * This method checks if a person is already a member of a club. If the person is
  * already a member, it will throw an exception. Otherwise, it adds the row containing the
  * user ID and club ID to the Memberships table.
  *
  * @param  dbClient    the database client
  * @param  userId      the user ID string used to query the membership table
  * @param  clubId      the club ID string used to query the membership table
  */
  public static void getAddMembershipTransaction(DatabaseClient dbClient, String userId, String clubId) {
    dbClient
        .readWriteTransaction()
        .run(
          new TransactionCallable<Void>() {
            @Override
            public Void run(TransactionContext transaction) throws Exception {
              Boolean exists = StorageHandlerHelper.checkMembership(transaction, userId, clubId);
              if (!exists) {
                transaction.buffer(StorageHandlerCommonMutations.addMembershipMutation(userId, clubId));
              } else {
                throw new IllegalArgumentException(MembershipConstants.PERSON_ALREADY_IN_CLUB);
              }
              return null;
            }
          }
        );
  }

  /**
  * Gets a transaction that deletes a membership from the database.
  * This method checks if a person is already a member of a club by calling a read row transaction function
  * from the StorageHandlerHelper class.
  *
  * @param  userId      the user ID string used to query the membership table
  * @param  clubId      the club ID string used to query the membership table
  */
  public static void getDeleteMembershipTransaction(DatabaseClient dbClient, String userId, String clubId) {
    dbClient
        .readWriteTransaction()
        .run(
          new TransactionCallable<Void>() {
            @Override
            public Void run(TransactionContext transaction) throws Exception {
              Boolean exists = StorageHandlerHelper.checkMembership(transaction, userId, clubId);
              if (exists) {
                transaction.buffer(StorageHandlerCommonMutations.deleteMembershipMutation(userId, clubId));
              } else {
                throw new IllegalArgumentException(MembershipConstants.PERSON_NOT_IN_CLUB);
              }
              return null;
            }
          }
        );
  }
}
