# VGU Computer Network Project - FTP Client Implementation in Java

Implement a FTP Client using Java that can interact with a public FTP server (e.g., ftp.gnu.org).

## Libraries Used

- [X] java.io.*
- [X] java.net.*
- [X] java.util.*
- [ ] javax.swing.*
- [ ] java.fx.* (for GUI)

## Required Features

- [x] Connect to FTP server (default port 21)
- [x] Anonymous login and custom username/password login
- [x] pwd (PWD command)
- [x] cd (CWD command)
- [x] ls using PASV + LIST
- [x] get using PASV + RETR (binary mode)
- [x] put using PASV + STOR (binary mode)
- [x] delete (DELE command)
- [x] mkdir (MKD command)
- [x] rmdir (RMD command)
- [x] quit (QUIT command)

### Optional Features

- [x] help command to list available commands and their usage
- [ ] GUI interface for file browsing and operations
- [ ] size (SIZE command)
- [ ] Progress bar for file uploads/downloads

## Technical Requirements

- [x] Must correctly handle control connection and separate data connection. [Partially implemented]
- [x] Must correctly parse FTP reply codes, including multiline responses.  
- [x] Must properly close sockets and streams. [?]
- [x] Must handle errors gracefully.[Partially implemented]

## Requirements

- Java 21 or later

## Project Structure

```terminal
ftclient
├───.vscode
├───Download
├───src
│   ├───main
│   │   ├───java
│   │   │   └───ftpclient
│   │   │       └───com
│   │   └───resources
│   └───test
│       └───java
├───target
│   ├───classes
│   │   └───ftpclient
│   │       └───com
│   ├───generated-sources
│   │   └───annotations
│   ├───maven-status
│   │   └───maven-compiler-plugin
│   │       └───compile
│   │           └───default-compile
│   └───test-classes
└───Upload
```

## Build and run guide

```terminal
mvn clean install
mvn clean compile
mvn exec:java "-Dexec.mainClass=ftpclient.com.Main" 
```

## How to run GUI

I will implement the Gui later.

## License

This project is licensed under the MIT License, see the [LICENSE](LICENSE) file for details.
