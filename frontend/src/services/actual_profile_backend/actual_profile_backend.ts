/**
 * Communicates with update 
 */
export class ProfileBackendService {
  
  loadProfile(id: string): Promise<string> {
    /** TODO: Upon OAuth and GetProfile being implemented, fetch /get-profile. */
    const returnMessage = "TODO: Implement loadProfile upon OAuth being implemented.";
    return Promise.resolve(returnMessage);
  }

  /** TODO: Make sure you're updating person, not just adding them  */
  async updateProfile(personAndChangeFieldsJson: string): Promise<string> {
    try {
      const newPerson = await fetch('/update-person', {
        method: 'POST',
        body: personAndChangeFieldsJson,
      })
      const newPersonJson = JSON.stringify(newPerson);
      return newPersonJson;
    } catch(err) {
      /** TODO: Add and return different types of errors */
      return Promise.resolve(personAndChangeFieldsJson);
    }
  }

  deleteProfile(id: string): Promise<boolean> {
    /** TODO: Upon /delete-profile being implemented, fetdch /delete-profile. */
    return Promise.resolve(false);
  }
}
