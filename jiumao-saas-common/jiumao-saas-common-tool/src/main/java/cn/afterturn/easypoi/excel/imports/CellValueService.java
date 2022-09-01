/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.excel.imports;

import cn.afterturn.easypoi.excel.entity.params.ExcelImportEntity;
import cn.afterturn.easypoi.excel.entity.sax.SaxReadCellEntity;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelImportEnum;
import cn.afterturn.easypoi.handler.inter.IExcelDataHandler;
import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.util.PoiReflectorUtil;
import cn.hutool.extra.spring.SpringUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Cell 鍙栧�兼湇鍔�
 * 鍒ゆ柇绫诲瀷澶勭悊鏁版嵁 1.鍒ゆ柇Excel涓殑绫诲瀷 2.鏍规嵁replace鏇挎崲鍊� 3.handler澶勭悊鏁版嵁 4.鍒ゆ柇杩斿洖绫诲瀷杞寲鏁版嵁杩斿洖
 *
 * @author JueYue
 * 2014骞�6鏈�26鏃� 涓嬪崍10:42:28
 */
public class CellValueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CellValueService.class);

    private List<String> handlerList = null;

    /**
     * 鑾峰彇鍗曞厓鏍煎唴鐨勫��
     *
     * @param cell
     * @param entity
     * @return
     */
    private Object getCellValue(String classFullName, Cell cell, ExcelImportEntity entity) {
        if (cell == null) {
            return "";
        }
        Object result = null;
        if ("class java.util.Date".equals(classFullName)
                || "class java.sql.Date".equals(classFullName)
                || ("class java.sql.Time").equals(classFullName)
                || ("class java.time.Instant").equals(classFullName)
                || ("class java.time.LocalDate").equals(classFullName)
                || ("class java.time.LocalDateTime").equals(classFullName)
                || ("class java.sql.Timestamp").equals(classFullName)) {
            //FIX: 鍗曞厓鏍紋yyyMMdd鏁板瓧鏃跺�欎娇鐢� cell.getDateCellValue() 瑙ｆ瀽鍑虹殑鏃ユ湡閿欒
            if (CellType.NUMERIC == cell.getCellType() && DateUtil.isCellDateFormatted(cell)) {
                result = DateUtil.getJavaDate(cell.getNumericCellValue());
            } else {
                String val = "";
                try {
                    val = cell.getStringCellValue();
                } catch (Exception e) {
                    return null;
                }

                result = getDateData(entity, val);
                if (result == null) {
                    return null;
                }
            }
            if (("class java.time.Instant").equals(classFullName)) {
                result = ((Date) result).toInstant();
            } else if (("class java.time.LocalDate").equals(classFullName)) {
                result = ((Date) result).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (("class java.time.LocalDateTime").equals(classFullName)) {
                result = ((Date) result).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else if (("class java.time.OffsetDateTime").equals(classFullName)) {
                result = ((Date) result).toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime();
            }  else if (("class java.time.ZonedDateTime").equals(classFullName)) {
                result = ((Date) result).toInstant().atZone(ZoneId.systemDefault());
            }  else if (("class java.sql.Date").equals(classFullName)) {
                result = new java.sql.Date(((Date) result).getTime());
            } else if (("class java.sql.Time").equals(classFullName)) {
                result = new Time(((Date) result).getTime());
            } else if (("class java.sql.Timestamp").equals(classFullName)) {
                result = new Timestamp(((Date) result).getTime());
            }
        } else {
            switch (cell.getCellType()) {
                case STRING:
                    result = cell.getRichStringCellValue() == null ? ""
                            : cell.getRichStringCellValue().getString();
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        if ("class java.lang.String".equals(classFullName)) {
                            result = formateDate(entity, cell.getDateCellValue());
                        }
                    } else {
                        result = readNumericCell(cell);
                    }
                    break;
                case BOOLEAN:
                    result = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    break;
                case ERROR:
                    break;
                case FORMULA:
                    try {
                        result = readNumericCell(cell);
                    } catch (Exception e1) {
                        try {
                            result = cell.getRichStringCellValue() == null ? ""
                                    : cell.getRichStringCellValue().getString();
                        } catch (Exception e2) {
                            throw new RuntimeException("鑾峰彇鍏紡绫诲瀷鐨勫崟鍏冩牸澶辫触", e2);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private Object readNumericCell(Cell cell) {
        Object result = null;
        double value  = cell.getNumericCellValue();
        if (((int) value) == value) {
            result = (int) value;
        } else {
            result = value;
        }
        return result;
    }

    /**
     * 鑾峰彇鏃ユ湡绫诲瀷鏁版嵁
     *
     * @param entity
     * @param value
     * @return
     * @author JueYue
     * 2013骞�11鏈�26鏃�
     */
    private Date getDateData(ExcelImportEntity entity, String value) {
        if (StringUtils.isNotEmpty(entity.getFormat()) && StringUtils.isNotEmpty(value)) {
            SimpleDateFormat format = new SimpleDateFormat(entity.getFormat());
            try {
                return format.parse(value);
            } catch (ParseException e) {
                try {
                    return DateUtil.getJavaDate(Double.parseDouble(value));
                } catch (NumberFormatException ex) {
                    LOGGER.error("鏃堕棿鏍煎紡鍖栧け璐�,鏍煎紡鍖�:{},鍊�:{}", entity.getFormat(), value);
                    throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
                }
            }
        }
        return null;
    }

    private String formateDate(ExcelImportEntity entity, Date value) {
        if (StringUtils.isNotEmpty(entity.getFormat()) && value != null) {
            SimpleDateFormat format = new SimpleDateFormat(entity.getFormat());
            return format.format(value);
        }
        return null;
    }


    /**
     * 鑾峰彇cell鐨勫��
     *
     * @param object
     * @param cell
     * @param excelParams
     * @param titleString
     * @param dictHandler
     */
    public Object getValue(IExcelDataHandler<?> dataHandler, Object object, Object cell,
                           Map<String, ExcelImportEntity> excelParams,
                           String titleString, IExcelDictHandler dictHandler) throws Exception {

        ExcelImportEntity entity        = excelParams.get(titleString);
        String            classFullName = "class java.lang.Object";
        Class             clazz         = null;
        if (!(object instanceof Map)) {
            Method setMethod = entity.getMethods() != null && entity.getMethods().size() > 0
                    ? entity.getMethods().get(entity.getMethods().size() - 1) : entity.getMethod();
            Type[] ts = setMethod.getGenericParameterTypes();
            classFullName = ts[0].toString();
            clazz = (Class) ts[0];
        }
        Object result = null;
        if(cell instanceof Cell){
            result = getCellValue(classFullName, (Cell) cell, entity);
        }else{
            result = cell;
        }
        if (entity != null) {
            result = handlerSuffix(entity.getSuffix(), result);
            result = replaceValue(entity.getReplace(), result);
            result = replaceValue(entity.getReplace(), result);
            if (dictHandler != null && StringUtils.isNoneBlank(entity.getDict())) {
                result = dictHandler.toValue(entity.getDict(), object, entity.getName(), result);
            }
        }
        result = handlerValue(dataHandler, object, result, titleString);
        return getValueByType(classFullName, result, entity, clazz);
    }

    /**
     * 鑾峰彇cell鍊�
     *
     * @param dataHandler
     * @param object
     * @param cellEntity
     * @param excelParams
     * @param titleString
     * @return
     */
    public Object getValue(IExcelDataHandler<?> dataHandler, Object object,
                           SaxReadCellEntity cellEntity, Map<String, ExcelImportEntity> excelParams,
                           String titleString) {
        ExcelImportEntity entity = excelParams.get(titleString);
        Method setMethod = entity.getMethods() != null && entity.getMethods().size() > 0
                ? entity.getMethods().get(entity.getMethods().size() - 1) : entity.getMethod();
        Type[] ts            = setMethod.getGenericParameterTypes();
        String classFullName = ts[0].toString();
        Object result        = cellEntity.getValue();
        result = handlerSuffix(entity.getSuffix(), result);
        result = replaceValue(entity.getReplace(), result);
        result = handlerValue(dataHandler, object, result, titleString);
        return getValueByType(classFullName, result, entity, (Class) ts[0]);
    }

    /**
     * 鎶婂悗缂�鍒犻櫎鎺�
     *
     * @param result
     * @param suffix
     * @return
     */
    private Object handlerSuffix(String suffix, Object result) {
        if (StringUtils.isNotEmpty(suffix) && result != null
                && result.toString().endsWith(suffix)) {
            String temp = result.toString();
            return temp.substring(0, temp.length() - suffix.length());
        }
        return result;
    }

    /**
     * 鏍规嵁杩斿洖绫诲瀷鑾峰彇杩斿洖鍊�
     *
     * @param classFullName
     * @param result
     * @param entity
     * @param clazz
     * @return
     */
    private Object getValueByType(String classFullName, Object result, ExcelImportEntity entity, Class clazz) {
        try {
            //杩囨护绌哄拰绌哄瓧绗︿覆,濡傛灉鍩烘湰绫诲瀷null浼氬湪涓婂眰鎶涘嚭,杩欓噷灏变笉澶勭悊浜�
            if (result == null || StringUtils.isBlank(result.toString())) {
                return null;
            }
            if ("class java.util.Date".equals(classFullName) && result instanceof String) {
                return DateUtils.parseDate(result.toString(), entity.getFormat());
            }
            if ("class java.lang.Boolean".equals(classFullName) || "boolean".equals(classFullName)) {
                return Boolean.valueOf(String.valueOf(result));
            }
            if ("class java.lang.Double".equals(classFullName) || "double".equals(classFullName)) {
                return Double.valueOf(String.valueOf(result));
            }
            if ("class java.lang.Long".equals(classFullName) || "long".equals(classFullName)) {
                try {
                	if (StringUtils.isNotEmpty(entity.getImportValueConverter())) {
                		Object bean = SpringUtil.getBean(entity.getImportValueConverter());
                		Method method = bean.getClass().getMethod(entity.getMethod().getName().replace("set", "").toLowerCase()+"Converter", String.class);
                		return method.invoke(bean, result);
                	}
                    return Long.valueOf(String.valueOf(result));
                } catch (Exception e) {
                    //鏍煎紡閿欒鐨勬椂鍊�,灏辩敤double,鐒跺悗鑾峰彇Int鍊�
                    return Double.valueOf(String.valueOf(result)).longValue();
                }
            }
            if ("class java.lang.Float".equals(classFullName) || "float".equals(classFullName)) {
                return Float.valueOf(String.valueOf(result));
            }
            if ("class java.lang.Integer".equals(classFullName) || "int".equals(classFullName)) {
                try {
                    return Integer.valueOf(String.valueOf(result));
                } catch (Exception e) {
                    //鏍煎紡閿欒鐨勬椂鍊�,灏辩敤double,鐒跺悗鑾峰彇Int鍊�
                    return Double.valueOf(String.valueOf(result)).intValue();
                }
            }
            if ("class java.math.BigDecimal".equals(classFullName)) {
                return new BigDecimal(String.valueOf(result));
            }
            if ("class java.lang.String".equals(classFullName)) {
                //閽堝String 绫诲瀷,浣嗘槸Excel鑾峰彇鐨勬暟鎹嵈涓嶆槸String,姣斿Double绫诲瀷,闃叉绉戝璁℃暟娉�
                if (result instanceof String) {
                    return result;
                }
                // double绫诲瀷闃叉绉戝璁℃暟娉�
                if (result instanceof Double) {
                    return PoiPublicUtil.doubleToString((Double) result);
                }
                return String.valueOf(result);
            }
            if (clazz != null && clazz.isEnum()) {
                if (StringUtils.isNotEmpty(entity.getEnumImportMethod())) {
                    return PoiReflectorUtil.fromCache(clazz).execEnumStaticMethod(entity.getEnumImportMethod(), result);
                } else {
                    return Enum.valueOf(clazz, result.toString());
                }
            }
            return result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelImportException(ExcelImportEnum.GET_VALUE_ERROR);
        }
    }

    /**
     * 璋冪敤澶勭悊鎺ュ彛澶勭悊鍊�
     *
     * @param dataHandler
     * @param object
     * @param result
     * @param titleString
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object handlerValue(IExcelDataHandler dataHandler, Object object, Object result,
                                String titleString) {
        if (dataHandler == null || dataHandler.getNeedHandlerFields() == null
                || dataHandler.getNeedHandlerFields().length == 0) {
            return result;
        }
        if (handlerList == null) {
            handlerList = Arrays.asList(dataHandler.getNeedHandlerFields());
        }
        if (handlerList.contains(titleString)) {
            return dataHandler.importHandler(object, titleString, result);
        }
        return result;
    }

    /**
     * 鏇挎崲鍊�
     *
     * @param replace
     * @param result
     * @return
     */
    private Object replaceValue(String[] replace, Object result) {
        if (replace != null && replace.length > 0) {
            String   temp = String.valueOf(result);
            String[] tempArr;
            for (int i = 0; i < replace.length; i++) {
                tempArr = replace[i].split("_");
                if (temp.equals(tempArr[0])) {
                    return tempArr[1];
                }
            }
        }
        return result;
    }
}