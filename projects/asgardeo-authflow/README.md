# Getting Started

Read more here [Securing Spring Boot APIs with Asgardeo](https://blog.stackademic.com/how-i-secured-my-spring-boot-apis-with-asgardeo-in-7-steps-24bbee813dd8)

- Get Authorization Code

```shell
curl https://api.asgardeo.io/t/{your_org_name}/oauth2/authorize?response_type=code&client_id={client_id}&redirect_uri=http://localhost:8080/login/oauth2/code/asgardeo&scope=openid%20email%20profile
```
- After authenticating, you’ll be redirected to a URL with a code parameter.

- Exchange the code for an access token.

```shell
curl -X POST \
  'https://api.asgardeo.io/t/{your_org_name}/oauth2/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Authorization: Basic {base64_encoded_client_credentials}' \
  --data-urlencode 'grant_type=authorization_code' \
  --data-urlencode 'code={authorization_code}' \
  --data-urlencode 'redirect_uri=http://localhost:8080/login/oauth2/code/asgardeo'
```

- Call Protected API

```shell
curl -H "Authorization: Bearer {access_token}" http://localhost:8080/api/posts/my
```
