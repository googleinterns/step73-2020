import * as React from "react";
import Button from "@material-ui/core/Button";
import CheckCircleIcon from "@material-ui/icons/CheckCircle";
import { createStyles } from "@material-ui/core/styles";
import ErrorIcon from '@material-ui/icons/Error';
import FilledInput from "@material-ui/core/FilledInput";
import FormControl from "@material-ui/core/FormControl";
import FormHelperText from "@material-ui/core/FormHelperText";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import { makeStyles } from "@material-ui/core/styles";
import OutlinedInput from "@material-ui/core/OutlinedInput";
import { PersonInterface } from
    "../../../services/backend_service_interface/backend_service_interface";
import { ServiceContext } from "../../contexts/contexts";
import { Theme } from "@material-ui/core/styles";
import TextField from '@material-ui/core/TextField';

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
    submissionMessage: {
      display: 'flex',
      marginTop: '10px'
    }
  }),
);

export default function Profile() {

  const classes = useStyles();

  /**
   * ServiceHandlers is an object containing various TS Handlers and provides
   * functionality to communicate data from the frontend to the backend
   */
  const contextServices = React.useContext(ServiceContext);
  const profileHandlerService = contextServices.profileHandlerService;
  const loginStatusHandlerService = contextServices.loginStatusHandlerService;

  const [person, setPerson] = React.useState<PersonInterface | undefined>(undefined);
  const [submitSuccess, setSubmitSuccess] = React.useState<boolean|undefined>(undefined);

  React.useEffect(() => {
    (async() => {
      const person = await profileHandlerService.getPerson(
          loginStatusHandlerService.getUserToken());
      setPerson(person);
    })();
  }, []);

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
      if (person.email === "" || person.nickname === "") {
        setSubmitSuccess(false);
        return;
      }
      try {
        await profileHandlerService.updatePerson(
            person, loginStatusHandlerService.getUserToken());
        setSubmitSuccess(true);
      } catch (err) {
        setSubmitSuccess(false);
      }
    })();
  };

  return (
    <div className={classes.root}>
      <form>
        <div>
          <TextField
            error={person ? person.nickname === "" : false }
            fullWidth
            helperText = {person
              ? (person.nickname === ""
                ? "Nickname is a required field."
                : "Preferred nickname to be displayed on your public profile.")
              : "Nickname is a required field."}
            id="nickname"
            InputLabelProps={{
              shrink: true,
            }}
            label="Nickname"
            margin="normal"
            onChange={handleNameChange}
            placeholder="Nickname"
            required
            value={person ? person.nickname : ""}
            variant="outlined"
          />
          <TextField
            fullWidth
            helperText="Preferred pronouns (eg. 'they/them') to be displayed
                        on your public profile."
            id="pronouns"
            InputLabelProps={{
              shrink: true,
            }}
            label="Pronouns"
            margin="normal"
            onChange={handlePronounsChange}
            placeholder="Pronouns"
            value={person ? person.pronouns : ""}
            variant="outlined"
          />
          <TextField
            error={person ? person.email === "" : false }
            fullWidth
            helperText = {person
              ? (person.email === ""
                ? "Email is a required field."
                : "Preferred email that opted-in club updates will be sent to.")
              : "Email is a required field."}
            id="email"
            InputLabelProps={{
              shrink: true,
            }}
            label="Email"
            margin="normal"
            onChange={handleEmailChange}
            placeholder="Email"
            required
            value={person ? person.email : ""}
            variant="outlined"
          />
        </div>
        <div>
          <Button
            color="primary"
            onClick={handleSubmit}
            variant="contained"
          >
            Submit
          </Button>
        </div>
        <DisplaySubmitStatus success={submitSuccess} personProps={person} />
      </form>
    </div>
  );
}

interface DisplaySubmitStatusProps {
  success: boolean;
  personProps: PersonInterface;
}

/**
 * Displays success or failure message to the user after they submit
 * a profile change.
 */
function DisplaySubmitStatus(props: DisplaySubmitStatusProps) {
  const classes = useStyles();
  const success = props.success;
  const person = props.personProps;

  if (success === undefined) {
    return (<div></div>);
  } else {
    if (success) {
      return (
        <div className={classes.submissionMessage}>
          <CheckCircleIcon />
          <p style={{ color: 'green' }}>
            Profile successfully updated.
          </p>
        </div>
      );
    } else {
      return (
        <div className={classes.submissionMessage}>
          <ErrorIcon />
          <p style={{ color: 'red' }}>
            Something went wrong.
          </p>
        </div>
      );
    }
  }
}
