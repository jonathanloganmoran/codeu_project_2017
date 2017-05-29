
# CODEU CHAT SERVER | README


## DISCLAIMER

CODEU is a program created by Google to develop the skills of future software
engineers. This project is not an offical Google Product. This project is a
playground for those looking to develop their coding and software engineering
skills.

## WEB ENVIRONMENT

### To Use, Visit: <a href="http://codeu.dgarry.com" target="_blank">codeu.dgarry.com</a>

### To Run Locally:
Install composer, a package manager for PHP.

```
brew update
brew install homebrew/php/composer
```

Then **navigate to the root of the webportal folder** and install dependencies.

`composer install`

Run the server.

`composer start`

Navigate to **localhost:8080** in your browser.

#### PHP versioning
We use PHP7.
```
brew update
brew tap homebrew/dupes
brew tap homebrew/versions
brew tap homebrew/homebrew-php

# unlink previous php70 if you have it installed
brew unlink php70
brew install php71

# check if php is now version 7.1.0
php --version

# should result in terminal output similar to:
PHP 7.1.2 (cli) (built: MONTH DAY YEAR HOURS:MINUTES:SECONDS) ( NTS )
Copyright (c) 1997-2017 The PHP Group
```

## JAVA ENVIRONMENT

All instructions below here are relative to a LINUX environment. There will be some
differences if you are working on a non-LINUX system. We will not support any
other development environment.

This project was built using JAVA 7. It is recommended that you install
JAVA&nbsp;7 when working with this project.


## JAVA GETTING STARTED

  1. To build the project:
       ```
       $ sh clean.sh
       $ sh make.sh
       ```

  1. To test the project:
       ```
       $ sh test.sh
       ```

  1. To run the project you will need to run both the client and the server. Run
     the following two commands in separate shells:

       ```
       $ sh run_server.sh
       $ sh run_client.sh
       ```

     The `run_server` and `run_client` scripts have hard-coded addresses for
     your local machine. If you are running the server on a different machine
     than the client, you will need to change the host portion of the address
     in `run_client.sh` to the name of the host where your server is running.
     Make sure the client and server are using the same port number.

All running images write informational and exceptional events to log files.
The default setting for log messages is "INFO". You may change this to get
more or fewer messages, and you are encouraged to add more LOG statements
to the code. The logging is implemented in `codeu.chat.util.Logger.java`,
which is built on top of `java.util.logging.Logger`, which you can refer to
for more information.

In addition to your team's client and server, the project also includes a
Relay Server and a script that runs it (`run_relay.sh`).
This is not needed to get started with the project.


## Java: Finding your way around the project

All the source files (except test-related source files) are in
`./src/codeu/chat`.  The test source files are in `./test/codeu/chat`. If you
use the supplied scripts to build the project, the `.class` files will be placed
in `./bin`. There is a `./third_party` directory that holds the jar files for
JUnit (a Java testing framework). Your environment may or may not already have
this installed. The supplied scripts use the version in ./third_party.

Finally, there are some high-level design documents in the project Wiki. Please
review them as they can help you find your way around the sources.


## Java: Source Directories

The major project components have been separated into their own packages. The
main packages/directories under `src/codeu/chat` are:

### Java: codeu.chat.client

Classes for building the two clients (`codeu.chat.ClientMain` and
`codeu.chat.SimpleGuiClientMain`).

### Java: codeu.chat.server

Classes for building the server (`codeu.chat.ServerMain`).

### Java: codeu.chat.relay

Classes for building the Relay Server (`codeu.chat.RelayMain`). The Relay Server
is not needed to get started.

### Java: codeu.chat.common

Classes that are shared by the clients and servers.

### Java: codeu.chat.util

Some basic infrastructure classes used throughout the project.
