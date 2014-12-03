// default package
// Generated Nov 24, 2014 4:55:08 PM by Hibernate Tools 4.0.0
package qa.qcri.aidr.dbmanager.entities.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * NominalLabelEvaluationDataId generated by hbm2java
 */
@Embeddable
public class NominalLabelEvaluationDataId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6826560258449521693L;
	private Long documentId;
	private Long crisisId;
	private Long nominalLabelId;
	private Long nominalAttributeId;
	private String wordFeatures;

	public NominalLabelEvaluationDataId() {
	}

	public NominalLabelEvaluationDataId(Long documentId, Long crisisId,
			Long nominalLabelId, Long nominalAttributeId) {
		this.documentId = documentId;
		this.crisisId = crisisId;
		this.nominalLabelId = nominalLabelId;
		this.nominalAttributeId = nominalAttributeId;
	}

	public NominalLabelEvaluationDataId(Long documentId, Long crisisId,
			Long nominalLabelId, Long nominalAttributeId, String wordFeatures) {
		this.documentId = documentId;
		this.crisisId = crisisId;
		this.nominalLabelId = nominalLabelId;
		this.nominalAttributeId = nominalAttributeId;
		this.wordFeatures = wordFeatures;
	}

	@Column(name = "documentID", nullable = false)
	public Long getDocumentId() {
		return this.documentId;
	}

	public void setDocumentId(long documentId) {
		this.documentId = documentId;
	}

	@Column(name = "crisisID", nullable = false)
	public Long getCrisisId() {
		return this.crisisId;
	}

	public void setCrisisId(Long crisisId) {
		this.crisisId = crisisId;
	}

	@Column(name = "nominalLabelID", nullable = false)
	public Long getNominalLabelId() {
		return this.nominalLabelId;
	}

	public void setNominalLabelId(Long nominalLabelId) {
		this.nominalLabelId = nominalLabelId;
	}

	@Column(name = "nominalAttributeID", nullable = false)
	public Long getNominalAttributeId() {
		return this.nominalAttributeId;
	}

	public void setNominalAttributeId(Long nominalAttributeId) {
		this.nominalAttributeId = nominalAttributeId;
	}

	@Column(name = "wordFeatures", length = 65535)
	public String getWordFeatures() {
		return this.wordFeatures;
	}

	public void setWordFeatures(String wordFeatures) {
		this.wordFeatures = wordFeatures;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof NominalLabelEvaluationDataId))
			return false;
		NominalLabelEvaluationDataId castOther = (NominalLabelEvaluationDataId) other;

		return (this.getDocumentId() == castOther.getDocumentId())
				&& (this.getCrisisId() == castOther.getCrisisId())
				&& (this.getNominalLabelId() == castOther.getNominalLabelId())
				&& (this.getNominalAttributeId() == castOther
						.getNominalAttributeId())
				&& ((this.getWordFeatures() == castOther.getWordFeatures()) || (this
						.getWordFeatures() != null
						&& castOther.getWordFeatures() != null && this
						.getWordFeatures().equals(castOther.getWordFeatures())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getDocumentId().intValue();
		result = 37 * result + this.getCrisisId().intValue();
		result = 37 * result + this.getNominalLabelId().intValue();
		result = 37 * result + this.getNominalAttributeId().intValue();
		result = 37
				* result
				+ (getWordFeatures() == null ? 0 : this.getWordFeatures()
						.hashCode());
		return result;
	}

}