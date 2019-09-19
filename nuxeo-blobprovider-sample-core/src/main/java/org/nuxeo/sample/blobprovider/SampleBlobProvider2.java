package org.nuxeo.sample.blobprovider;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.blob.AbstractBlobProvider;
import org.nuxeo.ecm.core.blob.BlobInfo;
import org.nuxeo.ecm.core.blob.ManagedBlob;
import org.nuxeo.ecm.core.blob.SimpleManagedBlob;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SampleBlobProvider2 extends AbstractBlobProvider {

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public Blob readBlob(BlobInfo blobInfo) throws IOException {
        return new SimpleManagedBlob(blobInfo);
    }

    @Override
    public String writeBlob(Blob blob) throws IOException {
        /*
         * This Blob provider is not associated with any storage so it can't write new blobs All it does is referencing
         * external blobs using URLs
         */
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public InputStream getStream(ManagedBlob blob) throws IOException {
        /*
         * This blob provider manages blob accessible with a simpleURL so all we have to do is get the URL from the blob
         * key, open an HTTP connection and return the inputStream
         */
        String urlStr = extractUrl(blob);
        URL url = new URL(urlStr);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    /**
     * @param blob the URL of the blob is stored in the key variable
     * @return the blob URL
     */
    protected String extractUrl(ManagedBlob blob) {
        String key = blob.getKey();
        // strip prefix (the name of the provider)
        int colon = key.indexOf(':');
        if (colon >= 0) {
            key = key.substring(colon + 1);
        }
        return key;
    }

}
