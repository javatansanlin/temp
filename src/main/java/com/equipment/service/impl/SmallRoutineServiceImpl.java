package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.equipment.dao.EquipHeartDetailDao;
import com.equipment.dao.EquipInfoDao;
import com.equipment.entity.EquipHeartDetail;
import com.equipment.model.old.Drivers;
import com.equipment.model.old.IN_Info;
import com.equipment.model.querymodel.FindRoundEqModel;
import com.equipment.service.SmallRoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 15:26 2018/10/11
 * @Modified By:
 */
@Service
@Transactional
public class SmallRoutineServiceImpl implements SmallRoutineService {

    /** 注入dao */
    @Autowired
    EquipHeartDetailDao heartDetailDao;
    @Autowired
    EquipInfoDao equipInfoDao;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 域名配置 */
    @Value("${ser.yuming}")
    private String yuming;

    /**
     * 根据坐标点查询附近点的设备
     */
    @Override
    public Map<String, Object> findRoundEq(Double lo, Double la) {
        Map<String ,Object> result = new HashMap<>();
        //判断参数
        if (lo==null || la==null){
            result.put("code" ,1);
            result.put("msg" ,"请传入正确的经纬度");
            return result;
        }
        //先计算查询点的经纬度范围
        double r = 6371;//地球半径千米
        double dis = 200;//0.5千米距离
        double dlng =  2*Math.asin(Math.sin(dis/(2*r))/Math.cos(la*Math.PI/180));
        dlng = dlng*180/Math.PI;//角度转为弧度
        double dlat = dis/r;
        dlat = dlat*180/Math.PI;
        double minla =la-dlat;
        double maxla = la+dlat;
        double minlo = lo -dlng;
        double maxlo = lo + dlng;

        //新的需求：根据店铺的经纬度查询出附近设备
        List<FindRoundEqModel> newFindRoundEqModels = new ArrayList<>();
        List<FindRoundEqModel> findRoundEqModels = heartDetailDao.newFindRoundEqByShop(minlo, maxlo, minla, maxla);
        if (findRoundEqModels!=null){
            for (int i = 0; i < findRoundEqModels.size(); i++) {
                int cb = 0;//可借数量
                int cr = 0;//可还数量
                FindRoundEqModel e = findRoundEqModels.get(i);
                //拿到店铺id，查询出所有设备的可还和可借
                List<String> eq = equipInfoDao.findEqCodeByShopId(e.getId());
                if (eq!=null && eq.size()>0){
                    for (String q:eq) {
                        //获取redis里面的数据
                        String s= (String) redisTemplate.opsForValue().get(q+"-Info");
                        if (s!=null && !"".equals(s)){
                            IN_Info in_info = JSON.parseObject(s, IN_Info.class);
                            Drivers dd = in_info.getDD();
                            if (dd!=null){
                                cb = cb + Integer.parseInt(dd.getCB());//可借数
                                cr = cr + Integer.parseInt(dd.getCR());//可还数
                            }
                        }else {//离线便查询最新的心跳记录
                            EquipHeartDetail h = heartDetailDao.selectSDBCCBCRByCode(e.getMi());//查询出最新的心跳记录
                            if (h!=null){//有心跳记录的情况下执行下面逻辑
                                cb = cb + Integer.parseInt(h.getCb());//可借数
                                cr = cr + Integer.parseInt(h.getCr());//可还数
                            }
                        }
                    }
                    e.setCb(cb+"");//赋值可借数
                    e.setCr(cr+"");//可还数
                    e.setSLogo(yuming+":8075/"+e.getSLogo());
                    newFindRoundEqModels.add(e);
                }
            }
        }

        result.put("code" ,3);
        result.put("msg" ,"成功");
        result.put("data" ,newFindRoundEqModels);
        return result;
    }
}
