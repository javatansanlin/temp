package com.equipment.controller;

import com.alibaba.fastjson.JSON;
import com.equipment.model.equipmanager.EquipManagePage;
import com.equipment.model.equipmanager.EquipPower;
import com.equipment.service.EquipManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 设备管理页面的相关接口
 * @Date: Created in 17:49 2018/8/15
 * @Modified By:
 */
@RestController()
@RequestMapping("/equipManage")
public class EquipManageController {

    @Autowired
    EquipManageService equipManageService;

    /** 设备管理页面的分页以及条件查询 */
    @PostMapping("/findAllEquip")
    public String findAllEquip(String code , Integer indexPage, Integer pageCount ,Integer equipType ,Integer state
            ,String shopName ,String play ,String openId ,String agentName, String time){
        if (indexPage==null || indexPage<=0){
            indexPage = 1;
        }
        if (pageCount==null || pageCount<=0){
            pageCount = 1;
        }
        if (code==null || "".equals(code.trim())){
            code = null;
        }
        return JSON.toJSONString(equipManageService.findEquipManagePageAndByOdition(indexPage,pageCount,code,equipType,state,shopName,play,openId,agentName, time));
    }

    /** 设备管理根据查询条件查询所有用于导出使用 */
    @PostMapping("/findEquipManageByOdition")
    public List<EquipManagePage> findEquipManageByOdition(String code, Integer equipType, Integer state,
                                                   String shopName, String play, String openId, String agentName, String time) {
        return equipManageService.findEquipManageByOdition(code, equipType, state, shopName, play, openId, agentName, time);
    }

    /** 所有的设备类型 */
    @PostMapping("/findAllEquipType")
    public String findAllEquipType(){
        return JSON.toJSONString(equipManageService.findAllEquipType());
    }

    /** 查看每台设备的电池 */
    @PostMapping("/equipPowerDetail")
    public Map<String , Object> equipPowerDetail(String equipCode){
        Map<String , Object> result = new HashMap<>();
        if (equipCode!=null){
            Map<Integer, EquipPower> powerMap = equipManageService.equipPowersDetail(equipCode);
            result.put("powerList",powerMap);
        }
        return result;
    }

    /** 查询店铺已绑定的设备 **/
    @PostMapping("/bundedByshopCode")
    public String bundedByshopCode(String shopCode){
        if (shopCode!=null && !"".equals(shopCode.trim())){
            return JSON.toJSONString(equipManageService.bundedByshopCode(shopCode));
        }
        return null;
    }

    /** 查询店铺已绑定的设备 **/
    @PostMapping("/bundedByshopCodeAndCode")
    public String bundedByshopCode(String shopCode,String code){
        if (shopCode!=null && !"".equals(shopCode.trim())){
            return JSON.toJSONString(equipManageService.bundedByshopCode(shopCode,code));
        }
        return null;
    }

    /** 查询正常在库的未绑定的设备 **/
    @PostMapping("/findNotBundEquip")
    public Map<String , Object> findNotBundEquip(Integer indexPage, Integer pageCount ,String code){
        Map<String , Object> result = new HashMap<>();
        //处理分页的参数
        if (indexPage==null || indexPage<=0){
            indexPage = 1;
        }
        if (pageCount==null || pageCount <=0){
            pageCount = 15;
        }
        return equipManageService.findNotBundEquip(indexPage, pageCount, code);
    }

    /** 店铺绑定设备 **/
    @PostMapping("/bundEquip")
    public Map<String ,Object> bundEquip(Long[] equipId , String shopCode){
        Map<String ,Object> result = new HashMap<>();
        if (equipId==null || shopCode==null || equipId.length<=0 || "".equals(shopCode.trim())){
            result.put("code" , 1);//参数不正确
        }else {
            for (int i = 0; i < equipId.length; i++) {
                result = equipManageService.bundEquip(shopCode , equipId[i]);
            }
        }
        return result;
    }

    /** 店铺通过扫二维码绑定设备 */
    @PostMapping("/bundEquipByCodeUrl")
    public Map<String ,Object> bundEquipByCodeUrl(String shopCode , String codeUrl){
        return equipManageService.bundEquip(shopCode , codeUrl);
    }

    /** 店铺解绑设备 **/
    @PostMapping("/untieEquip")
    public Map<String ,Object> untieEquip(Long equipId){
        Map<String ,Object> result = new HashMap<>();
        if (equipId==null || equipId<=0){
            result.put("code" , 1);//参数不正确
        }else {
            result = equipManageService.untieEquip(equipId);//执行解绑逻辑
        }
        return result;
    }


    /** 批量店铺绑定设备 暂时仅用于线充 **/
    @PostMapping("/bundEquipBatch")
    public Map<String ,Object> bundEquipBatch(String equipCode , Integer num , String shopCode){
        Map<String ,Object> result = new HashMap<>();
        if (equipCode==null || shopCode==null || num ==0 || num == null || "".equals(shopCode.trim())){
            result.put("code" , 1);//参数不正确
        }else {
            result.put("code" , 3);//成功
            result.put("data" , equipManageService.bundEquipBatch(equipCode,num,shopCode));
            return  result;
        }
        return result;
    }

    /** 批量解绑设备 暂时仅用于线充 **/
    @PostMapping("/untieEquipBatch")
    public Map<String ,Object> untieEquipBatch(String equipCode , Integer num){
        Map<String ,Object> result = new HashMap<>();
        if (equipCode==null  || num ==0 || num == null){
            result.put("code" , 1);//参数不正确
        }else {
            result.put("code" , 3);//成功
            result.put("data" , equipManageService.untieEquipBatch(equipCode,num));
            return  result;
        }
        return result;
    }


    /** 批量店铺绑定设备 **/
    @PostMapping("/bundEquipBatchNew")
    public Map<String ,Object> bundEquipBatchNew(String equipCode[] ,String shopCode){
        Map<String ,Object> result = new HashMap<>();
        if (equipCode==null || shopCode==null ||   "".equals(shopCode.trim())){
            result.put("code" , 1);//参数不正确
        }else {
            result.put("code" , 3);//成功
            result.put("data" , equipManageService.bundEquipBatchNew(equipCode,shopCode));
            return  result;
        }
        return result;
    }

    /** 批量解绑设备  **/
    @PostMapping("/untieEquipBatchNew")
    public Map<String ,Object> untieEquipBatchNew(String equipCode[],String shopCode){
        Map<String ,Object> result = new HashMap<>();
        if (equipCode==null  ){
            result.put("code" , 1);//参数不正确
        }else {
            result.put("code" , 3);//成功
            result.put("data" , equipManageService.untieEquipBatchNew(equipCode,shopCode));
            return  result;
        }
        return result;
    }

    /** 临时生成设备的excel文档的文件夹 */
    @Value("${down.cachePath}")
    private String exPath;

    /** 把所有的设备生成excel文档，并且下载 */
    @GetMapping("/createEqEx")
    public void createEqEx(String code , Integer equipType , Integer state, String shopName , String play , String openId , String agentName ,
                           String time,
                           HttpServletResponse response) throws Exception{
        //参数处理
        if (code!=null && "".equals(code.trim())){
            code = null;
        }
        if (shopName!=null && "".equals(shopName.trim())){
            shopName = null;
        }
        if (play!=null && "".equals(play.trim())){
            play = null;
        }
        if (openId!=null && "".equals(openId.trim())){
            openId = null;
        }
        if (agentName!=null && "".equals(agentName.trim())){
            agentName = null;
        }

        //获取根目录下的地址
        String path = "C://" + exPath+"/";
        String ex = equipManageService.createEqEx(path ,code, equipType, state, shopName, play, openId, agentName, time);
        File file = new File(path+ex);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + ex );// 设置文件名
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

    /** 把所有的设备生成PDF文档，并且下载 */
    @GetMapping("/createEqPDF")
    public void createEqPDF(String code , Integer equipType , Integer state, String shopName , String play , String openId , String agentName ,
                            String time,
                            HttpServletResponse response) throws Exception{
        //参数处理
        if (code!=null && "".equals(code.trim())){
            code = null;
        }
        if (shopName!=null && "".equals(shopName.trim())){
            shopName = null;
        }
        if (play!=null && "".equals(play.trim())){
            play = null;
        }
        if (openId!=null && "".equals(openId.trim())){
            openId = null;
        }
        if (agentName!=null && "".equals(agentName.trim())){
            agentName = null;
        }

        //获取根目录下的地址
        String path = "C://" + exPath+"/";
        byte[] ex = equipManageService.createEqPDF(path ,code, equipType, state, shopName, play, openId, agentName, time);
        if (ex!=null) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + "equipment.pdf" );// 设置文件名
            try {
                OutputStream os = response.getOutputStream();
                os.write(ex);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }

        }
    }

    /** 伪删除设备（更新设备的出库状态） */
    @PostMapping("/delete")
    public Map<String ,Object> delete(Long id){
        return equipManageService.delete(id);
    }

    /** 查询设备周围的wifi */
    @PostMapping("/findRoundWIFI")
    public Map<String ,Object> findRoundWIFI(String eqCode){
        return equipManageService.findRoundWIFI(eqCode);
    }

    /** 根据账户密码连接wifi */
    @PostMapping("/connectWIFI")
    public Map<String ,Object> connectWIFI(String eqCode ,String name ,String pwd){
        return equipManageService.connectWIFI(eqCode ,name ,pwd);
    }

    //手动更新设备心跳时间
    @PostMapping("/updateEquipHeartTs")
    public String updateEquipHeartTs(Long id){
        String ts = equipManageService.updateEquipHeartTs(id);
        return JSON.toJSONString(ts);
    }


    /** //代理商后台绑定设备（已经绑定了店铺） **/
    @PostMapping("/bindEqForAgent")
    public Map<String ,Object> bindEqForAgent(String[] equipCode ,String shopCode){
        Map<String ,Object> result = new HashMap<>();
        if (equipCode == null || shopCode == null ){
            result.put("code" , 1);//参数不正确
        }else {
            result = equipManageService.bindEqForAgent(equipCode,shopCode);
        }
        return result;
    }

}
