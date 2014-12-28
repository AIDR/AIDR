package qa.qcri.aidr.manager.service.impl;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import qa.qcri.aidr.common.code.JacksonWrapper;
import qa.qcri.aidr.dbmanager.dto.CrisisAttributesDTO;
import qa.qcri.aidr.dbmanager.dto.CrisisDTO;
import qa.qcri.aidr.dbmanager.dto.NominalAttributeDTO;
import qa.qcri.aidr.dbmanager.dto.NominalLabelDTO;
import qa.qcri.aidr.dbmanager.dto.UsersDTO;
import qa.qcri.aidr.manager.dto.*;
import qa.qcri.aidr.manager.exception.AidrException;
import qa.qcri.aidr.manager.hibernateEntities.AidrCollection;
import qa.qcri.aidr.manager.service.TaggerService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;
@Service("taggerService")
public class TaggerServiceImpl implements TaggerService {

	private Logger logger = Logger.getLogger(getClass());

	//@Autowired
	//private Client client;
	@Value("${taggerMainUrl}")
	private String taggerMainUrl;

	@Value("${crowdsourcingAPIMainUrl}")
	private String crowdsourcingAPIMainUrl;

	@Value("${persisterMainUrl}")
	private String persisterMainUrl;

	@Value("${outputAPIMainUrl}")
	private String outputAPIMainUrl;

	//new DTOs introduced. -Imran
	@Override
	public List<TaggerCrisisType> getAllCrisisTypes() throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/crisisType/all");
			WebTarget webResource = client.target(taggerMainUrl + "/crisisType/all");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerAllCrisesTypesResponse crisesTypesResponse = objectMapper.readValue(jsonResponse, TaggerAllCrisesTypesResponse.class);

			if (crisesTypesResponse.getCrisisTypes() != null) {
				logger.info("Tagger returned " + crisesTypesResponse.getCrisisTypes().size() + " crises types");
			}

			return crisesTypesResponse.getCrisisTypes();
		} catch (Exception e) {
			throw new AidrException("Error while getting all crisis from Tagger", e);
		}
	}

	@Override
	public List<TaggerCrisis> getCrisesByUserId(Long userId) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/crisis?userID=" + userId);
			WebTarget webResource = client.target(taggerMainUrl + "/crisis?userID=" + userId);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerAllCrisesResponse taggerAllCrisesResponse = objectMapper.readValue(jsonResponse, TaggerAllCrisesResponse.class);

			if (taggerAllCrisesResponse.getCrisises() != null) {
				logger.info("Tagger returned " + taggerAllCrisesResponse.getCrisises().size() + " crisis for user");
			}

			return taggerAllCrisesResponse.getCrisises();
		} catch (Exception e) {
			throw new AidrException("No collection is enabled for Tagger. Please enable tagger for one of your collections.", e);
		}
	}

	@Override
	public String createNewCrises(TaggerCrisisRequest crisis) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			CrisisDTO dto = crisis.toDTO();
			logger.info("Going to create new crisis: " + dto.getCode());
			WebTarget webResource = client.target(taggerMainUrl + "/crisis");
			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(dto)), Response.class);

			return clientResponse.readEntity(String.class);
		} catch (Exception e) {
			throw new AidrException("Error while creating new crises in Tagger", e);
		}
	}

	// (6)
	@Override
	public Collection<TaggerAttribute> getAttributesForCrises(Integer crisisID, Long userId) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			// Rest call to Tagger
			//WebResource webResource = client.resource(taggerMainUrl + "/attribute/crisis/all?exceptCrisis=" + crisisID);
			WebTarget webResource = client.target(taggerMainUrl + "/attribute/crisis/all?exceptCrisis=" + crisisID);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			List<CrisisAttributesDTO> crisisAttributesResponse = objectMapper.readValue(jsonResponse, List.class);

			if (crisisAttributesResponse != null) {
				logger.info("Tagger returned " + crisisAttributesResponse.size() + " attributes available for crises with ID " + crisisID);
			} else {
				return Collections.emptyList();
			}

			return convertTaggerCrisesAttributeToDTO(crisisAttributesResponse, userId);
		} catch (Exception e) {
			throw new AidrException("Error while getting all attributes for crisis from Tagger", e);
		}
	}

	public Map<String, Integer> countCollectionsClassifiers(List<ValueModel> collectionCodes) throws AidrException {

		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(taggerMainUrl + "/crisis/crises");

			String input = "";

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			input = objectMapper.writeValueAsString(collectionCodes);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).post(Entity.json(input));
			String jsonResponse = clientResponse.readEntity(String.class);
			HashMap<String, Integer> rv = objectMapper.readValue(jsonResponse, HashMap.class);

			return rv;
		} catch (Exception e) {
			throw new AidrException("Error while getting amount of classifiers by collection codes in Tagger", e);
		}
	}

	@Override
	public TaggerCrisisExist isCrisesExist(String code) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(taggerMainUrl + "/crisis/code/" + code);
			WebTarget webResource = client.target(taggerMainUrl + "/crisis/code/" + code);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerCrisisExist crisisExist = objectMapper.readValue(jsonResponse, TaggerCrisisExist.class);

			if (crisisExist.getCrisisId() != null) {
				logger.info("Crises with the code " + code + " already exist in Tagger.");
				return crisisExist;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AidrException("Error while checking if crisis exist in Tagger", e);
		}
	}

	@Override
	public Long isUserExistsByUsername(String userName) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/user/" + userName);
			WebTarget webResource = client.target(taggerMainUrl + "/user/" + userName);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//         .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerUser taggerUser = objectMapper.readValue(jsonResponse, TaggerUser.class);

			if (taggerUser != null && taggerUser.getUserID() != null) {
				logger.info("User with the user name " + userName + " already exist in Tagger and has ID: " + taggerUser.getUserID());
				return taggerUser.getUserID();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AidrException("Error while checking if user exist in Tagger", e);
		}
	}

	@Override
	public Long addNewUser(TaggerUser taggerUser) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			WebTarget webResource = client.target(taggerMainUrl + "/user");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(taggerUser)), Response.class);

			String jsonResponse = clientResponse.readEntity(String.class);

			//TaggerUser createdUser = objectMapper.readValue(jsonResponse, TaggerUser.class);
			UsersDTO dto = objectMapper.readValue(jsonResponse, UsersDTO.class);
			if (dto != null) {
				TaggerUser createdUser = new TaggerUser(dto); 

				if (createdUser != null && createdUser.getUserID() != null) {
					logger.info("User with ID " + createdUser.getUserID() + " was created in Tagger");
					return createdUser.getUserID();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AidrException("Error while adding new user to Tagger", e);
		}
	}

	@Override
	public Integer addAttributeToCrisis(TaggerModelFamily modelFamily) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/modelfamily");
			WebTarget webResource = client.target(taggerMainUrl + "/modelfamily");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
			objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .post(ClientResponse.class, objectMapper.writeValueAsString(modelFamily));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(modelFamily)), Response.class);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerModelFamily createdModelFamily = objectMapper.readValue(jsonResponse, TaggerModelFamily.class);
			if (createdModelFamily != null && createdModelFamily.getModelFamilyID() != null) {
				logger.info("Attribute was added to crises");
				return createdModelFamily.getModelFamilyID();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AidrException("Error while adding attribute to crises", e);
		}
	}

	@Override
	public TaggerCrisis getCrisesByCode(String code) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			WebTarget webResource = client.target(taggerMainUrl + "/crisis/by-code/" + code);
			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			String jsonResponse = clientResponse.readEntity(String.class);

			//TaggerCrisis crisis = objectMapper.readValue(jsonResponse, TaggerCrisis.class);
			CrisisDTO dto = objectMapper.readValue(jsonResponse, CrisisDTO.class);
			if (dto != null) {
				TaggerCrisis crisis = new TaggerCrisis(dto);
				if (crisis != null) {
					logger.info("Tagger returned crisis with code" + crisis.getCode());
				}
				return crisis;
			}
			return null;
		} catch (Exception e) {
			throw new AidrException("Error while getting crisis by code from Tagger", e);
		}
	}

	@Override
	public TaggerCrisis updateCode(TaggerCrisis crisis) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			CrisisDTO dto = crisis.toDTO();
			WebTarget webResource = client.target(taggerMainUrl + "/crisis");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.put(Entity.json(objectMapper.writeValueAsString(dto)), Response.class);

			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerCrisis updatedCrisis = objectMapper.readValue(jsonResponse, TaggerCrisis.class);
			if (updatedCrisis != null) {
				logger.info("Crisis with id " + updatedCrisis.getCrisisID() + " was updated in Tagger");
			}

			return crisis;
		} catch (Exception e) {
			throw new AidrException("Error while getting crisis by code from Tagger", e);
		}
	}

	@Override
	public List<TaggerModel> getModelsForCrisis(Integer crisisID) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/model/crisis/" + crisisID);

			int retrainingThreshold = getCurrentRetrainingThreshold();
			WebTarget webResource = client.target(taggerMainUrl + "/model/crisis/" + crisisID);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerCrisisModelsResponse crisisModelsResponse = objectMapper.readValue(jsonResponse, TaggerCrisisModelsResponse.class);
			if (crisisModelsResponse.getModelWrapper() != null) {
				logger.info("Tagger returned " + crisisModelsResponse.getModelWrapper().size() + " models for crises with ID " + crisisID);
				List<TaggerModel> tempTaggerModel = new ArrayList<TaggerModel>();
				for (TaggerModel temp : crisisModelsResponse.getModelWrapper()) {

					TaggerModel tm = new TaggerModel();
					// System.out.println("reset0 : " + retrainingThreshold);
					tm.setRetrainingThreshold(retrainingThreshold);
					tm.setAttributeID(temp.getAttributeID());
					tm.setModelID(temp.getModelID());
					tm.setAttribute(temp.getAttribute());
					tm.setAuc(temp.getAuc());
					tm.setStatus(temp.getStatus());
					tm.setTrainingExamples(temp.getTrainingExamples());
					tm.setClassifiedDocuments(temp.getClassifiedDocuments());
					tm.setModelFamilyID(temp.getModelFamilyID());

					// System.out.println("reset : " + tm.getRetrainingThreshold());
					tempTaggerModel.add(tm);
				}

				return tempTaggerModel;
			}
			return null;
		} catch (Exception e) {
			throw new AidrException("Error while getting all models for crisis from Tagger", e);
		}
	}

	// (1)
	@Override
	public boolean createNewAttribute(TaggerAttribute attribute) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			WebTarget webResource = client.target(taggerMainUrl + "/attribute");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .post(ClientResponse.class, objectMapper.writeValueAsString(attribute));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(attribute)), Response.class);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			Boolean response = objectMapper.readValue(jsonResponse, Boolean.class);
			if (response != null) {
				logger.info("Attribute with ID " + attribute.getNominalAttributeID() + " was created in Tagger");
			}
			return response;
		} catch (Exception e) {
			throw new AidrException("Error while creating new attribute in Tagger", e);
		}
	}

	// (4)
	@Override
	public TaggerAttribute getAttributeInfo(Integer id) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			// Rest call to Tagger
			WebTarget webResource = client.target(taggerMainUrl + "/attribute/" + id);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerAttribute response = objectMapper.readValue(jsonResponse, TaggerAttribute.class);
			if (response != null) {
				logger.info("Attribute with ID " + response.getNominalAttributeID() + " was retrieved from Tagger");
				return response;
			}
			return null;
		} catch (Exception e) {
			throw new AidrException("Error while getting attribute from Tagger", e);
		}
	}

	@Override
	public TaggerLabel getLabelInfo(Integer id) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			// Rest call to Tagger
			WebTarget webResource = client.target(taggerMainUrl + "/label/" + id);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);
			TaggerLabel response = objectMapper.readValue(jsonResponse, TaggerLabel.class);
			if (response != null) {
				logger.info("Label with ID " + response.getNominalLabelID() + " was retrieved from Tagger");
				return response;
			}
			return null;
		} catch (Exception e) {
			throw new AidrException("Error while getting label from Tagger", e);
		}
	}

	// (3)
	@Override
	public boolean deleteAttribute(Integer id) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			WebTarget webResource = client.target(taggerMainUrl + "/attribute/" + id);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .delete(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).delete();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerStatusResponse response = objectMapper.readValue(jsonResponse, TaggerStatusResponse.class);
			if (response != null && response.getStatusCode() != null) {
				if ("SUCCESS".equals(response.getStatusCode())) {
					logger.info("Attribute with ID " + id + " was deleted in Tagger");
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			throw new AidrException("Error while deleting attribute in Tagger", e);
		}
	}

	@Override
	public boolean deleteTrainingExample(Integer id) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/document/removeTrainingExample/" + id);
			WebTarget webResource = client.target(taggerMainUrl + "/document/removeTrainingExample/" + id);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .delete(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).delete();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerStatusResponse response = objectMapper.readValue(jsonResponse, TaggerStatusResponse.class);
			if (response != null && response.getStatusCode() != null) {
				if ("SUCCESS".equals(response.getStatusCode())) {
					logger.info("Document with ID " + id + " was deleted in Tagger");
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			throw new AidrException("Error while deleting document in Tagger", e);
		}
	}

	@Override
	public boolean removeAttributeFromCrises(Integer modelFamilyID) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			deletePybossaApp(modelFamilyID);
			//WebResource webResource = client.resource(taggerMainUrl + "/modelfamily/" + modelFamilyID);
			WebTarget webResource = client.target(taggerMainUrl + "/modelfamily/" + modelFamilyID);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .delete(ClientResponse.class);
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).delete();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerStatusResponse response = objectMapper.readValue(jsonResponse, TaggerStatusResponse.class);
			if (response != null && response.getStatusCode() != null) {
				if ("SUCCESS".equals(response.getStatusCode())) {
					logger.info("Classifier was remove from crises by modelFamilyID: " + modelFamilyID);
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			throw new AidrException("Error while removing classifier from crisis in Tagger", e);
		}
	}

	// (2)
	@Override
	public TaggerAttribute updateAttribute(TaggerAttribute attribute) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			WebTarget webResource = client.target(taggerMainUrl + "/attribute");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .put(ClientResponse.class, objectMapper.writeValueAsString(attribute));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.put(Entity.json(objectMapper.writeValueAsString(attribute)), Response.class);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			NominalAttributeDTO updatedAttribute = objectMapper.readValue(jsonResponse, NominalAttributeDTO.class);
			if (updatedAttribute != null) {
				logger.info("Attribute with id " + updatedAttribute.getNominalAttributeId() + " was updated in Tagger");
			} else {
				return null;
			}

			return dto2taggerAttribute(updatedAttribute);
		} catch (Exception e) {
			throw new AidrException("Error while updating attribute in Tagger", e);
		}
	}
	
	private TaggerAttribute dto2taggerAttribute(NominalAttributeDTO src) {
		TaggerAttribute dest = new TaggerAttribute();
		dest.setCode(src.getCode());
		dest.setDescription(src.getDescription());
		dest.setName(src.getName());
		dest.setNominalAttributeID(src.getNominalAttributeId());
		dest.setUsers(new TaggerUser(src.getUsersDTO().getUserID()));
		List<TaggerLabel> tll = new ArrayList<TaggerLabel>();
		for (NominalLabelDTO i : src.getNominalLabelsDTO()) {
			TaggerLabel tl = new TaggerLabel();
			tl.setNominalLabelID(i.getNominalLabelId());
			tl.setName(i.getName());
			tl.setNominalLabelCode(i.getNominalLabelCode());
			tl.setDescription(i.getDescription());
			tl.setSequence(i.getSequence());
			//tl.setNominalAttribute(i.getNominalAttributeDTO());
			tll.add(tl);
		}
		dest.setNominalLabelCollection(tll);
		return dest;
	}


	@Override
	public TaggerLabel updateLabel(TaggerLabelRequest label) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/label");
			WebTarget webResource = client.target(taggerMainUrl + "/label");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .put(ClientResponse.class, objectMapper.writeValueAsString(label));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.put(Entity.json(objectMapper.writeValueAsString(label)), Response.class);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerLabel updatedLabel = objectMapper.readValue(jsonResponse, TaggerLabel.class);
			if (updatedLabel != null) {
				logger.info("Label with id " + updatedLabel.getNominalLabelID() + " was updated in Tagger");
			} else {
				return null;
			}

			return updatedLabel;
		} catch (Exception e) {
			throw new AidrException("Error while updating label in Tagger", e);
		}
	}

	@Override
	public TaggerLabel createNewLabel(TaggerLabelRequest label) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/label");
			WebTarget webResource = client.target(taggerMainUrl + "/label");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .post(ClientResponse.class, objectMapper.writeValueAsString(label));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(label)), Response.class);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerLabel response = objectMapper.readValue(jsonResponse, TaggerLabel.class);
			if (response != null) {
				logger.info("Label with ID " + response.getNominalLabelID() + " was created in Tagger");
				return response;
			} else {
				throw new AidrException("Error while creating new label in Tagger");
			}
		} catch (Exception e) {
			throw new AidrException("Error while creating new label in Tagger", e);
		}
	}

	// (7)
	@Override
	public TaggerAttribute attributeExists(String code) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(taggerMainUrl + "/attribute/code/" + code);
			WebTarget webResource = client.target(taggerMainUrl + "/attribute/code/" + code);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerAttribute attribute = objectMapper.readValue(jsonResponse, TaggerAttribute.class);
			if (attribute != null) {
				logger.info("Attribute with the code " + code + " already exist in Tagger.");
				return attribute;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AidrException("Error while checking if attribute exist in Tagger", e);
		}
	}

	@Override
	public List<TrainingDataDTO> getTrainingDataByModelIdAndCrisisId(Integer modelFamilyId,
			Integer crisisId,
			Integer start,
			Integer limit,
			String sortColumn,
			String sortDirection) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			WebTarget webResource = client.target(taggerMainUrl + "/misc/getTrainingData?crisisID=" + crisisId
					+ "&modelFamilyID=" + modelFamilyId
					+ "&fromRecord=" + start
					+ "&limit=" + limit
					+ "&sortColumn=" + sortColumn
					+ "&sortDirection=" + sortDirection);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TrainingDataRequest trainingDataRequest = objectMapper.readValue(jsonResponse, TrainingDataRequest.class);
			if (trainingDataRequest != null && trainingDataRequest.getTrainingData() != null) {
				logger.info("Tagger returned " + trainingDataRequest.getTrainingData().size() + " training data records for crisis with ID: "
						+ crisisId + " and family model with ID: " + modelFamilyId);
				return trainingDataRequest.getTrainingData();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AidrException("Error while Getting training data for Crisis and Model.", e);
		}
	}

	@Override
	public String getAssignableTask(Integer id, String userName) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//            taskBufferNumber currently always 1
			int taskBufferNumber = 1;
			//WebResource webResource = client.resource(crowdsourcingAPIMainUrl + "/taskbuffer/getassignabletask/" + userName + "/" + id + "/" + taskBufferNumber);
			WebTarget webResource = client.target(crowdsourcingAPIMainUrl
					+ "/document/getassignabletask/"
					+ userName + "/" + id + "/" + taskBufferNumber);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			logger.info("getAssignableTask - clientResponse : " + clientResponse);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new AidrException("Error while getting Assignable Task in Tagger", e);
		}
	}

	@Override
	public String getTemplateStatus(String code) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(crowdsourcingAPIMainUrl + "/template/status/crisis/code/" + code);
			WebTarget webResource = client.target(crowdsourcingAPIMainUrl + "/template/status/crisis/code/" + code);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			logger.info("getTemplateStatus - clientResponse : " + clientResponse);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new AidrException("Error while getting Template Status in Tagger", e);
		}
	}

	@Override
	public String skipTask(Integer id, String userName) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(crowdsourcingAPIMainUrl + "/taskassignment/revert/searchByDocUserName/" + userName + "/" + id);
			WebTarget webResource = client.target(crowdsourcingAPIMainUrl
					+ "/taskassignment/revert/searchByDocUserName/" + userName + "/" + id);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			logger.info("skipTask - clientResponse : " + clientResponse);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new AidrException("Error while Skip Task operation", e);
		}
	}

	@Override
	public boolean saveTaskAnswer(List<TaskAnswer> taskAnswer) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(crowdsourcingAPIMainUrl + "/taskanswer/save");
			WebTarget webResource = client.target(crowdsourcingAPIMainUrl + "/taskanswer/save");
			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();

			logger.info("saveTaskAnswer - postData : " + objectMapper.writeValueAsString(taskAnswer));
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .post(ClientResponse.class, objectMapper.writeValueAsString(taskAnswer));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(taskAnswer)), Response.class);
			logger.info("saveTaskAnswer - response status : " + clientResponse.getStatus());

			return clientResponse.getStatus() == 204;
		} catch (Exception e) {
			return true;
			//throw new AidrException("Error while saving TaskAnswer in AIDRCrowdsourcing", e);
		}
	}

	@Override
	public String loadLatestTweets(String code, String constraints) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			WebTarget webResource = client.target(outputAPIMainUrl + "/crisis/fetch/channel/filter/" + code + "?count=1000");
			System.out.println("Invoking: " + outputAPIMainUrl + "/crisis/fetch/channel/filter/" + code + "?count=1000");
			System.out.println("constraints: " + constraints);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(constraints), Response.class);

			String jsonResponse = clientResponse.readEntity(String.class);
			System.out.println("jsonResponse: " + jsonResponse);

			if (jsonResponse != null && (jsonResponse.startsWith("{") || jsonResponse.startsWith("["))) {
				return jsonResponse;
			} else {
				return "";
			}
		} catch (Exception e) {
			throw new AidrException("Error while generating Tweet Ids link in taggerPersister", e);
		}
	}

	@Override
	public ModelHistoryWrapper getModelHistoryByModelFamilyID(Integer start, Integer limit, Integer id) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(taggerMainUrl + "/model/modelFamily/" + id
			//        + "?start=" + start
			//        + "&limit=" + limit);
			WebTarget webResource = client.target(taggerMainUrl + "/model/modelFamily/" + id
					+ "?start=" + start
					+ "&limit=" + limit);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			ModelHistoryWrapper modelHistoryWrapper = objectMapper.readValue(jsonResponse, ModelHistoryWrapper.class);
			return modelHistoryWrapper;
		} catch (Exception e) {
			throw new AidrException("Error while Getting history records for Model.", e);
		}
	}

	@Override
	public List<TaggerModelNominalLabel> getAllLabelsForModel(Integer modelID) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/modelNominalLabel/" + modelID);
			WebTarget webResource = client.target(taggerMainUrl + "/modelNominalLabel/" + modelID);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggerModelLabelsResponse modelLabelsResponse = objectMapper.readValue(jsonResponse, TaggerModelLabelsResponse.class);

			if (modelLabelsResponse.getModelNominalLabelsDTO() != null) {
				logger.info("Tagger returned " + modelLabelsResponse.getModelNominalLabelsDTO().size() + " labels for model with ID " + modelID);
			}

			return modelLabelsResponse.getModelNominalLabelsDTO();
		} catch (Exception e) {
			throw new AidrException("Error while getting all labels for model from Tagger", e);
		}
	}

	@Override
	public String getRetainingThreshold() throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();

		try {

			WebTarget webResource = client.target(taggerMainUrl + "/train/samplecountthreshold");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new AidrException("getRetainingThreshold : ", e);

		}

	}

	public Map<String, Integer> getTaggersForCollections(List<String> collectionCodes) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			/**
			 * Rest call to Tagger
			 */
			//WebResource webResource = client.resource(taggerMainUrl + "/modelfamily/taggers-by-codes");
			WebTarget webResource = client.target(taggerMainUrl + "/modelfamily/taggers-by-codes");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .post(ClientResponse.class, objectMapper.writeValueAsString(new TaggersForCollectionsRequest(collectionCodes)));
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(objectMapper.writeValueAsString(new TaggersForCollectionsRequest(collectionCodes))), Response.class);

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			TaggersForCollectionsResponse taggersResponse = objectMapper.readValue(jsonResponse, TaggersForCollectionsResponse.class);
			if (taggersResponse != null && !taggersResponse.getTaggersForCodes().isEmpty()) {
				Map<String, Integer> result = new HashMap<String, Integer>();
				for (TaggersForCodes taggerForCode : taggersResponse.getTaggersForCodes()) {
					result.put(taggerForCode.getCode(), taggerForCode.getCount());
				}
				return result;
			} else {
				return Collections.emptyMap();
			}
		} catch (Exception e) {
			throw new AidrException("Error while adding new user to Tagger", e);
		}
	}

	@Override
	public boolean pingTagger() throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(taggerMainUrl + "/misc/ping");
			WebTarget webResource = client.target(taggerMainUrl + "/misc/ping");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			PingResponse pingResponse = objectMapper.readValue(jsonResponse, PingResponse.class);
			if (pingResponse != null && "RUNNING".equals(pingResponse.getStatus())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new AidrException("Error while Getting training data for Crisis and Model.", e);
		}
	}

	@Override
	public boolean pingTrainer() throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(crowdsourcingAPIMainUrl + "/util/ping/heartbeat");
			WebTarget webResource = client.target(crowdsourcingAPIMainUrl + "/util/ping/heartbeat");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			PingResponse pingResponse = objectMapper.readValue(jsonResponse, PingResponse.class);
			if (pingResponse != null && "200".equals(pingResponse.getStatus())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new AidrException("Error while Getting training data for Crisis and Model.", e);
		}
	}

	@Override
	public boolean pingAIDROutput() throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//WebResource webResource = client.resource(outputAPIMainUrl + "/manage/ping");
			WebTarget webResource = client.target(outputAPIMainUrl + "/manage/ping");

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			//String jsonResponse = clientResponse.getEntity(String.class);
			String jsonResponse = clientResponse.readEntity(String.class);

			PingResponse pingResponse = objectMapper.readValue(jsonResponse, PingResponse.class);
			if (pingResponse != null && "RUNNING".equals(pingResponse.getStatus())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new AidrException("Error while Getting training data for Crisis and Model.", e);
		}
	}

	// Added by koushik
	@Override
	public Map<String, Object> generateCSVLink(String code) throws AidrException {
		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/genCSV?collectionCode=" + code + "&exportLimit=100000");
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			//String jsonResponse = clientResponse.readEntity(String.class);
			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateCSVLink] Error while generating CSV link in Persister", e);
		}
	}

	// Added by koushik
	@Override
	public Map<String, Object> generateTweetIdsLink(String code) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		System.out.println("[generateTweetIdsLink] Received request for code: " + code);
		try {
			System.out.println("Invoked URL: " + persisterMainUrl + "/taggerPersister/genTweetIds?collectionCode=" + code + "&downloadLimited=true");
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/genTweetIds?collectionCode=" + code
					+ "&downloadLimited=true");
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			//String jsonResponse = clientResponse.readEntity(String.class);

			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			logger.info("Returning from func: " + jsonResponse);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateTweetIdsLink] Error while generating Tweet Ids link in Persister", e);
		}
	}

	@Override
	public Map<String, Object> generateJSONLink(String code, String jsonType) throws AidrException {
		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/genJson?collectionCode=" + code + "&exportLimit=100000"
					+ "&jsonType=" + jsonType);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			//String jsonResponse = clientResponse.readEntity(String.class);
			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateJSONLink] Error while generating JSON download link in Persister", e);
		}
	}

	// Added by koushik
	@Override
	public Map<String, Object> generateJsonTweetIdsLink(String code, String jsonType) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		System.out.println("[generateJsonTweetIdsLink] Received request for code: " + code);
		try {
			System.out.println("[generateJsonTweetIdsLink] Invoked URL: " + persisterMainUrl + "/taggerPersister/genJsonTweetIds?collectionCode=" + code
					+ "&downloadLimited=true&" + "&jsonType=" + jsonType);
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/genJsonTweetIds?collectionCode=" + code + "&downloadLimited=true&" + "&jsonType=" + jsonType);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			//String jsonResponse = clientResponse.readEntity(String.class);

			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			logger.info("Returning from func: " + jsonResponse);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateJsonTweetIdsLink] Error while generating JSON Tweet Ids download link in Persister", e);
		}
	}

	@Override
	public Map<String, Object> generateCSVFilteredLink(String code, String queryString) throws AidrException {
		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/filter/genCSV?collectionCode=" + code + "&exportLimit=100000");
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(queryString), Response.class);
			//String jsonResponse = clientResponse.readEntity(String.class);
			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateCSVFilteredLink] Error while generating JSON download link in Persister", e);
		}
	}

	// Added by koushik
	@Override
	public Map<String, Object> generateTweetIdsFilteredLink(String code, String queryString) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		System.out.println("[generateJsonTweetIdsLink] Received request for code: " + code);
		try {
			System.out.println("[generateTweetIdsLink] Invoked URL: " + persisterMainUrl + "/taggerPersister/filter/genTweetIds?collectionCode=" + code
					+ "&downloadLimited=true");
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/filter/genTweetIds?collectionCode=" + code + "&downloadLimited=true");
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(queryString), Response.class);
			//String jsonResponse = clientResponse.readEntity(String.class);

			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			logger.info("Returning from func: " + jsonResponse);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateTweetIdsFilteredLink] Error while generating JSON Tweet Ids download link in Persister", e);
		}
	}

	@Override
	public Map<String, Object> generateJSONFilteredLink(String code, String queryString, String jsonType) throws AidrException {
		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/filter/genJson?collectionCode=" + code + "&exportLimit=100000"
					+ "&jsonType=" + jsonType);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(queryString), Response.class);
			//String jsonResponse = clientResponse.readEntity(String.class);
			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateJSONFilteredLink] Error while generating JSON download link in Persister", e);
		}
	}

	// Added by koushik
	@Override
	public Map<String, Object> generateJsonTweetIdsFilteredLink(String code, String queryString, String jsonType) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		System.out.println("[generateJsonTweetIdsLink] Received request for code: " + code);
		try {
			System.out.println("[generateJsonTweetIdsLink] Invoked URL: " + persisterMainUrl + "/taggerPersister/filter/genJsonTweetIds?collectionCode=" + code
					+ "&downloadLimited=true&" + "&jsonType=" + jsonType);
			WebTarget webResource = client.target(persisterMainUrl + "/taggerPersister/filter/genJsonTweetIds?collectionCode=" + code + "&downloadLimited=true&" + "&jsonType=" + jsonType);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(queryString), Response.class);
			//String jsonResponse = clientResponse.readEntity(String.class);

			Map<String, Object> jsonResponse = clientResponse.readEntity(Map.class);
			logger.info("Returning from func: " + jsonResponse);
			return jsonResponse;
			/*
             if (jsonResponse != null && "http".equals(jsonResponse.substring(0, 4))) {
             return jsonResponse;
             } else {
             return "";
             }*/
		} catch (Exception e) {
			throw new AidrException("[generateJsonTweetIdsFilteredLink] Error while generating JSON Tweet Ids download link in Persister", e);
		}
	}

	private Collection<TaggerAttribute> convertTaggerCrisesAttributeToDTO(List<CrisisAttributesDTO> attributes, Long userId) {
		Map<Integer, TaggerAttribute> result = new HashMap<Integer, TaggerAttribute>();
		for (CrisisAttributesDTO a : attributes) {
			if (!result.containsKey(a.getNominalAttributeID())) {
				if (!userId.equals(a.getUserID()) && !(new Integer(1)).equals(a.getUserID())) {
					continue;
				}
				TaggerUser user = new TaggerUser(a.getUserID().longValue());
				List<TaggerLabel> labels = new ArrayList<TaggerLabel>();
				TaggerLabel label = new TaggerLabel(a.getLabelName(), a.getLabelID().longValue());
				labels.add(label);
				TaggerAttribute taggerAttribute = new TaggerAttribute(a.getCode(), a.getDescription(), a.getName(), a.getNominalAttributeID().longValue(), user, labels);
				result.put(a.getNominalAttributeID(), taggerAttribute);
			} else {
				TaggerAttribute taggerAttribute = result.get(a.getNominalAttributeID());
				List<TaggerLabel> labels = taggerAttribute.getNominalLabelCollection();
				TaggerLabel label = new TaggerLabel(a.getLabelName(), a.getLabelID().longValue());
				labels.add(label);
			}
		}
		return result.values();
	}

	private int getCurrentRetrainingThreshold() throws Exception {
		try {
			String retrainingThreshold = this.getRetainingThreshold();

			ObjectMapper mapper = JacksonWrapper.getObjectMapper();
			JsonFactory factory = mapper.getJsonFactory(); // since 2.1 use mapper.getFactory() instead
			JsonParser jp = factory.createJsonParser(retrainingThreshold);
			JsonNode actualObj = mapper.readTree(jp);

			JsonNode nameNode = actualObj.get("sampleCountThreshold");

			int sampleCountThreshold = Integer.parseInt(nameNode.asText());

			return sampleCountThreshold;
		} catch (Exception e) {
			return 50;

		}
	}

	private void deletePybossaApp(Integer modelFamilyID) {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {

			System.out.print("removeAttributeFromCrises: starting ......................................");
			//WebResource webResource = client.resource(taggerMainUrl + "/modelfamily/" + modelFamilyID);
			WebTarget webResource = client.target(taggerMainUrl + "/modelfamily/" + modelFamilyID);

			ObjectMapper objectMapper = JacksonWrapper.getObjectMapper();
			objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .delete(ClientResponse.class);
			Response resp = webResource.request(MediaType.APPLICATION_JSON).get();
			String jsonResp = resp.readEntity(String.class);
			TaggerModelFamily tm = objectMapper.readValue(jsonResp, TaggerModelFamily.class);
			String crisisCode = tm.getCrisis().getCode();
			String attributeCode = tm.getNominalAttribute().getCode();

			System.out.print("crisisCode: " + crisisCode);
			System.out.print("attributeCode: " + attributeCode);

			WebTarget webResp = client.target(crowdsourcingAPIMainUrl + "/clientapp/delete/" + crisisCode + "/" + attributeCode);

			//ClientResponse clientResponse = webResource.type(MediaType.APPLICATION_JSON)
			//        .accept(MediaType.APPLICATION_JSON)
			//        .get(ClientResponse.class);
			Response clientResp = webResource.request(MediaType.APPLICATION_JSON).get();
			logger.info("deactivated - clientResponse : " + clientResp);
		} catch (Exception e) {
			logger.error("deactivated - deletePybossaApp : " + e);
		}
	}

	@Override
	public String getAttributesAndLabelsByCrisisId(Integer id) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			//            http://scd1.qcri.org:8084/AIDRTrainerAPI/rest/crisis/id/117
			WebTarget webResource = client.target(crowdsourcingAPIMainUrl + "/crisis/id/" + id);

			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();
			logger.info("getAssignableTask - clientResponse : " + clientResponse);

			String jsonResponse = clientResponse.readEntity(String.class);

			return jsonResponse;
		} catch (Exception e) {
			throw new AidrException("Error while getting all nominal attributes and their labels for a given crisisID", e);
		}
	}

	@Override
	public int trashCollection(AidrCollection collection) throws Exception {
		int retVal = 0;
		Long crisisID = -1L;
		System.out.println("[trashCollection] request received for collection: " + collection.getCode());
		// First clean up the aidr-predict database of documents
		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(taggerMainUrl + "/manage/collection/trash/crisis/" + collection.getCode());
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			String jsonResponse = clientResponse.readEntity(String.class);
			System.out.println("[trashCollection] response from tagger-api: " + jsonResponse);
			if (jsonResponse != null && jsonResponse.contains("TRASHED")) {
				retVal = 1;
				crisisID = Long.parseLong(jsonResponse.substring(jsonResponse.indexOf(":") + 1, jsonResponse.indexOf("}")));
			} else {
				retVal = 0;
			}
		} catch (Exception e) {
			throw new AidrException("Error while attempting /trash REST call for aidr_predict", e);
		}
		System.out.println("[trashCollection] result of cleaning aidr-predict: " + crisisID);
		if (retVal > 0 && crisisID < 0) {
			return 1;		// crisis does not exist in aidr_predict table. Reason: no classifier attached
		}
		if (retVal > 0 && crisisID > 0) {
			// Final DB task - cleanup the aidr-scheduler database of micromapper tasks
			try {
				Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
				WebTarget webResource = client.target(crowdsourcingAPIMainUrl + "/clientapp/delete/crisis/" + crisisID);
				Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

				String jsonResponse = clientResponse.readEntity(String.class);
				System.out.println("[trashCollection] response from trainer-api: " + jsonResponse);
				if (jsonResponse != null && jsonResponse.equalsIgnoreCase("{\"status\":200}")) {
					System.out.println("[trashCollection] Success in trashing " + collection.getCode());
					return 1;
				} else {
					return 0;
				}
			} catch (Exception e) {
				throw new AidrException("Error while attempting /trash REST call for aidr_scheduler", e);
			}
		}
		return 0;
	}

	@Override
	public int untrashCollection(String collectionCode) throws Exception {
		System.out.println("[untrashCollection] request received for collection: " + collectionCode);
		try {
			Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
			WebTarget webResource = client.target(taggerMainUrl + "/manage/collection/untrash/crisis/" + collectionCode);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON).get();

			String jsonResponse = clientResponse.readEntity(String.class);
			System.out.println("[untrashCollection] response from tagger-api: " + jsonResponse);
			if (jsonResponse != null && jsonResponse.equalsIgnoreCase("{\"status\": \"UNTRASHED\"}")) {
				System.out.println("[trashCollection] Success in untrashing + " + collectionCode);
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			throw new AidrException("Error while attempting /untrash REST call", e);
		}
	}

	@Override
	public String loadLatestTweetsWithCount(String code, int count) throws AidrException {
		Client client = ClientBuilder.newBuilder().register(JacksonFeature.class).build();
		try {
			String constraints = "{\"constraints\":[]}";
			WebTarget webResource = client.target(outputAPIMainUrl + "/crisis/fetch/channel/filter/" + code + "?count=" + count);
			System.out.println("[loadLatestTweetsWithCount] Invoking: " + outputAPIMainUrl + "/crisis/fetch/channel/filter/" + code + "?count=" + count);
			System.out.println("[loadLatestTweetsWithCount] constraints: " + constraints);
			Response clientResponse = webResource.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(constraints), Response.class);

			String jsonResponse = clientResponse.readEntity(String.class);

			if (jsonResponse != null && (jsonResponse.startsWith("{") || jsonResponse.startsWith("["))) {
				System.out.println("[loadLatestTweetsWithCount] jsonResponse for collection " + code + ": " + jsonResponse);
				return jsonResponse;
			} else {
				System.out.println("[loadLatestTweetsWithCount] jsonResponse for collection " + code + ": \"\"");
				return "";
			}
		} catch (Exception e) {
			throw new AidrException("Error while loadLatestTweetsWithCount", e);
		}
	}

}
