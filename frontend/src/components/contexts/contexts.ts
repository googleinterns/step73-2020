import * as React from "react";
import { MockProfileBackendService } from "../../services/mock_profile_backend";
import { ProfileHandlerService } from "../../services/profile_handler_service";

const NUM_PROFILES = 10;

/** TODO: Change to real backend service implementation */
export const mockBackendService = new MockProfileBackendService(NUM_PROFILES);
export const defaultServices = { 
  profileHandlerService: new ProfileHandlerService(mockBackendService),
}
export const ServiceContext = React.createContext(defaultServices);
