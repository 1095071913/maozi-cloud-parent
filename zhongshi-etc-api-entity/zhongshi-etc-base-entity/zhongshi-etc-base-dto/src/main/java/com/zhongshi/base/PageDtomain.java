package com.zhongshi.base;

import java.util.List;
import java.util.function.Supplier;
import javax.validation.Valid;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.hutool.extra.cglib.CglibUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDtomain<D> {

	private Long pageSize=1L;
	
	private Long pageNum=10L;
	
	@Valid
	private D data;
	
	public static <D,T> PageDtomain<List<T>> convertPageDtomain(IPage<D> page,Supplier<T> target) {
		return new PageDtomain<List<T>>(page.getCurrent(),page.getSize(),CglibUtil.copyList(page.getRecords(), target));
	}
	
}
