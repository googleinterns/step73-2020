// React Framework imports
import * as React from "react";
import { hot } from "react-hot-loader";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import clsx from 'clsx';

// Material-UI Imports
import CssBaseline from '@material-ui/core/CssBaseline';

// Component Imports
import AppBar from "./appbar/AppBar";
import Drawer from "./drawer/Drawer";

// Page Imports
import { Explore } from "./pages/explore/Explore";
import { Login } from "./pages/login/Login"; 
import { Profile } from "./pages/profile/Profile";
import { YourClubs } from "./pages/your_clubs/YourClubs";

interface AppProps {}

interface AppState {
  open: boolean;
}

class App extends React.Component<AppProps, AppState> {

  constructor(props) {
    super(props);
    
    this.state = {
      open: false
    };
  }

  // Handles opening drawer in AppBar and Drawer components
  handleDrawerOpen = () => {
    this.setState({open: true});
  };

  // Handles closing drawer in AppBar and Drawer components
  handleDrawerClose = () => {
    this.setState({open: false});
  };

  public render() {
    return (
      <BrowserRouter>
        <CssBaseline />
        <AppBar 
          open={this.state.open}
          handleDrawerOpen={this.handleDrawerOpen.bind(this)}
          handleDrawerClose={this.handleDrawerClose.bind(this)}
        />
        <Drawer 
          open={this.state.open}
          handleDrawerOpen={this.handleDrawerOpen.bind(this)}
          handleDrawerClose={this.handleDrawerClose.bind(this)}
        />
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