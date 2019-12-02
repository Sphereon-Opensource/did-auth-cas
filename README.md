CAS Overlay Template
=======================

Generic CAS WAR overlay to exercise the latest versions of CAS. This overlay could be freely used as a starting template for local CAS war overlays.

# Versions

- CAS `6.0.x`
- JDK `11`

# Overview

You may invoke build commands using the `build.sh` script to work with your chosen overlay using:

```bash
./build.sh [command]
```

To see what commands are available to the build script, run:

```bash
./build.sh help
```

# Configuration

- The `etc` directory contains the configuration files and directories that need to be copied to `/etc/cas/config`.
- The specifics of the build are controlled using the `gradle.properties` file.

## Adding Modules

CAS modules may be specified under the `dependencies` block of the [Gradle build script](build.gradle):

```gradle
dependencies {
    compile "org.apereo.cas:cas-server-some-module:${project.casVersion}"
    ...
}
```

Study material:

- https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html
- https://docs.gradle.org/current/userguide/dependency_management.html

## Clear Gradle Cache

If you need to, on Linux/Unix systems, you can delete all the existing artifacts (artifacts and metadata) Gradle has downloaded using:

```bash
# Only do this when absolutely necessary!
rm -rf $HOME/.gradle/caches/
```

Same strategy applies to Windows too, provided you switch `$HOME` to its equivalent in the above command.

# Deployment

- Create a keystore file `thekeystore` under `/etc/cas`. Use the password `changeit` for both the keystore and the key/certificate entries.
- Ensure the keystore is loaded up with keys and certificates of the server.

On a successful deployment via the following methods, CAS will be available at:

* `https://cas.server.name:8443/cas`

## Building and running CAS with Passwordless DID Authentication

In order for this application to work, you need to setup connections to both `did-transports-ms` and `did-mapping-ms`:
* [Did Transports MS](https://github.com/Sphereon/did-transports-ms)
* [DID Mapping Client](https://github.com/Sphereon/did-mapping-ms)

The locations where these are running needs to be configured in `src/main/resources/application.yml` under the appropriate variables.

```yaml
sphereon:
  cas:
    did:
      auth:
        appId: <example-app-did>
        appDid: <example-did>
        didMapPort: 8080
        didMapHost: localhost
        didTransportsUrl: http://localhost:3000
        appSecret: <example-secret>
        baseCasUrl: <example-base-url>
```
The uPort app will send the signed response token to the callback based on `<example-base-url>`, note this will only work if `<example-base-url>` is an https address.

In addition, the `etc/cas/config/cas.properties` in this repo contains example configuration for enabling CAS to use http locally. In order to use these options, run:
```bash
./build.sh copy
```
to copy the configuration over to the machine directory at `/etc/cas/`.

Run the CAS web application as an executable WAR.

```bash
./build.sh run
```

## External

Deploy the binary web application file `cas.war` after a successful build to a servlet container of choice.