package com.github.brunodles.slackupload

import com.github.brunodles.util.WebClient
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by bruno on 30/09/16.
 */
public class UploadTask extends DefaultTask {

    public static final String SLACK_UPLOAD_URL = "https://slack.com/api/files.upload"
    String token
    String file
    String content
    String fileType
    String filename
    String title
    String initial_comment
    String channels

    @TaskAction
    def sendToSlack() {
        def file = null
        if (this.file) {
            file = new File(this.file)
            if (!filename)
                filename = file.getName()
        }

        def client = new WebClient(SLACK_UPLOAD_URL)
                .setLogLevel(WebClient.LOG_BODY)
                .setContentTypeMultipartFormData()
                .addPostParameter("token", token)
                .addPostParameter("filename", filename)
        if (file != null)
            client.addFileParameter("file", file)
        else if (content)
            client.addPostParameter("content", content)
        if (fileType)
            client.addPostParameter("filetype", fileType)
        if (title)
            client.addPostParameter("title", title)
        if (initial_comment)
            client.addPostParameter("initial_comment", initial_comment)
        if (channels)
            client.addPostParameter("channels", channels)
        def response = client.doPost()
        println "Status $response.code = $response.response"
    }
}
