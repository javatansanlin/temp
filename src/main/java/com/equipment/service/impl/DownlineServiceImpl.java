package com.equipment.service.impl;

import com.equipment.dao.EquipInfoDao;
import com.equipment.service.DownlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: JavaTansanlin
 * @Description: 设备下线逻辑
 * @Date: Created in 19:17 2018/8/14
 * @Modified By:
 */
@Service
@Transactional
public class DownlineServiceImpl implements DownlineService {

    /** 设备dao **/
    @Autowired
    EquipInfoDao equipInfoDao;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 设备下线逻辑 */
    @Override
    public void downLine(String equipCode) {
        equipInfoDao.updateEquipStateDown(2,equipCode);
        //删除设备电池链信息
        redisTemplate.delete(equipCode+"-Power");//删除电池链
    }

}
