/**
 * Communicates with the backend to load, update, and delete user profiles.
 */
export class ProfileBackendService {

  loadProfile(id: string): Promise<string> {
    /** TODO: Upon OAuth and GetProfile being implemented, fetch /get-profile. */
    const returnMessage = "TODO: Implement loadProfile upon OAuth being implemented.";
    return Promise.resolve(returnMessage);
  }

  /** TODO: Make sure you're updating person, not just adding them.  */
  async updateProfile(personAndChangeFieldsJson: string): Promise<string> {
    try {
      const response = await fetch('/update-person', {
        method: 'POST',
        body: personAndChangeFieldsJson,
      })
      return response.json();
    } catch(err) {
      /** TODO: Add and return different types of errors. */
      return Promise.resolve(personAndChangeFieldsJson);
    }
  }

  deleteProfile(id: string): Promise<boolean> {
    /** TODO: Upon /delete-profile being implemented, fetdch /delete-profile. */
    return Promise.resolve(false);
  }
}
