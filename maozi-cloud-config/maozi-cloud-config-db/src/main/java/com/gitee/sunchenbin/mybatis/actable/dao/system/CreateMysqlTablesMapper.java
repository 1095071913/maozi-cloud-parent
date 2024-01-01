package com.gitee.sunchenbin.mybatis.actable.dao.system;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.gitee.sunchenbin.mybatis.actable.command.SysMysqlColumns;
import com.gitee.sunchenbin.mybatis.actable.command.SysMysqlTable;
import com.gitee.sunchenbin.mybatis.actable.command.TableConfig;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;


/**
 * 创建更新表结构的Mapper
 * @author sunchenbin
 *
 */
@InterceptorIgnore(tenantLine = "true",illegalSql = "1")
public interface CreateMysqlTablesMapper {

	/**
	 * 根据结构注解解析出来的信息创建表
	 * @param tableMap 表结构的map
	 */
	public void createTable(@Param("tableMap") Map<String, TableConfig> tableMap);

	/**
	 * 根据表名查询表在库中是否存在
	 * @param tableName 表结构的map
	 * @return SysMysqlTable
	 */
	public SysMysqlTable findTableByTableName(@Param("tableName") String tableName);

	/**
	 * 根据表名查询库中该表的字段结构等信息
	 * @param tableName 表结构的map
	 * @return 表的字段结构等信息
	 */
	public List<SysMysqlColumns> findTableEnsembleByTableName(@Param("tableName") String tableName);

	/**
	 * 增加字段
	 * @param tableMap 表结构的map
	 */
	public void addTableField(@Param("tableMap") Map<String, Object> tableMap);

	/**
	 * 删除字段
	 * @param tableMap 表结构的map
	 */
	public void removeTableField(@Param("tableMap") Map<String, Object> tableMap);

	/**
	 * 更新表属性
	 * @param tableMap 表结构的map
	 */
	public void modifyTableProperty(@Param("tableMap") Map<String, TableConfig> tableMap);

	/**
	 * 修改字段
	 * @param tableMap 表结构的map
	 */
	public void modifyTableField(@Param("tableMap") Map<String, Object> tableMap);

	/**
	 * 删除主键约束，附带修改其他字段属性功能
	 * @param tableMap 表结构的map
	 */
	public void dropKeyTableField(@Param("tableMap") Map<String, Object> tableMap);

	/**
	 * 根据表名删除表
	 * @param tableName 表名
	 */
	public void dropTableByName(@Param("tableName") String tableName);

	/**
	 * 查询当前表存在的索引(除了主键索引primary)
	 * @param tableMap 表名
	 * @return 索引名列表
	 */
	public Set<String> findTableIndexByTableName(@Param("tableMap") Map<String, String> tableMap);

	/**
	 * 删除表索引
	 * @param tableMap
	 */
	public void dropTabelIndex(@Param("tableMap") Map<String, Object> tableMap);

	/**
	 * 创建索引
	 * @param tableMap
	 */
	public void addTableIndex(@Param("tableMap") Map<String, Object> tableMap);

	/**
	 * 创建唯一约束
	 * @param tableMap
	 */
	public void addTableUnique(@Param("tableMap") Map<String, Object> tableMap);

}
