// React Framework imports
import * as React from "react";
import { Link } from "react-router-dom"
import clsx from 'clsx';

// Material-UI Functionality imports
import Divider from '@material-ui/core/Divider';
import Drawer from '@material-ui/core/Drawer';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import { createStyles, makeStyles, 
         useTheme, Theme } from '@material-ui/core/styles';
import Typography from '@material-ui/core/Typography';

// Material-UI Icon imports
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import ExploreIcon from '@material-ui/icons/Explore';
import FaceIcon from '@material-ui/icons/Face';
import LibraryBooksIcon from '@material-ui/icons/LibraryBooks';

const drawerWidth = 240;

// Styles AppBar transition, page content in relation to AppBar
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
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

export default function DrawerComp(props) {
  const classes = useStyles();
  const theme = useTheme();

  const pagesList = [
    {text: "Your Clubs", url: "/YourClubs", icon: <LibraryBooksIcon />},
    {text: "Explore", url: "/Explore", icon: <ExploreIcon />},
    {text: "Profile", url: "/Profile", icon: <FaceIcon />}
  ];

  return (
    <div>
      <Drawer
        className={classes.drawer}
        variant="persistent"
        anchor="left"
        open={props.open}
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
      </Drawer>
    </div>
  )
}