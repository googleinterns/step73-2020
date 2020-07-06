import * as LayoutConstants from "../layout/layout_constants"
import * as React from "react";
import { Link } from "react-router-dom"
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import clsx from 'clsx';
import { createStyles } from '@material-ui/core/styles';
import Divider from '@material-ui/core/Divider';
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
import { Theme} from "@material-ui/core/styles";
import Typography from "@material-ui/core/Typography";
import { useTheme } from "@material-ui/core/styles";

const drawerWidth = LayoutConstants.DRAWER_WIDTH;

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

interface DrawerProps {
  navigationDrawerOpen: boolean;  // whether or not Drawer comp is displayed
  handleDrawerClose(): void;  // sets open to true
}

export default function DrawerComp(props: DrawerProps) {
  const classes = useStyles();
  const theme = useTheme();

  // Associates each URL with a text representation and icon
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
      </Drawer>
    </div>
  )
}
