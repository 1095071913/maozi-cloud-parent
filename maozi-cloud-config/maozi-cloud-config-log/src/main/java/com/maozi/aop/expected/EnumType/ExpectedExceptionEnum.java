package com.maozi.aop.expected.EnumType;
//package com.maozi.aop.expected.EnumType;
//
//import com.maozi.factory.result.AbstractBaseResult;
//import com.maozi.factory.result.code.CodeAttribute;
//import com.maozi.factory.result.error.ErrorResult;
//import com.maozi.factory.BaseResultFactory;
//import com.maozi.exception.BusinessException;
//import com.maozi.exception.ExpectedException;
//import com.maozi.exception.UnauthorizedException;
//import com.maozi.exception.PermissionDeniedException;
//
//public enum ExpectedExceptionEnum{
//	
//	BusinessException(BusinessException.class,BaseResultFactory.error(new CodeAttribute(400, ""),200)),
//	
//	UnauthorizedException(UnauthorizedException.class,BaseResultFactory.error(new CodeAttribute(401, ""),200)),
//	
//	PermissionDeniedException(PermissionDeniedException.class,BaseResultFactory.error(new CodeAttribute(403, ""),200)),
//			
//	DefaultException(Object.class,BaseResultFactory.error(new CodeAttribute(500, ""),200));
//	
//
//	ExpectedExceptionEnum(Class classType, AbstractBaseResult expectedException) {
//		this.classType=classType;
//		this.expectedException=expectedException;
//	}
//	
//	private Class classType;
//	
//	private AbstractBaseResult expectedException;
//	
//	
//	
//	public static AbstractBaseResult getErrorResult(ExpectedException expectedException){
//		
//		ExpectedExceptionEnum exceptionEnum =ExpectedExceptionEnum.DefaultException;
//        for(ExpectedExceptionEnum e : ExpectedExceptionEnum.values()){
//            if(e.classType.equals(expectedException.getClass())){
//            	exceptionEnum=e;
//                break;
//            }
//        }
//        
//        ErrorResult errorResult=(ErrorResult) exceptionEnum.expectedException;   
//        errorResult.setMsg(expectedException.getMessage()); 
//        errorResult.setSubCode(expectedException.getExceptionCode()); 
//        errorResult.getCodeData().setMessage(BaseResultFactory.applicationName+"------"+expectedException.getMessage()); 
//        return errorResult;
//        
//    }
//
//
//}
