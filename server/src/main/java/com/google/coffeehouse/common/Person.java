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
import java.util.Optional; 

/**
 * Person encapsulates the information associated with a user who has an account.
 * 
 * <p>It implementes the {@link Saveable} interface because it will be able to save itself
 * in the database.
 */
public class Person implements Saveable {
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
