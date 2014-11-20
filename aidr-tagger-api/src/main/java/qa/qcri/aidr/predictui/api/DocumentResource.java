package qa.qcri.aidr.predictui.api;

import java.io.IOException;
import java.util.List;

import qa.qcri.aidr.common.logging.ErrorLog;
import qa.qcri.aidr.predictui.util.ResponseWrapper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;





import org.codehaus.jackson.type.TypeReference;
//import org.apache.log4j.Logger;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jackson.type.TypeReference;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;



import qa.qcri.aidr.predictui.entities.Document;
import qa.qcri.aidr.predictui.facade.DocumentFacade;
import qa.qcri.aidr.task.ejb.TaskManagerRemote;

import static qa.qcri.aidr.predictui.util.ConfigProperties.getProperty;

/**
 * REST Web Service
 *
 * @author Imran
 */
@Path("/document")
@Stateless
public class DocumentResource {

	@Context
	private UriInfo context;

	@EJB
	private DocumentFacade documentLocalEJB;

	//private static Logger logger = Logger.getLogger(DocumentResource.class);
	private static Logger logger = LoggerFactory.getLogger(DocumentResource.class);
	private static ErrorLog elog = new ErrorLog();
	
	public DocumentResource() {
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public Response getAllDocuments() {
		//List<Document> documentList = documentLocalEJB.getAllDocuments();
		
		//TaskManagerEntityMapper mapper = new TaskManagerEntityMapper();
		List<Document> docList = documentLocalEJB.getAllDocuments();

		ResponseWrapper response = new ResponseWrapper();
		response.setMessage("SUCCESS");
		response.setDocuments(docList);
		return Response.ok(response).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response getDocumentByID(@PathParam("id") long id){
		logger.info("received request for : " + id);

		Document doc = documentLocalEJB.getDocumentByID(id);
		return Response.ok(doc).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{crisisID}/{attributeID}/labeled/all")
	public Response getAllLabeledDocumentByCrisisID(@PathParam("crisisID") int crisisID, @PathParam("attributeID") long attributeID){

		List<Document> documentList = documentLocalEJB.getAllLabeledDocumentbyCrisisID(crisisID, attributeID);
		
		ResponseWrapper response = new ResponseWrapper(getProperty("STATUS_CODE_SUCCESS"));
		response.setDocuments(documentList);
		return Response.ok(response).build();
	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteDocument(@PathParam("id") Long id) {
		try {
			int result = documentLocalEJB.deleteDocument(id);
			logger.info("deleted count = " + result);
		} catch (RuntimeException e) {
			return Response.ok(
					new ResponseWrapper(getProperty("STATUS_CODE_FAILED"), "Error while deleting Document.")).build();
		}
		return Response.ok(new ResponseWrapper(getProperty("STATUS_CODE_SUCCESS"))).build();
	}

	@DELETE
	@Path("/removeTrainingExample/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeTrainingExample(@PathParam("id") Long id) {
		try {
			documentLocalEJB.removeTrainingExample(id);
		} catch (RuntimeException e) {
			return Response.ok(
					new ResponseWrapper(getProperty("STATUS_CODE_FAILED"), "Error while removing Training Example.")).build();
		}
		return Response.ok(new ResponseWrapper(getProperty("STATUS_CODE_SUCCESS"))).build();
	}

	
}
