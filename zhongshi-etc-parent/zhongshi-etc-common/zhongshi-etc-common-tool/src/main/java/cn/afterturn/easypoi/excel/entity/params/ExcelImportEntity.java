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
package cn.afterturn.easypoi.excel.entity.params;

import lombok.Data;

import java.util.List;

/**
 * excel 瀵煎叆宸ュ叿绫�,瀵筩ell绫诲瀷鍋氭槧灏�
 * @author JueYue
 * @version 1.0 2013骞�8鏈�24鏃�
 */
@Data
public class ExcelImportEntity extends ExcelBaseEntity {

    public final static String IMG_SAVE_PATH = "/excel/upload/img";
    /**
     * 瀵瑰簲 Collection NAME
     */
    private String                  collectionName;
    /**
     * 淇濆瓨鍥剧墖鐨勫湴鍧�
     */
    private String                  saveUrl;
    /**
     * 淇濆瓨鍥剧墖鐨勭被鍨�,1鏄枃浠�,2鏄暟鎹簱
     */
    private int                     saveType;
    /**
     * 瀵瑰簲exportType
     */
    private String                  classType;
    /**
     * 鍚庣紑
     */
    private String                  suffix;
    /**
     * 瀵煎叆鏍￠獙瀛楁
     */
    private boolean                 importField;

    /**
     * 鏋氫妇瀵煎叆闈欐�佹柟娉�
     */
    private String                   enumImportMethod;

    private List<ExcelImportEntity> list;
    
    private String importValueConverter;

}
