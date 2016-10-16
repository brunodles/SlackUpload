package com.github.brunodles.slackupload

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static SlackUpload.SLACK_UPLOAD_EXTENSION
import static com.github.brunodles.slackupload.GradleHelper.tryGetExtension

class CreateTokenFileTask extends DefaultTask {

    @TaskAction
    def createFile() {
        def slackExtension = getSlackExtension()
        if (!slackExtension) return
        def tokenFile = project.file(slackExtension.tokenFile)
        if (!existis(tokenFile)) return
        String token = getToken()
        writeToken(token, tokenFile)
    }

    def getSlackExtension() {
        def extension = tryGetExtension(project, SLACK_UPLOAD_EXTENSION)
        if (!extension) {
            println "Can't find 'slackUpload' settings"
            return null
        }
        return extension;
    }

    private boolean existis(File tokenFile) {
        if (tokenFile.exists()) {
            println 'Token file already exists!'
            return false
        }
        return true
    }

    private String getToken() {
        String token = ""
        def console = System.console()
        println "Create a test token on slack\nhttps://api.slack.com/docs/oauth-test-tokens"
        if (console) {
            token = console.readLine('> Please enter your token: ')
        } else {
            println "Cannot get console, you're probably running the task on a daemon.\nCreating empty file."
        }
        token
    }

    private void writeToken(String token, File tokenFile) {
        if (token)
            token = token.trim()
        else
            token = ""
        tokenFile.write(token)
    }
}
