# SlackSender Plugin
A gradle task to send files to slack.

Where I work, we need to send files to slack, so this task does the work for me.
With it I also able to create custom gradle tasks that sends a file when it finishes.

This was built just to upload files, if you're looking for a plugin o send messages use [this one](https://github.com/Mindera/gradle-slack-plugin), which is based on [Java Slack-webhook](https://github.com/gpedro/slack-webhook).

# How to use

## App Token

First we need to generate a token for our app.
To do it, is simple access [this link](https://api.slack.com/docs/oauth-test-tokens) and you will be able to create a token for your team.

### Why we use a Test token?

Slack authorization was made for web integrations, where each step leads the user to another URL.

Well [here](https://api.slack.com/docs/oauth) you can read more about it, take a look, if you believe we can make it in some other way, please create a issue or a PR.

A interesting point, when you use a **test token** the slack post messages as **you**.

## How to Install

To install this task is simple, it's released into [Jitpack](https://jitpack.io/).

Go to your `build.gradle` and add the following, it can be on your *root level* or on *app level*:
```gradle
buildscript {
    repositories {
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath 'com.github.brunodles:SlackUpload:-SNAPSHOT'
    }
}
```

### Create your upload task

```gradle
task <your task name>(type: com.github.brunodles.slackupload.UploadTask) {
    token "<your token>" // required, to create the token look above
    file "<path to your file>" // required or send content
    content "<content>" // required or send file
    fileType "<type>" // optional
    filename "<file name>" // optional, will be used on download the file
    title "<title for this file>" // optional, will be shown on slack
    initial_comment "<first comment>" // optional, the description of the file
    channels "<channel name>" // required, can be a channel or a private group
}
```

### Samples

Just used to test the upload task.
```gradle
task testSlack(type: com.github.brunodles.slackupload.UploadTask) {
    token "xoxp-11111111111-22222222222-33333333333-654asd645asd645sager654hge"
    file "build.gradle"
    channels "#general"
}
```

I built this task for the project I'm working on.
```gradle
task sendQARelease(type: com.github.brunodles.slacksender.UploadTask, dependsOn: 'assembleRelease') {
    description "Send the apk to QA Channel on Slack"
    group "build"
    token "xoxp-11111111111-22222222222-33333333333-654asd645asd645sager654hge"
    file "app/build/outputs/apk/app-release.apk"
    channels "QA"
    initial_comment "*pr-${prNumber()}* - https://bitbucket.org/myteam/myproject/pull-requests/${prNumber()}"
    fileType "apk"
    filename "app-release${prNumber()}.apk"
    title getCurrentBranchCodeName()
}
```

# Contributing

Issues are welcome, create one and we will discourse about it.
If you saw any error, please reports, it will be a great help.

# Licence
You can use any code you found here, some of then I found on the internet too.

I'm using the MIT Licence, take a look on [Licence](LICENCE.md).

If you're using this task, please give me some credits too.

# Sources

* [Slack](https://slack.com/)
* [Slack fileUpload](https://api.slack.com/methods/files.upload)
* [Slack OAuth Scopes](https://api.slack.com/docs/oauth-scopes)
* [Slack OAuth test tokens](https://api.slack.com/docs/oauth-test-tokens)
* [Slack Create New App](https://api.slack.com/apps?new_app=1)

## Gradle
* [Writing Custom Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)
* [Custom Methods in build.gradle](http://stackoverflow.com/a/38032000/1622925)

## JitPack
* [JitPack](https://jitpack.io/)
* [Guide for Gradle Projects](https://jitpack.io/docs/BUILDING/#gradle-projects)
