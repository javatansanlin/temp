package com.equipment.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.equipment.entity.QrcodeInfo;
import com.equipment.model.equipmanager.QRManger;
import com.equipment.service.QrcodeService;
import com.equipment.util.DateUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码相关的接口
 * @Date: Created in 20:04 2018/9/6
 * @Modified By:
 */
@Controller
@RequestMapping("/qrEq")
public class QrcodeController {

    /** 二维码zip缓存文件的保存地址 */
    @Value("${qrZipCache}")
    private String qrZipCache;

    @Autowired
    QrcodeService qrcodeService;

    /** 生成需要下载得文件，返回文件名 */
    @RequestMapping("/createDownFile")
    @ResponseBody
    public Map<String ,Object> createDownFile(String id ,Integer typeId){
        return qrcodeService.qrCodeDown(id ,typeId);
    }

    /** 根据文件名下载文件 */
    @RequestMapping("/downQr")
    public void downQr(HttpServletRequest request, HttpServletResponse response ,String fileName){
        //获取压缩文件的地址
        String fileAdress = qrZipCache+fileName;
        File file = new File(fileAdress);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + DateUtil.Date2TimeStamp(new Date())+".zip" );// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                System.out.println("success");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /** 根据店铺名称，设备号，添加时间，是否绑定设备。查询二维码信息（修改：增加所属平台） */
    @PostMapping("/findQr")
    @ResponseBody
    public PageInfo<QRManger> findQr(String shopName , String eqCode , String time ,
                                     Integer isBan ,Integer pageIndex ,Integer pageCount ,Integer infoId){
        return qrcodeService.findQr(shopName ,eqCode ,time ,isBan,pageIndex,pageCount,infoId);
    }

    /** 删除选择的二维码 */
    @PostMapping("/deleSelect")
    @ResponseBody
    public Map<String ,Object> deleSelect(String id){
        return qrcodeService.deleSelect(id);
    }

    /** 删除全部二维码 */
    @PostMapping("/deleAll")
    @ResponseBody
    public Map<String ,Object> deleAll(){
        return qrcodeService.deleAll();
    }

    /** 查询所有的二维码信息 */
    @PostMapping("/findAllInfo")
    @ResponseBody
    public List<QrcodeInfo> findAllInfo(){
        return qrcodeService.findAllInfo();
    }

    /** 根据设备编号查询二维码资料 */
    @RequestMapping("/findCodeByEqCode")
    @ResponseBody
    public Map<String ,Object> findCodeByEqCode(String code){
        return qrcodeService.findCodeByEqCode(code);
    }

    /** 验证key */
    @PostMapping("/keyUp")
    @ResponseBody
    boolean keyUp(String key){
        return qrcodeService.keyUp(key);
    }

    /** 【后台系统】根据设备号查询可以解绑的设备 */
    @PostMapping("/queryCanUntie")
    @ResponseBody
    public Map<String ,Object> queryCanUntie(String data ,String key){
        return qrcodeService.queryCanUntie(data ,key);
    }

    /** 【后台系统】根据设备编号解绑设备 */
    @PostMapping("/untieEq")
    @ResponseBody
    public Map<String ,Object> untieEq(String data ,String key){
        return qrcodeService.untieEq(data,key);
    }

    /** 【后台系统】针对mqtt的视频机更新设备服务器的二维码 */
    @PostMapping("/updateMqEqQrCode")
    @ResponseBody
    public Map<String ,Object> updateMqEqQrCode(String eqCode){
        return qrcodeService.updateMqEqQrCode(eqCode);
    }

    /** 针对只生成二码合一的二维码 */
    @PostMapping("/createQr")
    @ResponseBody
    public Map<String ,Object> createQr(Integer num ,Long qrInfoId){
        return  qrcodeService.createQr(num ,qrInfoId);
    }

    /** 批量绑定线充设备 */
    @PostMapping("/genLineChargerCode")
    @ResponseBody
    public Map<String ,Object> genLineChargerCode(Long num,Integer type){
        return  qrcodeService.genLineChargerCode(num,type);
    }

}
