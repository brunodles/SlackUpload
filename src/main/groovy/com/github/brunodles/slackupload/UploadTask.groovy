package com.github.brunodles.slackupload

import com.github.brunodles.util.WebClient
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import static SlackUpload.SLACK_UPLOAD_EXTENSION

/**
 * Created by bruno on 30/09/16.
 */
public class UploadTask extends DefaultTask {

    public static final String SLACK_UPLOAD_URL = "https://slack.com/api/files.upload"
    String token
    String tokenFile
    String file
    String content
    String fileType
    String filename
    String title
    String initial_comment
    String channels

    private ArrayList<String> errors = new ArrayList<>()

    @TaskAction
    def trySendToSlack() {
        loadFromParameters()
        try {
            sendToSlack()
        } catch (Exception e) {
            e.printStackTrace(System.err)
        }
    }

    def loadFromParameters() {
        if (!project.extensions.getByName(SLACK_UPLOAD_EXTENSION)) return
        def slackExtension = project.slackUpload
        if (!token) token = slackExtension.token
        if (!tokenFile) tokenFile = slackExtension.tokenFile
    }

    def sendToSlack() {
        validParameters()
        if (!errors.isEmpty()) {
            errors.each { s -> println s }
            return
        }

        def client = new WebClient(SLACK_UPLOAD_URL)
                .setLogLevel(WebClient.LOG_BODY)
                .setContentTypeMultipartFormData()
                .addPostParameter("token", token)
        if (file) {
            def file = new File(this.file)
            if (!filename) filename = file.getName()
            client.addFileParameter("file", file)
        } else if (content) {
            client.addPostParameter("content", content)
        }
        client.addPostParameter("filename", filename)
        if (fileType) client.addPostParameter("filetype", fileType)
        if (title) client.addPostParameter("title", title)
        if (initial_comment) client.addPostParameter("initial_comment", initial_comment)
        if (channels) client.addPostParameter("channels", channels)
        def response = client.doPost()
        println "Status $response.code = $response.response"
    }

    def validParameters() {
        validateToken()
        validateFileName()
    }

    def validateToken() {
        if (token != null) return true
        if (tokenFile == null) {
            errors.add "The 'token' and the 'tokenFile' are empty, use one of those."
            return
        }

        def tokenFile = project.file(this.tokenFile)
        if (tokenFile.exists()) {
            token = tokenFile.text.trim()
        } else {
            println "Token file does not exist, we will create it for you."
            tokenFile.write("")
        }
    }

    def validateFileName() {
        if (file || content) return
        errors.add("Use 'file' or 'content' to upload something to slack.")
    }
}
