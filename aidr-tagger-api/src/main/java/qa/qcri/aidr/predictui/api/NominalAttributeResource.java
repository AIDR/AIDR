/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qa.qcri.aidr.predictui.api;

import java.util.List;

import qa.qcri.aidr.common.logging.ErrorLog;
import qa.qcri.aidr.predictui.util.ResponseWrapper;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qa.qcri.aidr.common.exception.PropertyNotSetException;
import qa.qcri.aidr.dbmanager.dto.CrisisAttributesDTO;
import qa.qcri.aidr.dbmanager.dto.NominalAttributeDTO;
import qa.qcri.aidr.predictui.facade.NominalAttributeFacade;
import static qa.qcri.aidr.predictui.util.ConfigProperties.getProperty;

/**
 * REST Web Service
 *
 * @author Imran
 */
@Path("/attribute")
@Stateless
public class NominalAttributeResource {

    @Context
    private UriInfo context;
    @EJB
    private NominalAttributeFacade attributeLocalEJB;

    public NominalAttributeResource() {
    }

    private static Logger logger = LoggerFactory.getLogger(NominalAttributeResource.class);
    private static ErrorLog elog = new ErrorLog();

    @GET
    @Produces("application/json")
    @Path("/all")
    public Response getAllNominalAttributes() throws PropertyNotSetException {
        List<NominalAttributeDTO> attributeList = attributeLocalEJB.getAllAttributes();
        ResponseWrapper response = new ResponseWrapper();
        response.setNominalAttributes(attributeList);
        return Response.ok(response).build();
    }

    @GET
    @Produces("application/json")
    @Path("/crisis/all")
    public Response getAllNominalAttributesWithCrisis(@QueryParam("exceptCrisis") Long crisisID) throws PropertyNotSetException {
        List<CrisisAttributesDTO> attributeList = attributeLocalEJB.getAllAttributesExceptCrisis(crisisID);
        ResponseWrapper response = new ResponseWrapper();
        if (attributeList.isEmpty() || attributeList == null) {
            response.setMessage("No attribute left.");
            return Response.ok(response).build();
        }
        response.setCrisisAttributes(attributeList);
        return Response.ok(response).build();
    }

    @GET
    @Produces("application/json")
    @Path("{attributeID}")
    public Response getAttributesnLabelByAttrID(@PathParam("attributeID") Long attributeID) throws PropertyNotSetException {
        NominalAttributeDTO attribute = attributeLocalEJB.getAttributeByID(attributeID);
        ResponseWrapper response = new ResponseWrapper();
        if (attribute != null) {
            return Response.ok(attribute).build();
        }
        response.setMessage("no attribute found with the given id.");
        return Response.ok(response).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAttribute(NominalAttributeDTO attribute) throws PropertyNotSetException {
        boolean response = attributeLocalEJB.addAttribute(attribute);
        return Response.ok(response).build();
    }

    @PUT
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response editAttribute(NominalAttributeDTO attribute) throws PropertyNotSetException {
        try {
            attribute = attributeLocalEJB.editAttribute(attribute);
        } catch (RuntimeException e) {
            logger.error("failed to edit attribute: " + attribute.getCode());
            logger.error(elog.toStringException(e));
            return Response.ok(new ResponseWrapper(getProperty("STATUS_CODE_FAILED"), e.getCause().getCause().getMessage())).build();
        }
        return Response.ok(attribute).build();
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAttribute(@PathParam("id") Long id) throws PropertyNotSetException {
        boolean response = attributeLocalEJB.deleteAttribute(id);
		return Response.ok(response).build();
    }

    @GET
    @Produces("application/json")
    @Path("/code/{code}")
    public Response isAttributeExists(@PathParam("code") String code) throws PropertyNotSetException {
        Long attributeID = attributeLocalEJB.isAttributeExists(code);
        if (attributeID == null) {
            attributeID = 0l;
        }
        //String response = "{\"code\":\"" + code + "\", \"nominalAttributeID\":\"" + attributeID + "\"}";
        return Response.ok(attributeID).build();
    }
}
