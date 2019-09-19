package com.capitalone.chariot.nuxeo.enterprise.webapp.voc;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.sf.json.JSONArray;

@RunWith(FeaturesRunner.class)
@Features({ CoreFeature.class })
@RepositoryConfig(cleanup = Granularity.METHOD, init = EnterpriseVocabularyRepositoryInit.class)
@Deploy({ "nuxeo-blobprovider-sample-core" })
public class VocabularyEntriesTest {
	public static final String ID = "Domain.getEntries";
	private static final Log log = LogFactory.getLog(VocabularyEntriesTest.class);
	@Inject
	CoreSession coreSession;

	public static final String domainConfigPath = "/default-domain/workspaces/Global_Config";
	protected String customDomainConfigPath = "/custom-domain/workspaces/Global_Config";

	@Test
	public void test() throws IOException {
		String directoryName = "article_type";
		String domain = "default-domain";
		StringBlob entries = null;
		DocumentModel model = getVocabuliryDocument(coreSession, "Global_Config");
		DocumentModel out = coreSession.createDocument(model);

		boolean checkGlobal = false;

		// Path for Global configBundle.
		PathRef customConfigPathref = new PathRef(customDomainConfigPath);
		DocumentModel customConfig = null;
		BlobHolder customConfigBH = null;
		Blob customConfigBlob = null;

		try {
			customConfig = coreSession.getDocument(customConfigPathref);
			customConfigBH = customConfig.getAdapter(BlobHolder.class);
			customConfigBlob = customConfigBH.getBlob();
		} catch (DocumentNotFoundException e) {
			log.error("Having issues in parsing or obtainging custom domain config ");
			checkGlobal = true;
		}

		ObjectMapper mapper = new ObjectMapper();
		List<VocabularyItem> vocabularyList = new ArrayList<VocabularyItem>();
		if (checkGlobal) {
			try {

				PathRef configPathref = new PathRef(domainConfigPath);

				DocumentModel domainConfig = null;
				BlobHolder domainConfigBH = null;
				Blob domainConfigBlob = null;
				try {
					domainConfig = coreSession.getDocument(configPathref);
					if (domainConfig != null) {
						domainConfigBH = domainConfig.getAdapter(BlobHolder.class);
					}
					if (domainConfigBH != null) {
						domainConfigBlob = domainConfigBH.getBlob();
					}
				} catch (DocumentNotFoundException e) {
					log.error("Having issues in parsing or obtainging local domain level config for domain " + domain
							+ " Resorting to Global Config");
					checkGlobal = false;
				}
				if (checkGlobal) {
					JsonNode rootNode = null;
					log.info("domainConfigBlob is " + domainConfigBlob);
					log.info("domainConfigBlob.getFile() is " + domainConfigBlob.getFile());

					if (domainConfigBlob != null && domainConfigBlob.getFile() != null) {
						rootNode = mapper.readTree(domainConfigBlob.getFile());
						log.info("rootNode is " + rootNode);
					}

					if (rootNode != null) {
						JsonNode cbRoot = rootNode.path("config_bundle");
						if (!cbRoot.isMissingNode() && cbRoot.has(directoryName)) {
							ArrayNode vocRoot = (ArrayNode) cbRoot.path(directoryName);
							log.info("config_bundle node and directoryName available");
							log.info("size of elements in " + directoryName + " is " + vocRoot.size());

							if (!vocRoot.isMissingNode() && vocRoot.has(0)) {
								vocRoot.forEach(node -> {
									try {
										Map<String, String> vocMap = mapper.readValue(node.toString(), Map.class);
										vocMap.entrySet().stream()
												.filter(voc -> !vocabularyList.stream().anyMatch(
														eItem -> eItem.getId().equalsIgnoreCase(voc.getKey())))
												.forEach(voc -> vocabularyList
														.add(new VocabularyItem(voc.getKey(), voc.getValue())));
										log.info("##### rootNode is " + node.toString());
										log.info("##### rootNode is " + vocMap.toString());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								});
							} else {
								// we need to resort to the global config bundle
								log.info("Domain level config for domain - " + domain
										+ " does not have the requried directoryName... Resorting to Global config for directory - "
										+ directoryName);
								checkGlobal = true;
							}
						}
					}
					if (vocabularyList.isEmpty()) {
						log.info("vocab is empty");
						log.info("Domain level config for domain - " + domain
								+ " does not have the requried directoryName... Resorting to Global config for directory - "
								+ directoryName);
						// checkGlobal = true;
					}
				}

			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				entries = new StringBlob(mapper.writeValueAsString(vocabularyList), "application/json");
				// UNABLE TO CREATE DOCUENT In CUSTOM domain
				// createVocabu(coreSession, entries,customDomainConfigPath);

			} catch (IOException e) {
				log.info("ERROR:: Converting parsing vocabulary list into JSON output." + e.getMessage().toString());
			}
		}
	}

	@SuppressWarnings("unused")
	private void createVocabu(CoreSession coreSession2, StringBlob entries, String customDomainConfigPath2) {
		// TODO Auto-generated method stub
		DocumentModel doc = coreSession2.createDocumentModel(customDomainConfigPath2, "config", "File");
		doc.setPropertyValue("file:content", (Serializable) entries);
		coreSession2.createDocument(doc);

	}

	private DocumentModel getVocabuliryDocument(CoreSession cs, String title) throws IOException {

		// Java API for JSON parsing
		/*
		 * ConfigBundle will hold something like this.
		 * {"config_bundle":{"ent_products":[{"Author":"Author",
		 * "Display":"Display"}],"article_type":[{"Suvendu": "Suvendu",
		 * "Swami":"Swami","Satya":"Satya","Jason":"Jason"}]}}
		 */
		DocumentModel doc = cs.createDocumentModel("/default-domain/workspaces", title, "File");
		String content = "{\"config_bundle\":{\"ent_products\":[{\"Author\":\"Author\",\"Display\":\"Display\"}],\"article_type\":[{\"id\":\"article\",\"label\":\"Article\",\"obsolete\":\"0\",\"ordering\":\"2\"}]}}";
		Blob blob = Blobs.createJSONBlob(content);
		doc.setPropertyValue("file:content", (Serializable) blob);
		return doc;
	}

}
