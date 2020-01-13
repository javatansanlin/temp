package com.equipment.controller;

import com.equipment.service.NettyToService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: JavaTansanlin
 * @Description: netty通知服务器的控制层
 * @Date: Created in 20:47 2018/8/14
 * @Modified By:
 */
@RestController
public class NettyToServiceController {

    @Autowired
    NettyToService nettyToService;

    /** 根据传过来的心跳值进行心跳记录，以及电池的所在的心跳更新 , online是上线通知，true为通知更改设备状态 **/
    @PostMapping("onlineAndHeartRecord")
    public Boolean onlineAndHeartRecord(String json , Boolean online){
        if (json!=null){
            return nettyToService.onlineAndHeartRecord(json , online);
        }
        return false;
    }

    /** 记录租借的指令（服务端的指令记录） , 返回记录id */
    @PostMapping("borrowRecordByServer")
    public Long borrowRecordByServer(String borrowInstruction , String code){
        if (borrowInstruction!=null && !"".equals(borrowInstruction.trim()) && code !=null && !"".equals(code.trim())){
            return nettyToService.borrowRecordByServer(borrowInstruction , code);
        }
        return null;
    }

    /** 记录租借指令（设备端的指令记录） id-指令记录的id，json-设备要记录的指令**/
    @PostMapping("borrowRecordByEquip")
    public Boolean borrowRecordByEquip(Long id , String json){
        if (id!=null && id>0 && json!=null && !"".equals(json.trim())){
            nettyToService.borrowRecordByEquip(id,json);
            return true;
        }
        return false;
    }

    /** 记录归还指令（根据现在的业务，设备端和服务端一起记录） **/
    @PostMapping("returnRecord")
    public Boolean returnRecord(String serverJson , String equipJson , String code){
        if (code!=null && !"".equals(code.trim())){
            nettyToService.returnRecord(serverJson , equipJson ,code);
            return true;
        }
        return false;
    }

}
