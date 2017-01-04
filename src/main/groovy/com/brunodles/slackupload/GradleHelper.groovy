package com.brunodles.slackupload

import org.gradle.api.UnknownDomainObjectException

final class GradleHelper {
    private GradleHelper() {
    }

    static def tryGetExtension(project, String extension){
        try {
            return project.extensions.getByName(extension)
        } catch (UnknownDomainObjectException e) {
            return null
        }
    }
}
