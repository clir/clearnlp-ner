package edu.emory.clir.clearnlp.ner;

import java.util.HashMap;
import java.util.Map;

public class EntityGraph {

	private Map<String,Entity> entity_map;

	
	public EntityGraph(){
		entity_map = new HashMap<>();
	}
	
	public void addEntity(Entity entity){
		if(entity_map.get(entity.getTitle())!=null){
			Entity temp = entity_map.get(entity.getTitle());
			temp.addAllInstance(entity.getAllInstance());
		}else{
		entity_map.put(entity.getTitle(), entity);
		}
	}
	
	
	public int addAlias(String entity, String alias){
		Entity temp;
		if((temp=entity_map.get(entity))!=null){
		temp.addAlias(alias);
		return 1;
		}
		else{
			return -1;
		}
	}

	public Map<String, Entity> getEntity_map() {
		return entity_map;
	}

	public void setEntity_map(Map<String, Entity> entity_map) {
		this.entity_map = entity_map;
	}
}
