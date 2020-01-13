package com.equipment.service;

/**
 * @Author: JavaTansanlin
 * @Description: 提供给netty的业务接口
 * @Date: Created in 18:05 2018/8/13
 * @Modified By:
 */
public interface NettyToService {

    /** 根据传过来的心跳值进行心跳记录，以及电池的所在的心跳更新 , online是上线通知，true为通知更改设备状态 **/
    Boolean onlineAndHeartRecord(String json ,Boolean online);

    /** 服务器的租借指令业务逻辑 **/
    Long borrowRecordByServer(String borrowInstruction, String code);

    /** 设备的租借结果指令业务逻辑 **/
    Integer borrowRecordByEquip(Long id , String json);

    /** 设备的归还指令业务逻辑 **/
    String returnRecord(String serverJson , String equipJson , String code);

}
