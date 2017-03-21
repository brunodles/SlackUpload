import com.brunodles.slackupload.CreateTokenFileTask
import com.brunodles.slackupload.SlackUploadPlugin
import com.brunodles.slackupload.SlackUploadPluginExtension
import com.brunodles.slackupload.UploadTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static junit.framework.TestCase.assertTrue

/**
 * Created by bruno on 16/10/16.
 */
class SlackUploadPluginTest {
    private Project project

    @Before
    public void applyGradlePlugin() {
        project = ProjectBuilder.builder().build()
        SlackUploadPlugin plugin = new SlackUploadPlugin()
        plugin.apply(project)
        project.task('uploadTask', type: UploadTask) {

        }
    }

    @Test
    public void shouldHave_createTokenFileTask() {
        assertTrue(project.tasks.createTokenFile instanceof CreateTokenFileTask)
    }

    @Test
    public void shouldHave_UploadTask() {
        assertTrue(project.tasks.uploadTask instanceof UploadTask)
    }

    @Test
    public void shouldHave_SlackUploadPluginExtension() {
        assertTrue(project.extensions.slackUpload instanceof SlackUploadPluginExtension)
    }
}
