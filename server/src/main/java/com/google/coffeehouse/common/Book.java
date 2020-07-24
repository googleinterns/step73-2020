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
import com.google.coffeehouse.storagehandler.StorageHandlerApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Book encapsulates the information associated with each book being used by a Club.
 * 
 * <p>Implements the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Book implements Saveable {
  /** The names of each possible field in the map in fromMap */
  public static final String AUTHOR_FIELD_NAME = "author";
  public static final String ISBN_FIELD_NAME = "isbn";
  public static final String TITLE_FIELD_NAME = "title";
  public static final String BOOK_ID_FIELD_NAME = "bookId";

  private String title;
  private String author;
  private String isbn;
  private String bookId;
  private StorageHandlerApi handler;

  private Book(Builder builder) {
    this.title = builder.title;
    this.author = builder.author;
    this.isbn = builder.isbn;
    this.bookId = builder.bookId;
    this.handler = builder.handler;
  }

  /** 
   * Creates a {@link Book} object from a Map with the relevant parameters as keys.
   * @param bookInfo the Map that contains the information used to construct the Book. At a
   *     minimum this includes a {@code "title"} key that is mapped to a String that describes
   *     the title of the Book to be created and a {@code "bookId"} key that is mapped to a
   *     String that is the bookId of the Book. Optional keys include a {@code "author"} key
   *     that is mapped to a String describing the author of the book, as well as an 
   *     {@code "isbn"} key that is mapped to a String that is the isbn number of the book
   * @return the created Book
   * @throws IllegalStateException if no valid {@code "title"} or {@code "bookId"} key is defined
   */
  public static Book fromMap(Map bookInfo) {
    Book.Builder bookBuilder = Book.newBuilder();
    if (bookInfo.containsKey(TITLE_FIELD_NAME)) {
      bookBuilder.setTitle((String) bookInfo.get(TITLE_FIELD_NAME));
    }
    if (bookInfo.containsKey(BOOK_ID_FIELD_NAME)) {
      bookBuilder.setBookId((String) bookInfo.get(BOOK_ID_FIELD_NAME));
    }
    if (bookInfo.containsKey(AUTHOR_FIELD_NAME)) {
      bookBuilder.setAuthor((String) bookInfo.get(AUTHOR_FIELD_NAME));
    }
    if (bookInfo.containsKey(ISBN_FIELD_NAME)) {
      bookBuilder.setIsbn((String) bookInfo.get(ISBN_FIELD_NAME));
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

  public static Builder newBuilder() {
    return new Builder();
  }

  public void setStorageHandler(StorageHandlerApi handler) {
    this.handler = handler;
  }

  @Override
  public void save() {
    List<Mutation> mutations = new ArrayList<>();
    Mutation.WriteBuilder bookMutation = 
        Mutation.newInsertOrUpdateBuilder("Books")
                .set("bookId").to(bookId)
                .set("title").to(title);
    if (getAuthor().isPresent()) {
      bookMutation.set("author").to(author);
    }
    if (getIsbn().isPresent()) {
      bookMutation.set("isbn").to(isbn);
    }

    mutations.add(bookMutation.build());
    handler.writeMutations(mutations);
  }

  @Override
  public String toString() {
    return String.format("title: %s, author: %s, isbn: %s, bookId %s",
                         title, author, isbn, bookId);
  }

  public static class Builder {
    private String title = null;
    private String author = null;
    private String isbn = null;
    private String bookId = null;
    private StorageHandlerApi handler = new StorageHandlerApi();

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setAuthor(String author) {
      this.author = author;
      return this;
    }

    public Builder setIsbn(String isbn) {
      this.isbn = isbn;
      return this;
    }

    public Builder setBookId(String bookId) {
      this.bookId = bookId;
      return this;
    }

    public Builder setStorageHandler(StorageHandlerApi handler) {
      this.handler = handler;
      return this;
    }

    public Book build() {
      if (title == null) {
        throw new IllegalStateException("Book must be instantiated with a non-null title");
      }
      if (bookId == null) {
        throw new IllegalStateException("Book must be instantiated with a non-null bookId");
      }
      return new Book(this);
    }
  }
}
