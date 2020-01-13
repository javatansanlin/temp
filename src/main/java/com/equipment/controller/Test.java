package com.equipment.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.equipment.dao.TestDao;
import com.equipment.entity.EquipInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 17:47 2019/8/26
 * @Modified By:
 */
@RestController
@RequestMapping("test")
public class Test {

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("addInt")
    public String addInt(){
        Integer one = 1;
        redisTemplate.opsForValue().increment("UseMinutes", one);
        return "success";
    }

    @GetMapping("addLong")
    public String addLong(){
        Long one = 1l;
        redisTemplate.opsForValue().increment("UseMinutes", one);
        return "success";
    }

    @GetMapping("geo")
    public Map<String ,Object> geo(){
        Map result = new HashMap();

        String key = "B";
        Point point = new Point(114.403629,30.475326);
        EquipInfo equipInfo1 = new EquipInfo();
        equipInfo1.setId(1L);
        equipInfo1.setCode("111111111");
        RedisGeoCommands.GeoLocation<Object> location = new RedisGeoCommands.GeoLocation<Object>(equipInfo1,point);
        Point point2 = new Point(114.386119,30.473688);
        EquipInfo equipInfo2 = new EquipInfo();
        equipInfo2.setId(2L);
        equipInfo2.setCode("222222222");
        RedisGeoCommands.GeoLocation <Object> location2 = new RedisGeoCommands.GeoLocation <Object>(equipInfo2,point2);
        // 保存坐标
        redisTemplate.opsForGeo().add(key,location);
        redisTemplate.opsForGeo().add(key,location2);



        Point center = new Point(114.403629, 30.475316);
        Distance radius = new Distance(50000, Metrics.NEUTRAL);
        Circle within = new Circle(center, radius);
        // order by 距离 limit 20 ，同时返回距离中心点的距离
        //设置geo查询参数
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
        geoRadiusArgs = geoRadiusArgs.includeCoordinates().includeDistance();//查询返回结果包括距离和坐标
        geoRadiusArgs.sortAscending();//ASC ： 根据中心的位置， 按照从近到远的方式返回位置元素。DESC ： 根据中心的位置， 按照从远到近的方式返回位置元素。
        geoRadiusArgs.limit(20);//限制查询数量
        GeoResults<RedisGeoCommands.GeoLocation<EquipInfo>> radius1 = redisTemplate.opsForGeo().radius(key, within, geoRadiusArgs);
        result.put("radius1" ,radius1);
        for (int i = 0; i < radius1.getContent().size(); i++) {
            System.out.println(radius1.getContent().get(i).getContent().getName().getCode());
        }

        return result;
    }

    @Autowired
    private TestDao testDao;

    /**
     * 处理有心跳却是离线的设备
     * @return
     */
    @GetMapping("dealEq")
    public Map<String ,Object> dealEq(){
        Map<String ,Object> result = new HashMap<>();
        List<String> allEq = testDao.findAllEq();
        if (allEq!=null){
            int s1 = 0;
            int f = 0;
            for (String s : allEq) {
                //获取心跳中是否有数据
                Object o = redisTemplate.opsForValue().get(s + "-Info");
                if (o!=null){
                    testDao.updateState(1 ,s);
                    s1++;
                }else {
                    testDao.updateState(2 ,s);
                    f++;
                }
            }
            result.put("msg" ,"处理上线数："+s1+"。下线数："+f);
        }else {
            result.put("msg" ,"暂无数据可处理");
        }
        return result;
    }

    @RequestMapping(value="/fileUpload",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity fileUpload(@RequestParam("files") MultipartFile[] files,HttpServletRequest request) throws IOException {

        /*CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());


        commonsMultipartResolver.setDefaultEncoding("utf-8");

        if (commonsMultipartResolver.isMultipart(request)) {
            MultipartHttpServletRequest mulReq = (MultipartHttpServletRequest) request;
            Map<String, MultipartFile> map = mulReq.getFileMap();

            // key为前端的name属性，value为上传的对象（MultipartFile）
            for (Map.Entry<String, MultipartFile> entry : map.entrySet()) {
                MultipartFile value = entry.getValue();
                // 自己的保存文件逻辑
                String filePath = value.getOriginalFilename();
                //分割文件名
                String last = filePath.substring(filePath.lastIndexOf("."));
                String filename = UUID.randomUUID().toString();
                //String logos= pathLogo.substring(pathLogo.lastIndexOf("/") + 1);
                // 转存文件
                value.transferTo(new File("E:/1111/" + filename + last));
            }

        }
        return ResponseEntity.status(HttpStatus.OK).body("success");*/
        List<String> list = new ArrayList<>();
        //判断file数组不能为空并且长度大于0
        if(files!=null && files.length>0) {
            try {
                //循环获取file数组中得文件
                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    String filePath =file.getOriginalFilename();
                    //分割文件名
                    String last = filePath.substring(filePath.lastIndexOf("."));
                    String filename = UUID.randomUUID().toString();
                    //String logos= pathLogo.substring(pathLogo.lastIndexOf("/") + 1);
                    // 转存文件
                    file.transferTo(new File("E:/1111/"+filename+last));
                    list.add(filename+last);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        JSONArray array = JSONArray.parseArray(JSON.toJSONString(list));
        return ResponseEntity.status(HttpStatus.OK).body(array);
    }

}
