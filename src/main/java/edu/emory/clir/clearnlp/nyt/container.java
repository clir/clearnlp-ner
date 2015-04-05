package edu.emory.clir.clearnlp.nyt;

public class container {

	private response response;
	private String status;
	private String copyright;
	public response getResponse() {
		return response;
	}
	public void setResponse(response response) {
		this.response = response;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	
}
