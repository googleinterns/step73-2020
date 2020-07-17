import * as React from "react";
import Button from "@material-ui/core/Button";
import { ClubProps } from "../../../services/mock_backend/mock_your_clubs_backend";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import TextField from '@material-ui/core/TextField';

interface CreateNewClubWindowProps {
  isCreatingNewClub: boolean,
  closeWindow(): void,
}

export function CreateNewClubWindow(props: CreateNewClubWindowProps) {
  const [club, setClub] = React.useState<ClubProps|undefined>(undefined);

  const handleClubNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setClub({...club, name: event.target.value});
  }

  return (
    <Dialog open={props.isCreatingNewClub}>
      <DialogTitle>Create New Club</DialogTitle>
      <DialogContent>
        <DialogContentText>
          By filling out the below fields and clicking 'Submit', a club of
          which you have administrative rights over will be created. 
        </DialogContentText>
        <form>
          <TextField
            error={false}
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
            placeholder="Nickname"
            required
            value={club ? club.name : ""}
            variant="outlined" 
          />
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
  );
}
