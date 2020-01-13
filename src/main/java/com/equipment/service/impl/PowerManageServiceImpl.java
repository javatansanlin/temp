package com.equipment.service.impl;

import com.equipment.dao.PowerInfoDao;
import com.equipment.model.equipmanager.PowerInfo;
import com.equipment.service.PowerManageService;
import com.equipment.util.Constants;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PowerManageServiceImpl implements PowerManageService {

    //日志
    private Logger log = LoggerFactory.getLogger(PowerManageServiceImpl.class);

    @Autowired
    private PowerInfoDao powerInfoDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> findPowerPage(Integer indexPage, Integer pageCount,
                                             String powerCode, String equipCode, Integer errorTimes,
                                             Integer powerStatus, Integer borrowStatus) throws Exception{
        Map<String, Object> result = new HashMap<String, Object>();
        //根据查询条件查询电池列表
        PageHelper.startPage(indexPage, pageCount);
        //设备编号模糊查询
        if(StringUtils.isNotBlank(equipCode)){
            equipCode = "%" + equipCode + "%";
        }
        List<Map> data = powerInfoDao.findPowerList(powerCode, equipCode, errorTimes, powerStatus, borrowStatus);
        /*for(Map p : data){
            //重置页面显示的总错误次数
            Integer totalErrorTimes = p.get("totalErrorTimes") == null ? 0 : (Integer)p.get("totalErrorTimes");
            Integer errorTimesTemp = p.get("errorTimes") == null ? 0 : (Integer)p.get("errorTimes");
            p.put("totalErrorTimes", totalErrorTimes + errorTimesTemp);
        }*/
        result.put("success", true);
        result.put("message", "操作成功");
        result.put("result", new PageInfo<>(data));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> resetPowerInfo(List<Integer> ids) throws Exception{
        Map<String, Object> result = new HashMap<String, Object>();
        //判断参数是否为空
        if(null == ids){
            result.put("success", false);
            result.put("message", "参数错误");
            return result;
        }

        for(Integer id : ids){
            //根据id查询电池，获取电池信息
            PowerInfo oldPower = powerInfoDao.findPowerById(id);
            String powerCode = oldPower.getPowerCode();
            Integer errorTimes = oldPower.getErrorTimes();
            //重置错误次数为0，电池锁定状态为不锁定，维修次数+1
            //1 更新数据库信息
            powerInfoDao.updatePowerDefault(id, errorTimes);

            //2 更新redis中的信息(电池编号-PowerInfo)
            //修改电池锁定状态 错误次数 维修次数
            redisTemplate.delete(powerCode + Constants.POWER_POWER_STATUS_KEY);//锁定状态为不锁定、删除key
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_ERROR_TIMES_KEY, 0);//错误次数0

            //3重置时去判断当前选中的电池是否存在租借中的key，是则删除
            Integer orderId = powerInfoDao.findOrderByCode(powerCode);
            if(orderId == null || orderId == 0){
                //不存在订单
                //删除租借key
                redisTemplate.delete(powerCode + "-Borrow");
            }
        }



        result.put("success", true);
        result.put("message", "操作成功");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updatePowerInfo(Integer id) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        //判断参数是否为空
        if(null == id){
            result.put("success", false);
            result.put("message", "参数错误");
            return result;
        }

        //手动刷新电池信息
        //实时的同步redis中的信息到数据中，设备编号 注册时间 维修次数不需要更新
        //查询数据库中的电池信息
        PowerInfo powerInfo = powerInfoDao.findPowerById(id);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        String powerCode = powerInfo.getPowerCode();//电池编号
        //租借状态
        Object borrowStatusObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_BORROW_STATUS_KEY);
        Integer borrowStatus = borrowStatusObj == null ? null : Integer.valueOf(borrowStatusObj+"");
        //租借状态为空
        if(null == borrowStatus){
            borrowStatus = 2;
        }
        //锁定状态
        Object powerStatusObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_POWER_STATUS_KEY);
        Integer powerStatus = powerStatusObj == null ? null : Integer.valueOf(powerStatusObj+"");
        if(null == powerStatus){
            powerStatus = 1;
        }
        //当前设备
        String onlineEquip = (String) redisTemplate.opsForValue().get(powerCode + Constants.POWER_ONLINE_EQUIP_KEY);
        //上一设备
        String beforeEquip = (String) redisTemplate.opsForValue().get(powerCode + Constants.POWER_BEFORE_EQUIP_KEY);
        //租借次数
        Object borrowTimesObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_BORROW_TIMES_KEY);
        Integer borrowTimes = borrowTimesObj == null ? null : Integer.valueOf(borrowTimesObj+"");
        if(null == borrowTimes){
            //租借次数丢失，不更新
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_BORROW_TIMES_KEY, powerInfo.getBorrowTimes());
            log.info("电池编号：{} 的租借次数丢失,不更新,同步值到redis ", powerCode);
        }
        //电量
        Object powerNumsObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_POWER_NUMS_KEY);
        Integer powerNums = powerNumsObj == null ? null : Integer.valueOf(powerNumsObj+"");
        if(null == powerNums){
            //电量丢失，不更新
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_POWER_NUMS_KEY, powerInfo.getPowerNums());
            log.info("电池编号：{} 的电量丢失,不更新,同步值到redis ", powerCode);
        }
        //错误次数
        Object errorTimesObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_ERROR_TIMES_KEY);
        Integer errorTimes = errorTimesObj == null ? null : Integer.valueOf(errorTimesObj+"");
        if(null == errorTimes){
            //错误次数丢失，不更新
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_ERROR_TIMES_KEY, powerInfo.getErrorTimes());
            log.info("电池编号：{} 的错误次数丢失,不更新,同步值到redis ", powerCode);
        }
        //使用时长
        Object useMinutesObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_USE_MINUTES_KEY);
        Long useMinutes = useMinutesObj == null ? null : Long.valueOf(useMinutesObj+"");
        if(null == useMinutes){
            //使用时长丢失，不更新
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_USE_MINUTES_KEY, powerInfo.getUseMinutes());
            log.info("电池编号：{} 的使用时长丢失,不更新,同步值到redis ", powerCode);
        }
        //收益
        Object powerProfitObj = redisTemplate.opsForValue().get(powerCode + Constants.POWER_POWER_PROFIT_KEY);
        Double powerProfit = powerProfitObj == null ? null : Double.valueOf(powerProfitObj+"");
        if(null == powerProfit){
            //收益丢失，不更新
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_POWER_PROFIT_KEY, powerInfo.getUseMinutes());
            log.info("电池编号：{} 的收益丢失,不更新,同步值到redis ", powerCode);
        }
        param.put("borrowStatus", borrowStatus);
        param.put("powerStatus", powerStatus);
        param.put("onlineEquip", onlineEquip);
        param.put("beforeEquip", beforeEquip);
        param.put("powerNums", powerNums);
        param.put("borrowTimes", borrowTimes);
        param.put("errorTimes", errorTimes);
        param.put("useMinutes", useMinutes);
        param.put("powerProfit", powerProfit);
        param.put("updateTime", new Date());

        powerInfoDao.updatePowerById(param);

        result.put("result", param);
        result.put("success", true);
        result.put("message", "操作成功");
        return result;
    }

}
