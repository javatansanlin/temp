package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.model.old.Battery;
import com.equipment.model.old.IN_Info;
import com.equipment.model.old.OrderToNetty;
import com.equipment.mqtt.PubMsg;
import com.equipment.service.SuperEqManageService;
import com.equipment.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Author: JavaTansanlin
 * @Description: 设备的超级管理  业务层实现类
 * @Date: Created in 16:20 2018/9/1
 * @Modified By:
 */
@Service
@Transactional
public class SuperEqManageServiceImpl implements SuperEqManageService {

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 发布 */
    @Autowired
    private PubMsg pubMsgl;

    /** 弹出需要检修的电池 */
    @Override
    public Map<String, Object> overhaulToPower(String[] powers, String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        if (powers==null || powers.length==0 || eqCode==null || "".equals(eqCode.trim())){
            result.put("code" ,1);
            result.put("msg" ,"参数不正确");
            return result;
        }
        //根据设备号查询该设备的心跳是否存在
        String lin = redisTemplate.opsForValue().get(eqCode + "-Info").toString();
        if (lin==null || "".equals(lin.trim())){
            result.put("code" ,2);
            result.put("msg" ,"该设备不在线");
            return result;
        }
        IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
        //判断该设备中是否存在该电池
        /*try {
            boolean falg = true;//不存在该电池
            List<Battery> bl = inInfo.getDD().getBL();
            for (Battery b:bl) {
                if (b.getBI().equals(powers)){
                    falg=false;//存在该电池
                    break;
                }
            }
            if (falg){
                result.put("code" ,4);
                result.put("msg" ,"该电池不存在该设备上");
                return result;
            }
        }catch (Exception e){
            result.put("code" ,4);
            result.put("msg" ,"该电池不存在该设备上");
            return result;
        }*/
        try {
            //判断是否是mq协议的设备，如果是，则走mq协议，否则，走老协议
            String dt = inInfo.getDD().getDT();
            redisTemplate.delete(eqCode+"-Power");//删除电池链
            if ("MqBattery".equals(dt) || "MqBattery-Video".equals(dt)){
                for (int i = 0; i < powers.length; i++) {
                    String str = "{\"MI\":\""+inInfo.getMI()+"\",\"BO\":\""+powers[i]+"\",\"AT\":\"Overhaul\",\"TS \":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                    pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+inInfo.getMI());
                }
            }else {
                for (int i = 0; i < powers.length; i++) {
                    OrderToNetty ot = new OrderToNetty(null,eqCode, powers[i], DateUtil.Date2TimeStamp(new Date()),"OverhaulRequest");
                    pubMsgl.publish(JSON.toJSONString(ot) ,"client-overhaul", "YDB-Order-To-Netty");
                    Thread.sleep(1000);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("code" ,13);
            result.put("msg" ,"系统错误");
            return result;
        }
        result.put("code" ,3);
        result.put("msg" ,"操作成功");
        return result;
    }

    /** 重启或者关机操作 type:off-关机操作，rest-重启操作*/
    @Override
    public Map<String, Object> offOrRest(String eqCode ,String type) {
        Map<String ,Object> result = new HashMap<>();
        if (type==null || eqCode==null || "".equals(eqCode.trim()) || ( !"off".equals(type) && !"rest".equals(type) ) ){
            result.put("code" ,1);
            result.put("msg" ,"参数不正确");
            return result;
        }
        //根据设备心跳查询该设备是否在线
        String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
        if (lin==null || "".equals(lin.trim())){
            result.put("code" ,2);
            result.put("msg" ,"该设备不在线");
            return result;
        }
        IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);//获取心跳钟的数据
        try {
            redisTemplate.delete(eqCode+"-Power");//删除电池链
            if ("MqBattery".equals(inInfo.getDD().getDT()) || "MqBattery-Video".equals(inInfo.getDD().getDT())){
                String str = null;
                if ("off".equals(type)){
                    str = "{\"MI\":\""+inInfo.getMI()+"\",\"AT\":\"Off\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                }else if ("rest".equals(type)){
                    str = "{\"MI\":\""+inInfo.getMI()+"\",\"AT\":\"Rest\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                }
                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+inInfo.getMI());
            }else {
                OrderToNetty ot = null;
                if ("off".equals(type)){
                    ot = new OrderToNetty(null,eqCode, null, DateUtil.Date2TimeStamp(new Date()),"OffRequest");
                }else if ("rest".equals(type)){
                    ot = new OrderToNetty(null,eqCode, null, DateUtil.Date2TimeStamp(new Date()),"RestRequest");
                }
                    pubMsgl.publish(JSON.toJSONString(ot) ,"client-offOrRest", "YDB-Order-To-Netty");
            }
        }catch (Exception e){
            result.put("code" ,13);
            result.put("msg" ,"系统错误");
            return result;
        }
        result.put("code" ,3);
        result.put("msg" ,"操作成功");
        return result;
    }

    /** 弹出设备的所有电池 */
    @Override
    public Map<String, Object> ohAllPower(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        if (eqCode==null || "".equals(eqCode.trim())){
            result.put("code" ,1);
            result.put("msg" ,"参数不正确");
            return result;
        }
        //根据设备号查询该设备的心跳是否存在
        String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
        if (lin==null || "".equals(lin.trim())){
            result.put("code" ,2);
            result.put("msg" ,"该设备不在线");
            return result;
        }
        try {

            IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
            String bc = inInfo.getDD().getBC();
            int i = Integer.parseInt(bc);
            //判断是否是mq协议的设备，如果是，则走mq协议，否则，走老协议
            redisTemplate.delete(eqCode+"-Power");//删除电池链
            String dt = inInfo.getDD().getDT();
            if ("MqBattery".equals(dt) || "MqBattery-Video".equals(dt)){//新mq协议
                for (int j = 0; j < i; j++) {
                    String str = "{\"MI\":\""+inInfo.getMI()+"\",\"BO\":\""+j+"\",\"AT\":\"Overhaul\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}";
                    pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+inInfo.getMI());
                }
            }else {//老协议
                for (int j = 0; j < i; j++) {
                    OrderToNetty ot = new OrderToNetty(null,eqCode, j+"", DateUtil.Date2TimeStamp(new Date()),"OverhaulRequest");
                    pubMsgl.publish(JSON.toJSONString(ot) ,"client-overhaul", "YDB-Order-To-Netty");
                    Thread.sleep(1000);
                }
            }

        }catch (Exception e){
            result.put("code" ,13);
            result.put("msg" ,"系统错误");
            return result;
        }
        result.put("code" ,3);
        result.put("msg" ,"操作成功");
        return result;
    }
}
