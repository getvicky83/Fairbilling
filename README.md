## Overview

**'SessionAnalyzer'** is a Java application designed to process a log file containing user session data and produce a report of the number of sessions and total duration of each user's sessions. The log file includes entries that record when a session starts or ends, and this application calculates the total time each user spent in sessions based on these entries.

## Features

* Parses log entries from a file. 
* Computes the total session time for each user. 
* Handles overlapping sessions. 
* Assumes missing session starts or ends based on the earliest or latest log entries.

## Usage
## 1. Running the java application without using dockerimage
**Command Line**

    java SessionAnalyzer <logFilePath>

* **`<logFilePath>`**: The path to the log file containing session data.

**Log File Format**

The log file should contain entries in the following format:

    HH:MM:SS USERNAME ACTION

* **HH:MM:SS**: The time of the entry.
* **USERNAME**: The username associated with the session.
* **ACTION**: Either **Start** or **End** indicating the start or end of a session.

**Example Log File**

    14:02:03 ALICE99 Start
    14:02:05 CHARLIE End
    14:02:34 ALICE99 End
    14:02:58 ALICE99 Start
    14:03:02 CHARLIE Start
    14:03:33 ALICE99 Start
    14:03:35 ALICE99 End
    14:03:37 CHARLIE End
    14:04:05 ALICE99 End
    14:04:23 ALICE99 End
    14:04:41 CHARLIE Start
    
## 2. Running the application with the docker image

**Build the Docker Image**

Navigate to the directory containing your Dockerfile and build the Docker image:

    docker build -t session-analyzer .


**Run the Docker Container with Volume Mounting**

Run the Docker container, mounting the directory where your log file is located:

    docker run -it -v $(pwd)/:/logfile --name session-analyzer-container session-analyzer

This command mounts the current directory ($(pwd)) to /logfile inside the Docker container.

**Inside the Container's Bash Shell**

Once inside the bash shell of the Docker container, you can run the Java application:
nal
    java -cp /app/FairBilling.jar SessionAnalyzer /logfile/logfile.txt
    
## Output

The application prints a summary of user sessions to the console in the following format:

    USERNAME SESSION_COUNT TOTAL_DURATION

* **USERNAME**: The name of the user.
* **SESSION_COUNT**: The number of sessions the user had.
* **TOTAL_DURATION**: The total duration of all sessions in seconds.

**Example Output**

    ALICE99 4 240
    CHARLIE 3 37

## Project Structure

* **SessionAnalyzer.java**: The main application class that reads the log file, parses entries, and produces the session report.
* **LogEntry.java**: A class representing a single log entry.
* **UserSession.java**: A class representing a user's session data, including logic to process start and end entries and calculate session durations.

## Building and Running
**Prerequisites**

* Java Development Kit (JDK) 8 or higher
* Apache Maven (for building the project)

**Building the Project**

To build the project, navigate to the project directory and run:

    mvn clean install

**Running the Application**

After building the project, run the application with the following command:

    java -cp target/FairBilling-1.0-SNAPSHOT.jar SessionAnalyzer <logFilePath>

Replace `<logFilePath>` with the path to your log file.

## Testing

The project includes JUnit tests to ensure the correct functionality of the session analysis logic. To run the tests, use the following command:
 
    mvn test
    
