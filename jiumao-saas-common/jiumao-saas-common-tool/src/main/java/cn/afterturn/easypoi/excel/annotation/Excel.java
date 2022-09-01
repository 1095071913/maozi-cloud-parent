/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel 瀵煎嚭鍩烘湰娉ㄩ噴
 * @author JueYue
 *  2014骞�6鏈�20鏃� 涓嬪崍10:25:12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {

    /**
     * 瀵煎嚭鏃堕棿璁剧疆,濡傛灉瀛楁鏄疍ate绫诲瀷鍒欎笉闇�瑕佽缃� 鏁版嵁搴撳鏋滄槸string 绫诲瀷,杩欎釜闇�瑕佽缃繖涓暟鎹簱鏍煎紡
     */
    public String databaseFormat() default "yyyyMMddHHmmss";

    /**
     * 瀵煎嚭鐨勬椂闂存牸寮�,浠ヨ繖涓槸鍚︿负绌烘潵鍒ゆ柇鏄惁闇�瑕佹牸寮忓寲鏃ユ湡
     */
    public String exportFormat() default "";
    /**
     * 瀵煎叆鐨勬椂闂存牸寮�,浠ヨ繖涓槸鍚︿负绌烘潵鍒ゆ柇鏄惁闇�瑕佹牸寮忓寲鏃ユ湡
     */
    public String importFormat() default "";
    /**
     * 鏃堕棿鏍煎紡,鐩稿綋浜庡悓鏃惰缃簡exportFormat 鍜� importFormat
     */
    public String format() default "";
    /**
     * 鏃堕棿鏃跺尯
     */
    public String timezone() default "";
    /**
     * 鏁板瓧鏍煎紡鍖�,鍙傛暟鏄疨attern,浣跨敤鐨勫璞℃槸DecimalFormat
     */
    public String numFormat() default "";

    /**
     * 瀵煎嚭鏃跺湪excel涓瘡涓垪鐨勯珮搴� 鍗曚綅涓哄瓧绗︼紝涓�涓眽瀛�=2涓瓧绗�
     * 浼樺厛閫夋嫨@ExportParams涓殑 height
     */
    @Deprecated
    public double height() default 10;

    /**
     * 瀵煎嚭绫诲瀷 1 浠巉ile璇诲彇 2 鏄粠鏁版嵁搴撲腑璇诲彇 榛樿鏄枃浠� 鍚屾牱瀵煎叆涔熸槸涓�鏍风殑
     */
    public int imageType() default 1;

    /**
     * 鏂囧瓧鍚庣紑,濡�% 90 鍙樻垚90%
     */
    public String suffix() default "";

    /**
     * 鏄惁鎹㈣ 鍗虫敮鎸乗n
     */
    public boolean isWrap() default true;

    /**
     * 鍚堝苟鍗曞厓鏍间緷璧栧叧绯�,姣斿绗簩鍒楀悎骞舵槸鍩轰簬绗竴鍒� 鍒檣1}灏卞彲浠ヤ簡
     */
    public int[] mergeRely() default {};

    /**
     * 绾靛悜鍚堝苟鍐呭鐩稿悓鐨勫崟鍏冩牸
     */
    public boolean mergeVertical() default false;

    /**
     * 瀵煎嚭鏃讹紝瀵瑰簲鏁版嵁搴撶殑瀛楁 涓昏鏄敤鎴峰尯鍒嗘瘡涓瓧娈碉紝 涓嶈兘鏈塧nnocation閲嶅悕鐨� 瀵煎嚭鏃剁殑鍒楀悕
     * 瀵煎嚭鎺掑簭璺熷畾涔変簡annotation鐨勫瓧娈电殑椤哄簭鏈夊叧 鍙互浣跨敤a_id,b_id鏉ョ‘瀹炴槸鍚︿娇鐢�
     */
    public String name();

    /**
     * 瀵煎嚭鏃讹紝琛ㄥご鍙岃鏄剧ず,鑱氬悎,鎺掑簭浠ユ渶灏忕殑鍊煎弬涓庢�讳綋鎺掑簭鍐嶅唴閮ㄦ帓搴�
     * 瀵煎嚭鎺掑簭璺熷畾涔変簡annotation鐨勫瓧娈电殑椤哄簭鏈夊叧 鍙互浣跨敤a_id,b_id鏉ョ‘瀹炴槸鍚︿娇鐢�
     * 浼樺厛寮变笌 @ExcelEntity 鐨刵ame鍜宻how灞炴��
     */
    public String groupName() default "";

    /**
     * 鏄惁闇�瑕佺旱鍚戝悎骞跺崟鍏冩牸(鐢ㄤ簬鍚湁list涓�,鍗曚釜鐨勫崟鍏冩牸,鍚堝苟list鍒涘缓鐨勫涓猺ow)
     */
    public boolean needMerge() default false;

    /**
     * 灞曠ず鍒扮鍑犱釜鍙互浣跨敤a_id,b_id鏉ョ‘瀹氫笉鍚屾帓搴�
     */
    public String orderNum() default "0";

    /**
     * 鍊煎緱鏇挎崲  瀵煎嚭鏄瘂a_id,b_id} 瀵煎叆鍙嶈繃鏉�,鎵�浠ュ彧鐢ㄥ啓涓�涓�
     */
    public String[] replace() default {};
    /**
     *  瀛楀吀鍚嶇О
     */
    public String dict() default  "";

    /**
     * 瀵煎叆璺緞,濡傛灉鏄浘鐗囧彲浠ュ～鍐�,榛樿鏄痷pload/className/ IconEntity杩欎釜绫诲搴旂殑灏辨槸upload/Icon/
     *
     */
    public String savePath() default "/excel/upload/img";

    /**
     * 瀵煎嚭绫诲瀷 1 鏄枃鏈� 2 鏄浘鐗�,3 鏄嚱鏁�,10 鏄暟瀛� 榛樿鏄枃鏈�
     */
    public int type() default 1;

    /**
     * 瀵煎嚭鏃跺湪excel涓瘡涓垪鐨勫 鍗曚綅涓哄瓧绗︼紝涓�涓眽瀛�=2涓瓧绗� 濡� 浠ュ垪鍚嶅垪鍐呭涓緝鍚堥�傜殑闀垮害 渚嬪濮撳悕鍒�6 銆愬鍚嶄竴鑸笁涓瓧銆�
     * 鎬у埆鍒�4銆愮敺濂冲崰1锛屼絾鏄垪鏍囬涓や釜姹夊瓧銆� 闄愬埗1-255
     */
    public double width() default 10;

    /**
     * 鏄惁鑷姩缁熻鏁版嵁,濡傛灉鏄粺璁�,true鐨勮瘽鍦ㄦ渶鍚庤拷鍔犱竴琛岀粺璁�,鎶婃墍鏈夋暟鎹兘鍜�
     * 杩欎釜澶勭悊浼氬悶娌″紓甯�,璇锋敞鎰忚繖涓�鐐�
     * @return
     */
    public boolean isStatistics() default false;

    /**
     * 杩欎釜鏄笉鏄秴閾炬帴,濡傛灉鏄渶瑕佸疄鐜版帴鍙ｈ繑鍥炲璞�
     * @return
     */
    public boolean isHyperlink() default false;

    /**
     *  瀵煎叆鏃朵細鏍￠獙杩欎釜瀛楁,鐪嬬湅杩欎釜瀛楁鏄笉鏄鍏ョ殑Excel涓湁,濡傛灉娌℃湁璇存槑鏄敊璇殑Excel
     *  鏈剰鏄兂鐢╰rue鐨�,鎯虫兂杩樻槸false姣旇緝濂�
     *  鍙互浣跨敤a_id,b_id鏉ョ‘瀹炴槸鍚︿娇鐢�
     * @return
     */
    public String isImportField() default "false";

    /**
     * 鍥哄畾鐨勬煇涓�鍒�,瑙ｅ喅涓嶅ソ瑙ｆ瀽鐨勯棶棰�
     * @return
     */
    public int fixedIndex() default -1;

    /**
     *  鏄惁闇�瑕侀殣钘忚鍒�
     * @return
     */
    public boolean isColumnHidden() default  false;

    /**
     * 鏋氫妇瀵煎嚭浣跨敤鐨勫瓧娈�
     * @return
     */
    public String enumExportField() default "";
    /**
     * 鏋氫妇瀵煎叆浣跨敤鐨勫嚱鏁�
     * @return
     */
    public String enumImportMethod() default "";
    
    public String importValueConverter() default "";


}
