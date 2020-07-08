import * as React from "react";
import { MockProfileBackendService } from "../../services/mock_profile_backend";
import { ProfileHandlerService } from "../../services/profile_handler_service";

const NUM_PROFILES = 10;

export const MockBackendService = new MockProfileBackendService(NUM_PROFILES);

export const ServiceHandlers = {
  profileHandlerService: new ProfileHandlerService(MockBackendService),
}

export const ServiceContext = React.createContext(ServiceHandlers);
