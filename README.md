# cse5473project
App to explore implementing secure communications


Program Flow 
1. All users enter desired username and click join
2. App creates a socket connection to server
3. Server stores sockets associated with each user {username: socket}
4. Malicious player is randomly chosen (where do we determine this? on the server probably)
5. Malicious player chooses to possess someone
6. 2 players are randomly chosen to guess malicious (also determined on server?)
7. The 2 players make their guess
8. Other players vote on the guesses
9. If guess correct, players win. Else, player is out. Malicious user wins when 3 players left


Message types

1: join match (desired username)

2: send message to other player (destination username, message)

3: possess user (username)

4: accuse daemon (username) 

5: vote on daemon accusations


Server responsibilities
- Store map of usernames and associated sockets
- randomly choose malicious user
- randomly choose 2 users to guess
- determine if votes correctly guessed malicious user or not
- keep track of game state, ie when players are out, let them know and prevent them from participating further
