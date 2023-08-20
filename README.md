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
* Run `./cicd/deploy/bump <version>`
* Push new commit and tag. GitHub Actions will deploy the container.