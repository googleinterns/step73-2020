import * as React from "react";
import Button from "@material-ui/core/Button";
import { ClubProps } from "../../../services/mock_backend/mock_your_clubs_backend";
import { createStyles } from "@material-ui/core/styles";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { makeStyles } from "@material-ui/core/styles";
import TextField from '@material-ui/core/TextField';
import { Theme } from "@material-ui/core/styles";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    bookField: {
      justifyContent: 'center',
      margin: theme.spacing(2),
    }
  }),
);

interface CreateNewClubWindowProps {
  isCreatingNewClub: boolean,
  closeWindow(): void,
}

export function CreateNewClubWindow(props: CreateNewClubWindowProps) {
  const classes = useStyles();
  const [club, setClub] = React.useState<ClubProps|undefined>(undefined);

  const handleClubNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setClub({...club, name: event.target.value});
  }

  const handleClubDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setClub({...club, description: event.target.value});
  }

  const handleClubContentWarningsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const contentWarningsArray = [event.target.value];
    setClub({...club, contentWarnings: contentWarningsArray});
  }

  return (
    <div>
      <Dialog open={props.isCreatingNewClub} maxWidth="lg">
        <DialogTitle>Create New Club</DialogTitle>
        <DialogContent>
          <DialogContentText>
            By filling out the below fields and clicking 'Submit', a club of
            which you have administrative rights over will be created. 
          </DialogContentText>
          <form>
            <TextField
              fullWidth
              helperText = {"Name of the club to be displayed publically to \
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
              fullWidth
              helperText = {"Description of the club to be displayed publically \
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
                            material or discussions that occur in the club."}
              id="description"
              InputLabelProps={{
                shrink: true,
              }}
              label="Club Content Warnings"
              margin="normal"
              onChange={handleClubContentWarningsChange}
              placeholder="Content Warnings"
              required
              value={club ? club.contentWarnings: ""}
              variant="outlined" 
            />
            <div>
              <TextField label="Book Name" className={classes.bookField} />
              <TextField label="Book Author" className={classes.bookField} /> 
              <TextField label="Book ISBN" className={classes.bookField} /> 
            </div>
          </form>
          <DialogActions>
            <Button onClick={props.closeWindow} color="primary">
              Cancel
            </Button>
            <Button onClick={props.closeWindow} color="primary">
              Submit
            </Button>
          </DialogActions>
        </DialogContent>
      </Dialog>
    </div>
  );
}
