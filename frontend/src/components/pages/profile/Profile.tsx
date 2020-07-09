import * as React from "react";
import Button from "@material-ui/core/Button";
import { createStyles } from "@material-ui/core/styles";
import FilledInput from "@material-ui/core/FilledInput";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import { makeStyles } from "@material-ui/core/styles";
import OutlinedInput from "@material-ui/core/OutlinedInput";
import { PersonProps } from "../../../services/mock_profile_backend";
import { ProfileHandlerService } from "../../../services/profile_handler_service";
import { MockProfileBackendService } from "../../../services/mock_profile_backend";
import { ServiceContext } from "../../contexts/contexts";
import { ServiceHandlers } from "../../contexts/contexts";
import { Theme } from "@material-ui/core/styles";
import TextField from '@material-ui/core/TextField';

const USER_ID = "user_0";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
      flexWrap: 'wrap',
      marginTop: '100px',
      marginLeft: theme.spacing(4),
    },
    '& input:invalid + fieldset': {
      borderColor: 'red',
      borderWidth: 2,
    },
    textField: {
      marginLeft: theme.spacing(1),
      marginRight: theme.spacing(1),
      width: '25ch',
    },
  }),
);

interface ProfileProps {
  profileHandlerService: ProfileHandlerService;
}

interface ProfileState {
  personProps: PersonProps;
}

export default function Profile(props: ProfileProps, state: ProfileState) {

  const classes = useStyles();

  /** 
   * ServiceHandlers is an object containing various TS Handlers and provides
   * functionality to communicate data from the frontend to the backend
   */
  const serviceHandlers = React.useContext(ServiceContext);
  const profileHandlerService = serviceHandlers.profileHandlerService;
  
  const [person, setPerson] = React.useState<PersonProps|undefined>(undefined);
  const [profileId, setProfileId] = React.useState<string>(undefined);
  const [submitSuccess, setSubmitSuccess] = React.useState<Boolean|undefined>(undefined);
  
  React.useEffect(() => {
    (async() => {
      const personPromise = await profileHandlerService.getPerson(USER_ID);
      setPerson(personPromise);
    })();
  }, [profileId]); /** Re-renders Profile only when the userId changes */

  const handleEmailChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPerson({...person, email: event.target.value});
  }

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPerson({...person, nickname: event.target.value});
  }

  const handlePronounsChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setPerson({...person, pronouns: event.target.value});
  }

  const handleSubmit = (event) => {
    event.preventDefault();

    (async() => {
      const success = await profileHandlerService.updatePerson(person);
      setSubmitSuccess(success);
    })();
  };

  return (
    <div className={classes.root}>
      <form>
        <div>
          <TextField
            fullWidth
            helperText="to be displayed around CoffeeHouse"
            id="nickname"
            InputLabelProps={{
              shrink: true,
            }}
            label="Nickname"
            margin="normal"
            onChange={handleNameChange}
            placeholder="Nickname"
            style={{ margin: 8 }}
            value={person? person.nickname : ""}
            variant="outlined"
          />
          <TextField
            fullWidth
            helperText="to be displayed around CoffeeHouse"
            id="pronouns"
            InputLabelProps={{
              shrink: true,
            }}
            label="Pronouns"
            margin="normal"
            onChange={handlePronounsChange}
            placeholder="Pronouns"
            style={{ margin: 8 }}
            value={person? person.pronouns : ""}
            variant="outlined"
          />
          <TextField
            fullWidth
            id="email"
            InputLabelProps={{
              shrink: true,
            }}
            label="Email"
            margin="normal"
            onChange={handleEmailChange}
            placeholder="Email"
            style={{ margin: 8 }}
            value={person? person.email : ""}      
            variant="outlined"
          />
        </div>
        <div>
          <Button 
            color="primary" 
            onClick={handleSubmit}
            style={{ margin: 8 }} 
            variant="contained" 
          >
            Submit
          </Button>
        </div>
        <DisplaySubmitStatus success={submitSuccess} />
      </form>
    </div>
  );
}

/**
 * Displays a success or failure message to the user after they submit a 
 * a profile change. 
 */
function DisplaySubmitStatus(props) {
  const success = props.success;

  if (success === undefined) {
    return (<div></div>);
  } else {
    if (success) {
      return (
        <div>
          <p style={{ color: 'green', margin: 8 }}>
            Profile successfully updated.
          </p>
        </div>
      );
    } else {
      return (
        <div>
          <p style={{ color: 'red', margin: 8 }}>
            Something went wrong.
          </p>
        </div>
      )
    }
  }
}
