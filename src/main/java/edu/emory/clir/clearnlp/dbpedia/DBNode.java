package edu.emory.clir.clearnlp.dbpedia;

public class DBNode
{
	private String title;
	private DBNode super_class;
	
	public DBNode(String title)
	{
		this.title = title;
	}
	
	public boolean isTitle(String title)
	{
		return this.title.equals(title);
	}
	
	public void setSuperClass(DBNode node)
	{
		super_class = node;
	}
	
	public String getTitle()
	{
		return title;
	}

	public boolean isSubclassOf(String superClassTitle)
	{
		DBNode node = super_class;
		
		while (node != null)
		{
			if (node.isTitle(superClassTitle))
				return true;
		}
		
		return false;
	}
}
