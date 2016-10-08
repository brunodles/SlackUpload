package com.github.brunodles.slackupload

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackUpload implements Plugin<Project> {

    void apply(Project project) {
        project.task('UploadTask', type: UploadTask)
    }
}
