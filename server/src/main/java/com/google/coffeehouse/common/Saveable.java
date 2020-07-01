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

/**
 * This interface is to be implemented by all objects that can be saved in the database.
 */
public interface Saveable {

  /**
   * The save method will save the object in the database.
   * 
   * <p>The object can not be in an invalid state when saving, otherwise retrieval of the 
   * object will retrieve an invalid object. Any changes made to the object will not persist
   * unless the object saves itself after modification.
   */
  public void save();
}
