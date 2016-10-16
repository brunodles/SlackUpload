package com.github.brunodles.slackupload

import org.gradle.api.UnknownDomainObjectException

/**
 * Created by bruno on 16/10/16.
 */
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
