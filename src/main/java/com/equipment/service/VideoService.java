package com.equipment.service;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:视频相关的业务逻辑
 * @Date: Created in 15:16 2018/12/10
 * @Modified By:
 */
public interface VideoService {

    /**
     * 查询设备视频列表
     * @param eqCode 设备编号
     * @param vCode 视频编号
     * @param vName 视频名称
     * @param sCode 店铺编号
     * @param sName 店铺名称
     * @param agentOid 代理oid
     * @param pageSize 显示每页数据数量
     * @param pageNum 当前页
     * @return
     */
    Map<String,Object> eqList(String eqCode, String vCode, String vName, String sCode, String sName,
                              String agentOid, Integer pageSize, Integer pageNum);

    /**
     * 添加视频标签
     * @param name 标签名字
     * @param operator 操作员
     * @return
     */
    Map<String,Object> addLable(String name, Long operator);

    /** 查看所有的视频标签 */
    Map<String,Object> findAllVideoLable();

    /**
     * 添加视频
     * @param name 名字
     * @param labelId 标签id
     * @param filePath 视频地址
     * @return
     */
    Map<String,Object> addVideo(String name, Long labelId, String filePath ,Long operator);

    /** 查询该视频名字是否存在 */
    Map<String,Object> videoNameIsExit(String name);

    /**
     * 查询视频列表
     * @param eqCode 设备编号
     * @param vCode 视频编号
     * @param vName 视频名称
     * @param shopCode 店铺编号
     * @param shopName 店铺名称
     * @param agentOid 代理oid
     * @param pageSize 显示每页数据数量
     * @param pageNum 当前页
     * @return
     */
    Map<String,Object> videoList(String eqCode, String vCode, String vName, String shopCode, String shopName, String agentOid, Integer pageSize, Integer pageNum);

    /** 删除视频 */
    Map<String,Object> deleVideo(Long vId);

    /** 设备绑定视频-->查找视频 */
    Map<String,Object> eqBindFindVideo(String eqCode, String vName, String vCode, Integer pageSize, Integer pageNum);

    /** 设备绑定视频 */
    Map<String,Object> eqBindVideo(String eqCode, String vid ,Long operator);

    /** 设备解绑视频 evId-视频设备关系记录id */
    Map<String,Object> untying(Long evId);

    /** 向设备发送更新视频指令 */
    Map<String,Object> updateEqVideo(String eqCode);

    /** 查询已经绑定的视频列表 */
    Map<String,Object> findEqBindedVideo(String eqCode);

    /** 根据设备编号查询设备的当前音量 */
    Map<String,Object> findEqVolume(String eqCode);

    /** 调节设备音量 */
    Map<String,Object> adjustEqVolume(String eqCode, Integer volume);

    /** 批量操作-设备绑定视频 */
    Map<String,Object> batchEqBindVideo(String batchVid, String batchEq ,Long operator ,Integer ti);

    Map<String,Object> delEqBindVideo(String batchVid, String batchEq);

    /** 设备绑定默认logo */
    Map<String,Object> binDefalutLogo(String eqCode);

    /** 设置视频机的公共模板 */
    Map<String,Object> settingMovieLocal(Integer upType, String upUrl, Integer centerType, String centerUrl, Integer downType, String downUrl);

    /** 回显视频机的公共模板 */
    Map<String,Object> getMovieLocal();
}
