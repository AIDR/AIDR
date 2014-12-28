package qa.qcri.aidr.manager.dto;

import java.util.ArrayList;
import java.util.List;

import qa.qcri.aidr.dbmanager.dto.NominalAttributeDTO;
import qa.qcri.aidr.dbmanager.dto.NominalLabelDTO;

public class TaggerAttribute {

    private String code;

    private String description;

    private String name;

    private Long nominalAttributeID;

    private TaggerUser users;

    private List<TaggerLabel> nominalLabelCollection;

    public TaggerAttribute() {
    }

    public TaggerAttribute(Long nominalAttributeID) {
        this.nominalAttributeID = nominalAttributeID;
    }

    public TaggerAttribute(String code, String description, String name, Long nominalAttributeID,
                           TaggerUser users, List<TaggerLabel> nominalLabelCollection) {
        this.code = code;
        this.description = description;
        this.name = name;
        this.nominalAttributeID = nominalAttributeID;
        this.users = users;
        this.nominalLabelCollection = nominalLabelCollection;
    }

    public TaggerAttribute(NominalAttributeDTO dto) throws Exception {
    	if (dto != null) {
    		this.setCode(dto.getCode());
    		this.setDescription(dto.getDescription());
    		this.setName(dto.getName());
    		this.setNominalAttributeID(dto.getNominalAttributeId().longValue());
    		this.setUsers(new TaggerUser(dto.getUsersDTO()));
    		
    		List<TaggerLabel> labelList = new ArrayList<TaggerLabel>();
    		for (NominalLabelDTO nb: dto.getNominalLabelsDTO()) {
    			labelList.add(new TaggerLabel(nb));
    		}
    		this.setNominalLabelCollection(labelList);
    	}
    }

    public NominalAttributeDTO toDTO() throws Exception {
    	NominalAttributeDTO dto = new NominalAttributeDTO(new Long(this.getNominalAttributeID()), this.getName(), this.getDescription(), this.getCode());
    	dto.setUsersDTO(this.getUsers().toDTO());
    	
    	List<NominalLabelDTO> nbList = new ArrayList<NominalLabelDTO>();
    	for (TaggerLabel label: this.getNominalLabelCollection()) {
    		nbList.add(label.toDTO());
    	}
    	dto.setNominalLabelsDTO(nbList);
    	return dto;
    }
    
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNominalAttributeID() {
        return nominalAttributeID;
    }

    public void setNominalAttributeID(Long nominalAttributeID) {
        this.nominalAttributeID = nominalAttributeID;
    }

    public TaggerUser getUsers() {
        return users;
    }

    public void setUsers(TaggerUser users) {
        this.users = users;
    }

    public List<TaggerLabel> getNominalLabelCollection() {
        return nominalLabelCollection;
    }

    public void setNominalLabelCollection(List<TaggerLabel> nominalLabelCollection) {
        this.nominalLabelCollection = nominalLabelCollection;
    }

}
