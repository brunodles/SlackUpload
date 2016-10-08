import com.github.brunodles.slacksender.UploadTask;
import com.github.brunodles.util.WebClient;

import java.io.File;
import java.io.IOException;

/**
 * Created by bruno on 30/09/16.
 */
public class TestSlackApi {
    public static void main(String[] args) throws IOException {
        File file = new File("settings.gradle");
        WebClient client = new WebClient(UploadTask.SLACK_UPLOAD_URL)
                .setLogLevel(WebClient.LOG_BODY)
                .setContentTypeMultipartFormData()
                .addPostParameter("token", "xoxp-69648668736-69600513651-85981698355-6a7680d63e8fa366a9f345a7a6f98af4")
                .addPostParameter("filename", file.getName())
                .addFileParameter("file", file)
//                .addPostParameter("content", "Content")
//                .addPostParameter("filetype", "groovy")
                .addPostParameter("title", "Tittle")
                .addPostParameter("initial_comment", "First!")
                .addPostParameter("channels", "#general");
        WebClient.Response response = client.doPost();
        System.out.printf("Status %s = %s", response.code, response.response);
    }
}
