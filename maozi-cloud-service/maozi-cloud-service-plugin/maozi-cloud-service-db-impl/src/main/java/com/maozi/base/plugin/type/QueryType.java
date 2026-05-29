package com.maozi.base.plugin.type;

import com.maozi.base.plugin.QueryBasePlugin;
import com.maozi.base.plugin.impl.query.QueryEqPlugin;
import com.maozi.base.plugin.impl.query.QueryGePlugin;
import com.maozi.base.plugin.impl.query.QueryInPlugin;
import com.maozi.base.plugin.impl.query.QueryLePlugin;
import com.maozi.base.plugin.impl.query.QueryLikePlugin;
import com.maozi.base.plugin.impl.query.QueryNePlugin;
import com.maozi.base.plugin.query.QueryBaseType;
import lombok.Getter;

@Getter
public enum QueryType {

	EQ(QueryBaseType.EQ, new QueryEqPlugin()),

	LIKE(QueryBaseType.LIKE, new QueryLikePlugin()),

	IN(QueryBaseType.IN, new QueryInPlugin()),

	NE(QueryBaseType.NE, new QueryNePlugin()),

	GE(QueryBaseType.GE, new QueryGePlugin()),

	LE(QueryBaseType.LE, new QueryLePlugin()),

	;
	
	QueryType(QueryBaseType type,QueryBasePlugin queryPlugin) {
		
		this.type = type;
		
		this.queryPlugin = queryPlugin;
		
	}
	
	private final QueryBaseType type;
	
	private final QueryBasePlugin queryPlugin;
	
	
	public static QueryType get(String type) {
		
		QueryType[] queryTypes = QueryType.values();
		
		for(QueryType queryType : queryTypes) {
			
			if(queryType.getType().getType().equals(type)) {
				return queryType;
			}
			
		}
		
		return null;
		
	}
	
}
