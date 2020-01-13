package com.equipment.controller;

import com.equipment.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * @Author: JavaTansanlin
 * @Description:视频相关的接口
 * @Date: Created in 11:47 2018/12/7
 * @Modified By:
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    /** 注入业务层 */
    @Autowired
    private VideoService videoService;

    /**
     * 添加视频标签
     * @param name 标签名字
     * @param operator 操作员
     * @return
     */
    @PostMapping("/addLable")
    public Map<String ,Object> addLable(String name ,Long operator){
        return videoService.addLable(name ,operator);
    }

    /** 查看所有的视频标签 */
    @PostMapping("/findAllVideoLable")
    public Map<String ,Object> findAllVideoLable(){
        return videoService.findAllVideoLable();
    }

    /** 查询该视频名字是否存在 */
    @PostMapping("/videoNameIsExit")
    public Map<String ,Object> videoNameIsExit(String name){
        return videoService.videoNameIsExit(name);
    }

    /**
     * 添加视频
     * @param name 名字
     * @param labelId 标签id
     * @param filePath 视频地址
     * @return
     */
    @PostMapping("/addVideo")
    public Map<String ,Object> addVideo(String name ,Long labelId ,String filePath ,Long operator){
        return videoService.addVideo(name ,labelId ,filePath ,operator);
    }

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
    @PostMapping("/videoList")
    public Map<String ,Object> videoList(String eqCode ,String vCode , String vName ,String shopCode ,
                                         String shopName ,String agentOid ,Integer pageSize ,Integer pageNum){
        return videoService.videoList(eqCode ,vCode ,vName ,shopCode ,shopName ,agentOid ,pageSize ,pageNum);
    }

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
    @PostMapping("/eqList")
    public Map<String ,Object> eqList(String eqCode ,String vCode ,String vName ,
                                      String sCode ,String sName ,String agentOid ,Integer pageSize ,Integer pageNum){
        return videoService.eqList(eqCode ,vCode ,vName ,sCode ,sName ,agentOid ,pageSize ,pageNum);
    }

    /** 删除视频 */
    @PostMapping("/deleVideo")
    public Map<String ,Object> deleVideo(Long vId){
        return videoService.deleVideo(vId);
    }

    /** 设备绑定视频-->查找视频 */
    @PostMapping("/eqBindFindVideo")
    public Map<String ,Object> eqBindFindVideo(String eqCode ,String vName ,String vCode ,Integer pageSize ,Integer pageNum){
        return videoService.eqBindFindVideo(eqCode ,vName ,vCode ,pageSize ,pageNum);
    }

    /** 设备绑定视频 */
    @PostMapping("/eqBindVideo")
    public Map<String ,Object> eqBindVideo(String eqCode ,String vid ,Long operator){
        return videoService.eqBindVideo(eqCode ,vid ,operator);
    }

    /** 设备解绑视频 evId-视频设备关系记录id */
    @PostMapping("/untying")
    public Map<String ,Object> untying(Long evId){
        return videoService.untying(evId);
    }

    /** 向设备发送更新视频指令 */
    @PostMapping("/updateEqVideo")
    public Map<String ,Object> updateEqVideo(String eqCode){
        return videoService.updateEqVideo(eqCode);
    }

    /** 查询已经绑定的视频列表 */
    @PostMapping("/findEqBindedVideo")
    public Map<String ,Object> findEqBindedVideo(String eqCode){
        return videoService.findEqBindedVideo(eqCode);
    }

    /** 根据设备编号查询设备的当前音量 */
    @PostMapping("/findEqVolume")
    public Map<String ,Object> findEqVolume(String eqCode){
        return videoService.findEqVolume(eqCode);
    }

    /** 调节设备音量 */
    @PostMapping("/adjustEqVolume")
    public Map<String ,Object> adjustEqVolume(String eqCode ,Integer volume){
        return videoService.adjustEqVolume(eqCode ,volume);
    }

    /** 批量操作-设备绑定视频 */
    @PostMapping("/batchEqBindVideo")
    public Map<String ,Object> batchEqBindVideo(String batchVid ,String batchEq ,Long operator ,Integer ti){
        return videoService.batchEqBindVideo(batchVid ,batchEq ,operator ,ti);
    }

    /** 编辑视频 删除部分已绑定的视频*/
    @PostMapping("/delEqBindVideo")
    public Map<String ,Object> delEqBindVideo(String batchVid ,String eq){
        return videoService.delEqBindVideo(batchVid ,eq);
    }


    /** 设备绑定默认logo */
    @PostMapping("/binDefalutLogo")
    public Map<String ,Object> binDefalutLogo(String eqCode){
        return videoService.binDefalutLogo(eqCode);
    }

    /** 设置视频机的公共模板 */
    @PostMapping("/settingMovieLocal")
    public Map<String ,Object> settingMovieLocal(Integer upType ,String upUrl ,Integer centerType ,String centerUrl,
                                                 Integer downType ,String downUrl){
        return videoService.settingMovieLocal(upType ,upUrl ,centerType ,centerUrl ,downType ,downUrl);
    }

    /** 回显视频机的公共模板 */
    @PostMapping("/getMovieLocal")
    public Map<String ,Object> getMovieLocal(){
        return videoService.getMovieLocal();
    }

}
