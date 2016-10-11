package com.github.brunodles.slackupload

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackUpload implements Plugin<Project> {

    public static final String SLACK_UPLOAD_EXTENSION = "slackUpload"

    @Override
    void apply(Project project) {
        project.extensions.create(SLACK_UPLOAD_EXTENSION, SlackUploadPluginExtension)
        project.task('createTokenFile') {
            description "Create a token file using 'slackUpload' plugin settings"
            group 'setup'
            doLast {
                if (!project.extensions.getByName(SLACK_UPLOAD_EXTENSION)) {
                    println "Can't find 'slackUpload' settings"
                    return
                }
                tokenFile = project.slackUpload.tokenFile
                project.file(tokenFile).write("")
            }
        }
    }
}

class SlackUploadPluginExtension {
    String token
    String tokenFile
}