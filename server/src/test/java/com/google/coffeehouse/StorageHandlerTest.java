// Copyright 2019 Google LLC
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

package com.google.coffeehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
/** */
@RunWith(JUnit4.class)
public final class StorageHandlerTest {

  private String personWithPronounsId = "linamontes10";
  private String personWithNullPronounsId = "test";
  private String personWithEmptyPronounsId = "test-pronouns";
  private ByteArrayOutputStream bout;
  private PrintStream stdOut = System.out;
  private PrintStream out;
  @Before
  public void setUp() {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);
  }

  @After
  public void tearDown() {
    System.setOut(stdOut);
  }

  @Test
  public void testGetPersonWithPronounsQuery() throws Exception {
    StorageHandler.main(personWithPronounsId);
    String actual = bout.toString();
    String expected = "User ID: linamontes10 || Email: linamontes@google.com || Nickname: lina || Pronouns: she/her/ella\n";
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testGetPersonWithNullPronounsQuery() throws Exception {
    StorageHandler.main(personWithNullPronounsId);
    String actual = bout.toString();
    String expected = "User ID: test || Email: testNoPronouns@gmail.com || Nickname: test no pronouns || No pronouns\n";
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testGetPersonWithEmptyPronounsQuery() throws Exception {
    StorageHandler.main(personWithEmptyPronounsId);
    String actual = bout.toString();
    String expected = "User ID: test-pronouns || Email: testWithEmptyStringPronouns@gmail.com || Nickname: empty pronouns || No pronouns\n";
    Assert.assertEquals(expected, actual);
  }
}
