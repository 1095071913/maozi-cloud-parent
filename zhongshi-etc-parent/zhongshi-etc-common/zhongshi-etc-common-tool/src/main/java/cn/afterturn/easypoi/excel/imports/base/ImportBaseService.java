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
package cn.afterturn.easypoi.excel.imports.base;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelCollectionParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelImportEntity;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;
import cn.afterturn.easypoi.exception.excel.enums.ExcelImportEnum;
import cn.afterturn.easypoi.handler.inter.IExcelI18nHandler;
import cn.afterturn.easypoi.util.PoiPublicUtil;
import cn.afterturn.easypoi.util.PoiReflectorUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 瀵煎叆鍩虹鍜�,鏅�氭柟娉曞拰Sax鍏辩敤
 * @author JueYue
 *  2015骞�1鏈�9鏃� 涓嬪崍10:25:53
 */
public class ImportBaseService {

    protected IExcelI18nHandler i18nHandler;


    /**
     * 鎶婅繖涓敞瑙ｈВ鏋愭斁鍒扮被鍨嬪璞′腑
     *
     * @param targetId
     * @param field
     * @param excelEntity
     * @param pojoClass
     * @param getMethods
     * @param temp
     * @throws Exception
     */
    public void addEntityToMap(String targetId, Field field, ExcelImportEntity excelEntity,
                               Class<?> pojoClass, List<Method> getMethods,
                               Map<String, ExcelImportEntity> temp, ExcelEntity excelEntityAnn) throws Exception {
        Excel excel = field.getAnnotation(Excel.class);
        excelEntity = new ExcelImportEntity();
        excelEntity.setType(excel.type());
        excelEntity.setSaveUrl(excel.savePath());
        excelEntity.setImportValueConverter(excel.importValueConverter());
        excelEntity.setSaveType(excel.imageType());
        excelEntity.setReplace(excel.replace());
        excelEntity.setDatabaseFormat(excel.databaseFormat());
        excelEntity.setSuffix(excel.suffix());
        excelEntity.setImportField(Boolean
                .valueOf(PoiPublicUtil.getValueByTargetId(excel.isImportField(), targetId, "false")));
        excelEntity.setFixedIndex(excel.fixedIndex());
        excelEntity.setName(PoiPublicUtil.getValueByTargetId(excel.name(), targetId, null));
        if (StringUtils.isNoneEmpty(excel.groupName())) {
            excelEntity.setName(excel.groupName() + "_" + excelEntity.getName());
        }
        if (excelEntityAnn != null && excelEntityAnn.show()) {
            excelEntity.setName(excelEntityAnn.name() + "_" + excelEntity.getName());
        }
        if (i18nHandler != null) {
            excelEntity.setName(i18nHandler.getLocaleName(excelEntity.getName()));
        }
        excelEntity.setMethod(PoiReflectorUtil.fromCache(pojoClass).getSetMethod(field.getName()));
        if (StringUtils.isNotEmpty(excel.importFormat())) {
            excelEntity.setFormat(excel.importFormat());
        } else {
            excelEntity.setFormat(excel.format());
        }
        excelEntity.setDict(excel.dict());
        excelEntity.setEnumImportMethod(excel.enumImportMethod());
        if (getMethods != null) {
            List<Method> newMethods = new ArrayList<Method>();
            newMethods.addAll(getMethods);
            newMethods.add(excelEntity.getMethod());
            excelEntity.setMethods(newMethods);
        }
        if (excelEntity.getFixedIndex() != -1) {
            temp.put("FIXED_" + excelEntity.getFixedIndex(), excelEntity);
        } else {
            temp.put(excelEntity.getName(), excelEntity);
        }

    }

    /**
     * 鑾峰彇闇�瑕佸鍑虹殑鍏ㄩ儴瀛楁
     * @param targetId
     * @param fields
     * @param excelParams
     * @param excelCollection
     * @param pojoClass
     * @param getMethods
     * @throws Exception
     */
    public void getAllExcelField(String targetId, Field[] fields,
                                 Map<String, ExcelImportEntity> excelParams,
                                 List<ExcelCollectionParams> excelCollection, Class<?> pojoClass,
                                 List<Method> getMethods, ExcelEntity excelEntityAnn) throws Exception {
        ExcelImportEntity excelEntity = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (PoiPublicUtil.isNotUserExcelUserThis(null, field, targetId)) {
                continue;
            }
            if (PoiPublicUtil.isCollection(field.getType())) {
                // 闆嗗悎瀵硅薄璁剧疆灞炴��
                ExcelCollection excel = field.getAnnotation(ExcelCollection.class);
                ExcelCollectionParams collection = new ExcelCollectionParams();
                collection.setName(field.getName());
                Map<String, ExcelImportEntity> temp = new HashMap<String, ExcelImportEntity>();
                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                Class<?> clz = (Class<?>) pt.getActualTypeArguments()[0];
                collection.setType(clz);
                getExcelFieldList(StringUtils.isNotEmpty(excel.id()) ? excel.id() : targetId,
                        PoiPublicUtil.getClassFields(clz), clz, temp, null);
                collection.setExcelParams(temp);
                collection.setExcelName(PoiPublicUtil.getValueByTargetId(
                        field.getAnnotation(ExcelCollection.class).name(), targetId, null));
                additionalCollectionName(collection);
                excelCollection.add(collection);
            } else if (PoiPublicUtil.isJavaClass(field) || field.getType().isEnum()) {
                addEntityToMap(targetId, field, excelEntity, pojoClass, getMethods, excelParams, excelEntityAnn);
            } else {
                List<Method> newMethods = new ArrayList<Method>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                //
                newMethods.add(PoiReflectorUtil.fromCache(pojoClass).getGetMethod(field.getName()));
                ExcelEntity excel = field.getAnnotation(ExcelEntity.class);
                if (excel.show() && StringUtils.isEmpty(excel.name())) {
                    throw new ExcelImportException("if use ExcelEntity ,name mus has value ,data: " + ReflectionToStringBuilder.toString(excel), ExcelImportEnum.PARAMETER_ERROR);
                }
                getAllExcelField(StringUtils.isNotEmpty(excel.id()) ? excel.id() : targetId,
                        PoiPublicUtil.getClassFields(field.getType()), excelParams, excelCollection,
                        field.getType(), newMethods, excel);
            }
        }
    }

    /**
     * 杩藉姞闆嗗悎鍚嶇О鍒板墠闈�
     * @param collection
     */
    private void additionalCollectionName(ExcelCollectionParams collection) {
        Set<String> keys = new HashSet<String>();
        keys.addAll(collection.getExcelParams().keySet());
        for (String key : keys) {
            collection.getExcelParams().put(collection.getExcelName() + "_" + key,
                    collection.getExcelParams().get(key));
            collection.getExcelParams().remove(key);
        }
    }


    public void getExcelFieldList(String targetId, Field[] fields, Class<?> pojoClass,
                                  Map<String, ExcelImportEntity> temp,
                                  List<Method> getMethods) throws Exception {
        ExcelImportEntity excelEntity = null;
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (PoiPublicUtil.isNotUserExcelUserThis(null, field, targetId)) {
                continue;
            }
            if (PoiPublicUtil.isJavaClass(field) || field.getType().isEnum()) {
                addEntityToMap(targetId, field, excelEntity, pojoClass, getMethods, temp, null);
            } else {
                List<Method> newMethods = new ArrayList<Method>();
                if (getMethods != null) {
                    newMethods.addAll(getMethods);
                }
                ExcelEntity excel = field.getAnnotation(ExcelEntity.class);
                newMethods.add(PoiReflectorUtil.fromCache(pojoClass).getSetMethod(field.getName()));
                getExcelFieldList(StringUtils.isNotEmpty(excel.id()) ? excel.id() : targetId,
                        PoiPublicUtil.getClassFields(field.getType()), field.getType(), temp,
                        newMethods);
            }
        }
    }

    public Object getFieldBySomeMethod(List<Method> list, Object t) throws Exception {
        Method m;
        for (int i = 0; i < list.size() - 1; i++) {
            m = list.get(i);
            t = m.invoke(t, new Object[]{});
        }
        return t;
    }

    public void saveThisExcel(ImportParams params, Class<?> pojoClass, boolean isXSSFWorkbook,
                              Workbook book) throws Exception {
        String path = getSaveExcelUrl(params, pojoClass);
        File savefile = new File(path);
        if (!savefile.exists()) {
            savefile.mkdirs();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyMMddHHmmss");
        FileOutputStream fos = new FileOutputStream(
                path + File.separator + format.format(new Date()) + "_" + Math.round(Math.random() * 100000)
                        + (isXSSFWorkbook == true ? ".xlsx" : ".xls"));
        book.write(fos);
        IOUtils.closeQuietly(fos);
    }

    /**
     * 鑾峰彇淇濆瓨鐨凟xcel 鐨勭湡瀹炶矾寰�
     *
     * @param params
     * @param pojoClass
     * @return
     * @throws Exception
     */
    public String getSaveExcelUrl(ImportParams params, Class<?> pojoClass) throws Exception {
        String url = "";
        if (ImportParams.SAVE_URL.equals(params.getSaveUrl())) {
            url = pojoClass.getName().split("\\.")[pojoClass.getName().split("\\.").length - 1];
            return params.getSaveUrl() + File.separator + url;
        }
        return params.getSaveUrl();
    }

    /**
     * 澶氫釜get 鏈�鍚庡啀set
     *
     * @param setMethods
     * @param object
     */
    public void setFieldBySomeMethod(List<Method> setMethods, Object object,
                                     Object value) throws Exception {
        Object t = getFieldBySomeMethod(setMethods, object);
        setMethods.get(setMethods.size() - 1).invoke(t, value);
    }

    /**
     *
     * @param entity
     * @param object
     * @param value
     * @throws Exception
     */
    public void setValues(ExcelImportEntity entity, Object object, Object value) throws Exception {
        // 鍘绘帀闈炲寘瑁呯被鐨勫紓甯告儏鍐�
        if (value == null) {
            return;
        }
        if (entity.getMethods() != null) {
            setFieldBySomeMethod(entity.getMethods(), object, value);
        } else {
            entity.getMethod().invoke(object, value);
        }
    }

}
