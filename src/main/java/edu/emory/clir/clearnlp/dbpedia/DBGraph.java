package edu.emory.clir.clearnlp.dbpedia;

import java.util.HashMap;
import java.util.Map;

import com.sun.xml.internal.txw2.IllegalAnnotationException;

public class DBGraph
{
	private Map<String,DBNode> node_map;
	
	public DBGraph()
	{
		node_map = new HashMap<>();
	}

	public void addNode(DBNode node)
	{
		node_map.put(node.getTitle(), node);
	}
	
	public void setSuperClass(DBNode node, String superClassTitle)
	{
		DBNode superClass = node_map.get(superClassTitle);
		
		if (superClass != null)
			node.setSuperClass(superClass);
		else
			throw new IllegalAnnotationException("Super class doesn't exist: "+superClassTitle);
	}
}