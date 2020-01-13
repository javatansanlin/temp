package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.dao.*;
import com.equipment.entity.*;
import com.equipment.model.old.Battery;
import com.equipment.model.old.IN_Borrow;
import com.equipment.model.old.IN_Info;
import com.equipment.service.NettyToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: JavaTansanlin
 * @Description: 提供给netty的业务接口 实现类
 * @Date: Created in 18:09 2018/8/13
 * @Modified By:
 */
@Service
public class NettyToServiceImpl implements NettyToService {

    /** 日志 */
    private Logger log = LoggerFactory.getLogger(getClass());

    /** 设备心跳dao */
    @Autowired
    EquipHeartDetailDao heartDetailDao;

    /** 设备dao **/
    @Autowired
    EquipInfoDao equipInfoDao;

    /** 电池dao **/
    @Autowired
    EquipPowerDetailDao equipPowerDetailDao;

    /** 指令dao **/
    @Autowired
    EquipInstructionDao equipInstructionDao;

    /** 基础参数dao */
    @Autowired
    BasesettingDao basesettingDao;

    /** 设备类型dao */
    @Autowired
    EquipTypeDao equipTypeDao;

    @Autowired
    RedisTemplate redisTemplate;

    /** 根据传过来的心跳值进行心跳记录，以及电池的所在的心跳更新 , online是上线通知，true为通知更改设备状态 **/
    @Override
    public Boolean onlineAndHeartRecord(String json,Boolean online) {
        IN_Info info = JSON.parseObject(json, IN_Info.class);
        //判断该设备是否存在，进而插入心跳表中的数据，并且更新电池的心跳信息，有则更新状态，无则执行入库操作
        int code = equipInfoDao.findEquipByCode(info.getMI());
        if (code<=0){//设备不存在，执行入库操作
            EquipType type = null;
            //电池卡口数
            String bc = info.getDD().getBC();
            //查询出对应的设备类型
            if ( "Battery".equals(info.getDD().getDT()) || "MqBattery".equals(info.getDD().getDT()) ){//电池版本
                type = equipTypeDao.findTypeByVersion(1 ,Integer.parseInt(bc));
            }else if ("MqBattery-Video".equals(info.getDD().getDT())){//十口设备视频版本
                type = equipTypeDao.findTypeByVersion(3 ,Integer.parseInt(bc));
            } else {//其它版本
                type = equipTypeDao.findTypeByVersion(2,Integer.parseInt(bc));
            }
            EquipInfo equipInfo = new EquipInfo();
            equipInfo.setCode(info.getMI());//设备号
            equipInfo.setState(1);//上线状态
            equipInfo.setType(type.getId());//设备类型
            equipInfo.setIsstock(1);//标记为在库状态
            equipInfoDao.insert(equipInfo);//插入记录
        }
        if(online!=null && online && code>0){//设备上线请求,前提是设备存在
            //更新设备表中的在线状态信息
            equipInfoDao.updateEquipState(1,info.getMI());
            return true;
        }
        EquipHeartDetail heart = new EquipHeartDetail();//实例化一个数据库的对象
        heart.setTe(info.getTE());
        heart.setMi(info.getMI());
        heart.setSd(info.getSD());
        heart.setAt(info.getAT());
        heart.setTs(info.getTS());
        heart.setDi(info.getDD().getDI());
        heart.setDt(info.getDD().getDT());
        String ll = info.getDD().getLL();
        heart.setLl(ll);
        heart.setVideos(JSON.toJSONString(info.getDD().getVideos()));
        //分割经纬度
        if (ll!=null){
            try {
                String[] split = ll.split(",");
                heart.setLo(Double.parseDouble(split[0]));
                heart.setLa(Double.parseDouble(split[1]));
            }catch (Exception e){
                log.error("【记录设备心跳时，分割经纬度错误】"+e.getMessage(),e);
            }
        }
        heart.setBc(info.getDD().getBC());
        heart.setCb(info.getDD().getCB());
        heart.setCr(info.getDD().getCR());
        heartDetailDao.insert(heart);

        //增加一个当前心跳记录数据(如果存在，删除后添加，不存在，则保存)
        //根据设备编号去心跳表查询
        EquipHeartDetail current = heartDetailDao.selectCurrent(heart.getMi());
        if(current == null){
            heartDetailDao.insertCurrent(heart);
        }else{
            Long currentId = current.getId();
            BeanUtils.copyProperties(heart, current);
            heartDetailDao.deleteCurrent(currentId);
            current.setId(currentId);
            heartDetailDao.insertCurrent(current);
        }

        if (heart.getId()!=null && info.getDD().getBL()!=null && info.getDD().getBL().size()>0){
            for ( Battery bi:info.getDD().getBL()) {
                //查询该电池编号是否存在

            }
        }
        return true;
    }

    /** 服务器的租借指令业务逻辑 **/
    @Override
    public Long borrowRecordByServer(String borrowInstruction , String code) {
        EquipInstruction e = new EquipInstruction();
        e.setServerJson(borrowInstruction);
        e.setType("Borrow");
        e.setServerJsonTime(new Date());
        equipInstructionDao.insert(e, code);
        return e.getId();
    }

    /** 设备的租借结果指令业务逻辑 **/
    @Override
    public Integer borrowRecordByEquip(Long id, String json) {
        try {
            IN_Borrow in_borrow = JSON.parseObject(json, IN_Borrow.class);
            if (in_borrow!=null && in_borrow.getSC()!=null && "err".equals(in_borrow.getSC())){
                //查询最大错误的基础参数
                Integer powerErrorLimit = basesettingDao.findPowerErrorLimit();
                if (powerErrorLimit!=null){
                    //获取电池的错误次数
                    Integer o = (Integer)redisTemplate.opsForValue().get(in_borrow.getBI() + "-ErrorTimes");
                    if (powerErrorLimit<=o){
                        redisTemplate.opsForValue().set(in_borrow.getBI()+"-PowerStatus" ,1);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //获取基础参数
        return equipInstructionDao.insertByBorrowEquip(id,json);
    }

    /** 设备的归还指令业务逻辑 **/
    @Override
    public String returnRecord(String serverJson, String equipJson, String code) {
        EquipInstruction e = new EquipInstruction();
        e.setType("Return");
        e.setServerJson(serverJson);
        e.setEquipJson(equipJson);
        e.setEquipJsonTime(new Date());
        e.setServerJsonTime(new Date());
        equipInstructionDao.insert(e,code);
        return e.getId()+"";
    }
}
