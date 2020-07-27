import * as React from "react";
import { BookInterface } from "../../../services/backend_service_interface/backend_service_interface";
import { createStyles, makeStyles, Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    boldTextElement: {
      fontWeight: 'bold',
      marginBottom: theme.spacing(0),
    },
    textElement: {
      marginBottom: theme.spacing(0),
      marginTop: theme.spacing(0),
    },
  })
);

interface BookInfoProps {
  book: BookInterface;
}

/** Displays the title and author of the club's current book. */
export const BookInfo = (props: BookInfoProps) => {
  const classes = useStyles();

  return (
    <>
      <div className={classes.boldTextElement}>
        Current Book:
      </div>
      <p className={classes.textElement}>
        <div>{props.book.title}</div>
        <div>by {props.book.author}</div>
      </p>
    </>
  );
}
