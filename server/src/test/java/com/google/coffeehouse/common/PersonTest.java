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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.coffeehouse.util.IdentifierGenerator;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Unit tests for {@link Person}.
 */
@RunWith(JUnit4.class)
public final class PersonTest {
  private static final String NICKNAME = "Arthi";
  private static final String ALT_NICKNAME = "New Name";
  private static final String EMAIL = "test@fake.fake";
  private static final String ALT_EMAIL = "New Email";
  private static final String PRONOUNS = "she/her";
  private static final String ALT_PRONOUNS = "New Pronouns";
  private static final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private Person.Builder personBuilder;
  private Map personInfo;
  
  @Mock private IdentifierGenerator idGen;

  @Before
  public void setUp() {
    personInfo = new HashMap<String, String>();

    idGen = mock(IdentifierGenerator.class);
    when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);

    personBuilder = Person.newBuilder(EMAIL, NICKNAME);
  }

  @Test
  public void getNickname_exists() {
    Person p = personBuilder.build();
    assertEquals(NICKNAME, p.getNickname());
  }

  @Test
  public void getEmail_exists() {
    Person p = personBuilder.build();
    assertEquals(EMAIL, p.getEmail());
  }

  @Test
  public void getUserId_exists() {
    Person p = personBuilder.setIdGenerator(idGen).build();
    assertEquals(IDENTIFICATION_STRING, p.getUserId());
  }

  @Test
  public void getPronouns_exists() {
    Person p = personBuilder.setPronouns(PRONOUNS).build();
    assertTrue(p.getPronouns().isPresent());
    assertEquals(PRONOUNS, p.getPronouns().get());
  }

  @Test
  public void getPronouns_notExist() {
    Person p = personBuilder.build();
    assertFalse(p.getPronouns().isPresent());
  }

  @Test
  public void setNickname() {
    Person p = personBuilder.build();
    p.setNickname(ALT_NICKNAME);
    assertEquals(ALT_NICKNAME, p.getNickname());
  }

  @Test
  public void setEmail() {
    Person p = personBuilder.build();
    p.setEmail(ALT_EMAIL);
    assertEquals(ALT_EMAIL, p.getEmail());
  }

  @Test
  public void setPronouns() {
    Person p = personBuilder.build();
    p.setPronouns(ALT_PRONOUNS);
    assertTrue(p.getPronouns().isPresent());
    assertEquals(ALT_PRONOUNS, p.getPronouns().get());
  }

  @Test 
  public void fromMap_invalidInput() {
    personInfo.put(Person.NICKNAME_FIELD_NAME, NICKNAME);
    assertThrows(IllegalArgumentException.class, () -> {
        Person.fromMap(personInfo);
    });
  }

  @Test 
  public void fromMap_minimumValidInput() {
    personInfo.put(Person.NICKNAME_FIELD_NAME, NICKNAME);
    personInfo.put(Person.EMAIL_FIELD_NAME, EMAIL);
    Person p = Person.fromMap(personInfo);
    assertEquals(NICKNAME, p.getNickname());
    assertEquals(EMAIL, p.getEmail());
    assertFalse(p.getPronouns().isPresent());
  }

  @Test 
  public void fromMap_maximumValidInput() {
    personInfo.put(Person.NICKNAME_FIELD_NAME, NICKNAME);
    personInfo.put(Person.EMAIL_FIELD_NAME, EMAIL);
    personInfo.put(Person.PRONOUNS_FIELD_NAME, PRONOUNS);
    Person p = Person.fromMap(personInfo);
    assertEquals(NICKNAME, p.getNickname());
    assertEquals(EMAIL, p.getEmail());
    assertTrue(p.getPronouns().isPresent());
    assertEquals(PRONOUNS, p.getPronouns().get());
  }

  // TODO: test saving @linamontes10
}
