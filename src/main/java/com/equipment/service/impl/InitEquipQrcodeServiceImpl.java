package com.equipment.service.impl;

import com.equipment.dao.EquipHeartDetailDao;
import com.equipment.dao.EquipInfoDao;
import com.equipment.dao.QrcodeStoreDao;
import com.equipment.entity.EquipHeartDetail;
import com.equipment.entity.EquipInfo;
import com.equipment.entity.QrcodeStore;
import com.equipment.model.querymodel.InitEQ2EquipQueryModel;
import com.equipment.service.InitEquipQrcodeService;
import com.equipment.util.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 设备与二维码绑定相关的服务  业务实现类
 * @Date: Created in 20:13 2018/8/28
 * @Modified By:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InitEquipQrcodeServiceImpl implements InitEquipQrcodeService {

    /** 设备dao */
    @Autowired
    private EquipInfoDao equipInfoDao;

    /** 设备心跳dao **/
    @Autowired
    private EquipHeartDetailDao heartDetailDao;

    /** 二维码dao */
    @Autowired
    private QrcodeStoreDao qrcodeStoreDao;

    /** 根据设备编号查询设备 */
    @Override
    public PageInfo<InitEQ2EquipQueryModel> findEquip(Integer indexPage, Integer pageCount,String code) {
        PageHelper.startPage(indexPage, pageCount);
        List<InitEQ2EquipQueryModel> ae = equipInfoDao.findNotBindQREquipByCode(code);
        //查询出最新的心跳记录
        if (ae!=null && ae.size()>0){
            //循环所有的设备，获取心跳相关的数据
            for (int i = 0; i < ae.size(); i++) {
                InitEQ2EquipQueryModel e = ae.get(i);
                EquipHeartDetail h = heartDetailDao.selectSDBCCBCRByCode(ae.get(i).getCode());//查询出最新的心跳记录
                if (h!=null){//有心跳记录的情况下执行下面逻辑
                    String ts = h.getTs();//获取心跳的时间戳
                    if (ts!=null && !"".equals(ts))
                        e.setTime(DateUtil.TimeStamp2Date(ts,"yyyy-MM-dd HH:mm:ss"));
                    ae.set(i,e);//替换原来的元素
                }
            }
        }
        return new PageInfo<InitEQ2EquipQueryModel>(ae);
    }

    /** 设备绑定二维码（以设备id和二维码链接为条件） */
    @Override
    public Map<String, Object> bindEquipQrcode(Long equipId, String codeUrl) {
        Map<String , Object> result = new HashMap<>();
        //判断参数
        if(equipId==null || codeUrl==null || equipId<=0 || "".equals(codeUrl)){
            result.put("code" ,1);
            return result;//参数不正确
        }

        try {
            //处理url转义
            codeUrl = URLDecoder.decode(codeUrl, "UTF-8");
        }catch (Exception e){

        }
        //查找该二维码是否存在
        QrcodeStore qrcodeStore = null;
        if (codeUrl.indexOf("alipay.com") >=0){//支付宝链接
            qrcodeStore = qrcodeStoreDao.findQrcodeByAliCode(codeUrl.substring((codeUrl.lastIndexOf("/")+1)));
        }else if (codeUrl.indexOf("weixin.qq.com") >=0){//微信链接
            qrcodeStore = qrcodeStoreDao.findQrcodeByWeChatCode(codeUrl);
        }else {//二码合一链接
            //切割位置
            int i = codeUrl.lastIndexOf("oneCode=") == -1 ? 0 : codeUrl.lastIndexOf("oneCode=") + 8;
            qrcodeStore = qrcodeStoreDao.findQrcodeByOneCode(codeUrl.substring(i));
        }
        if (qrcodeStore==null){
            result.put("code" ,2);
            return result;//查找该二维码错误
        }
        //判断该二维码是否已经绑定
        if(qrcodeStore.getEquip()!=null){
            result.put("code" ,4);
            return result;//该二维码已经绑定，无法再次绑定
        }
        //查询该设备是否存在
        EquipInfo equip = equipInfoDao.findEquipEntityById(equipId);
        if (equip==null){
            result.put("code" ,5);
            return result;//该设备号不存在
        }
        //查询该设备号是否已经绑定
        QrcodeStore byEquipId = qrcodeStoreDao.findQrcodeByEquipId(equipId);
        if (byEquipId!=null){
            result.put("code" ,6);
            return result;//该设备已经绑定过二维码
        }
        //进行绑定操作
        qrcodeStore.setEquip(equipId);
        qrcodeStoreDao.updateQrcodeByEquipId(qrcodeStore);//执行绑定操作
        result.put("code" ,3);
        return result;
    }
}
