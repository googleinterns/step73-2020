import * as React from "react";
import { BookProps, ClubProps } from "../../../services/mock_backend/mock_your_clubs_backend";
import Button from "@material-ui/core/Button";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import { createStyles } from "@material-ui/core/styles";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { makeStyles } from "@material-ui/core/styles";
import { ServiceContext } from "../../contexts/contexts";
import TextField from '@material-ui/core/TextField';
import { Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    bookField: {
      justifyContent: 'center',
      margin: theme.spacing(2),
    },
    successButton: {
      marginLeft: theme.spacing(2),
      marginBottom: theme.spacing(2),
    },
  }),
);

interface CreateNewClubWindowProps {
  isCreatingNewClub: boolean,
  closeWindow(successfulCreation: boolean): void,
}

export function CreateNewClubWindow(props: CreateNewClubWindowProps) {
  const classes = useStyles();
  const [club, setClub] = React.useState<ClubProps|undefined>(undefined);
  const [book, setBook] = React.useState<BookProps|undefined>(undefined);
  const [missingField, setMissingField] = React.useState<boolean>(false);
  const [submitSuccess, setSubmitSuccess] = React.useState<boolean>(false);
  const [contentWarningsDisplay, setContentWarningsDisplay] = React.useState<string|undefined>(undefined);

  const contextServices = React.useContext(ServiceContext);
  const yourClubsHandlerService = contextServices.yourClubsHandlerService;

  const handleClubNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setClub({...club, name: event.target.value});
  }

  const handleClubDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setClub({...club, description: event.target.value});
  }

  const handleClubContentWarningsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    /* User should see newline chars, but for array the string must be split. */
    setContentWarningsDisplay(event.target.value);
    const contentWarningsArray = (event.target.value).split('\n');
    setClub({...club, contentWarnings: contentWarningsArray});
  }

  const handleBookTitleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setBook({...book, title: event.target.value});
    setClub({...club, currentBook: book});
  }

  const handleBookAuthorChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setBook({...book, author: event.target.value});
    setClub({...club, currentBook: book});
  }

  const handleBookIsbnChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setBook({...book, isbn: event.target.value});
    setClub({...club, currentBook: book});
  }

  const handleCloseCreate = (successfulCreation: boolean) => {
    setClub(undefined);
    setBook(undefined);
    setContentWarningsDisplay(undefined);
    setMissingField(false);
    setSubmitSuccess(false);
    props.closeWindow(successfulCreation);
  }

  const handleClubSubmission = async () => {
    if (club && book ? !(club.name && club.description && book.title && book.author) : true){
      setMissingField(true);
      setSubmitSuccess(false);
    } else {
      setMissingField(false);
      const success = yourClubsHandlerService.createClub(club);
      if (success) {
        setSubmitSuccess(true);
      }
    }
  }

  return (
    <div>
      <Dialog open={props.isCreatingNewClub} maxWidth="lg">
        <DialogTitle>Create New Club</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {submitSuccess 
              ? <b>Submission successful.</b> 
              : "By filling out the below fields and clicking 'Submit', a club of \
                 which you have administrative rights over will be created." }
          </DialogContentText>
          <form>
            <TextField
              error = {missingField ? (club ? (club.name ? false : true) : true) : false}
              fullWidth
              helperText = {missingField
                ? club 
                  ? (club.name 
                    ? "Name of the club to be displayed publically to \
                       all subsequent users who view it."
                    : "Club Name is a required field.")
                  : "Club Name is a required field."
                : "Name of the club to be displayed publically to \
                   all subsequent users who view it."}

              id="clubName"
              InputLabelProps={{
                shrink: true,
              }}
              label="Club Name"
              margin="normal"
              onChange={handleClubNameChange}
              placeholder="Club Name"
              required
              value={club ? club.name : ""}
              variant="outlined" 
            />
            <TextField
              error = {missingField ? (club ? (club.description ? false : true) : true) : false}
              fullWidth
              helperText = {missingField
                ? club 
                  ? (club.description
                    ? "Description of the club to be displayed publically \
                       to all subsequent users who view it."
                    : "Club Description is a required field.")
                  : "Club Description is a required field."
                : "Description of the club to be displayed publically \
                   to all subsequent users who view it."}
              id="description"
              InputLabelProps={{
                shrink: true,
              }}
              label="Club Description"
              margin="normal"
              multiline
              onChange={handleClubDescriptionChange}
              placeholder="Description"
              required
              rows={4}
              value={club ? club.description: ""}
              variant="outlined" 
            />
            <TextField
              fullWidth
              helperText = {"Content Warnings that are relevant to any reading \
                            material or discussions that occur in the club. \
                            Please put each on a new line."}
              id="description"
              InputLabelProps={{
                shrink: true,
              }}
              label="Club Content Warnings"
              margin="normal"
              multiline
              onChange={handleClubContentWarningsChange}
              placeholder="Content Warnings"
              rows={4}
              value={contentWarningsDisplay ? contentWarningsDisplay : ""}
              variant="outlined" 
            />
            <div>
              <TextField 
                className={classes.bookField}
                error = {missingField ? (book ? (book.title ? false : true) : true) : false}
                helperText = {missingField
                  ? book 
                    ? (book.title
                      ? "Title of the current book that the club will be \
                         discussing."
                      : "Book Title is a required field.")
                    : "Book Title is a required field."
                  : "Title of the current book that the club will be \
                     discussing."}
                label="Book Title"
                onChange={handleBookTitleChange}
                required
                value={book ? book.title : ""}
              />
              <TextField 
                className={classes.bookField}
                error = {missingField ? (book ? (book.author ? false : true) : true) : false}
                helperText = {missingField
                  ? book 
                    ? (book.author
                      ? "Author of the current book that the club will be \
                         discussing."
                      : "Book Author is a required field.")
                    : "Book Author is a required field."
                  : "Author of the current book that the club will be \
                     discussing."}
                label="Book Author"
                onChange={handleBookAuthorChange}
                required
                value={book ? book.author : ""}
              /> 
              <TextField  
                className={classes.bookField}
                helperText = {"ISBN of the current book thaty the club will \
                               be discussing."}
                label="Book ISBN"
                onChange={handleBookIsbnChange}
                value={book? book.isbn : ""}
              /> 
            </div>
          </form>
          <DialogActions>
            <div hidden={submitSuccess}>
              <Button onClick={() => (handleCloseCreate(submitSuccess))} color="primary">
                Cancel
              </Button>
              <Button onClick={handleClubSubmission} color="primary">
                Submit
              </Button>
            </div>
            <div hidden={!submitSuccess}>
              <CheckCircleIcon />
              <Button onClick={() => (handleCloseCreate(submitSuccess))} color="primary" className={classes.successButton}>
                Close
              </Button>
            </div>
          </DialogActions>
        </DialogContent>
      </Dialog>
    </div>
  );
}
