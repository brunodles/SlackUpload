# SlackSender Plugin
A gradle plugin to send files to slack.

Sometime in your developer life, you will need to send files to slack.
When this became a routine I wrote this plugin that does the work for me.
With it we're able to *create custom gradle tasks* that sends some file.

This was built just to upload files, if you're looking for a plugin to send messages use [this one](https://github.com/Mindera/gradle-slack-plugin), which is based on [Java Slack-webhook](https://github.com/gpedro/slack-webhook).

# How to use

## App Token

First we need to generate a token for our app.
Click on the buttom bellow to generate a token for you.

<a href="https://slack.com/oauth/authorize?scope=files:write:user&amp;client_id=69648668736.86028092004"><img alt="Add to Slack" height="40" width="139" src="https://platform.slack-edge.com/img/add_to_slack.png" srcset="https://platform.slack-edge.com/img/add_to_slack.png 1x, https://platform.slack-edge.com/img/add_to_slack@2x.png 2x"></a>

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
You can create a new task to upload the file you want.

```gradle
task <your task name>(type: com.github.brunodles.slackupload.UploadTask) {
    token "<your token>" // required, A token to identify who you are and where to send. To create the token look above. Don't need to be filled if you you use the `tokenFile`.
    tokenFile "<path to your tokenfile>" // fill with the path to your token file. Don't use when `token` is filled.
    file "<path to your file>" // fill with the path to the file you want to send. Don't fill this if `content` is filled
    content "<content>" // fill with any content, in this case the content will be converted into a Snippet. Don't fill this if `file` is filled.
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

Did you noticed the `getCurrentBranchCodeName`? It's a custom method inside my gradle file, so you can call it inside the task setup.

### Protect you token
As I said on the description, this task post as **you**, won't be cool everyone posting as you on the slack.
So you can read the token from a file.
~~To do that just replace the token value by a *file read operation* `project.file("token").text`.~~
To do that use the `tokenFile` property to setup your task.
Like this

```gradle
task sendQARelease(type: com.github.brunodles.slacksender.UploadTask, dependsOn: 'assembleRelease') {
    ...
    tokenFile "token"
}
```
Now add the file (`token`) into your `.gitignore`.

### Plugin and Multiple upload tasks
When you have multiple upload tasks you can create a *plugin extension* in your project and apply the `slackUpload` plugin in you *app level* gradle file. `root/app/build.gradle`

```gradle
apply plugin: 'com.github.brunodles.SlackUpload'
slackUpload {
    // choose one
    token "<your token>"
    tokenFile "<path to your tokenfile>" // <- this one is suggested
}
```
Just need to add this before your tasks.

# Contributing

Issues are welcome, create one and we will discourse about it.
If you saw any error, please reports, it will be a great help.

# Licence
You can use any code you found here, some of then I found on the internet too.

I'm using the MIT Licence, take a look on [Licence](LICENCE.md).

If you're using this task, please give me some credits too.

# Sources

## Slack
* [Main Page](https://slack.com/)
* [fileUpload](https://api.slack.com/methods/files.upload)
* [OAuth Scopes](https://api.slack.com/docs/oauth-scopes)
* [OAuth test tokens](https://api.slack.com/docs/oauth-test-tokens)
* [Create New App](https://api.slack.com/apps?new_app=1)

## Groovy
* [Working with files ](http://mrhaki.blogspot.com.br/2009/08/groovy-goodness-working-with-files.html)

## Gradle
* [Writing Custom Tasks](https://docs.gradle.org/current/userguide/custom_tasks.html)
* [Writing Custom Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)
* [Custom Methods in build.gradle](http://stackoverflow.com/a/38032000/1622925)
* [Writing Test for gradle tasks](https://docs.gradle.org/current/userguide/test_kit.html)
* [Execute a task in unit test ](https://discuss.gradle.org/t/how-to-execute-a-task-in-unit-test-for-custom-plugin/6771/2)
* [Input from console](http://mrhaki.blogspot.com.br/2010/09/gradle-goodness-get-user-input-values.html)
* [Null console on Gradle Task?](http://stackoverflow.com/questions/19487576/gradle-build-null-console-object)

## JitPack
* [JitPack](https://jitpack.io/)
* [Guide for Gradle Projects](https://jitpack.io/docs/BUILDING/#gradle-projects)
