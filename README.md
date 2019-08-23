## Status ##

[![Build Status](https://travis-ci.org/javakeyring/java-keyring.svg?branch=master)](https://travis-ci.org/javakeyring/java-keyring)
[![Build status](https://ci.appveyor.com/api/projects/status/x0wjmw353hid9ol4?svg=true)](https://ci.appveyor.com/project/rexhoffman/java-keyring)
[![Maven Site](https://img.shields.io/badge/maven_site-1.0.0-green.svg)](https://javakeyring.github.io/java-keyring/1.0.0/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.javakeyring/java-keyring/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.javakeyring/java-keyring)
[![codebeat badge](https://codebeat.co/badges/ebdaafc6-987c-41bd-8902-e277334aac30)](https://codebeat.co/projects/github-com-javakeyring-java-keyring-master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/13a3630bfb6b4bfc90f3e53f838b0ab3)](https://www.codacy.com/app/javakeyring/java-keyring?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=javakeyring/java-keyring&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/javakeyring/java-keyring/branch/master/graph/badge.svg)](https://codecov.io/gh/javakeyring/java-keyring)

## Summary ##

<img align="left" width="180" height="180" src="./src/site/resources/javakeyring.png">

java-keyring is a small library which provides a simple java API to store passwords and secrets securely in native os keystores.

Currently Mac OS X, Windows and Linux (GNOME) are supported.

## History ##

Initially an abandoned bitbucket repo, but lotsa love has been given to it.
*   Proper windows credential store access.
*   Delete support.
*   Solid testing.
*   Automated builds in all target environements.

Initial repo: [https://bitbucket.org/east301/java-keyring](https://bitbucket.org/east301/java-keyring)

Cloned from: [https://bitbucket.org/bpsnervepoint/java-keyring](https://bitbucket.org/bpsnervepoint/java-keyring)

## Implementation ##

__Mac OS X__
*   Passwords are stored using [OS X Keychain](https://support.apple.com/guide/keychain-access/welcome/mac) using [Keychain Services](https://developer.apple.com/documentation/security/keychain_services/keychain_items) api via "Legacy Password Storage". 
  
__Linux/Freedesktop__
*   Passwords are stored using [DBus Secret Service](https://specifications.freedesktop.org/secret-service/), you probably used [Seahorse](https://en.wikipedia.org/wiki/Seahorse_(software)).  Connection is made via the excellent [secret-service](https://github.com/swiesend/secret-service) library.

__Windows__
*   Passwords are stored using [Credential Manager](https://support.microsoft.com/en-us/help/4026814/windows-accessing-credential-manager), exceptions will contain [Error Codes](https://docs.microsoft.com/en-us/windows/win32/debug/system-error-codes).   Access is via the [Wincred](https://docs.microsoft.com/en-us/windows/win32/api/wincred/) api.  

## Usage ##

Dirt simple:

```java
    Keyring keyring = Keyring.create();
    keyring.setPassword("domain", "account", "secret");
    String secret = keyring.getPassword("domain", "account");
    keyring.deletePassword("domain", "account");
```

Recommend creating a dummy value if getPassword() fails, so that users know where to go set the value in their applications.

```java
    final Keyring keyring = Keyring.create();
    final String domain = "someDomain";
    final String account = "someAccount";
    try {
      return keyring.getPassword(domain, account);
    } catch ( PasswordAccessException ex ) {
      keyring.setPassword(domain, account, "ChangeMe");
      throw new RuntimeException("Please add the correct credentials to you keystore " 
          + keyring.getKeyringStorageType()
          + ". The credential is stored under '" + domain + "|" + account + "'"
          + "with a password that is currently 'ChangeMe'");
    }
```

## Building ##

```bash
mvn clean install
```

## License ##

Source code of java-keyring is available under a BSD license. 
See the file LICENSE.EAST301 for more details.

## PRs are Welcome ##

Outstanding work:

*   Windows error message conversion.
*   Provide easy binding for Spring / CDI / etc.
*   Support for build tools like Maven/Gradle.
*   Perhaps optional UI requests for passwords (Wincred/secret-service have Apis at least to prompt users).
*   Convert to Kotlin and test in different Kotlin build target (node/jvm/binary).
*   Update the osx binding to use non-legacy apis.

That said, this library is perfectly usable today and tested on all systems. Checkout the badges above!

## Special Thanks ##

java-keyring uses the following library, thanks a lot!
java-keyring package contains copy of compiled JNA library. 
Source code of the library is available at its project page.

*   [Java native access (JNA)](https://github.com/twall/jna)
*   [Secret Service](https://github.com/swiesend/secret-service)
