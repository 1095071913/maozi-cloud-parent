package com.jiumao.swagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.bean.validators.plugins.parameter.ExpandedParameterMinMaxAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterNotBlankAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterNotNullAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterPatternAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.ExpandedParameterSizeAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.MinMaxAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.NotBlankAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.NotNullAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.PatternAnnotationPlugin;
import springfox.bean.validators.plugins.parameter.SizeAnnotationPlugin;
import springfox.bean.validators.plugins.schema.DecimalMinMaxAnnotationPlugin;

@Configuration
public class BeanValidatorPluginsConfiguration {

	@Bean
	public ExpandedParameterMinMaxAnnotationPlugin expanderMinMax() {
		return new ExpandedParameterMinMaxAnnotationPlugin();
	}

	@Bean
	public ExpandedParameterNotNullAnnotationPlugin expanderNotNull() {
		return new ExpandedParameterNotNullAnnotationPlugin();
	}

	@Bean
	public ExpandedParameterNotBlankAnnotationPlugin expanderNotBlank() {
		return new ExpandedParameterNotBlankAnnotationPlugin();
	}

	@Bean
	public ExpandedParameterPatternAnnotationPlugin expanderPattern() {
		return new ExpandedParameterPatternAnnotationPlugin();
	}

	@Bean
	public ExpandedParameterSizeAnnotationPlugin expanderSize() {
		return new ExpandedParameterSizeAnnotationPlugin();
	}

	@Bean
	public springfox.bean.validators.plugins.parameter.MinMaxAnnotationPlugin parameterMinMax() {
		return new springfox.bean.validators.plugins.parameter.MinMaxAnnotationPlugin();
	}

	@Bean
	public springfox.bean.validators.plugins.parameter.NotNullAnnotationPlugin parameterNotNull() {
		return new springfox.bean.validators.plugins.parameter.NotNullAnnotationPlugin();
	}

	@Bean
	public springfox.bean.validators.plugins.parameter.NotBlankAnnotationPlugin parameterNotBlank() {
		return new springfox.bean.validators.plugins.parameter.NotBlankAnnotationPlugin();
	}

	@Bean
	public springfox.bean.validators.plugins.parameter.PatternAnnotationPlugin parameterPattern() {
		return new springfox.bean.validators.plugins.parameter.PatternAnnotationPlugin();
	}

	@Bean
	public springfox.bean.validators.plugins.parameter.SizeAnnotationPlugin parameterSize() {
		return new springfox.bean.validators.plugins.parameter.SizeAnnotationPlugin();
	}

	@Bean
	public MinMaxAnnotationPlugin minMaxPlugin() {
		return new MinMaxAnnotationPlugin();
	}

	@Bean
	public DecimalMinMaxAnnotationPlugin decimalMinMaxPlugin() {
		return new DecimalMinMaxAnnotationPlugin();
	}

	@Bean
	public SizeAnnotationPlugin sizePlugin() {
		return new SizeAnnotationPlugin();
	}

	@Bean
	public NotNullAnnotationPlugin notNullPlugin() {
		return new NotNullAnnotationPlugin();
	}

	@Bean
	public NotBlankAnnotationPlugin notBlankPlugin() {
		return new NotBlankAnnotationPlugin();
	}

	@Bean
	public PatternAnnotationPlugin patternPlugin() {
		return new PatternAnnotationPlugin();
	}
}