import * as LayoutConstants from "../layout/layout_constants"
import * as React from "react";
import AccountCircleIcon from "@material-ui/icons/AccountCircle";
import AppBar from "@material-ui/core/AppBar";
import clsx from "clsx";
import { createStyles } from "@material-ui/core/styles";
import IconButton from "@material-ui/core/IconButton";
import { makeStyles } from "@material-ui/core/styles";
import MenuIcon from "@material-ui/icons/Menu";
import { Theme } from "@material-ui/core/styles";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import { useTheme } from "@material-ui/core/styles";

const drawerWidth = LayoutConstants.DRAWER_WIDTH;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
    appBar: {
      transition: theme.transitions.create(['margin', 'width'], {
        duration: theme.transitions.duration.leavingScreen,
        easing: theme.transitions.easing.sharp,
      }),
    },
    appBarShift: {
      marginLeft: drawerWidth,
      transition: theme.transitions.create(['margin', 'width'], {
        duration: theme.transitions.duration.enteringScreen,
        easing: theme.transitions.easing.easeOut,
      }),
      width: `calc(100% - ${drawerWidth}px)`,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    hide: {
      display: 'none',
    },
    content: {
      flexGrow: 1,
      padding: theme.spacing(3),
      transition: theme.transitions.create('margin', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
      marginLeft: -drawerWidth,
    },
    contentShift: {
      marginLeft: 0,
      transition: theme.transitions.create('margin', {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
    },
    accountIcon: {
      marginLeft: "auto"
    }
  }),
);

interface AppBarProps {
  navigationDrawerOpen: boolean;  // whether or not Drawer comp is displayed
  handleDrawerOpen(): void;  // sets open to true
}

export default function AppBarComp(props: AppBarProps) {
  const classes = useStyles();
  const theme = useTheme();

  return (
    <div className={classes.root}>
      <AppBar
        position="fixed"
        className={clsx(classes.appBar, {
          [classes.appBarShift]: props.navigationDrawerOpen,
        })}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="Show drawer with navigation links"
            onClick={props.handleDrawerOpen}
            edge="start"
            className={clsx(classes.menuButton, 
                            props.navigationDrawerOpen && classes.hide)}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap>
            CoffeeHouse
          </Typography>
          <IconButton
            aria-label="User's profile avatar"
            color="inherit"
            className={classes.accountIcon}
          >
            <AccountCircleIcon fontSize="large"/>
          </IconButton>
        </Toolbar>
      </AppBar>
    </div>
  );
}
