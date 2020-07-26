export interface PersonInterface {
  userId: string,
  nickname: string,
  email: string,
  pronouns?: string,
}

export interface BookInterface {
  bookId: string,
  title: string,
  author: string,
  isbn?: string,
}

export interface ClubInterface {
  clubId: string,
  ownerId: string,
  contentWarnings: string[],
  description: string,
  currentBook: BookInterface,
}

export interface BackendProfileServiceInterface {
  createPerson(person: PersonInterface): Promise<PersonInterface>,
  loadProfile(token: string): Promise<PersonInterface>,
  updateProfile(
      person: PersonInterface, token: string): Promise<PersonInterface>,
}

export interface BackendYourClubsServiceInterface {
  createClub(ClubInterface: string): Promise<ClubInterface>,
  listClubs(
    token: string,
    membership: "member" | "not member"): Promise<ClubInterface[]>,
  leaveClub(clubId: string, token: string): Promise<boolean>,
  joinClub(clubId: string, token: string): Promise<boolean>,
}

export interface BackendAuthenticationInterface {
  retrieveToken(code: string, redirectUri: string): Promise<string>,
}
