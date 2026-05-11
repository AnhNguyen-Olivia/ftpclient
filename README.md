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

Note: If you need a self-contained executable JAR with dependency bundling, let me know and I can add the Maven Shade plugin.

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

There are GUI-related classes in `src/main/java/ftpclient/com/UI_Compoments` and `FtpUI.java`, but the GUI is not fully implemented. Use the CLI for now.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file.
