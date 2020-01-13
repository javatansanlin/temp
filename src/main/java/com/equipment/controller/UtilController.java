package com.equipment.controller;

import com.alibaba.fastjson.JSON;
import com.equipment.model.old.Battery;
import com.equipment.model.old.IN_Info;
import com.equipment.mqtt.PubMsg;
import com.equipment.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: JavaTansanlin
 * @Description: 辅助工具接口
 * @Date: Created in 15:33 2019/5/5
 * @Modified By:
 */
@RestController
@RequestMapping("/util")
public class UtilController {

    /** 注入业务层 */
    @Autowired
    private UtilService utilService;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 发布 */
    @Autowired
    private PubMsg pubMsgl;

    /** 更新视频表的md5值 */
    @PostMapping("/addVideoFileMd5")
    public Map<String ,Object> addVideoFileMd5(){
        return utilService.addVideoFileMd5();
    }

    /** 根据设备编号弹出所有的电池 */
    @GetMapping("/borrowTest")
    public String borrowTest(String borrowTest){
        if (borrowTest!=null){
            //获取设备心跳中的电池
            String o = (String)redisTemplate.opsForValue().get(borrowTest + "-Info");
            if (o==null){
                return "心跳不存在";
            }
            try {
                IN_Info in_info = JSON.parseObject(o, IN_Info.class);
                List<Battery> bl = in_info.getDD().getBL();
                if (bl==null){
                    return "电池为空！";
                }
                int i = 0;
                for (Battery battery:bl) {
                    String b = "{\"equipCode\":\""+borrowTest+"\",\"orderCode\":\"19156136310301111111\",\"powerBi\":\""+battery.getBI()+"\",\"time\":1561363103053,\"type\":\"BorrowRequest\"}";
                    pubMsgl.publish(b,UUID.randomUUID().toString(),"YDB-Order-To-Netty");
                    Thread.sleep(800);
                    i++;
                }
                return "成功发送："+i;
            }catch (Exception e){
                return "心跳解析报错";
            }
        }
        return "设备编号空";
    }

}
