package com.equipment.sqlprovider;

import com.equipment.entity.EquipHeartDetail;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 心跳详情的相关拼接sql
 * @Date: Created in 15:14 2018/8/13
 * @Modified By:
 */
public class SqlProvider {

    /** 设备心跳的sql语句 **/
    public String insertEquipHeartDetail(Map<String,Object> map) {
        EquipHeartDetail heartDetail = (EquipHeartDetail)map.get("equipHeartDetail");
        StringBuilder sql = new StringBuilder("insert into EQUIP_HEART_DETAIL ");
        //get sql via reflection
        Map<String,String> sqlMap = getAllPropertiesForSql(heartDetail, "equipHeartDetail");
        sql.append(sqlMap.get("field")).append(sqlMap.get("value"));
        System.out.println(sql.toString());
        return sql.toString();
    }







    //根据传入的对象 基于反射生成两部分sql语句
    private  Map<String,String> getAllPropertiesForSql(Object obj, String objName) {
        Map<String,String> map = new HashMap<>();
        if(null == obj) return map;
        StringBuilder filedSql = new StringBuilder("(");
        StringBuilder valueSql = new StringBuilder("value (");
        Field[] fields = obj.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            filedSql.append(fields[i].getName() + ",");
            valueSql.append("#{" + objName + "." + fields[i].getName() + "},");
        }
        valueSql.deleteCharAt(valueSql.length() - 1);
        filedSql.deleteCharAt(filedSql.length() - 1);
        valueSql.append(") ");
        filedSql.append(") ");
        map.put("field",filedSql.toString());
        map.put("value", valueSql.toString());
        System.out.println("database filed sql: " + filedSql.toString());
        System.out.println("value sql:" + valueSql.toString());
        return map;
    }
}
