# Collecto

![Game Background](/Collecto.png)

### Team members:

- Vo Nhat Minh
- Jesse Hermjan Snoijer

# I. Project Overview:

---

<p>
Project Demo: https://youtu.be/E_NhPe7djKc
    
This game is the game Collecto.
This is a German game that works with balls that you can move in directions. When 2 balls are
next to each other the balls can be removed from the board and be added to your collection. A
move can only be made when there are balls to collect after performing the move. When no
single move is possible, a double move may be made. When there is no double move possible,
the game has ended. Then for every 3 balls of the same color, you gain a point. The winner is
the person with the most points. Is it a draw, then the winner is the one with the most balls
collected. If this is also the same, the game ended in a draw.
The game is made completely in Java as a final project for module 2 Software System, University of Twente
</p>
<br/>

# II. How to set up the file?

We provided a zip file with 2 running options.
## 1. Jar file installation: 

You have to extract the zip two jar files namely CollectoClient.jar and CollectoServer.jar. To run the files you first need to go inside of the File
Explorer of your PC to the folder where the jar file is located. Then you type in the navigation
bar: `cmd`, this will open a command prompt. 

run `java -jar CollectoServer.jar` to start the Collecto server.

run `java -jar CollectoClient.jar` if you want to run the CollectoClient.

## 2. Normal installation

There are 2 main files are CollectoClient.java (in CollectoGame/src/collectoclient package) and CollectoServer.java (in CollectoGame/src/server
package). These files have the main method to start client and server

# III. Game rule:

You can consult Collecto game rule in:

https://foxmind.com/wp-content/uploads/2019/10/Collecto_Rules_print.pdf


# IV. How to start the game?
● The client:

1. Run the “CollectoClient.java”. Then the TUI will ask “Do you want to play the
   game online? (Type y/n for online/offline option)”
2. Type “n”/”no” for the offline option.

- Then it will ask “What is the first player's name?”. Type the first player’s
  name (Ex: Minh)
- “What is the second player's name?”. Type the second player’s name.
  (And then you can move to the game start step)

3. Type “y/yes” for the online option

- Then it will ask “Enter the IP or host to connect to”. Type the IP address
  (Ex: “130.89.253.65”)
- Then it will ask “What is the portNumber?”. Type the port number.
  (Ex: “4114”)
- Then a help menu will be printed
  “Available commands are:
  LOGIN (name) - to login to the server with your name
  QUEUE - to enter or leave the queue for playing a game
  LIST - to get the names of all the online players
  MOVE (move1) (move2) - to play a (double) move during the game
  HELP - to print this help-menu
  EXIT - to exit the program
  Successfully connected to server: CollectoM server
  To start a game first login. Type "login" followed by a space and your
  name”

4. To join the server. First type “login” / ”LOGIN”

- Then the TUI will ask “What is your user name?” then type your name
  (Ex: Minh)
- Or you can type “login Minh” to join the game

5. To check the list of players. Type “list” or “LIST”
6. To join the queue. Type “queue” or “QUEUE” (only available if you are not in a
   game). The TUI will display “You successfully entered the queue. To leave it,
   type "queue" again.”
7. If you want to leave the queue. Type “queue” or “QUEUE” again (only available if
   you are not in a game). Then the TUI will ask “You are already in the queue. Do
   you want to leave it?”. Type “y”/”yes” to leave the queue. The TUI will display
   “You successfully left the queue”
8. When the game starts:

- TUI will display “ALRIGHT This is our TURN” when it’s your turn and you
  also can only type MOVE command if it’s your turn (if play as a human
  player)
- If this is your first turn
  ● First type “move”. The TUI will ask you (Do you want a bot to play
  the game? (Type y/n for Bot/Human option)
  Bot Player
  ● Type “y”/”yes” if you want to play as a BOT
  Then the TUI will ask “Do you want the SMART or NAIVE strategy
  to play the game ? (Type y/n for SMART/NAIVE option). Type “y”
  or “n” to switch between SMART strategy and NAIVE strategy.
  (Playing as a bot you don’t have to type move every time because
  the client will send it)
  Human Player
  ● Type “n” or “no” if you want to play as Human Player
  The TUI will ask you “Do you want to get a hint? (Type y/n)
  Type “y” if you want to show the hint
  (Example TUI’s answer: Possible single moves are | 2| 3| 4| 9|
  ● Then TUI will ask “> Minh, what is your single move? “ Type your
  single move’s number (Example: 3)
- If you are playing as Human Player and this is not your first turn
  ● When the TUI send you “ALRIGHT This is our TURN”
  ● Then type “move”/”MOVE”
  ● The TUI will ask you “Do you want to get a hint? (Type y/n)
  ● Then TUI will ask “> Minh, what is your single move? “ Type your
  single move’s number (Example: 3) (If this is a single move)
  ● If this is a double move then the TUI will ask 2 time
  “What is your first double move” Type your first move’s number
  Then it will ask “What is your second double move” Type your
  second move’s number
- To start the server

1. Run the CollectoServer.java
2. The TUI will ask “Please enter the server port”
3. Type the server’s port number (Ex: “8080”)
4. When a new client joins the server.
   The TUI will display Ex: “> [null] Incoming: HELLO~Yellow-1.3 Minh&Jesse”
5. When a message is sent by the client it will be displayed in this format
   > [user’s name] Incoming: Protocol message
   > (Example: > [Minh] Incoming: LIST)
