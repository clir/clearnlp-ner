package edu.emory.clir.clearnlp.lexicon.nytimes;

public class response {

	private meta meta;
	private web_url[] docs;
	
	public response(){
		
	}
	public meta getMeta() {
		return meta;
	}
	public void setMeta(meta meta) {
		this.meta = meta;
	}
	public web_url[] getDocs() {
		return docs;
	}
	public void setDocs(web_url[] docs) {
		this.docs = docs;
	}
	
	public String getDocString(){
		return docs.toString();
	}
	
}
