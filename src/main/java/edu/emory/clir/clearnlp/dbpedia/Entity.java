package edu.emory.clir.clearnlp.dbpedia;

import java.util.HashSet;
import java.util.Set;

public class Entity {
	private String title;
	private Set<String> aliases;
	//private Set<InstanceType> instance_types;
	private Set<String> instance;
	public Entity(String title)
	{
		this.title = title;
		aliases = new HashSet<>();
		//instance_types = new HashSet<>();
		instance = new HashSet<>();
	}
	
	public void addAlias(String alias){
		aliases.add(alias);
	}
	public void addInstance(String inst){
		instance.add(inst);
	}
	public void addAllInstance(Set<String> inst){
		instance.addAll(inst);
	}
	public String getInstance(){
		return instance.toString();
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<String> getAliases() {
		return aliases;
	}

	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}
//
//	public Set<InstanceType> getInstance_types() {
//		return instance_types;
//	}
//
//	public void setInstance_types(Set<InstanceType> instance_types) {
//		this.instance_types = instance_types;
//	}

	public Set<String> getAllInstance() {
		return instance;
	}


}