import * as PageConstants from "./pages/page_constants";
import * as React from "react";
import AppBar from "./appbar/AppBar";
import { BrowserRouter, Redirect, Route, Switch } from "react-router-dom";
import clsx from "clsx";
import CssBaseline from "@material-ui/core/CssBaseline";
import { defaultServices } from "./contexts/default_services";
import Drawer from "./drawer/Drawer";
import { Explore } from "./pages/explore/Explore";
import { hot } from "react-hot-loader";
import { Login } from "./pages/login/Login";
import Profile from "./pages/profile/Profile";
import { ServiceContext, UserLoginStatusContext } from "./contexts/contexts";
import { userLoginServices } from "./contexts/user_login_services";
import { withStyles } from "@material-ui/core/styles";
import { YourClubs } from "./pages/your_clubs/YourClubs";

function App() {
  // Construct dependencies to determine status of user login.
  const userLoginStatusService = userLoginServices.loginStatusHandlerService;
  const [navigationDrawerOpen, setNavigationDrawerOpen] = React.useState<boolean>(false);
  const [userLoggedIn, setUserLoggedIn] = React.useState<boolean|undefined>(undefined);
  
  const handleDrawerOpen = () => {
    setNavigationDrawerOpen(true);
  };

  const handleDrawerClose = () => {
    setNavigationDrawerOpen(false);
  };

  const handleUserLogin = () => {
    setUserLoggedIn(true);
  };

  const handleUserLogout = () => {
    setUserLoggedIn(false);
  };

  React.useEffect(() => {
    setUserLoggedIn(userLoginStatusService.getUserLoginStatus());
    console.log(userLoginStatusService.getUserLoginStatus());
  }, [userLoginStatusService.getUserLoginStatus()]);

  return (userLoggedIn
    ? <UserLoggedIn 
        handleDrawerOpen={handleDrawerOpen}
        handleDrawerClose={handleDrawerClose}
        navigationDrawerOpen={navigationDrawerOpen}
      />
    : <UserNotLoggedIn handleUserLogin={handleUserLogin} />
  );
}

interface UserLoggedInProps {
  handleDrawerOpen(): void,
  handleDrawerClose(): void, 
  navigationDrawerOpen: boolean,
}

function UserLoggedIn(props: UserLoggedInProps) {
  return (
    <BrowserRouter>
      <UserLoginStatusContext.Provider value={userLoginServices}>
        <ServiceContext.Provider value={defaultServices}>
          <CssBaseline />
          <AppBar
            navigationDrawerOpen={props.navigationDrawerOpen}
            handleDrawerOpen={props.handleDrawerOpen}
          />
          <Drawer
            navigationDrawerOpen={props.navigationDrawerOpen}
            handleDrawerClose={props.handleDrawerClose}
          />
          <main>
            <Switch>
              <Route exact path={PageConstants.URL_EXPLORE} component={Explore} />
              <Route exact path={PageConstants.URL_PROFILE} component={Profile} />
              <Route exact path={PageConstants.URL_YOUR_CLUBS} component={YourClubs} />
            </Switch>
          </main>
        </ServiceContext.Provider>
      </UserLoginStatusContext.Provider>
    </BrowserRouter>
  );
}

interface UserNotLoggedInProps {
  handleUserLogin(): void,
}

function UserNotLoggedIn(props: UserNotLoggedInProps) {
  return (
    <BrowserRouter>
      <UserLoginStatusContext.Provider value={userLoginServices}>
        <ServiceContext.Provider value={defaultServices}>
          <Route 
            exact path={PageConstants.URL_LOGIN}
            component={()=><Login handleUserLogin={props.handleUserLogin} />}
          />
          <Redirect to={PageConstants.URL_LOGIN} />
        </ServiceContext.Provider>
      </UserLoginStatusContext.Provider>
    </BrowserRouter>
  );
}

declare let module: object;
export default hot(module)(App);
