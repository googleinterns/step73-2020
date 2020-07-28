import { BackendProfileServiceInterface, PersonInterface } from "../backend_service_interface/backend_service_interface";

/** Error that occurs when profile does not exist. */
export class NonExistentProfileError extends Error {
  constructor() {
    super("Profile does not exist");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, NonExistentProfileError.prototype);
  }
}

/** Error that occurs if profile was unable to be updated. */
export class FailureToUpdateProfileError extends Error {
  constructor() {
    super("Unable to update Profile");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToUpdateProfileError.prototype);
  }
}

/** Error that occurs if Id was unable to be created. */
export class FailureToCreatePersonError extends Error {
  constructor() {
    super("Unable to create Person");

    // Explicitly set prototype to allow expect().toThrow() testing.
    Object.setPrototypeOf(this, FailureToCreatePersonError.prototype);
  }
}

/**
 * Communicates with backend to retrieve, update, and create profiles.
 */
export class ProfileHandlerService {
  /** Backend is responsible for holding all profile information. */
  constructor(private readonly backend: BackendProfileServiceInterface) {};

  /**
   * Retrieves a profile from the backend given an ID token.
   * @param token the ID token for the profile to be retrieved
   * @return the person object retrieved from the backend
   * @throws NonExistentProfileError if profile doesn't exist
   */
  async getPerson(token: string): Promise<PersonInterface> {
    try {
      return await this.backend.loadProfile(token);
    } catch (err) {
      throw new NonExistentProfileError();
    }
  }

  /**
   * Updates a profile given the updated person and their ID token.
   * @param person the updated person object
   * @param token the ID token for the person to be updated
   * @return the person object updated from the backend
   * @throws FailureToUpdateProfileError if failure updating profile occurred
   */
  async updatePerson(
      person: PersonInterface, token: string): Promise<PersonInterface> {
    try {
      return await this.backend.updateProfile(person, token);
    } catch (err) {
      throw new FailureToUpdateProfileError();
    }
  }

  /**
   * Creates a person on the backend given the person to create.
   * @param person the person object to create
   * @return the person object created
   * @throws FailureToCreatePersonError if person was unable to be created
   */
  async createPerson(person: PersonInterface): Promise<PersonInterface> {
    try {
      return await this.backend.createPerson(person);
    } catch (err) {
      throw new FailureToCreatePersonError();
    }
  }
}
