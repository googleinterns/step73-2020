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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Person}.
 */
@RunWith(JUnit4.class)
public final class PersonTest {
  private final String NICKNAME = "Arthi";
  private final String ALT_NICKNAME = "New Name";
  private final String EMAIL = "test@fake.fake";
  private final String ALT_EMAIL = "New Email";
  private final String PRONOUNS = "she/her";
  private final String ALT_PRONOUNS = "New Pronouns";
  private final String IDENTIFICATION_STRING = "predetermined-identification-string";
  private Person.Builder personInProgress = Person.newBuilder(EMAIL, NICKNAME);
  
  @Mock private IdentifierGenerator idGen;

  @Before
  public void beforeTest() {
    idGen = Mockito.mock(IdentifierGenerator.class);
    Mockito.when(idGen.generateId()).thenReturn(IDENTIFICATION_STRING);
  }

  @Test
  public void getExistingNickname() {
    Person p = personInProgress.build();
    Assert.assertEquals(NICKNAME, p.getNickname());
  }

  @Test
  public void getExistingEmail() {
    Person p = personInProgress.build();
    Assert.assertEquals(EMAIL, p.getEmail());
  }

  @Test
  public void getExistingUserId() {
    Person p = personInProgress.setIdGenerator(idGen).build();
    Assert.assertEquals(IDENTIFICATION_STRING, p.getUserId());
  }

  @Test
  public void getExistingPronouns() {
    Person p = personInProgress.setPronouns(PRONOUNS).build();
    Assert.assertTrue(p.getPronouns().isPresent());
    Assert.assertEquals(PRONOUNS, p.getPronouns().get());
  }

  @Test
  public void getNoPronouns() {
    Person p = personInProgress.build();
    Assert.assertFalse(p.getPronouns().isPresent());
  }

  @Test
  public void setNewNickname() {
    Person p = personInProgress.build();
    p.setNickname(ALT_NICKNAME);
    Assert.assertEquals(ALT_NICKNAME, p.getNickname());
  }

  @Test
  public void setNewEmail() {
    Person p = personInProgress.build();
    p.setEmail(ALT_EMAIL);
    Assert.assertEquals(ALT_EMAIL, p.getEmail());
  }

  @Test
  public void setNewPronouns() {
    Person p = personInProgress.build();
    p.setPronouns(ALT_PRONOUNS);
    Assert.assertTrue(p.getPronouns().isPresent());
    Assert.assertEquals(ALT_PRONOUNS, p.getPronouns().get());
  }

  // TODO: test saving @linamontes10
}
