package qa.qcri.aidr.task.dto.util;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.aidr.task.dto.DocumentDTO;
import qa.qcri.aidr.task.entities.Document;

public class DocumentDTOHelper {
	
	public static Document toDocument(DocumentDTO documentDTO) {
		Document doc = new Document();
		if (documentDTO != null) {
			doc.setDocumentID(documentDTO.getDocumentID());
			
			doc.setCrisisID(documentDTO.getCrisisID());
	
			doc.setDoctype(documentDTO.getDoctype());
			doc.setData(documentDTO.getData());
			doc.setIsEvaluationSet(documentDTO.getIsEvaluationSet());
			doc.setGeoFeatures(documentDTO.getGeoFeatures());
			doc.setLanguage(documentDTO.getLanguage());
			doc.setHasHumanLabels(documentDTO.getHasHumanLabels());

			doc.setReceivedAt(documentDTO.getReceivedAt());
			doc.setWordFeatures(documentDTO.getWordFeatures());
			doc.setValueAsTrainingSample(documentDTO.getValueAsTrainingSample());
	
			doc.setTaskAssignment(TaskAssignmentDTOHelper.toTaskAssignment(documentDTO.getTaskAssignment()));
			
			doc.setNominalLabelCollection(NominalLabelDTOHelper.toNominalLabelCollection(documentDTO.getNominalLabelCollection()));
			return doc;
		} 
		return null;
	}

	public static List<Document> toDocumentList(List<DocumentDTO> documentList) {
		List<Document> docList = null;
		if (documentList != null) {
			docList = new ArrayList<Document>(documentList.size());
			for (DocumentDTO document: documentList) {
				Document doc = new Document();
				if (document != null) {
					doc.setDocumentID(document.getDocumentID());
					doc.setCrisisID(document.getCrisisID());
					doc.setDoctype(document.getDoctype());
					doc.setData(document.getData());
					doc.setIsEvaluationSet(document.getIsEvaluationSet());
					doc.setGeoFeatures(document.getGeoFeatures());
					doc.setLanguage(document.getLanguage());
					doc.setHasHumanLabels(document.getHasHumanLabels());

					doc.setReceivedAt(document.getReceivedAt());
	
					doc.setWordFeatures(document.getWordFeatures());
					doc.setValueAsTrainingSample(document.getValueAsTrainingSample());
					
					doc.setTaskAssignment(TaskAssignmentDTOHelper.toTaskAssignment(document.getTaskAssignment()));
					
					doc.setNominalLabelCollection(NominalLabelDTOHelper.toNominalLabelCollection(document.getNominalLabelCollection()));
					docList.add(doc);
				}
			}
		} 
		return docList;
	}

	public static DocumentDTO toDocumentDTO(Document document) {
		DocumentDTO doc = new DocumentDTO();
		if (document != null) {
			doc.setDocumentID(document.getDocumentID());
			doc.setCrisisID(document.getCrisisID());
			doc.setDoctype(document.getDoctype());
			doc.setData(document.getData());
			doc.setIsEvaluationSet(document.getIsEvaluationSet());
			doc.setGeoFeatures(document.getGeoFeatures());
			doc.setLanguage(document.getLanguage());
			doc.setHasHumanLabels(document.getHasHumanLabels());

			doc.setReceivedAt(document.getReceivedAt());

			doc.setWordFeatures(document.getWordFeatures());
			doc.setValueAsTrainingSample(document.getValueAsTrainingSample());
			
			doc.setTaskAssignmentDTO(TaskAssignmentDTOHelper.toTaskAssignmentDTO(document.getTaskAssignment()));
			
			doc.setNominalLabelCollection(NominalLabelDTOHelper.toNominalLabelDTOCollection(document.getNominalLabelCollection()));
			return doc;
		} 
		return null;
	}

	public static List<DocumentDTO> toDocumentDTOList(List<Document> documentList) {
		List<DocumentDTO> docList = null;
		if (documentList != null) {
			docList = new ArrayList<DocumentDTO>(documentList.size());
			for (Document document: documentList) {
				DocumentDTO doc = new DocumentDTO();
				if (document != null) {
					doc.setDocumentID(document.getDocumentID());
					doc.setCrisisID(document.getCrisisID());
					doc.setDoctype(document.getDoctype());
					doc.setData(document.getData());
					doc.setIsEvaluationSet(document.getIsEvaluationSet());
					doc.setGeoFeatures(document.getGeoFeatures());
					doc.setLanguage(document.getLanguage());
					doc.setHasHumanLabels(document.getHasHumanLabels());

					doc.setReceivedAt(document.getReceivedAt());
	
					doc.setWordFeatures(document.getWordFeatures());
					doc.setValueAsTrainingSample(document.getValueAsTrainingSample());
					
					doc.setTaskAssignmentDTO(TaskAssignmentDTOHelper.toTaskAssignmentDTO(document.getTaskAssignment()));
					
					doc.setNominalLabelCollection(NominalLabelDTOHelper.toNominalLabelDTOCollection(document.getNominalLabelCollection()));
					docList.add(doc);
				} 
			}
		}
		return docList;
	}
}