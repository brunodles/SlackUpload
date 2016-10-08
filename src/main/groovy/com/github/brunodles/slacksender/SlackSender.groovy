package com.github.brunodles.slacksender

import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackSender implements Plugin<Project> {

    void apply(Project project) {
        project.task('UploadTask', type: UploadTask)
    }
}
