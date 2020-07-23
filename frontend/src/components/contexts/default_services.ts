import { AuthenticationHandlerService } from "../../services/authentication_handler_service/authentication_handler_service";
import { AuthenticationBackendService } from "../../services/backend/authentication_backend";
import { MockProfileBackendService } from "../../services/mock_backend/mock_profile_backend";
import { MockYourClubsBackendService } from "../../services/mock_backend/mock_your_clubs_backend";
import { NUM_PROFILES } from "../../utils/temporary_testing_consts";
import { ProfileHandlerService } from "../../services/profile_handler_service/profile_handler_service";
import { YourClubsHandlerService } from "../../services/your_clubs_handler_service/your_clubs_handler_service";

/** TODO: Change to real backend service implementation. */
export const mockProfileBackendService = new MockProfileBackendService(NUM_PROFILES);
export const mockYourClubsBackendService = new MockYourClubsBackendService();
export const actualAuthenticationBackendService = new AuthenticationBackendService();

export const defaultServices = {
  profileHandlerService: new ProfileHandlerService(mockProfileBackendService),
  yourClubsHandlerService: new YourClubsHandlerService(mockYourClubsBackendService),
  authenticationHandlerService: new AuthenticationHandlerService(actualAuthenticationBackendService),
}
