// React Framework imports
import * as React from "react";
import { hot } from "react-hot-loader";
import { BrowserRouter, Route, Switch, Link} from "react-router-dom"
import clsx from 'clsx';

// Material-UI Functionality imports
import AppBar from '@material-ui/core/AppBar';
import CssBaseline from '@material-ui/core/CssBaseline';
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import { makeStyles, useTheme, Theme, createStyles } from '@material-ui/core/styles';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

// Material-UI Icon imports
import AccountCircleIcon from '@material-ui/icons/AccountCircle';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import ExploreIcon from '@material-ui/icons/Explore';
import FaceIcon from '@material-ui/icons/Face';
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';
import MenuIcon from '@material-ui/icons/Menu';

// Page Imports
import { Explore } from "../pages/explore/Explore";
import { Login } from "../pages/login/Login"; 
import { Profile } from "../pages/profile/Profile";
import { YourClubs } from "../pages/your_clubs/YourClubs"

const drawerWidth = 240;

// Styles AppBar transition, page content in relation to AppBar
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
    appBar: {
      transition: theme.transitions.create(['margin', 'width'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
    },
    appBarShift: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
      transition: theme.transitions.create(['margin', 'width'], {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    hide: {
      display: 'none',
    },
    drawer: {
      width: drawerWidth,
      flexShrink: 0,
    },
    drawerPaper: {
      width: drawerWidth,
    },
    drawerHeader: {
      display: 'flex',
      alignItems: 'center',
      padding: theme.spacing(0, 1),
      // necessary for content to be below app bar
      ...theme.mixins.toolbar,
      justifyContent: 'flex-end',
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
      transition: theme.transitions.create('margin', {
        easing: theme.transitions.easing.easeOut,
        duration: theme.transitions.duration.enteringScreen,
      }),
      marginLeft: 0,
    },
    link: {
      textDecoration: 'none', 
      color: theme.palette.text.primary
    },
    accountIcon: {
      marginLeft: "auto"
    }
  }),
);

export default function PersistentDrawerLeft() {
  const classes = useStyles();
  const theme = useTheme();
  const [open, setOpen] = React.useState(false);

  const handleDrawerOpen = () => {
    setOpen(true);
  };

  const handleDrawerClose = () => {
    setOpen(false);
  };

  return (
    <BrowserRouter>
      <div className={classes.root}>
        <CssBaseline />
        <AppBar
          position="fixed"
          className={clsx(classes.appBar, {
            [classes.appBarShift]: open,
          })}
        >
          <Toolbar>
            <IconButton
              color="inherit"
              aria-label="open drawer"
              onClick={handleDrawerOpen}
              edge="start"
              className={clsx(classes.menuButton, open && classes.hide)}
            >
              <MenuIcon />
            </IconButton>
            <Typography variant="h6" noWrap>
              CoffeeHouse
            </Typography>
            <IconButton
              aria-label="profile avatar"
              color="inherit"
              className={classes.accountIcon}
            >
              <AccountCircleIcon fontSize="large"/>
            </IconButton>
          </Toolbar>
        </AppBar>
        <Drawer
          className={classes.drawer}
          variant="persistent"
          anchor="left"
          open={open}
          classes={{
            paper: classes.drawerPaper,
          }}
        >
          <div className={classes.drawerHeader}>
            <IconButton onClick={handleDrawerClose}>
              {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
            </IconButton>
          </div>
          <List>
            <Link to="/YourClubs" className={classes.link}>
              <ListItem button key="Your Clubs">
                <ListItemIcon><LibraryBooksIcon /></ListItemIcon>
                <ListItemText primary="Your Clubs" />
              </ListItem>
            </Link>
            <Link to="/Explore" className={classes.link}>
              <ListItem button key="Explore">
                <ListItemIcon><ExploreIcon /></ListItemIcon>
                <ListItemText primary="Explore" />
              </ListItem>
            </Link>
            <Link to="/Profile" className={classes.link}>
              <ListItem button key="Profile">
                <ListItemIcon><FaceIcon /></ListItemIcon>
                <ListItemText primary="Profile" />
              </ListItem>
            </Link>
          </List>
          <Divider />
        </Drawer>
        <main
          className={clsx(classes.content, {
            [classes.contentShift]: open,
          })}
        >
          <Switch>
            <Route exact path="/Login" component={Login} />
            <Route exact path="/YourClubs" component={YourClubs} />
            <Route exact path="/Explore" component={Explore} />
            <Route exact path="/Profile" component={Profile} />
          </Switch>
        </main>
      </div>
    </BrowserRouter>
  );
}
