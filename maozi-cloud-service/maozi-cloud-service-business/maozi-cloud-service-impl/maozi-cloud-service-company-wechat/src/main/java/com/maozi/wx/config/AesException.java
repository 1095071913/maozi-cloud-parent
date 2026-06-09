package com.maozi.wx.config;

/**
 * AES 加解密异常
 * <p>
 * 定义企业微信消息加解密过程中的错误码和错误描述。
 * </p>
 *
 * @author maozi
 */
@SuppressWarnings("serial")
public class AesException extends Exception {

    /** 成功 */
	public final static int OK = 0;
    /** 签名验证错误 */
	public final static int ValidateSignatureError = -40001;
    /** XML 解析失败 */
	public final static int ParseXmlError = -40002;
    /** SHA 加密生成签名失败 */
	public final static int ComputeSignatureError = -40003;
    /** SymmetricKey 非法 */
	public final static int IllegalAesKey = -40004;
    /** corpid 校验失败 */
	public final static int ValidateCorpidError = -40005;
    /** AES 加密失败 */
	public final static int EncryptAESError = -40006;
    /** AES 解密失败 */
	public final static int DecryptAESError = -40007;
    /** 解密后得到的 buffer 非法 */
	public final static int IllegalBuffer = -40008;

    /** 错误码 */
	private int code;

    /**
     * 根据错误码获取错误描述
     *
     * @param code 错误码
     * @return 错误描述
     */
	private static String getMessage(int code) {
		switch (code) {
		case ValidateSignatureError:
			return "签名验证错误";
		case ParseXmlError:
			return "xml解析失败";
		case ComputeSignatureError:
			return "sha加密生成签名失败";
		case IllegalAesKey:
			return "SymmetricKey非法";
		case ValidateCorpidError:
			return "corpid校验失败";
		case EncryptAESError:
			return "aes加密失败";
		case DecryptAESError:
			return "aes解密失败";
		case IllegalBuffer:
			return "解密后得到的buffer非法";
		default:
			return null; // cannot be
		}
	}

    /**
     * 获取错误码
     *
     * @return 错误码
     */
	public int getCode() {
		return code;
	}

    /**
     * 构造方法
     *
     * @param code 错误码
     */
	AesException(int code) {
		super(getMessage(code));
		this.code = code;
	}

}
