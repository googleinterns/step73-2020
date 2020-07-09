import * as React from "react";
import * as ReactDOM from "react-dom";
import * as TestUtils from "react-dom/test-utils";
import Profile from "./Profile";
import { ProfileHandlerService } from "../../../services/profile_handler_service";

export const USER_ID = "user_0";

const TEST_USER_1 = {
  nickname: "user_0",
  pronouns: "she/her", 
  email: "email1@gmail.com", 
  userId: "1",
};

const TEST_USER_2 = {
  nickname: "user_3", 
  pronouns: "they/them", 
  email: "email2@gmail.com", 
  userId: "2",
};

const TEST_USER_3 = {
  nickname: "user_7", 
  pronouns: "he/him", 
  email: "email3@gmail.com", 
  userId: "3", 
}
