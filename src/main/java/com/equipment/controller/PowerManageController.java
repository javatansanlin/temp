package com.equipment.controller;

import com.equipment.service.PowerManageService;
import com.equipment.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <电池管理模块相关接口>
 * @author wangyinzi
 */
@RestController
@RequestMapping("/powerManage")
public class PowerManageController {
    //日志
    private Logger log = LoggerFactory.getLogger(PowerManageController.class);

    @Autowired
    private PowerManageService powerManageService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * <电池管理列表查询 分页查询>
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param powerCode 搜索栏 电池编号
     * @param equipCode 搜索栏 设备编号
     * @param powerStatus 搜索栏 电池状态
     * @param errorTimes 搜索栏 错误次数
     * @return （success true:成功，false：失败）（result 返回结果）（message：信息）
     */
    @PostMapping("/findPowerPage")
    public Map<String, Object> findPowerPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "15")Integer pageSize,
                                             String powerCode, String equipCode, Integer errorTimes, Integer powerStatus){
        //返回查询结果
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            Integer borrowStatus = null;
            //根据查询条件返回查询结果
            if(StringUtils.isBlank(powerCode)){
                powerCode = null;
            }
            if(StringUtils.isBlank(equipCode)){
                equipCode = null;
            }

            //搜索栏的电池状态 1待租（在库） 2在租（借出） 3：锁定
            if(null != powerStatus && powerStatus == 0){
                powerStatus = null;
            }
            if(null != powerStatus){
                if(powerStatus == 3){
                    //查询锁定状态为2的电池
                    powerStatus = Constants.POWER_STATUS_2;
                }else{
                    //租借状态为 1 或者 为2 并且锁定状态为不锁定1
                    borrowStatus = powerStatus;
                    powerStatus = Constants.POWER_STATUS_1;
                }
            }

            result = powerManageService.findPowerPage(pageNum, pageSize, powerCode, equipCode, errorTimes, powerStatus, borrowStatus);
        }catch (Exception e){
            log.info("查询电池列表异常", e);
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }

    /**
     * <重置电池的错误次数>
     * @param ids 电池主键ids
     * @return success true:成功，false：失败
     */
    @PostMapping("/resetErrorTimes")
    public Map<String, Object> resetErrorTimes(@RequestBody List<Integer> ids){
        Map<String, Object> result = new HashMap<String, Object>();
        try{
            result = powerManageService.resetPowerInfo(ids);
        }catch (Exception e){
            log.info("重置电池错误次数、锁定状态异常", e);
            result.put("success", false);
            result.put("message", "系统错误");
        }

        return result;
    }

    /**
     * <手动刷新电池信息>
     * @param id 电池主键id
     * @return success true:成功，false：失败
     */
    @PostMapping("/updatePowerInfo")
    public Map<String, Object> updatePowerInfo(@RequestParam(value = "id", required = true) Integer id){
        Map<String, Object> result = new HashMap<String, Object>();
        try{
            result = powerManageService.updatePowerInfo(id);
        }catch (Exception e){
            log.info("刷新电池信息异常", e);
            result.put("success", false);
            result.put("message", "系统错误");
        }

        return result;
    }

    @PostMapping("/testAddPowerToRedis")
    public Map<String, Object> testAddPowerToRedis(String powerCode, String deCode, Integer powerNums){
        Map<String, Object> result = new HashMap<String, Object>();
        try{
            redisTemplate.opsForSet().add(Constants.POWER_UPDATE_LIST_KEY, powerCode);//更新列表
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_ONLINE_EQUIP_KEY, deCode);//所在设备
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_POWER_NUMS_KEY, powerNums);//电量
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_BORROW_STATUS_KEY, 1);//在库
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }

    @PostMapping("/testUpdateErrorTimesToRedis")
    public Map<String, Object> testUpdateErrorTimesToRedis(String powerCode, Integer errorTimes){
        Map<String, Object> result = new HashMap<String, Object>();
        try{
            redisTemplate.opsForSet().add(Constants.POWER_UPDATE_LIST_KEY, powerCode);//更新列表
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_ERROR_TIMES_KEY, errorTimes);//所在设备
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }

    @PostMapping("/testUpdateBorrowTimesToRedis")
    public Map<String, Object> testUpdateBorrowTimesToRedis(String powerCode, Integer borrowTimes){
        Map<String, Object> result = new HashMap<String, Object>();
        try{
            redisTemplate.opsForSet().add(Constants.POWER_UPDATE_LIST_KEY, powerCode);//更新列表
            redisTemplate.opsForValue().set(powerCode + Constants.POWER_BORROW_TIMES_KEY, borrowTimes);//所在设备
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }

    @PostMapping("/testRemovePowerToRedis")
    public Map<String, Object> testRemovePowerToRedis(){
        Map<String, Object> result = new HashMap<String, Object>();
        try{
            //取出全部
            Set<String> list = redisTemplate.opsForSet().members(Constants.POWER_UPDATE_LIST_KEY);
            for(String str : list){
                String obj = (String)redisTemplate.opsForSet().pop(Constants.POWER_UPDATE_LIST_KEY);
            }
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误");
        }
        return result;
    }
}
