import * as React from "react";
import { hot } from "react-hot-loader";

// Component Imports
import PersistentDrawerLeft from "./drawer/Drawer"

class App extends React.Component<{}, undefined> {
    public render() {
        return (
          <PersistentDrawerLeft />
        );
    }
}

declare let module: object;

export default hot(module)(App);
