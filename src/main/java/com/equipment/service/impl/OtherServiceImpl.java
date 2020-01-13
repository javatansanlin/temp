package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.dao.EquipHeartDetailDao;
import com.equipment.dao.EquipInfoDao;
import com.equipment.dao.EquipLocationDao;
import com.equipment.entity.EquipHeartDetail;
import com.equipment.entity.EquipInfo;
import com.equipment.entity.EquipLocation;
import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.old.Drivers;
import com.equipment.model.old.IN_Info;
import com.equipment.service.OtherService;
import com.equipment.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: JavaTansanlin
 * @Description:其它业务接口实现类
 * @Date: Created in 11:36 2018/11/1
 * @Modified By:
 */
@Service
@Transactional
public class OtherServiceImpl implements OtherService {

    /** 日志 */
    private Logger log = LoggerFactory.getLogger(getClass());

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 注入dao */
    @Autowired
    private EquipHeartDetailDao heartDetailDao;

    /** 注入设备dao */
    @Autowired
    private EquipInfoDao equipInfoDao;

    /** 注入设备位置dao */
    @Autowired
    private EquipLocationDao equipLocationDao;

    /** 获取临时密钥 */
    @Override
    public Map<String, Object> getKey(Integer t) {
        Map<String ,Object> result = new HashMap<>();
        //生成key
        String key = UUID.randomUUID().toString().replace("-", "");
        t = t==null?600:t;
        //放到redis:规则是-->值是0说明未被任何使用过，1-->是被解绑二维码使用过
        redisTemplate.opsForValue().set(key, 0, t*60, TimeUnit.SECONDS);
        result.put("code" ,3);
        result.put("key" ,key);
        return result;
    }

    /** 把设备心跳表的数据同步更新到设备位置表 */
    @Override
    public Map<String, Object> synlyHealTolocation() {
        Map<String ,Object> result = new HashMap<>();

        int n = 0;//新增数
        int t = 0;//更新数
        int f = 0;//失败数

        //查询出所有的设备编号
        List<EquipInfo> all = equipInfoDao.findAll();
        if (all!=null && all.size()>0){
            //循环所有的设备，获取心跳相关的数据
            for (int i = 0; i < all.size(); i++) {
                EquipInfo equipInfo = all.get(i);
                String code = equipInfo.getCode();//设备编号
                //经纬度
                String ll = null;
                //获取redis里面的数据
                String s= (String) redisTemplate.opsForValue().get(code+"-Info");
                if (s!=null && !"".equals(s)){
                    IN_Info in_info = JSON.parseObject(s, IN_Info.class);
                    Drivers dd = in_info.getDD();
                    if (dd!=null){
                        ll = dd.getLL();
                    }
                }else {//离线便查询最新的心跳记录
                    EquipHeartDetail h = heartDetailDao.selectSDBCCBCRByCode(code);//查询出最新的心跳记录
                    if (h!=null){//有心跳记录的情况下执行下面逻辑
                       ll = h.getLl();
                    }
                }
                if (ll!=null){
                    //分割经纬度
                    String[] split = ll.split(",");
                    //查询该位置是否存在，存在则更新，不存在则插入
                    EquipLocation record = equipLocationDao.findNewRecordByEqCode(code);
                    if (record==null){//不存在
                        heartDetailDao.insertEqLocation(new EquipLocation(code ,Double.parseDouble(split[0]) ,Double.parseDouble(split[1])));
                        n++;
                    }else {
                        record.setLo(Double.parseDouble(split[0]));
                        record.setLa(Double.parseDouble(split[1]));
                        equipLocationDao.updateRecord(record);
                        t++;
                    }
                }else {
                    f++;
                }
            }
        }

        result.put("code" ,3);
        result.put("msg" ,"新增数："+n+"。更新数："+t+"。失败数："+f);
        return result;
    }
}
