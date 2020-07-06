import * as PageConstants from "./pages/page_constants";
import * as React from "react";
import AppBar from "./appbar/AppBar";
import { BrowserRouter } from "react-router-dom";
import clsx from 'clsx';
import CssBaseline from '@material-ui/core/CssBaseline';
import Drawer from "./drawer/Drawer";
import { Explore } from "./pages/explore/Explore";
import { hot } from "react-hot-loader";
import { Login } from "./pages/login/Login"; 
import { Profile } from "./pages/profile/Profile";
import { Route } from "react-router-dom";
import { Switch } from "react-router-dom";
import { YourClubs } from "./pages/your_clubs/YourClubs";

interface AppProps {}

interface AppState {
  /** @state determines if drawer is shown or not*/ open: boolean;
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
            <Route exact path={PageConstants.URL_EXPLORE} component={Explore} />
            <Route exact path={PageConstants.URL_LOGIN} component={Login} />
            <Route exact path={PageConstants.URL_PROFILE} component={Profile} />
            <Route exact path={PageConstants.URL_YOUR_CLUBS} component={YourClubs} />
          </Switch>
        </main>
      </BrowserRouter>
    );
  }
}

declare let module: object;
export default hot(module)(App);
