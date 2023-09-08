<br />
<div align="center">
  <a href="https://github.com/justdeko/piley-server">
    <img src="docs/piley-server-logo.png" alt="Logo" height="120">
  </a>

<h3 align="center">piley-server</h3>

  <p align="center">
    <a href="https://github.com/justdeko/piley-server/issues">report bug</a>
    ·
    <a href="https://github.com/justdeko/piley-server/issues">suggest feature</a>
    ·
    <a href="https://github.com/justdeko/piley">piley</a>
  </p>
</div>

Backend application for uploading and synchronizing [piley](https://github.com/justdeko/piley) tasks.

This service supports the following features:

* Creating and managing user accounts
* Uploading and updating backups for user accounts

## Prerequisites

To deploy or debug the service, you need the following tools:

* docker
* docker-compose
* postgres or some sql db query engine (if you want to debug the db)
* A compatible JDK that can run java jars
* (optional) IntelliJ or another IDE that provides a lot of the tools and configuration detection out of the box

That's it!

## How to deploy

To deploy the service locally, you can either run it standalone, or use docker-compose.

### Standalone

To simply run the application in debug mode, use an IDE and run it with any configuration you want.

Otherwise, there is the option to run a jar:

First, run the `buildJar.sh` script. With the resulting jar, run the following command:

    java -jar build/libs/piley-server-all.jar -port=8080

The database needs to be deployed separately, either as a standalone service or as a docker container.
Make sure, you change the `DB_BASE_URL` system variable if you use a base url other than `localhost`.
The service needs a running postgres database to function.

### Docker

With docker, you also first need to build the jar that the dockerfile will use:

    buildJar.sh

Then, you can just run

    docker-compose up -d

to deploy the database, the service and the reverse proxy server all at once.

The default exposed port of the server is 8081, if you use it with localhost certificates and docker, you can also access it
via https://localhost/. Otherwise, use http://localhost:8081

## Usage and Customization

To use the server in combination with the app, you need to expose it to a level the android application also can see.
Based on how extensively you want to use piley, this could be anything from a local machine, the local wireless network
to entirely public.

If you don't use the application port in combination with `http`, make sure to add a valid SSL certificate as the nginx config reroutes all
http requests to https. Add the certificate and key to the following location:

    nginx/ssl

they both should be named `localhost.crt` and `localhost.key` respectively. If you want to use other names, you need to
adapt the `nginx.conf` file. If you want to have auto-renewing certificates, extend the
deployment (`docker-compose.yml`).

### Customize the application

If you want to add new user properties, check out the `app/piley/model/User.kt` class and
also `app/piley/routes/UserRoutes.kt` for api endpoints. Don't forget to modify the DAO and its implementation
under `app/piley/dao` as well.

Aside from extending the code, you can also customize the database base url inside `docker-config.env`, which is `db` (
the base url inside a network) by default.

## Authentication and limitations

piley-server currently uses basic authentication to perform any type of requests other than registering. This comes with
the traditional limitations of basic auth. The basic auth pairs are stored during runtime in a hashed table. For more
details, read the
[ktor documentation](https://ktor.io/docs/basic.html#validate-user-hash).

When the password is saved to the database, it
is encrypted using the blowfish cipher and decrypted accordingly upon retrieval. piley-server uses
the [jBCrypt library](https://github.com/jeremyh/jBCrypt) for this.

## Monitoring

If you want to connect your application to a monitoring service for health checks or similar, you can make a `GET`
request to the base service url. It will return 200 with a "Hello world" plaintext message. Otherwise, there is a
swagger UI available under `<base-url>/swagger` for api debugging
purposes.

## Planned features

To view planned features and track the general progress, visit
the [project board](https://github.com/users/justdeko/projects/1).

## Contributing

piley-server is FOSS and was developed for free. You are welcome to contribute and support, here are a few
ways:

* [Report a bug or suggest a new feature](https://github.com/justdeko/piley-server/issues)
* Extend the app

## License

[MIT](https://github.com/justdeko/piley-server/blob/main/LICENSE)
