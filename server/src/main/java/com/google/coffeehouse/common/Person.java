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
 * Person encapsulates the information associated with a user who has an account.
 * 
 * <p>It implements the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Person implements Saveable {
  /** The names of each possible field in the map in fromMap */
  public static final String NICKNAME_FIELD_NAME = "nickname";
  public static final String EMAIL_FIELD_NAME = "email";
  public static final String PRONOUNS_FIELD_NAME = "pronouns";

  /** The message on the IllegalArgumentException when there is no valid name or book key */
  public static final String NO_VALID_NICKNAME_EMAIL_FROM_MAP = 
      "No valid \"" + NICKNAME_FIELD_NAME + "\" or \"" + EMAIL_FIELD_NAME + "\" key defined.";
  
  private String nickname;
  private String email;
  private String pronouns;
  private String userId;

  private Person(Builder builder) {
    this.nickname = builder.nickname;
    this.email = builder.email;
    this.pronouns = builder.pronouns;
    this.userId = builder.idGenerator.generateId();
  }

  /** Overloaded static factory, calls other fromMap with null IdentifierGenerator. */
  public static Person fromMap(Map personInfo) {
    return Person.fromMap(personInfo, null);
  }

  /** 
   * Creates a {@link Person} object from a Map with the relevant parameters as keys.
   * @param personInfo the Map that contains the information used to construct the Person. At a
   *     minimum this includes an {@code "email"} key that is mapped to a String that describes
   *     the email of the Person to be created, as well as a {@code "nickname"} key that is mapped
   *     to a String that describes the name of the Person to be created. A {@code "pronouns"} key
   *     that is mapped to a String describing the pronouns of the Person can optionally be added.
   * @param idGen the {@link IdentifierGenerator} used when constructing the Person. If
   *     null, the default generator will be used
   * @return the created Person
   * @throws IllegalArgumentException if no valid {@code "email"} key or 
   *     {@code "nickname"} key is defined
   */
  public static Person fromMap(Map personInfo, IdentifierGenerator idGen) {
    String email = (String) personInfo.getOrDefault(EMAIL_FIELD_NAME, null);
    String nickname = (String) personInfo.getOrDefault(NICKNAME_FIELD_NAME, null);
    if (email == null || nickname == null) {
      throw new IllegalArgumentException(NO_VALID_NICKNAME_EMAIL_FROM_MAP);
    }

    Person.Builder personBuilder = Person.newBuilder(email, nickname);
    if (personInfo.containsKey(PRONOUNS_FIELD_NAME)) {
      personBuilder.setPronouns((String) personInfo.get(PRONOUNS_FIELD_NAME));
    }
    if (idGen != null) {
      personBuilder.setIdGenerator(idGen);
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

  /** Starts the building process of a new Person object. */
  public static Builder newBuilder(String email, String nickname) {
    return new Builder(email, nickname);
  }

  public String toString() {
    return String.format("email: %s, nickname: %s, pronouns: %s, userId: %s",
                         email, nickname, pronouns, userId);
  }

  @Override
  public void save() {
    // TODO: implement saving @linamontes10
  }

  /** A builder class to create a Person object. */
  public static class Builder {
    private String email;
    private String nickname;
    private String pronouns = null;
    private IdentifierGenerator idGenerator = new UuidWrapper();

    private Builder(String email, String nickname) {
      this.email = email;
      this.nickname = nickname;
    }

    public Builder setPronouns(String pronouns) {
      this.pronouns = pronouns;
      return this;
    }

    /**
     * Used for injecting a non-default {@link IdentifierGenerator} to generate the {@code userId}.
     * @param idGenerator the object that generates the ID for the user
     * @return the builder that was modified by this method
     */
    public Builder setIdGenerator(IdentifierGenerator idGenerator) {
      this.idGenerator = idGenerator;
      return this;
    }

    public Person build() {
      return new Person(this);
    }
  }
}
