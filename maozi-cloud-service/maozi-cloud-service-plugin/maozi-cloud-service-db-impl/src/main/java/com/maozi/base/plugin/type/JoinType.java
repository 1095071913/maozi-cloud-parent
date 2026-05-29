package com.maozi.base.plugin.type;

import com.maozi.base.plugin.JoinBasePlugin;
import com.maozi.base.plugin.impl.join.QueryInnerJoinPlugin;
import com.maozi.base.plugin.impl.join.QueryLeftJoinPlugin;
import com.maozi.base.plugin.impl.join.QueryRightJoinPlugin;
import com.maozi.base.plugin.join.JoinBaseType;
import lombok.Getter;

@Getter
public enum JoinType {

	INNER_JOIN(JoinBaseType.INNER_JOIN, new QueryInnerJoinPlugin()),

	LEFT_JOIN(JoinBaseType.LEFT_JOIN, new QueryLeftJoinPlugin()),

	RIGHT_JOIN(JoinBaseType.RIGHT_JOIN, new QueryRightJoinPlugin()),

	;

	JoinType(JoinBaseType type, JoinBasePlugin joinPlugin) {
		
		this.type = type;
		
		this.joinPlugin = joinPlugin;
		
	}
	
	private final JoinBaseType type;
	
	private final JoinBasePlugin joinPlugin;
	
	
	public static JoinType get(String type) {

		JoinType[] joinTypes = JoinType.values();
		
		for(JoinType joinType : joinTypes) {
			
			if(joinType.getType().getType().equals(type)) {
				return joinType;
			}
			
		}
		
		return null;
		
	}
	
}
