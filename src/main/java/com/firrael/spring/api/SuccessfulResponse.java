package com.firrael.spring.api;

public class SuccessfulResponse extends ApiResponse<Boolean> {

	private Boolean success = data;
	
	public SuccessfulResponse(Boolean data) {
		super(data);
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}
