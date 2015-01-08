package qa.qcri.aidr.dbmanager.ejb.remote.facade.imp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.qcri.aidr.dbmanager.dto.DocumentDTO;
import qa.qcri.aidr.dbmanager.dto.NominalAttributeDTO;
import qa.qcri.aidr.dbmanager.dto.NominalLabelDTO;
import qa.qcri.aidr.dbmanager.dto.TaskAssignmentDTO;
import qa.qcri.aidr.dbmanager.dto.taggerapi.ItemToLabelDTO;
import qa.qcri.aidr.dbmanager.dto.taggerapi.TrainingDataDTO;
import qa.qcri.aidr.dbmanager.ejb.local.facade.impl.CoreDBServiceFacadeImp;
import qa.qcri.aidr.dbmanager.ejb.remote.facade.MiscResourceFacade;
import qa.qcri.aidr.dbmanager.entities.task.Document;

/**
 * 
 * @author Koushik
 *
 */
@Stateless(name="MiscResourceFacadeImp")
public class MiscResourceFacadeImp extends CoreDBServiceFacadeImp<Document, Long> implements MiscResourceFacade {
	private static Logger logger = LoggerFactory.getLogger("aidr-db-manager");

	@EJB
	private qa.qcri.aidr.dbmanager.ejb.remote.facade.DocumentResourceFacade documentEJB;

	@EJB
	private qa.qcri.aidr.dbmanager.ejb.remote.facade.TaskAssignmentResourceFacade taskAssignmentEJB;

	public MiscResourceFacadeImp() {
		super(Document.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrainingDataDTO> getTraningDataByCrisisAndAttribute(Long crisisID, Long modelFamilyID, int fromRecord, int limit,
			String sortColumn, String sortDirection) {
		List<TrainingDataDTO> trainingDataList = new ArrayList<TrainingDataDTO>();

		String orderSQLPart = "";
		if (sortColumn != null && !sortColumn.isEmpty()){
			if (sortDirection != null && !sortDirection.isEmpty()) {
				if ("ASC".equals(sortDirection)) {
					sortDirection = "ASC";
				} else {
					sortDirection = "DESC";
				}
			} else {
				sortDirection = "DESC";
			}
			orderSQLPart += " ORDER BY " + sortColumn + " " + sortDirection + " ";
		}
		else{
			orderSQLPart += " ORDER BY dnl.timestamp DESC";
		}

		String sql = " SELECT distinct lbl.nominalLabelID, lbl.name labelName, d.data tweetJSON, u.userID, u.name labelerName, dnl.timestamp, d.documentID "
				+ " FROM document_nominal_label dnl "
				+ " JOIN nominal_label lbl on lbl.nominalLabelID=dnl.nominalLabelID "
				+ " JOIN model_family mf on mf.nominalAttributeID=lbl.nominalAttributeID "
				+ " JOIN document d on d.documentID = dnl.documentID "
				+ " JOIN users u on u.userID = dnl.userID "
				+ " WHERE mf.modelFamilyID = :modelFamilyID AND d.crisisID = :crisisID " + orderSQLPart
				+ " LIMIT :fromRecord, :limit";

		String sqlCount = " SELECT count(distinct lbl.nominalLabelID, lbl.name, d.data, u.userID, u.name, dnl.timestamp, d.documentID) "
				+ " FROM document_nominal_label dnl "
				+ " JOIN nominal_label lbl on lbl.nominalLabelID=dnl.nominalLabelID "
				+ " JOIN model_family mf on mf.nominalAttributeID=lbl.nominalAttributeID "
				+ " JOIN document d on d.documentID = dnl.documentID "
				+ " JOIN users u on u.userID = dnl.userID "
				+ " WHERE mf.modelFamilyID = :modelFamilyID AND d.crisisID = :crisisID ";

		logger.info("getTraningDataByCrisisAndAttribute : " + sql);
		try {
			Integer totalRows;

			Query queryCount = em.createNativeQuery(sqlCount);
			queryCount.setParameter("crisisID", crisisID);
			queryCount.setParameter("modelFamilyID", modelFamilyID);
			Object res = queryCount.getSingleResult();
			totalRows = Integer.parseInt(res.toString());

			if (totalRows > 0){
				Query query = em.createNativeQuery(sql);
				query.setParameter("crisisID", crisisID);
				query.setParameter("modelFamilyID", modelFamilyID);
				query.setParameter("fromRecord", fromRecord);
				query.setParameter("limit", limit);

				List<Object[]> rows = query.getResultList();
				TrainingDataDTO trainingDataRow;
				for (Object[] row : rows) {
					trainingDataRow = new TrainingDataDTO();
					//                    Removed .intValue() as we already cast to Integer
					trainingDataRow.setLabelID((Integer) row[0]);
					trainingDataRow.setLabelName((String) row[1]);
					trainingDataRow.setTweetJSON((String) row[2]);
					trainingDataRow.setLabelerID((Integer) row[3]);
					trainingDataRow.setLabelerName((String) row[4]);
					trainingDataRow.setLabeledTime(((Date) row[5]));
					trainingDataRow.setDocumentID(((BigInteger) row[6]).longValue());
					trainingDataRow.setTotalRows(totalRows);
					trainingDataList.add(trainingDataRow);
				}
			}
			logger.info("Fetched training data list size: " + (trainingDataList != null ? trainingDataList.size() : 0));
			return trainingDataList;
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemToLabelDTO getItemToLabel(Long crisisID, Long modelFamilyID) {
		// with attributeID get attribute and labels details
		// with crisisID get an item from document table for which hasHumanLabel is FALSE
		// packup both info into one class DTO and return

		// TODO: the fields of NominalAttributeDTO need to match the ones in Tagger-API original
		NominalAttributeDTO attributeDTO = new NominalAttributeDTO();
		ItemToLabelDTO itemToLabel = new ItemToLabelDTO();
		try{
			String sqlToGetAttribute = "SELECT na.nominalAttributeID, na.code, na.name, na.description FROM nominal_attribute na"
					+ " JOIN model_family mf on mf.nominalAttributeID = na.nominalAttributeID WHERE mf.modelFamilyID = :modelFamilyID";
			Query attributeQuery = em.createNativeQuery(sqlToGetAttribute);
			attributeQuery.setParameter("modelFamilyID", modelFamilyID);
			List<Object[]> attributeResults = attributeQuery.getResultList();
			if (attributeResults != null && !attributeResults.isEmpty()) {
				attributeDTO.setNominalAttributeId(((Long)attributeResults.get(0)[0]).longValue());
				attributeDTO.setCode((String) attributeResults.get(0)[1]);
				attributeDTO.setName((String) attributeResults.get(0)[2]);
				attributeDTO.setDescription((String) attributeResults.get(0)[3]);

				String sqlToGetLabel = "SELECT nominalLabelCode, name, description FROM nominal_label WHERE nominalAttributeID = :attributeID";
				Query labelQuery = em.createNativeQuery(sqlToGetLabel);
				labelQuery.setParameter("attributeID", attributeDTO.getNominalAttributeId());
				List<Object[]> labelsResults = labelQuery.getResultList();

				List<NominalLabelDTO> labelDTOList = new ArrayList<NominalLabelDTO>();

				for (Object[] label: labelsResults){
					NominalLabelDTO labelDTO = new NominalLabelDTO();
					labelDTO.setNominalLabelCode((String)label[0]);
					labelDTO.setName((String) label[1]);
					labelDTO.setDescription((String) label[2]);

					labelDTOList.add(labelDTO);
				}
				attributeDTO.setNominalLabelsDTO(labelDTOList);
			}
			//here retrieve data from document table
			//String sqlToGetItem = "SELECT documentID, data FROM document WHERE crisisID = :crisisID AND hasHumanLabels = 0 ORDER BY RAND() LIMIT 0, 1";
			//Query documentQuery = em.createNativeQuery(sqlToGetItem);
			//documentQuery.setParameter("crisisID", crisisID);
			//List<Object[]> documentResult = documentQuery.getResultList();
			//itemToLabel.setItemID(((BigInteger) documentResult.get(0)[0]));
			//itemToLabel.setItemText(documentResult.get(0)[1].toString());

			DocumentDTO documentResult = getNewTask(crisisID);
			if (documentResult != null) {
				logger.info("For crisisID: " + crisisID + ", fetched doc id in ItemToLabel: " + documentResult.getDocumentID());
				itemToLabel.setItemID(BigInteger.valueOf(documentResult.getDocumentID()));
				itemToLabel.setItemText(documentResult.getData());
				itemToLabel.setAttribute(attributeDTO);
			} else {
				logger.info("For crisisID: " + crisisID + ", doc id: null");
			}

		} catch(Exception e) {
			logger.error("exception", e);
			return null;  
		}
		return itemToLabel;
	}

	private DocumentDTO getNewTask(Long crisisID) {
		Criterion newCriterion = Restrictions.conjunction()
				.add(Restrictions.eq("crisis.crisisId",crisisID))
				.add(Restrictions.eq("hasHumanLabels",false));
		try {
			Document document = getByCriteria(newCriterion);
			logger.debug("New task: " + document);
			if (document != null)  {
				List<TaskAssignmentDTO> tList = taskAssignmentEJB.findTaskAssignmentByID(document.getDocumentId());
				if (tList != null && tList.isEmpty()) {
					logger.info("New task: " + document.getDocumentId());
					return new DocumentDTO(document);
				}
			} else {
				logger.info("[getNewTask] New task: " + document);
			}
		} catch (Exception e) {
			logger.error("Error in getting new Task for crisisID: " + crisisID);
			logger.error("exception", e);
		}
		return null;
	}
}
