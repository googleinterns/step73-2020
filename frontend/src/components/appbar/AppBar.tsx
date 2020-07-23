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
import { UserLoginStatusContext } from "../contexts/contexts";
import { useTheme } from "@material-ui/core/styles";

const drawerWidth = LayoutConstants.DRAWER_WIDTH;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    accountIcon: {
      marginLeft: "auto",
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
    avatarImg: {
      borderRadius: '50%',
      height: '32px',
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
    hide: {
      display: 'none',
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
  }),
);

interface AppBarProps {
  navigationDrawerOpen: boolean;  // whether or not Drawer comp is displayed
  handleDrawerOpen(): void;
}

export default function AppBarComp(props: AppBarProps) {
  const classes = useStyles();
  const theme = useTheme();

  return (
    <>
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
            className={classes.accountIcon}
            color="inherit"
          >
            <UserAvatarImage />
          </IconButton>
        </Toolbar>
      </AppBar>
    </>
  );
}

/**
 * Extracts the user profile image contained in the OAuth token.
 * If the user token is not defined, returns a stock account icon.
 */
export function UserAvatarImage() {
  const classes = useStyles();

  const contextServices = React.useContext(UserLoginStatusContext);
  const loginStatusHandlerService = contextServices.loginStatusHandlerService;

  const token = loginStatusHandlerService.getUserToken();
  if (token) {
    const parsedToken = JSON.parse(atob(token.split('.')[1]));
    const profileImg = parsedToken.picture;
    return (profileImg
      ? <img src={profileImg} className={classes.avatarImg} />
      : <AccountCircleIcon />
    );
  } else {
    return <AccountCircleIcon />
  }
}
