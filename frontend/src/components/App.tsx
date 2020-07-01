// React Framework imports
import * as React from "react";
import { hot } from "react-hot-loader";
import { BrowserRouter, Route, Switch, Link} from "react-router-dom";
import clsx from 'clsx';

// Material-UI Imports
import { makeStyles, useTheme, Theme, createStyles } from '@material-ui/core/styles';

// Component Imports
import AppBar from "./appbar/AppBar";

// Page Imports
import { Explore } from "./pages/explore/Explore";
import { Login } from "./pages/login/Login"; 
import { Profile } from "./pages/profile/Profile";
import { YourClubs } from "./pages/your_clubs/YourClubs";

const drawerWidth = 240;

class App extends React.Component<{}, undefined> {

  public render() {
    return (
      <BrowserRouter>
        <AppBar />
        <main>
          <Switch>
            <Route exact path="/Login" component={Login} />
            <Route exact path="/YourClubs" component={YourClubs} />
            <Route exact path="/Explore" component={Explore} />
            <Route exact path="/Profile" component={Profile} />
          </Switch>
        </main>
      </BrowserRouter>
    );
  }
}

declare let module: object;
export default hot(module)(App);