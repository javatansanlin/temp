package com.equipment.redis;

/**
 * @Author: JavaTansanlin
 * @Description: Redis消息接收器
 * @Date: Created in 12:36 2018/8/13
 * @Modified By:
 */

import com.equipment.service.DownlineService;
import com.equipment.service.EquipManageService;
import com.equipment.service.NettyToService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    private CountDownLatch latch;

    @Autowired
    public Receiver(CountDownLatch latch) {
        this.latch = latch;
    }

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** netty的业务接口 */
    @Autowired
    private NettyToService nettyToService;

    /** 下线业务逻辑 **/
    @Autowired
    private DownlineService downlineService;

    @Autowired
    private EquipManageService equipManageService;

    public void receiveMessage(String message) {
        System.out.println("接收到消息："+message);
        try {
            //进行逻辑处理
            if(message.endsWith("-Info")){//判断是否是心跳超时事件
                //获取key中的设备号
                String equitCode = message.replaceAll("-Info","");
                //执行离线业务逻辑
                downlineService.downLine(equitCode);

                //更新系统最后时间为当前系统时间
                long ts = new Date().getTime()/1000;
                equipManageService.updateCurrentHeartTs(equitCode, ts+"");
            }else if (message.endsWith("-Info-save")){//判断是否是要保存心跳数据
                //获取redis中的心跳资源
                String info = message.replaceAll("-save","");
                String json = (String) redisTemplate.opsForValue().get(info);
                if (json!=null){
                    //调用服务
                    nettyToService.onlineAndHeartRecord(json , false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("接收到的设备key消失时间报错"+e.getMessage());
        }
        latch.countDown();
    }


}