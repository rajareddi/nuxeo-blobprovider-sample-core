package org.nuxeo.sample.blobprovider;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.blob.BlobInfo;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class })
@RepositoryConfig(cleanup = Granularity.METHOD)
@Deploy({ "nuxeo-blobprovider-sample-core", "nuxeo-blobprovider-sample-core:blobprovider-test.xml" })
public class TestSampleBlobProvider {

    public static final String URL = "https://upload.wikimedia.org/wikipedia/en/thumb/6/66/"
            + "Nuxeo-Zago-logo_big_white.png/330px-Nuxeo-Zago-logo_big_white.png";

    /*
     * The CoreSession object provides the methods to add, modify and delete content in the repository
     */
    @Inject
	CoreSession session;

    @Test
    public void testBlobProvider() throws Exception {

        /*
         * A content object is represented by a DocumentModel object in Java
         */
        DocumentModel doc = session.createDocumentModel("File");

        /*
         * The setPropertyValue is used to set a document property Here we set the document title
         */
        doc.setPropertyValue("dc:title", "MyTestDocument");

        /*
         * The BlobManager object contains a registry of all the providers declared in the application The Framework
         * object provides static methods that you can use to obtain an implementation object of a service. Here we use
         * it to get the BlobManager
         */
        BlobManager blobManager = Framework.getService(BlobManager.class);

        /*
         * Our sample blob provider is declared in the file blobprovider-test.xml which is loaded by the runtime. If you
         * have a look at it, you'll see that it declares a blob provider "sampleProvider" which implementation class is
         * SampleBlobProvider
         */
        BlobProvider myProvider = blobManager.getBlobProvider("sampleProvider");

        /*
         * The BlobInfo object is a descriptor that contains at least the necessary information to retrieve a blob
         */
        BlobInfo blobInfo = new BlobInfo();
        /*
         * The key is the most important information. Here you can see it contains the URL of the Blob
         */
        blobInfo.key = "sampleProvider:" + URL;
        blobInfo.mimeType = "image/png";
        blobInfo.filename = "logo340x60.png";
        /*
         * The length of the file must also be set. Here we need to open an http connection to get it
         */
        java.net.URL url = new URL(URL);
        URLConnection connection = url.openConnection();
        final long length = connection.getContentLengthLong();
        blobInfo.length = length;

        /*
         * The readBlob methods takes the blob description object and turns it into a Blob object
         */
        Blob blob = myProvider.readBlob(blobInfo);

        /*
         * We can now set the blob in the document using the same method as previously. Since our blob provider does not
         * store files, what is saved in the repository is really just the descriptor (the key, mimetype and filename).
         * file:content is the default property to store a blob in the File document type
         */

        doc.setPropertyValue("file:content", (Serializable) blob);

        /*
         * Now we can commit the changes in the repository
         */

        doc = session.createDocument(doc);
        session.save();

        /*
         * Let's simulate a user request and download the blob locally
         */

        /*
         * to get the blob object, we simple use the getPropertyValue method of the DocumentModel object
         */
        Blob myBlob = (Blob) doc.getPropertyValue("file:content");

        /*
         * To download the blob locally we just use the getStream method of the blob object and the standard i/o methods
         * of the Java SDK
         */
        Path filePath = Paths.get("logo.png");
        Files.copy(myBlob.getStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        File file = filePath.toFile();
        Assert.assertTrue(file.exists());
        Assert.assertEquals(length, file.length());

    }

}
