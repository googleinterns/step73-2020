export interface PersonInterface {
  userId: string,
  nickname: string,
  email: string,
  pronouns?: string,
}

export interface IBook {
  bookId: string,
  title: string,
  author: string,
  isbn?: string,
}

export interface IClub {
  clubId: string,
  ownerId: string,
  contentWarnings: string[],
  description: string,
  currentBook: IBook,
}

export interface BackendProfileServiceInterface {
  createPerson(person: PersonInterface): Promise<PersonInterface>,
  loadProfile(token: string): Promise<PersonInterface>,
  updateProfile(
      person: PersonInterface, token: string): Promise<PersonInterface>,
}

export interface BackendYourClubsServiceInterface {
  createClub(clubJson: string): Promise<boolean>,
  listClubs(numClubs: number): Promise<string>,
  leaveClub(clubId: string): Promise<boolean>,
}

export interface BackendAuthenticationInterface {
  retrieveToken(code: string, redirectUri: string): Promise<string>,
}
