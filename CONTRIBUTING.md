# Contributing guidelines

## Frontend

The frontend is an Angular application and needs to be built separately¹. The working directory is in the `frontend` subfolder. For more information see its [README.md](./frontend/README.md).

¹ While the pipeline builds the frontend and bundles it into released JAR files, for local development it is needed to build the frontend yourself. This way you get the fancy stuff like hot reloading etc.

## Adding parsers

- At least one Unit test must be provided
- Scan a real fax and run tesseract to get realistic input data (Don't transcribe it)
- **Remove any sensitive or personal information** from the test data!