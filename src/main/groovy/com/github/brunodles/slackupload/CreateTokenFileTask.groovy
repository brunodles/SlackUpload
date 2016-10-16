package com.github.brunodles.slackupload

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static SlackUpload.SLACK_UPLOAD_EXTENSION
import static com.github.brunodles.slackupload.GradleHelper.tryGetExtension

class CreateTokenFileTask extends DefaultTask {

    @TaskAction
    def createFile() {
        def slackExtension = tryGetExtension(project, SLACK_UPLOAD_EXTENSION)
        if (!slackExtension) {
            println "Can't find 'slackUpload' settings"
            return
        }
        def tokenFile = slackExtension.tokenFile
        project.file(tokenFile).write("")
        println "Create a test token on slack\nhttps://api.slack.com/docs/oauth-test-tokens"
    }
}
