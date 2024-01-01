package com.maozi.base.plugin.type;

import com.maozi.base.plugin.JoinBasePlugin;
import com.maozi.base.plugin.impl.join.QueryInnerJoinPlugin;
import com.maozi.base.plugin.impl.join.QueryLeftJoinPlugin;
import com.maozi.base.plugin.impl.join.QueryRightJoinPlugin;
import com.maozi.base.plugin.join.JoinBaseType;
import lombok.Getter;

/**  
 * @Title: QueryType.java
 *
 * @Description: TODO
 *
 * @Author: 彭晋龙
 *
 * @Date: 2023-06-21 09:48:40
 *
 */

@Getter
public enum JoinType {

	innerJoin(JoinBaseType.innerJoin,new QueryInnerJoinPlugin()),leftJoin(JoinBaseType.leftJoin,new QueryLeftJoinPlugin()),rightJoin(JoinBaseType.rightJoin,new QueryRightJoinPlugin());

	JoinType(JoinBaseType type, JoinBasePlugin joinPlugin) {
		
		this.type = type;
		
		this.joinPlugin = joinPlugin;
		
	}
	
	private JoinBaseType type;
	
	private JoinBasePlugin joinPlugin;
	
	
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
