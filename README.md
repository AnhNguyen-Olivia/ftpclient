# VGU Computer Network Project - FTP Client (Java)

This repository contains a Java implementation of an FTP client that can interact with public FTP servers (for example, ftp.gnu.org). It offers a command-line interface (CLI) for common FTP operations and has preliminary GUI components under `src/main/java/ftpclient/com/UI_Compoments`.

## Prerequisites

- Java 21 or later
- Apache Maven (3.6+)
- Internet access to connect to public FTP servers (optional for local testing)

## Quick Overview

- Main entry: `ftpclient.com.FtpApp` (configured in the Maven `exec` plugin)
- CLI supports: `connect`, `login`, `pwd`, `cd`, `ls`, `get`, `put`, `delete`, `mkdir`, `rmdir`, `quit`, and `help`

## Build

From the project root, run:

```bash
mvn clean install
```

This compiles sources into `target/classes` and packages the project into `target/ftpclient-1.0-SNAPSHOT.jar`.

### Run (using compiled classes)

Run the main class from the compiled classes directory. Pass the mode as the first argument (cli or gui) to avoid the initial prompt:

```bash
java -cp target/classes ftpclient.com.FtpApp cli
```

### Run (packaged JAR)

If you prefer to run the packaged JAR and the project has no external dependencies, you can run:

```bash
mvn package
java -cp target/ftpclient-1.0-SNAPSHOT.jar ftpclient.com.FtpApp cli
```

## Basic usage examples

1. Start the application (see commands above).
2. At the prompt, connect and operate:

```text
> connect ftp.gnu.org
> login anonymous
> ls
> get README
> put localfile.bin
> quit
```

## GUI

There are GUI-related classes in `src/main/java/ftpclient/com/UI_Compoments` and `FtpUI.java`. It is fully functional use this command to start the GUI directly:

```bash
java -cp target/classes ftpclient.com.FtpApp gui
```

## Note

If you need a test server for testing this program, I recomend using this website to create a free FTP for one hour [free ftp server](https://sftpcloud.io/tools/free-ftp-server)

In the cli option you can use the following command to connect to the server:

```bash
[System]> Before we start, do you want to login as anoymous? [y/n/o] (o for other credentials)
you can choose y to login as anoymous, n to skip login and o to input your own credentials and own server. (Hint: Use the free ftp I provide above for testing)
```

## License and Integrity Declaration

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file.

I confirm that this submission is my own work. I did not copy code from AI tools, classmates, or online repositories.
