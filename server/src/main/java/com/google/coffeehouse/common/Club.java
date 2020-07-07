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

import com.google.coffeehouse.util.IdentifierGenerator;
import com.google.coffeehouse.util.UuidWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Club encapsulates the information associated with each book club.
 * 
 * <p>Implementes the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Club implements Saveable {
  public static final String NO_VALID_NAME_BOOK_FROM_MAP = 
      "No valid \"name\" or \"currentBook\" key defined.";
  public static final String NAME_FIELD_NAME = "name";
  public static final String CURRENTBOOK_FIELD_NAME = "currentBook";
  public static final String CONTENTWARNINGS_FIELD_NAME = "contentWarnings";
  public static final String DESCRIPTION_FIELD_NAME = "description";
  private String name;
  private Book currentBook;
  private String clubId;
  private String description;
  private List<String> contentWarnings;

  private Club(Builder builder) {
    this.name = builder.name;
    this.currentBook = builder.currentBook;
    this.clubId = builder.idGenerator.generateId();
    this.description = builder.description;
    this.contentWarnings = builder.contentWarnings;
  }

  /** Overloaded static factory, calls other fromMap with null IdentifierGenerator. */
  public static Club fromMap(Map clubInfo) {
    return Club.fromMap(clubInfo, null);
  }

  /** 
   * Creates a {@link Club} object from a Map with the relevant parameters as keys.
   * @param clubInfo the Map that contains the information used to construct the Club. At a 
   *     minimum this includes a {@code "name"} key that corresponds to the String describing the
   *     name of the club, as well as a {@code "currentBook"} key that is mapped to another Map
   *     that follows the format described in {@link Book#fromMap(Map, IdentifierGenerator)}.
   *     Optional keys include a {@code "description"} key that maps to a String describing the 
   *     Club's description, as well as a {@code "contentWarnings"} key that maps to a List of 
   *     Strings describing the content warnings of the Club. Suitable defaults will be created
   *     in the absence of these optional keys
   * @param idGen the {@link IdentifierGenerator} used when constructing the Club and Book. If
   *     null, the default generator will be used
   * @return the created Club
   * @throws IllegalArgumentException if no valid {@code "name"} key or 
   *     {@code "currentBook"} key is defined
   */
  public static Club fromMap(Map clubInfo, IdentifierGenerator idGen) {
    String name = (String) clubInfo.getOrDefault(NAME_FIELD_NAME, null);
    Map bookInfo = (Map) clubInfo.getOrDefault(CURRENTBOOK_FIELD_NAME, null);
    if (name == null || bookInfo == null) {
      throw new IllegalArgumentException(NO_VALID_NAME_BOOK_FROM_MAP);
    }

    Book currentBook = Book.fromMap(bookInfo, idGen);
    Club.Builder clubBuilder = 
        Club.newBuilder(name, currentBook)
            .setContentWarnings((List) clubInfo.getOrDefault(CONTENTWARNINGS_FIELD_NAME, new ArrayList<>()))
            .setDescription((String) clubInfo.getOrDefault(DESCRIPTION_FIELD_NAME, ""));
    if (idGen != null) {
      clubBuilder.setIdGenerator(idGen);
    }
    return clubBuilder.build();
  }

  public String getName() {
    return name;
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
    // TODO: add the old book to book history @linamontes10
    this.currentBook = book;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setContentWarnings(List<String> contentWarnings) {
    this.contentWarnings = contentWarnings;
  }

  /** Starts the building process of a new Club object. */
  public static Builder newBuilder(String name, Book book) {
    return new Builder(name, book);
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
    // TODO: implement saving @linamontes10
  }

  public static class Builder {
    private String name;
    private Book currentBook;
    private String description = "";
    private IdentifierGenerator idGenerator = new UuidWrapper();
    private List<String> contentWarnings = null;
    private static final String DEFAULT_DESCRIPTION = "A book club about %s.";

    public Builder(String name, Book currentBook) {
      this.name = name;
      this.currentBook = currentBook;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setContentWarnings(List<String> contentWarnings) {
      this.contentWarnings = contentWarnings;
      return this;
    }

    public Builder setIdGenerator(IdentifierGenerator idGenerator) {
      this.idGenerator = idGenerator;
      return this;
    }

    private String generateDefaultDescription() {
      return String.format(DEFAULT_DESCRIPTION, currentBook.getTitle());
    }

    public Club build() {
      description = description == "" ? generateDefaultDescription() : description;
      return new Club(this);
    }
  }
}
