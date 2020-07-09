import { EMAILS } from "../utils/random_data";
import { NICKNAMES } from "../utils/random_data";
import { PRONOUNS } from "../utils/random_data"
import { pickRandom } from "../utils/random_data";

export interface PersonProps {
  email: string;
  nickname: string;
  pronouns?: string; 
  userId: string;
}

/** Error thrown when an invalid user ID is queried. */
export class InvalidUserIdError extends Error {}

/**
 * Mimics the functionality of a Java Servlet that fetches user information 
 * from a database based on a specified ID. 
 */
export class MockProfileBackendService {
  private mockProfiles: PersonProps[] = [];

  constructor(private readonly numProfiles: number) {
    for (let i = 0; i < numProfiles; i++) {
      this.mockProfiles.push({
        email: pickRandom(EMAILS),
        nickname: pickRandom(NICKNAMES),
        pronouns: pickRandom(PRONOUNS),
        userId: `user_${i}`,
      });
    }
  }
  
  loadPerson(id: string): Promise<PersonProps> {
    const matched = this.mockProfiles.find((mockProfile) => mockProfile.userId === id);
    if (matched) { 
      return Promise.resolve(matched); 
    }
    throw new InvalidUserIdError();
  }

  /** TODO: Make sure you're updating person, not just adding them  */
  updatePerson(person: PersonProps): Promise<boolean> {
    const id = person.userId;
    for (let i = 0; i < this.numProfiles; i++) {
      if (id === this.mockProfiles[i].userId) {
        this.mockProfiles[i].email = person.email;
        this.mockProfiles[i].nickname = person.nickname;
        this.mockProfiles[i].pronouns = person.pronouns;
        return Promise.resolve(true);
      }
    }
    return Promise.resolve(false);
  }

  deletePerson(id: string): Promise<boolean> {
    for (let i = 0; i < this.numProfiles; i++) {
      if (id === this.mockProfiles[i].userId) {
        return Promise.resolve(true);
      }
    }
    return Promise.resolve(false);
  }
}
