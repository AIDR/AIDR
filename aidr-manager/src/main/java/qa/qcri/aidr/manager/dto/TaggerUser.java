package qa.qcri.aidr.manager.dto;

import qa.qcri.aidr.dbmanager.dto.UsersDTO;

public class TaggerUser {

	private Long userID;

	private String name;

	private String role;

	public TaggerUser() {
	}

	public TaggerUser(Long userID) {
		this.userID = userID;
	}

	public TaggerUser(String name, String role) {
		this.name = name;
		this.role = role;
	}

	public TaggerUser(UsersDTO dto) throws Exception {
		if (dto != null) {
			this.setName(dto.getName());
			this.setRole(dto.getRole());
			this.setUserID(dto.getUserID());
		}
	}

	public UsersDTO toDTO() throws Exception {
		UsersDTO dto = new UsersDTO(new Long(this.getUserID()), this.getName(), this.getRole());
		return dto;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}