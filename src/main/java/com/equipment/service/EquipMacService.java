package com.equipment.service;

import com.equipment.model.old.IN_Info;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface EquipMacService {

    /**
     * 保存设备的mac地址
     */
    void saveEquipMac(String mi, String mac, String ve, String sd);

    /**
     * 获取设备的物料信息
     */
    Map<String, Object> getEquipVideos(Long id);

    /**
     * 显示设备物料详情
     */
    List<Map> showMaterial(Long id);

    /**
     * 下发视频绑定命令
     */
    Map<String, Object> sendVideoMsg(Long id, String mi) throws Exception;

    /**
     * 上报视频播放次数
     * @param info
     */
    Map<String, Object> sendEquipVideoTimes(IN_Info info) throws Exception;

    /**
     * 分页查询设备列表
     * @param pageNum
     * @param pageSize
     * @param mi
     * @return
     */
    PageInfo<Map> findEquipPage(int pageNum, int pageSize, String mi, Integer count);

    /**
     * 查询设备列表全部
     * @param mi
     * @return
     */
    List<Map> findEquipAll(String mi, Integer count);
}
