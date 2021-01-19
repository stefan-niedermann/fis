# FIS

(`Firefighter information system`? `Fiery info screen`? Don't know yet...)

## What is this?

FIS will poll a FTP server for incoming PDF files, use OCR to get the Text and parse the given information. It is a
special use case for german firefighters, who usually get information about new operations via Fax.

FIS can be used to display the information on an info screen. While nothing happens, it will display the current weather
situation. When a new operation arrives, it will switch the screen for some time to an operation screen and return to
the default info screen after a configurable duration.

## Setup

- Make sure, that Java Runtime Environment 11 or higher is installed on your system.
- The optical character software `Tesseract` expects the following environment variable to be set:
  ```
  LC_ALL=C
  ```
- Make sure that you pass the path to the `tessdata` folder as an argument. If it is not present, we'll assume, that it
  is in the folder of the current user.

## Run

### Starting the server

```aidl
java -jar fis.jar
```

Then start a web browser at [`http://localhost:8080`](http://localhost:8080).

### Mandatory arguments

```
--ftp.user=<secret>
--ftp.password=<secret>
--weather.key=<secret> // OpenWeatherMap API key
```

### Optional arguments

```
--ftp.host
--ftp.path
--ftp.file.suffix
--ftp.poll.interval
--tesseract.tessdata
--tesseract.lang
--weather.lang
--weather.units
--weather.location
--weather.poll.interval
--operation.duration
```