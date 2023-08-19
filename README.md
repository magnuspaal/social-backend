### Social application

This is a social application backend that is 
based on Twitter.

#### Environment arguments
```agsl
ALLOWED_ORIGINS=http://localhost:3000
FILE_SERVER_URL=http://localhost:8082
FILE_SERVER_API_KEY=
JWT_SECRET=
```

#### Release
* Bump version
  * Change version in **pom.xml**
  * Create commit with message `docs: bump to <version>`
  * Create git tag with version
* Run Maven `package` lifecycle action.
* Build Docker image `docker build -t <registry/social-backend:version> --build-arg="APP_VERSION=<version>" .`
* Push Docker image `docker push <registry/social-backend:version>`