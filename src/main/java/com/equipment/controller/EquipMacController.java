package com.equipment.controller;

import com.alibaba.fastjson.JSON;
import com.equipment.model.old.IN_Info;
import com.equipment.service.EquipMacService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/equipMac")
@Slf4j
public class EquipMacController {

    @Autowired
    private EquipMacService equipMacService;

    /**
     * 保存设备的mac地址
     * @return
     */
    @PostMapping("/saveEquipMac")
    public Map<String, Object> saveEquipMac(String mi, String mac, String ve, String sd){
        Map<String, Object> result = new HashMap<>();
        try {
            log.info("saveEquipMac请求参数：mi:"+mi+",mac:"+mac+",ve:"+ve+",sd:"+sd);
            equipMacService.saveEquipMac(mi, mac, ve, sd);
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            log.info("保存mac地址异常", e);
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 获取设备的物料信息详情
     * @return
     */
    @PostMapping("/showMaterial")
    public Map<String, Object> showMaterial(Long id){
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map> list = equipMacService.showMaterial(id);
            result.put("result", list);
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            log.info("获取物料信息失败", e);
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 获取设备的物料信息
     * @return
     */
    @PostMapping("/getEquipVideos")
    public Map<String, Object> getEquipVideos(Long id){
        Map<String, Object> result = new HashMap<>();
        try {
            result = equipMacService.getEquipVideos(id);
        }catch (Exception e){
            log.info("获取物料信息失败", e);
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 上报播放次数
     * @param info
     * @return
     */
    @PostMapping("/sendEquipVideoTimes")
    public Map<String, Object> sendEquipVideoTimes(@RequestBody IN_Info info){
        log.info("请求参数info："+JSON.toJSONString(info));
        Map<String, Object> result = new HashMap<>();
        try {
            result = equipMacService.sendEquipVideoTimes(info);
        }catch (Exception e){
            log.info("上报播放次数失败", e);
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 下发视频绑定命令
     * @param id 主键id
     * @param mi 设备编号
     * @return
     */
    @PostMapping("/sendVideoMsg")
    public Map<String, Object> sendVideoMsg(Long id, String mi){
        Map<String, Object> result = new HashMap<>();
        try{
            result = equipMacService.sendVideoMsg(id, mi);
        }catch (Exception e){
            log.info("下发视频绑定命令失败", e);
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 分页查询设备列表
     * @param pageNum
     *
     * @param pageSize
     * @param mi
     * @return
     */
    @PostMapping("/findEquipListPage")
    public Map<String, Object> findEquipListPage(@RequestParam(defaultValue = "1") int pageNum,
                                             @RequestParam(defaultValue = "15") int pageSize,
                                             String mi, Integer count){
        Map<String, Object> result = new HashMap<>();
        try {
            PageInfo pageInfo = equipMacService.findEquipPage(pageNum, pageSize, mi, count);
            result.put("result", pageInfo);
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            log.info("查询设备列表异常");
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }

    /**
     * 分页查询设备列表
     * @param mi
     * @return
     */
    @PostMapping("/findEquipListAll")
    public Map<String, Object> findEquipListAll(String mi, Integer count){
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map> list = equipMacService.findEquipAll(mi, count);
            result.put("result", list);
            result.put("success", true);
            result.put("message", "操作成功");
        }catch (Exception e){
            log.info("查询设备列表异常");
            result.put("success", false);
            result.put("message", "操作失败");
        }

        return result;
    }


}
