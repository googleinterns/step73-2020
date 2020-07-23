import * as LayoutConstants from "../layout/layout_constants"
import * as React from "react";
import { Link } from "react-router-dom"
import Button from "@material-ui/core/Button";
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import clsx from 'clsx';
import { createStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import Drawer from '@material-ui/core/Drawer';
import ExploreIcon from '@material-ui/icons/Explore';
import FaceIcon from '@material-ui/icons/Face';
import IconButton from "@material-ui/core/IconButton";
import LibraryBooksIcon from "@material-ui/icons/LibraryBooks";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import { makeStyles } from "@material-ui/core/styles";
import MeetingRoomIcon from "@material-ui/icons/MeetingRoom";
import { Theme} from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import { ServiceContext, UserLoginStatusContext } from "../contexts/contexts";
import { useTheme } from "@material-ui/core/styles";

const drawerWidth = LayoutConstants.DRAWER_WIDTH;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
    button: {
      marginLeft: theme.spacing(3),
      marginTop: theme.spacing(1),
      width: '75%',
    },
    drawer: {
      flexShrink: 0,
      width: drawerWidth,
    },
    drawerPaper: {
      width: drawerWidth,
    },
    drawerHeader: {
      alignItems: 'center',
      display: 'flex',
      justifyContent: 'flex-end',
      padding: theme.spacing(0, 1),
      // necessary for content to be below app bar
      ...theme.mixins.toolbar
    },
    link: {
      color: theme.palette.text.primary,
      textDecoration: 'none'
    },
  }),
);

interface DrawerProps {
  navigationDrawerOpen: boolean;  // whether or not Drawer comp is displayed
  handleDrawerClose(): void;
  handleUserSignOut(): void;
}

export default function DrawerComp(props: DrawerProps) {
  const classes = useStyles();
  const theme = useTheme();

  const contextServices = React.useContext(ServiceContext);
  const userLoginStatusService = React.useContext(UserLoginStatusContext);

  const [displaySignOutWindow, setDisplaySignOutWindow] = React.useState<boolean>(false);

  // Associates each URL with a text representation and icon
  const pagesList = [
    {text: "Your Clubs", url: "/YourClubs", icon: <LibraryBooksIcon />},
    {text: "Explore", url: "/Explore", icon: <ExploreIcon />},
    {text: "Profile", url: "/Profile", icon: <FaceIcon />}
  ];

  const handleOpenSignOutWindow = () => {
    setDisplaySignOutWindow(true);
  }

  const handleCloseSignOutWindow = () => {
    setDisplaySignOutWindow(false);
  }

  const handleSignOut = async () => {
    const authenticationHandlerService = contextServices.authenticationHandlerService;
    const loginStatusHandlerService = userLoginStatusService.loginStatusHandlerService;

    loginStatusHandlerService.setUserLoginStatus(false);

    const success = await authenticationHandlerService.signOut();
    if (success) {
      setDisplaySignOutWindow(false);
      props.handleDrawerClose();
      props.handleUserSignOut();
    }
  }

  return (
    <div>
      <Drawer
        className={classes.drawer}
        variant="persistent"
        anchor="left"
        open={props.navigationDrawerOpen}
        classes={{
          paper: classes.drawerPaper,
        }}
      >
        <div className={classes.drawerHeader}>
          <IconButton onClick={props.handleDrawerClose}>
            {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
          </IconButton>
        </div>
        <List>
          {pagesList.map((item, index) => (
            <Link to={item.url} className={classes.link} key={item.url}>
              <ListItem button key={item.text}>
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItem>
            </Link>
          ))}
        </List>
        <Divider />
        <Button
          className={classes.button}
          color="primary"
          endIcon={<MeetingRoomIcon />}
          onClick={handleOpenSignOutWindow}
          variant="contained"
        >
          Sign Out
        </Button>
      </Drawer>
      <SignOutWindow
        displaySignOutWindow={displaySignOutWindow}
        handleSignOutWindowClose={handleCloseSignOutWindow}
        handleSignOut={handleSignOut}
      />
    </div>
  )
}

interface SignOutWindowProps {
  displaySignOutWindow: boolean,
  handleSignOutWindowClose(): void,
  handleSignOut(): void,
}

/**
 * Prompts the user if they are sure they would like to sign out.
 * If user confirms, authenticationHandler and loginStatusHandler are called
 * to remove them from localStorage and render the Login screen.
 */
export function SignOutWindow(props: SignOutWindowProps) {
  const classes = useStyles();

  return (
    <Dialog open={props.displaySignOutWindow}>
      <DialogTitle>Sign Out?</DialogTitle>
      <DialogContent>
        <DialogContentText>
          By pressing 'Confirm', you will be signed out of CoffeeHouse.
          Press 'Cancel' to remain logged in.
        </DialogContentText>
        <DialogActions>
          <Button onClick={props.handleSignOutWindowClose} color="primary">
            Cancel
          </Button>
          <Button
            onClick={props.handleSignOut} color="primary"
          >
            Confirm
          </Button>
        </DialogActions>
      </DialogContent>
    </Dialog>
  );
}
