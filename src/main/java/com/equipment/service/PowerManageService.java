package com.equipment.service;

import java.util.List;
import java.util.Map;

public interface PowerManageService {
    /**
     * <电池管理列表查询 分页查询>
     * @param indexPage 页码
     * @param pageCount 每页记录数
     * @param powerCode 电池编号
     * @param equipCode 设备编号
     * @param powerStatus 电池状态
     * @param borrowStatus 租借状态
     * @return success true:成功，false：失败
     */
    Map<String, Object> findPowerPage(Integer indexPage, Integer pageCount,
                                      String powerCode, String equipCode, Integer errorTimes,
                                      Integer powerStatus, Integer borrowStatus) throws Exception;

    /**
     * <重置电池的错误次数>
     * @param ids 电池主键ids集合
     * @return success true:成功，false：失败
     */
    Map<String, Object> resetPowerInfo(List<Integer> ids) throws Exception;

    /**
     * <手动刷新电池信息>
     * @param id 电池主键id
     * @return success true:成功，false：失败
     */
    Map<String, Object> updatePowerInfo(Integer id) throws Exception;

}
