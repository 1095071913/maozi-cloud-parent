package com.maozi.base.param;

import java.util.Collection;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidCollectionParam<T> {
	
    @Valid
    private Collection<T> list;
    
}