package com.equipment.service.impl;

import com.equipment.dao.UtilDao;
import com.equipment.entity.Vedeo;
import com.equipment.service.UtilService;
import com.equipment.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description:  辅助工具业务层
 * @Date: Created in 15:36 2019/5/5
 * @Modified By:
 */
@Service
public class UtilServiceImpl implements UtilService {

    @Autowired
    private UtilDao utilDao;

    /** 更新视频表的md5值 */
    @Override
    public Map<String, Object> addVideoFileMd5() {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,3);
        int success= 0;
        //获取md5值字段为空的视频
        List<Vedeo> allVideoByMd5IsNot = utilDao.findAllVideoByMd5IsNot();
        if (allVideoByMd5IsNot!=null && allVideoByMd5IsNot.size()>0){
            for (Vedeo v:allVideoByMd5IsNot) {
                String md5 = Md5Util.fileUrlMd5(v.getFileUrl());
                if (md5!=null){
                    int i = utilDao.updateFileMd5ById(md5, v.getId());
                    success = success+i;
                }
            }
        }

        result.put("msg","成功更新设备md5数："+success);
        return result;
    }
}
