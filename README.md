# Telegram Notifier Jenkins Plugin

## Introduction

This Jenkins plugin sends build notification through Telegram. It is referenced from [Build Notifications Plugin](https://github.com/jenkinsci/build-notifications-plugin). 
This plugin is pipeline compatible.

## Build

```
mvn install
```

## Deploy to Jenkins

1. Copy `target/telegram-notifier-plugin.hpi` to `JENKINS_HOME/plugins`.
2. Restart Jenkins

## Set up plugin

1. `Manage Jenkins` => `Configure System` => `Telegram Notifier Plugin`
2. Put the bot token (e.g. `123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11`)
3. Save configuration

## Usage

### Parameters

* defaultChatIds: (String, required) Default telegram chat IDs.
* sendIfSuccess: (Boolean, optional) Send notification even the build succeeds. Default value is `false`.
* successfulChatIds: (String, optional) Telegram chat IDs for sending notifications regarding successful builds. Default chat IDs is used if it is not defined.
* brokenChatIds: (String, optional) Telegram chat IDs for sending notifications regarding broken builds. Default chat IDs is used if it is not defined.
* stillBrokenChatIds: (String, optional) Telegram chat IDs for sending notifications regarding subsequent broken builds. Default chat IDs is used if it is not defined.
* fixedChatIds: (String, optional) Telegram chat IDs for sending notifications regarding builds that fixed a broken one. Default chat IDs is used if it is not defined.

Multiple chat IDs should be separated with `,`. Example: `-12345678,-23456789`.

Example:

```groovy
post {
    always {
        step([$class: 'TelegramNotifier', defaultChatIds: '-12345678', sendIfSuccess: true, brokenChatIds: '-23456789', stillBrokenChatIds: '-23456789', fixedChatIds: '-12345678,-23456789'])
    }
}
```
