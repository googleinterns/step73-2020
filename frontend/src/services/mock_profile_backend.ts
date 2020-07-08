import { pickRandom, EMAILS, NICKNAMES, PRONOUNS } from "../util/random_data";

export interface PersonProps {
  email: string;
  nickname: string;
  pronouns?: string; 
  userId: string;
}

/** Error thrown when an invalid user ID is queried */
export class InvalidUserIdError extends Error {}

/**
 * Mimics the functionality of a Java Servlet that fetches user information 
 * from a database based on a specified ID. 
 */
export class ProfileBackendService {
  private readonly mockProfiles: PersonProps[] = [];
  private numProfiles = 0;

  constructor(profileCount: number) {
    for (let i = 0; i < profileCount; i++) {
      this.mockProfiles.push({
        email: pickRandom(EMAILS),
        nickname: pickRandom(NICKNAMES),
        pronouns: pickRandom(PRONOUNS),
        userId: `user_${i}`,
      });
    }
    this.numProfiles = profileCount;
  }

  loadProfile(id: string): Promise<PersonProps> {
    for (let i = 0; i < this.numProfiles; i++) {
      if (id === this.mockProfiles.get(i).userId) {
        return Promise.resolve(this.mockProfiles.get(i));
      }
    }
    throw new InvalidUserIdError();
  }

  delete(id: string): Promise<boolean> {
    for (let i = 0; i < this.numProfiles; i++) {
      if (id === this.mockProfiles.get(i).userId) {
        return Promise.resolve(true);
      }
    }
    return Promise.resolve(false);
  }
}