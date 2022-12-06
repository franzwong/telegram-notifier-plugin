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

* chatIds (String, required): The telegram group ID. Multiple group IDs are split with `,`. Example: `-12345678`, `-12345678,-23456789`.
* sendIfSuccess: (Boolean, optional): Send notification even the build succeeds. Default value is `false`.

```groovy
post {
    always {
        step([$class: 'TelegramNotifier', chatIds: '-12345678', sendIfSuccess: true])
    }
}
```
