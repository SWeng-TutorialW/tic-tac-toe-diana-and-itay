[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/F3j_ac3s)
# OCSF Mediator Example

## Structure
Pay attention to the three modules:
1. **client** - a simple client built using JavaFX and OCSF. We use EventBus (which implements the mediator pattern) in order to pass events between classes (in this case: between SimpleClient and PrimaryController.
2. **server** - a simple server built using OCSF.
3. **entities** - a shared module where all the entities of the project live.

## Running
1. Run Maven install **in the parent project**.
2. Run the server using the `exec:java` goal in the server module.
3. Run the client using the `javafx:run` goal in the client module.

## _How to Use_ ##
1. You need to open a server, put `server.jar` inside a directory, run cmd in there and type `java -jar server.jar` - this should open the server (only in 1 PC)
2. Open your client, do the same but with `client.jar` (on both PCs)
3. Inside the Lobby, put address and port to start the game, who ever wins, gets a notification and both players are brought back to the main screen `(AKA Lobby)`
4. Enjoyed the game? We hope so.
5. Thanks.