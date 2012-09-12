package net.z428.hybriddb;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entity implementation class for Entity: SimpleHybridEntity
 * 
 */
@Entity
// @JsonIgnoreProperties({ "id", "_id", "_rev" })
public class SimpleHybridEntity implements Serializable {

	private final Logger logger = LoggerFactory.getLogger(SimpleHybridEntity.class);

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private int identifier;
	private String timestamp = "";
	private String name = "";
	private String description = "";
	private String separatedTags = "";

	private String revision = null;

	public SimpleHybridEntity() {
		super();
	}

	public int getIdentifier() {
		return identifier;
	}

	@JsonProperty("_id")
	public String getId() {
		return "" + identifier;
	}

	@JsonProperty("_id")
	public void setId(String identifier) {
		this.identifier = Integer.parseInt(identifier);
	}

	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	@JsonProperty("_rev")
	public String getRevision() {
		return revision;
	}

	@JsonProperty("_rev")
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSeparatedTags() {
		return separatedTags;
	}

	public void setSeparatedTags(String separatedTags) {
		this.separatedTags = separatedTags;
	}

	@PostPersist
	public void persistToCouch() {
		logger.debug("trying create: {}", this.getId());
		try {
			this.getCouch().create(this);
		} catch (Exception ex) {
			logger.error("FAIL: postpersist ->  {}", ex);
		}
	}

	@PostUpdate
	public void updateInCouch() {
		logger.debug("trying update in cdb: {}", this.getId());
		try {
			final SimpleHybridEntity e = this.getCouch().get(SimpleHybridEntity.class, this.getId());
			this.setRevision(e.getRevision());
			this.getCouch().update(this);
		} catch (Exception ex) {
			logger.error("FAIL: postupdate ->  {}", ex);
		}
	}

	@PostRemove
	public void removeFromCouch() {
		logger.debug("trying remove from cdb: {}", this.getId());
		try {
			final SimpleHybridEntity e = this.getCouch().get(SimpleHybridEntity.class, this.getId());
			this.getCouch().delete(e);
		} catch (Exception ex) {
			logger.error("FAIL: postremove ->  {}", ex);
		}
	}

	private CouchDbConnector getCouch() throws IOException {
		final HttpClient httpClient = new StdHttpClient.Builder().url("http://localhost:5984").build();

		final CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		final CouchDbConnector db = new StdCouchDbConnector("hybriddb", dbInstance);
		db.createDatabaseIfNotExists();
		return db;
	}
}
