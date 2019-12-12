# ubiquitous-chat
group chat in java and swing GUI

The administrator (server) of the chat has to approve or discard every message

The Server class must be started before every Client

Clients are started launching the Login class, where they choose a username

If the username is already in use they can't establish a connection with the 
group chat, they get an error pop-up instead

This project has been made with IntelliJ IDEA

to compile this project:

~~~
cd ubiquitous-chat/chat/src/code
javac -d ../../out/production/chat/code/ \*java
~~~

run the server first:

~~~
cd ../../out/production/chat/
java code/Server
~~~

then Run the Login app;

~~~
java code/Login
~~~

after the submit, the Client window (where you can write messages) will pop up

you can create more Client windows with the same Login window
or run a different Login window for each Client
