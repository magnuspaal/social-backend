### Social application

This is a social application backend that is 
based on Twitter.

#### Environment arguments
```agsl
PORT=
ALLOWED_ORIGINS=http://localhost:3000
FILE_SERVER_URL=http://localhost:8082
JWT_SECRET=
DB_USERNAME=
DB_PASSWORD=
DB_URL=
```

#### Release
* Run `./cicd/deploy/bump <version>`
* Push new commit and tag. GitHub Actions will deploy the container.