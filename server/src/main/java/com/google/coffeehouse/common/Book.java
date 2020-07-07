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
import java.util.Map;
import java.util.Optional;

/**
 * Book encapsulates the information associated with each book being used by a Club.
 * 
 * <p>Implementes the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Book implements Saveable {
  public static final String NO_VALID_TITLE_FROM_MAP = "No valid \"title\" key defined.";
  public static final String TITLE_FIELD_NAME = "title";
  public static final String AUTHOR_FIELD_NAME = "author";
  public static final String ISBN_FIELD_NAME = "isbn";
  private String title;
  private String author;
  private String isbn;
  private String bookId;

  private Book(Builder builder) {
    this.title = builder.title;
    this.author = builder.author;
    this.isbn = builder.isbn;
    this.bookId = builder.idGenerator.generateId();
  }

  /** Overloaded static factory, calls other fromMap with null IdentifierGenerator. */
  public static Book fromMap(Map bookInfo) {
    return Book.fromMap(bookInfo, null);
  }

  /** 
   * Creates a {@link Book} object from a Map with the relevant parameters as keys.
   * @param bookInfo the Map that contains the information used to construct the Book. At a
   *     minimum this includes a {@code "title"} key that is mapped to a String that describes
   *     the title of the Book to be created. Optional keys include a {@code "author"} key 
   *     that is mapped to a String describing the author of the book, as well as an 
   *     {@code "isbn"} key that is mapped to a String that is the isbn number of the book
   * @param idGen the {@link IdentifierGenerator} used when constructing the Book. If
   *     null, the default generator will be used
   * @return the created Book
   * @throws IllegalArgumentException if no valid {@code "title"} key is defined
   */
  public static Book fromMap(Map bookInfo, IdentifierGenerator idGen) {
    String title = (String) bookInfo.getOrDefault(TITLE_FIELD_NAME, null);
    if (title == null) {
      throw new IllegalArgumentException(NO_VALID_TITLE_FROM_MAP);
    }

    Book.Builder bookBuilder = Book.newBuilder(title);
    if (bookInfo.containsKey(AUTHOR_FIELD_NAME)) {
      bookBuilder.setAuthor((String) bookInfo.get(AUTHOR_FIELD_NAME));
    }
    if (bookInfo.containsKey(ISBN_FIELD_NAME)) {
      bookBuilder.setIsbn((String) bookInfo.get(ISBN_FIELD_NAME));
    }
    if (idGen != null) {
      bookBuilder.setIdGenerator(idGen);
    }
    return bookBuilder.build();
  }

  public String getTitle() {
    return title;
  }

  /** Returns an optional because the author may, or may not, exist. */
  public Optional<String> getAuthor() {
    return author == null ? Optional.empty() : Optional.of(author);
  }

  /** Returns an optional because the ISBN may, or may not, exist. */
  public Optional<String> getIsbn() {
    return isbn == null ? Optional.empty() : Optional.of(isbn);
  }

  public String getBookId() {
    return bookId;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  public static Builder newBuilder(String title) {
    return new Builder(title);
  }

  @Override
  public void save() {
    // TODO: implement saving @linamontes10
  }

  @Override
  public String toString() {
    return String.format("title: %s, author: %s, isbn: %s, bookId %s",
                         title, author, isbn, bookId);
  }

  public static class Builder {
    private String title;
    private String author = null;
    private String isbn = null;
    private IdentifierGenerator idGenerator = new UuidWrapper();

    public Builder(String title) {
      this.title = title;
    }

    public Builder setAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder setIsbn(String isbn) {
      this.isbn = isbn;
      return this;
    }

    public Builder setIdGenerator(IdentifierGenerator idGenerator) {
      this.idGenerator = idGenerator;
      return this;
    }

    public Book build() {
      return new Book(this);
    }
  }
}
