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
 * Person encapsulates the information associated with a user who has an account.
 * 
 * <p>It implements the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Person implements Saveable {
  /** The name of the nickname field in the map in fromMap. */
  public static final String NICKNAME_FIELD_NAME = "nickname";
  /** The name of the email field in the map in fromMap. */
  public static final String EMAIL_FIELD_NAME = "email";
  /** The name of the pronouns field in the map in fromMap. */
  public static final String PRONOUNS_FIELD_NAME = "pronouns";
  /** The name of the userId field in the map in fromMap. */
  public static final String USER_ID_FIELD_NAME = "userId";
  
  private String nickname;
  private String email;
  private String pronouns;
  private String userId;
  private StorageHandlerApi handler;

  private Person(Builder builder) {
    this.nickname = builder.nickname;
    this.email = builder.email;
    this.userId = builder.userId;
    this.pronouns = builder.pronouns;
    this.handler = builder.handler;
  }

  /** 
   * Creates a {@link Person} object from a Map with the relevant parameters as keys.
   * @param personInfo the Map that contains the information used to construct the Person. At a
   *     minimum this includes an {@code "email"} key that is mapped to a String that describes
   *     the email of the Person to be created, a {@code "nickname"} key that is mapped
   *     to a String that describes the name of the Person to be created, as well as a
   *     {@code "userId"} key that is mapped to the userId of the person to be created.
   *     A {@code "pronouns"} key that is mapped to a String describing the pronouns of the Person
   *     can optionally be added
   * @return the created Person
   * @throws IllegalStateException if no valid {@code "email"} key or 
   *     {@code "nickname"} key or {@code "userId"} key is defined
   */
  public static Person fromMap(Map personInfo) {
    Person.Builder personBuilder = Person.newBuilder();
    if (personInfo.containsKey(NICKNAME_FIELD_NAME)) {
      personBuilder.setNickname((String) personInfo.get(NICKNAME_FIELD_NAME));
    }
    if (personInfo.containsKey(EMAIL_FIELD_NAME)) {
      personBuilder.setEmail((String) personInfo.get(EMAIL_FIELD_NAME));
    }
    if (personInfo.containsKey(USER_ID_FIELD_NAME)) {
      personBuilder.setUserId((String) personInfo.get(USER_ID_FIELD_NAME));
    }
    if (personInfo.containsKey(PRONOUNS_FIELD_NAME)) {
      personBuilder.setPronouns((String) personInfo.get(PRONOUNS_FIELD_NAME));
    }
    return personBuilder.build();
  }

  public String getNickname() {
    return nickname;
  }

  public String getEmail() {
    return email;
  }

  /** Returns an optional because the pronouns may, or may not, exist. */
  public Optional<String> getPronouns() {
    return pronouns == null ? Optional.empty() : Optional.of(pronouns);
  }

  public String getUserId() {
    return userId;
  }

  public void setPronouns(String pronouns) {
    this.pronouns = pronouns;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public void setStorageHandler(StorageHandlerApi handler) {
    this.handler = handler;
  }

  /** Starts the building process of a new Person object. */
  public static Builder newBuilder() {
    return new Builder();
  }

  public String toString() {
    return String.format("email: %s, nickname: %s, pronouns: %s, userId: %s",
                         email, nickname, pronouns, userId);
  }

  @Override
  public void save() {
    List<Mutation> mutations = new ArrayList<>();
    Mutation.WriteBuilder personMutation = 
        Mutation.newInsertOrUpdateBuilder("Persons")
                .set("userId").to(userId)
                .set("email").to(email)
                .set("nickname").to(nickname);
    if (getPronouns().isPresent()) {
      personMutation.set("pronouns").to(pronouns);
    }

    mutations.add(personMutation.build());
    handler.writeMutations(mutations);
  }

  /** A builder class to create a Person object. */
  public static class Builder {
    private String email = null;
    private String nickname = null;
    private String pronouns = null;
    private String userId = null;
    private StorageHandlerApi handler = new StorageHandlerApi();

    public Builder setNickname(String nickname) {
      this.nickname = nickname;
      return this;
    }

    public Builder setEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder setUserId(String userId) {
      this.userId = userId;
      return this;
    }

    public Builder setPronouns(String pronouns) {
      this.pronouns = pronouns;
      return this;
    }

    public Builder setStorageHandler(StorageHandlerApi handler) {
      this.handler = handler;
      return this;
    }

    public Person build() {
      if (email == null) {
        throw new IllegalStateException("Person must be instantiated with a non-null email");
      }
      if (nickname == null) {
        throw new IllegalStateException("Person must be instantiated with a non-null nickname");
      }
      if (userId == null) {
        throw new IllegalStateException("Person must be instantiated with a non-null userId");
      }
      return new Person(this);
    }
  }
}
