package com.capitalone.chariot.nuxeo.enterprise.webapp.voc;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;

public class EnterpriseVocabularyRepositoryInit extends DefaultRepositoryInit {
    private final long size;

    /**
     * Qualified default class constructor
     */
    public EnterpriseVocabularyRepositoryInit() {
        this(10);
    }

    /**
     * Full qualified default class default class constructor
     * @param size the target expected size
     */
    public EnterpriseVocabularyRepositoryInit(long size) {
        this.size = size;
    }

    /**
     * Gets the given expected size.
     * @return a valid document limit size
     */
    public long getSize() {
        return size;
    }

    @Override
    public void populate(CoreSession session) {
        super.populate(session);
        this.createDomain(session);
    }

    private void createDomain(CoreSession cs) {
        DocumentModel doc = cs.createDocumentModel("/default-domain/workspaces", "unittesting", "Workspace");
        doc.setProperty("dublincore", "title", "Unit Testing");

        // doc = cs.createDocument(doc);
        cs.createDocument(doc);
    }
}
