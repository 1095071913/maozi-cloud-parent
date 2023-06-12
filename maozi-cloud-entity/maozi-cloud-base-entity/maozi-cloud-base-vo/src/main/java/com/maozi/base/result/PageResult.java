package com.maozi.base.result;

import java.util.List;
import java.util.function.Supplier;

import javax.validation.Valid;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.maozi.base.AbstractBaseVomain;
import com.maozi.base.param.PageParam;

import cn.hutool.extra.cglib.CglibUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<D> extends AbstractBaseVomain {
	
	@ApiModelProperty(value = "页数",example = "1")
	private Long current;
	
	@ApiModelProperty(value = "每页数量",example = "10")
	private Long size;

	@ApiModelProperty(value = "数据总数",hidden = true)
	private Long total;
	
	@Valid
	private List<D> data;
	
	
	public static <D> PageResult<D> convertPageResult(Page page,Supplier<D> target) {
		
		List<D> data = Lists.newArrayList();
		
		if(page.getRecords().size() > 0) {
			data = CglibUtil.copyList(page.getRecords(), target);
		}
		
		return new PageResult<D>(page.getCurrent(),page.getSize(),page.getTotal(),data);
		
	}

	public static <D> PageResult<D> convertPageResult(Page page,List<D> list) {
		return new PageResult<D>(page.getCurrent(),page.getSize(),page.getTotal(),list);
	}
	
	public static <D> PageResult<D> convertPageResult(PageParam page,List list) {
		return new PageResult<D>(page.getCurrent(),page.getSize(),0L,list);
	}

}