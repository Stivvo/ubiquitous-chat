# ubiquitous-chat
group chat in java and swing GUI

You can run this chat on different computers connected to the same network and
send messages trough this chat. Even if you only run it on a single computer it
still must be connected to a network

The administrator (server) of the chat has to approve or discard every message

This project has been made with IntelliJ IDEA

## Compile
To compile this project you need the forms_rt.jar library and the
uiDesigner/core/\*class files, which are all included in this repo, they are
IntelliJ IDEA dependencies for the GUI

~~~
git clone https://github.com/Stivvo/ubiquitous-chat.git
cd ubiquitous-chat/chat/src/code
javac -d ../../out/production/chat/ -cp ".:../../forms_rt.jar" *java
~~~

# Run
Run the server first:

~~~
cd ../../out/production/chat/
java code/Server
~~~

The Server must run on the terminal. Stop it pressing ctr+c or closing the
terminal where the Server is running when all clients are disconnected
Open another terminal in the same directory, then Run the Login app;

~~~
java code/Login
~~~

# Usage
After the submit, the Client window (where you can write messages) will pop up

If the username is already in use the client can't establish a connection with
the group chat, so you get an error pop-up instead

When someone sends messages trough the Client window the Server must decide to
allow the message to be forwarded to all the clients in the network

You can create more Client windows with the same Login window
or run a different Login window for each Client

After a restarting or closing the server, the already spawned client windows
will become unusable.
