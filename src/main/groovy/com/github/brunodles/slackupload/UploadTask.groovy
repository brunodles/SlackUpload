package com.github.brunodles.slackupload

import com.github.brunodles.util.WebClient
import org.gradle.api.DefaultTask
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.tasks.TaskAction

import static SlackUpload.SLACK_UPLOAD_EXTENSION
import static com.github.brunodles.slackupload.GradleHelper.*
import static com.github.brunodles.slackupload.SlackUpload.SLACK_UPLOAD_EXTENSION

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
        def slackExtension = tryGetExtension(project, SLACK_UPLOAD_EXTENSION)
        if (!slackExtension) return
        if (!token) token = slackExtension.token
        if (!tokenFile) tokenFile = slackExtension.tokenFile
    }

    def sendToSlack() {
        validParameters()
        if (!errors.isEmpty()) {
            for (String error : errors)
                println error
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
        if (token && !token.isEmpty()) return
        if (tokenFile == null) {
            errors.add "The 'token' and the 'tokenFile' are empty, use one of those."
            return
        }

        def tokenFile = project.file(this.tokenFile)
        if (tokenFile.exists()) {
            token = tokenFile.text.trim()
        } else {
            errors.add "Token file does not exist, we will create it for you.\nCreate a test token on slack\nhttps://api.slack.com/docs/oauth-test-tokens"
            tokenFile.createNewFile()
        }
        if (token && token.isEmpty())
            errors.add "Token is empty. Look into the task or your token file"
    }

    def validateFileName() {
        if (file || content) return
        errors.add("Use 'file' or 'content' to upload something to slack.")
    }
}
