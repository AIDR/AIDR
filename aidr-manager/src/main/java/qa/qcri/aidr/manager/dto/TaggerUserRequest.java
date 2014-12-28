package qa.qcri.aidr.manager.dto;

import qa.qcri.aidr.dbmanager.dto.UsersDTO;

public class TaggerUserRequest {

    private Long userID;

    public TaggerUserRequest() {
    }

	public UsersDTO toDTO() throws Exception {
		UsersDTO dto = new UsersDTO();
		dto.setUserID(new Long(this.getUserID()));
		
		return dto;
	}
	
    public TaggerUserRequest(Long userID) {
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

}
