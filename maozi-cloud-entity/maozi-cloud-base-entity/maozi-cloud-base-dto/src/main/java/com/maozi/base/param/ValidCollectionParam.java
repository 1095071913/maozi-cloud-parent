package com.maozi.base.param;

import com.maozi.base.AbstractBaseDtomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidCollectionParam<T> extends AbstractBaseDtomain {
	
    @Valid
    private Collection<T> list;
    
}