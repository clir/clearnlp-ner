package edu.emory.clir.clearnlp.lexicon.nytimes;

public class meta {

	private String hits;
	private String time;
	private int offset;

	@Override
	public String toString(){
		return "meta object" + hits + time + offset;
		
	}

	public String getHits() {
		return hits;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
