package com.maozi.common.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidCollectionParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
	
    @Valid
    private Collection<?> data;
    
}