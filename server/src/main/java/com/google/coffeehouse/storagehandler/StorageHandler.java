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
import com.google.cloud.spanner.KeyRange;
import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.ReadOnlyTransaction;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.TransactionContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* The StorageHandler class holds the functions that either get information from the
* database and return the respective objects created with that information, or
* perform transactions to the database.
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
  * @return             the Person object built containing the person information
  */
  public static Person getPerson(DatabaseClient dbClient, String userId) {
    Struct row = 
        dbClient
            .singleUse()
            .readRow(
              "Persons",
              Key.of(userId),
              Arrays.asList("email", "nickname", "pronouns"));
    if (row != null) {
      Person.Builder personBuilder = Person.newBuilder()
                                           .setEmail(row.getString(/* emailIndex= */0))
                                           .setNickname(row.getString(/* nicknameIndex= */1))
                                           .setUserId(userId);

      if (!row.isNull(/* index= */2)) {
        personBuilder.setPronouns(row.getString(/* pronounsIndex= */2));
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
  * @return           the Book object built containing the book information
  */
  public static Book getBook(DatabaseClient dbClient, String bookId) {
    Struct row = 
        dbClient
            .singleUse()
            .readRow(
              "Books",
              Key.of(bookId),
              Arrays.asList("title", "author", "isbn"));
    if (row != null) {
      Book.Builder bookBuilder = Book.newBuilder()
                                     .setTitle(row.getString(/* titleIndex= */ 0))
                                     .setBookId(bookId);

      if (!row.isNull(/* authorIndex= */ 1)) {
        bookBuilder.setAuthor(row.getString(/* authorIndex= */ 1));
      }
      if (!row.isNull(/* isbnIndex= */ 2)) {
        bookBuilder.setIsbn(row.getString(/* isbnIndex= */ 2));
      }
      return bookBuilder.build();
    } else {
      throw new IllegalArgumentException(BOOK_DOES_NOT_EXIST);
    }
  }

  /**
  * Creates and returns a {@link Club} with the result of a strong read from the database.
  * This method builds a {@link Club} with a club's ID, book ID, description, name, and ownerID.
  *
  * @param  dbClient  the database client
  * @param  clubId    the club ID string used to query and get a club's information
  * @return           the Club object built containing the club information
  */
  public static Club getClub(DatabaseClient dbClient, String clubId) {
    Struct row = 
      dbClient
          .singleUse()
          .readRow(
            "Clubs",
            Key.of(clubId),
            Arrays.asList("bookId", "name", "description", "ownerId", "contentWarning"));
    if (row != null) {
      Book book = getBook(dbClient, row.getString(/* bookIdIndex= */ 0));
      Club.Builder clubBuilder = Club.newBuilder()
                                     .setCurrentBook(book)
                                     .setName(row.getString(/* nameIndex= */ 1))
                                     .setClubId(clubId)
                                     .setDescription(row.getString(/* descriptionIndex= */ 2))
                                     .setOwnerId(row.getString(/* ownerIdIndex= */ 3));                       
      if (!row.isNull(/* contentWarningIndex=*/ 4)) {
        List<String> contentWarnings =
            Arrays.asList(row.getString(/* contentWarningIndex=*/ 4).split("\\n"));
        clubBuilder.setContentWarnings(contentWarnings);
      }
      return clubBuilder.build();
    } else {
      throw new IllegalArgumentException(CLUB_DOES_NOT_EXIST);
    }
  }

  /**
  * Runs a transaction that adds a membership or ownership to the database.
  * This method checks if a person is already a member of a club by calling a helper function.
  * If the person does not exist, this method will buffer a single mutation that adds
  * the membership (or owner). Otherwise, it will throw an exception indicating that the person
  * is already a member of the club. If a person is already a member of a club, and is trying to
  * become an owner, it will throw an RuntimeException because only one unique key of
  * (userId, clubId) can exist in the database Memberships table at a time. 
  *
  * @param  dbClient             the database client
  * @param  userId               the user ID string used to perform the transaction
  * @param  clubId               the club ID string used to perform the transaction
  * @param  membershipLevel      the integer representing membership level (member or owner)
  */
  public static void runAddAnyMembershipTypeTransaction(DatabaseClient dbClient, String userId,
                                                            String clubId, int membershipLevel) {
    dbClient
        .readWriteTransaction()
        .run(
          new TransactionCallable<Void>() {
            @Override
            public Void run(TransactionContext transaction) throws Exception {
              Boolean exists = StorageHandlerHelper.checkAnyMembership(transaction, userId, clubId);
              if (!exists) {
                transaction.buffer(
                  StorageHandlerCommonMutations.addAnyMembershipTypeMutation(
                    userId, clubId, membershipLevel));
              } else {
                throw new IllegalArgumentException(MembershipConstants.PERSON_ALREADY_IN_CLUB);
              }
              return null;
            }
          }
        );
  }

  /**
  * Runs a transaction that deletes a membership (or ownership) from the database.
  * This method checks if the person is already a member of a club by calling a helper function.
  * This method also checks if the person is the owner of the club by calling a helper function.
  * If the person is a member, and not the owner, this method will buffer a single mutation
  * that deletes the membership. If the person is the owner of the club, it will throw an exception
  * indicating that the owner can't leave their own club. If the person is not a member, it will
  * throw an exception indicating that the person is already not a member of the club.
  *
  * @param  dbClient    the database client
  * @param  userId      the user ID string used to perform the transaction
  * @param  clubId      the club ID string used to perform the transaction
  */
  public static void runDeleteMembershipTransaction(DatabaseClient dbClient, String userId,
                                                                             String clubId) {
    dbClient
        .readWriteTransaction()
        .run(
          new TransactionCallable<Void>() {
            @Override
            public Void run(TransactionContext transaction) throws Exception {
              Boolean exists = StorageHandlerHelper.checkAnyMembership(transaction, userId, clubId);
              Boolean owner = StorageHandlerHelper.checkOwnership(transaction, userId, clubId);
              if (exists) {
                if (!owner) {
                  transaction.buffer(
                    StorageHandlerCommonMutations.deleteMembershipMutation(userId, clubId));
                } else {
                  throw new IllegalArgumentException(MembershipConstants.OWNER_CAN_NOT_LEAVE_CLUB);
                }
              } else {
                throw new IllegalArgumentException(MembershipConstants.PERSON_NOT_IN_CLUB);
              }
              return null;
            }
          }
        );
  }

  /**
  * Creates and returns a list of {@link Persons}s that are a member of a club.
  * This method builds a {@link Person} for each person who is a member of the club, as specified
  * by club ID. Each {@link Person} is added to a list that gets returned. If there are no members,
  * this method will throw an exception indicating that there are no members in the club.
  *
  * @param  dbClient    the database client
  * @param  clubId      the club ID string used to query
  * @return             the list of Person objects
  */
  public static List<Person> getListOfMembers(DatabaseClient dbClient, String clubId) {
    List<Person> persons = new ArrayList<>();
    Statement statement = 
        Statement.newBuilder(
                "SELECT userId "
                  + "FROM Memberships "
                  + "WHERE clubId = @clubId ")
            .bind("clubId")
            .to(clubId)
            .build();
    ResultSet resultSet = dbClient.singleUse().executeQuery(statement);
    while (resultSet.next()) {
      persons.add(getPerson(dbClient, resultSet.getString(/* userIdIndex= */0)));
    }
    if (persons.size() == 0) {
      throw new IllegalStateException(MembershipConstants.NO_MEMBERS);
    }
    return persons;
  }

  /**
  * Creates and returns a list of {@link Club}s depending on the user's membership status.
  * This method builds a {@link Club} for each club that a user is either a member of
  * or not a member of. Each {@link Club} is added to a list that gets returned.
  *
  * @param  dbClient          the database client
  * @param  userId            the user ID string used to query and get a list of clubs
  * @param  membershipStatus  the enum specifying whether the user is a member or not
  * @return                   the list of Clubs objects
  */
  public static List<Club> getListOfClubs(DatabaseClient dbClient, String userId,
                                          MembershipConstants.MembershipStatus membershipStatus) {
    ResultSet resultSet;
    List<Club> clubs = new ArrayList<>();
    ReadOnlyTransaction transaction = dbClient.readOnlyTransaction();
    if (membershipStatus == MembershipConstants.MembershipStatus.MEMBER) {
      resultSet = 
          transaction
              .read(
                "Memberships",
                KeySet.range(KeyRange.prefix(Key.of(userId))),
                Arrays.asList("clubId"));
    } else {
      Statement statement = 
        Statement.newBuilder(
                "SELECT clubId "
                  + "FROM Clubs "
                  + "WHERE clubId NOT IN ("
                  + "SELECT clubId "
                  + "FROM Memberships "
                  + "WHERE userId = @userId)")
            .bind("userId")
            .to(userId)
            .build();
      resultSet = dbClient.singleUse().executeQuery(statement);
    }
    while (resultSet.next()) {
      clubs.add(getClub(dbClient, resultSet.getString(/* clubIdIndex= */0)));
    }
    return clubs;
  }
}
