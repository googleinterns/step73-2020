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

import com.google.cloud.spanner.Mutation;
import com.google.cloud.spanner.Value;
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Club encapsulates the information associated with each book club.
 * 
 * <p>Implements the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Club implements Saveable {
  /** The names of each possible field in the map in fromMap. */
  public static final String NAME_FIELD_NAME = "name";
  public static final String CURRENT_BOOK_FIELD_NAME = "currentBook";
  public static final String CONTENT_WARNINGS_FIELD_NAME = "contentWarnings";
  public static final String DESCRIPTION_FIELD_NAME = "description";
  public static final String CLUB_ID_FIELD_NAME = "clubId";
  public static final String OWNER_ID_FIELD_NAME = "ownerId";

  private String name;
  private Book currentBook;
  private String clubId;
  private String ownerId;
  private String description;
  private List<String> contentWarnings;
  private StorageHandlerApi handler;

  private Club(Builder builder) {
    this.name = builder.name;
    this.currentBook = builder.currentBook;
    this.clubId = builder.clubId;
    this.description = builder.description;
    this.contentWarnings = builder.contentWarnings;
    this.ownerId = builder.ownerId;
    this.handler = builder.handler;
  }

  /** 
   * Creates a {@link Club} object from a Map with the relevant parameters as keys.
   * @param clubInfo the Map that contains the information used to construct the Club. At a
   *     minimum this includes a {@code "name"} key that corresponds to the String describing the
   *     name of the Club, a {@code "currentBook"} key that is mapped to another Map
   *     that follows the format described in {@link Book#fromMap(Map)}, a {@code "clubId"} key
   *     that corresponds to a String of the clubId of the Club, and a {@code "ownerId"} key that
   *     corresponds to a String of the userId of the owner of the Club. Optional keys include a
   *     {@code "description"} key that maps to a String describing the Club's description, as
   *     well as a {@code "contentWarnings"} key that maps to a List of Strings describing the
   *     content warnings of the Club. Suitable defaults will be created in the absence of these
   *     optional keys
   * @return the created Club
   * @throws IllegalStateException if no valid {@code "name"} key or 
   *     {@code "currentBook"} key or {@code "clubId"} or {@code "ownerId"} key is defined
   */
  public static Club fromMap(Map clubInfo) {
    Club.Builder clubBuilder = Club.newBuilder();
    if (clubInfo.containsKey(CURRENT_BOOK_FIELD_NAME)) {
      Map bookInfo = (Map) clubInfo.get(CURRENT_BOOK_FIELD_NAME);
      clubBuilder.setCurrentBook(Book.fromMap(bookInfo));
    }
    if (clubInfo.containsKey(NAME_FIELD_NAME)) {
      clubBuilder.setName((String) clubInfo.get(NAME_FIELD_NAME));
    }
    if (clubInfo.containsKey(CLUB_ID_FIELD_NAME)) {
      clubBuilder.setClubId((String) clubInfo.get(CLUB_ID_FIELD_NAME));
    }
    if (clubInfo.containsKey(OWNER_ID_FIELD_NAME)) {
      clubBuilder.setOwnerId((String) clubInfo.get(OWNER_ID_FIELD_NAME));
    }
    if (clubInfo.containsKey(CONTENT_WARNINGS_FIELD_NAME)) {
      clubBuilder.setContentWarnings((List) clubInfo.get(CONTENT_WARNINGS_FIELD_NAME));
    }
    if (clubInfo.containsKey(DESCRIPTION_FIELD_NAME)) {
      clubBuilder.setDescription((String) clubInfo.get(DESCRIPTION_FIELD_NAME));
    }
    return clubBuilder.build();
  }

  public String getName() {
    return name;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public Book getCurrentBook() {
    return currentBook;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getContentWarnings() {
    return contentWarnings;
  }

  public String getClubId() {
    return clubId;
  }

  public void setBook(Book book) {
    this.currentBook = book;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setContentWarnings(List<String> contentWarnings) {
    this.contentWarnings = contentWarnings;
  }

  public void setStorageHandler(StorageHandlerApi handler) {
    this.handler = handler;
  }

  /** Starts the building process of a new Club object. */
  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public String toString() {
    String clubInfo = String.format("name: %s, description: %s, contentWarnings: %s, clubId: %s",
                                    name, description, contentWarnings, clubId);
    String bookInfo = currentBook.toString();
    return clubInfo + ", currentBook: (" + bookInfo + ")";
  }

  @Override
  public void save() {
    currentBook.save();

    List<Mutation> mutations = new ArrayList<>();
    Mutation.WriteBuilder clubMutation = 
        Mutation.newInsertOrUpdateBuilder("Clubs")
                .set("clubId").to(clubId)
                .set("bookId").to(currentBook.getBookId())
                .set("description").to(description)
                .set("contentWarning").to(String.join("\n", contentWarnings))
                .set("name").to(name)
                .set("ownerId").to(ownerId)
                .set("timestamp").to(Value.COMMIT_TIMESTAMP);
    
    mutations.add(clubMutation.build());
    handler.writeMutations(mutations);
  }

  public static class Builder {
    private String name = null;
    private Book currentBook = null;
    private String description = null;
    private String clubId = null;
    private String ownerId = null;
    private List<String> contentWarnings = new ArrayList<>();
    private static final String DEFAULT_DESCRIPTION = "A book club about %s.";
    private StorageHandlerApi handler = new StorageHandlerApi();
        
    public Builder setCurrentBook(Book currentBook) {
      this.currentBook = currentBook;
      return this;
    }
    
    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setClubId(String clubId) {
      this.clubId = clubId;
      return this;
    }

    public Builder setOwnerId(String ownerId) {
      this.ownerId = ownerId;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setContentWarnings(List<String> contentWarnings) {
      this.contentWarnings = contentWarnings;
      return this;
    }

    public Builder setStorageHandler(StorageHandlerApi handler) {
      this.handler = handler;
      return this;
    }

    private String generateDefaultDescription() {
      return String.format(DEFAULT_DESCRIPTION, currentBook.getTitle());
    }

    public Club build() {
      if (name == null) {
        throw new IllegalStateException("Club must be instantiated with a non-null name");
      }
      if (currentBook == null) {
        throw new IllegalStateException("Club must be instantiated with a non-null currentBook");
      }
      if (clubId == null) {
        throw new IllegalStateException("Club must be instantiated with a non-null clubId");
      }
      if (ownerId == null) {
        throw new IllegalStateException("Club must be instantiated with a non-null ownerId");
      }
      description = description == null ? generateDefaultDescription() : description;
      return new Club(this);
    }
  }
}
