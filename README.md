# JarFIS

[![Android CI](https://github.com/stefan-niedermann/fis/workflows/Android%20CI/badge.svg)](https://github.com/stefan-niedermann/fis/actions)
[![GitHub issues](https://img.shields.io/github/issues/stefan-niedermann/fis.svg)](https://github.com/stefan-niedermann/nextcloud-fis/issues)
[![GitHub stars](https://img.shields.io/github/stars/stefan-niedermann/fis.svg)](https://github.com/stefan-niedermann/nextcloud-fis/stargazers)
[![License: GPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)

**J**ava **ar**chive **F**irefighter **I**nformation **S**ystem

## What is this?

`JarFIS` covers a special use case for german firefighters, who usually are notified about new operations via Fax. The
tool is capable of displaying the same information on an info screen to better spread the incoming information to new
volunteer firefighters who arrive at the fire department.

While nothing happens, it will display the current weather situation. When a new operation arrives, it will switch the
screen for some time to an operation screen and return to the default info screen after a configurable duration.

## How does it work?

The environment can vary of course, but the idea is that a Fax arrives at the primary number (1) and gets routed to the
Fax printing device (2). The Fax printing device should print the information and then forward the fax to a secondary
number (3) which will be stored by the router on an internal memory / FTP server as a PDF file.

`JarFIS` will poll this FTP storage (4) and download a new incoming PDF file (5). Then it extracts the text using optical
character recognition, parse the text to a machine-readable JSON file and display it on the info screen (6).

![Illustration](illustration.png)

## Setup

- You will need at least a Java Runtime Environment 11 or higher
- Install `Tesseract`, on Ubuntu based systems, this is usually done with
  ```aidl
  sudo apt install tesseract-ocr
  ```
- The optical character software `Tesseract` expects the following environment variable to be set:
  ```
  LC_ALL=C
  ```
- Make sure that you pass the path to the `tessdata` folder as an argument. If it is not present, we'll assume, that it
  is in the folder of the current user. You will need at least the trained data file for [your language](https://github.com/tesseract-ocr/tessdata_best).

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
--operation.highlight
```

## License

All contributions to this repository are considered to be licensed under the [`GNU GENERAL PUBLIC LICENSE 3+`](https://www.gnu.org/licenses/agpl-3.0).

Contributors to `JarFIS` retain their copyright. Therefore we recommend to add following line to the header of a file, if you changed it substantially:

```
@copyright Copyright (c) <year>, <your name> (<your email address>)
```