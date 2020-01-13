package com.equipment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.equipment.dao.BasicsettingDao;
import com.equipment.dao.EquipInfoDao;
import com.equipment.dao.QrcodeStoreDao;
import com.equipment.dao.VideoDao;
import com.equipment.entity.EquipInfo;
import com.equipment.entity.QrcodeInfo;
import com.equipment.entity.QrcodeStore;
import com.equipment.model.equipmanager.MovieLocal;
import com.equipment.model.equipmanager.MovieLocalDetail;
import com.equipment.model.equipmanager.QRManger;
import com.equipment.model.old.IN_Info;
import com.equipment.model.querymodel.FindQrcodeStore;
import com.equipment.mqtt.PubMsg;
import com.equipment.service.QrcodeService;
import com.equipment.util.DateUtil;
import com.equipment.util.QrcodeDown;
import com.equipment.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
/**
 * @Author: JavaTansanlin
 * @Description: 二维码相关的业务实现类
 * @Date: Created in 20:34 2018/9/6
 * @Modified By:
 */
@Service
@Transactional
public class QrcodeServiceImpl implements QrcodeService {

    /** 二维码图片保存的地址 */
    @Value("${qrCodeFileDir}")
    private  String qrCodeFileDir;
    /** 二维码zip缓存文件的保存地址 */
    @Value("${qrZipCache}")
    private String qrZipCache;

    /** 项目域名 */
    @Value("${qrcode.yuming}")
    private String yuming;

    /** redis操作 */
    @Autowired
    private RedisTemplate redisTemplate;

    /** 发布 */
    @Autowired
    private PubMsg pubMsgl;

    @Autowired
    QrcodeStoreDao storeDao;

    @Autowired
    BasicsettingDao basicsettingDao;

    @Autowired
    EquipInfoDao equipInfoDao;

    @Autowired
    private VideoDao videoDao;

    /** 静态资源地址 */
    @Value("${web.static-path}")
    private String staticPath;

    /** 查询出未绑定的二维码,并且生成二维码图片，打包成zip文件，并且返回路径 */
    @Override
    public Map<String ,Object> qrCodeDown(String id ,Integer typeId) {
        Map<String ,Object> result = new HashMap<>();
        //判断参数
        if (id==null){
            result.put("code" ,1);
            result.put("msg" ,"请选择你需要下载的二维码");
            return result;
        }
        try {
            JSONArray jsonArray = JSON.parseArray(id);
            List<Integer> integers = jsonArray.toJavaList(Integer.class);
            //查询未绑定的二维码
            List<FindQrcodeStore> list = storeDao.findNotBindQrcodeByListId(integers);
            if (list!=null && list.size()>0){
                if (typeId!=null && typeId==5){
                    //随机生成一个时间戳
                    String stamp = DateUtil.Date2TimeStamp(new Date());
                    //生成二码合一
                    String oneCode = "";
                    //文件个数
                    int filNum = 0;
                    //生成本地文件
                    for (FindQrcodeStore q:list) {

                        boolean flag = false;
                        //判断该二维码是否绑定了设备，如果绑定，判断是否是线充，不是，则不导出
                        if (q.getEquip()==null){
                            flag = true;
                        }else {
                            Long eqTypeId = storeDao.findEqTypeId(q.getEquip());
                            if (eqTypeId!=null && eqTypeId==5){
                                flag = true;
                            }
                        }

                        if (flag){
                            //设备编号
                            String s = "";
                            //判断如果是线充，生成虚拟设备编号信息，并且绑定该二维码
                            if (q.getEquip()==null && q.getIsbind()==2 && !q.getCode().startsWith("G")){
                                s = "XC-"+generateRandomStr(10);
                                //插入一条设备信息
                                EquipInfo equipInfo = new EquipInfo();
                                equipInfo.setCode(s);
                                equipInfo.setIsstock(1);
                                equipInfo.setType(5L);
                                equipInfo.setState(1);
                                equipInfo.setServer(1L);
                                equipInfoDao.insert(equipInfo);
                                //更新二维码绑定该设备
                                storeDao.updateQrCodeIsBanByID(equipInfo.getId() ,q.getId());
                            }else {
                                s = storeDao.findEqById(q.getEquip());
                            }

                            oneCode = q.getQrUrl()+"/xc?oneCode="+q.getOneQrcode();
                            String path = qrCodeFileDir+stamp + "/" + s + "/";
                            File folder = new File(path);
                            //判断文件夹是否存在
                            if (!folder.exists() && !folder.isDirectory()) {
                                folder.mkdirs();//创建文件夹
                            }

                            int width = 300; // 二维码图片宽度
                            int height = 300; // 二维码图片高度
                            String format = "jpg";// 二维码的图片格式

                            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                            hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码

                            if (oneCode!=null && !"".equals(oneCode)){
                                //生成二码合一
                                BitMatrix one = new MultiFormatWriter().encode(oneCode,
                                        BarcodeFormat.QR_CODE, width, height, hints);
                                File oneFile = new File(path + "one-" + q.getCode() +".jpg");
                                File oneFileId = new File(path + "one-" + q.getCode() +"_id.jpg");
                                QrcodeDown.writeToFile(one, format, oneFile);
                                QrcodeDown.writeToFileWithId("ID:"+q.getId(),one, format, oneFileId);
                            }


                            filNum++;
                        }
                    }
                    if (filNum==0){
                        result.put("code" ,1);
                        result.put("msg" ,"请选择线充类型设备的二维码");
                        return result;
                    }
                    File folder = new File(qrZipCache);
                    //判断文件夹是否存在
                    if (!folder.exists() && !folder.isDirectory()) {
                        folder.mkdirs();//创建文件夹
                    }
                    String fileName = stamp + ".zip";
                    String dir = qrZipCache + stamp + ".zip";
                    //生成一个压缩文件
                    QrcodeDown.toZip(qrCodeFileDir+stamp,qrZipCache+stamp+".zip",true);
                    //删除生成文件的文件夹
                    QrcodeDown.deleteAll(new File(qrCodeFileDir+stamp));
                    result.put("code" ,3);
                    result.put("file" ,fileName);
                    result.put("msg" ,"下载成功");
                    return result;
                }else {
                    //随机生成一个时间戳
                    String stamp = DateUtil.Date2TimeStamp(new Date());
                    //生成二码合一
                    String oneCode = "";
                    //文件个数
                    int filNum = 0;
                    //生成本地文件
                    for (FindQrcodeStore q:list) {

                        boolean flag = false;
                        //判断是什么类型的设备
                        if (q.getEquip()==null){
                            flag = true;
                        }else {
                            Long eqTypeId = storeDao.findEqTypeId(q.getEquip());
                            if (eqTypeId!=null && eqTypeId!=5){
                                flag = true;
                            }
                        }

                        if (flag){
                            oneCode = q.getQrUrl()+"/one/ttOne?oneCode="+q.getOneQrcode();
                            String path = qrCodeFileDir+stamp + "/" + q.getCode() + "/";
                            File folder = new File(path);
                            //判断文件夹是否存在
                            if (!folder.exists() && !folder.isDirectory()) {
                                folder.mkdirs();//创建文件夹
                            }

                            int width = 300; // 二维码图片宽度
                            int height = 300; // 二维码图片高度
                            String format = "jpg";// 二维码的图片格式
                            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                            hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); // 内容所使用字符集编码

                            // 生成微信的二维码
                            if (q.getWechatQrcode()!=null){
                                BitMatrix wx = new MultiFormatWriter().encode(q.getWechatQrcode(),
                                        BarcodeFormat.QR_CODE, width, height, hints);
                                File wxFile = new File(path + "WX-" + q.getCode() +".jpg");
                                QrcodeDown.writeToFile(wx, format, wxFile);
                            }

                            if (oneCode!=null && !"".equals(oneCode)){
                                //生成二码合一
                                BitMatrix one = new MultiFormatWriter().encode(oneCode,
                                        BarcodeFormat.QR_CODE, width, height, hints);
                                File oneFile = new File(path + "one-" + q.getCode() +".jpg");
                                QrcodeDown.writeToFile(one, format, oneFile);
                                File oneFileId = new File(path + "one-" + q.getCode() +"_id.jpg");
                                QrcodeDown.writeToFileWithId("ID:"+q.getId(),one, format, oneFileId);
                            }

                            if ( q.getAliQrcode()!=null ){
                                // 生成支付宝的二维码
                                String  pa = path + "ali-" + q.getCode() +".jpg";
                                QrcodeDown.downloadPicture(q.getAliQrcode() ,pa);
                            }
                            filNum++;
                        }
                    }
                    if (filNum==0){
                        result.put("code" ,1);
                        result.put("msg" ,"请选择对应类型设备的二维码");
                        return result;
                    }
                    File folder = new File(qrZipCache);
                    //判断文件夹是否存在
                    if (!folder.exists() && !folder.isDirectory()) {
                        folder.mkdirs();//创建文件夹
                    }
                    String fileName = stamp + ".zip";
                    String dir = qrZipCache + stamp + ".zip";
                    //生成一个压缩文件
                    QrcodeDown.toZip(qrCodeFileDir+stamp,qrZipCache+stamp+".zip",true);
                    //删除生成文件的文件夹
                    QrcodeDown.deleteAll(new File(qrCodeFileDir+stamp));
                    result.put("code" ,3);
                    result.put("file" ,fileName);
                    result.put("msg" ,"下载成功");
                    return result;
                }

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        result.put("code" ,1);
        result.put("msg" ,"请选择你需要下载的二维码");
        return result;
    }

    /** 根据店铺名称，设备号，添加时间，是否绑定设备。查询二维码信息 */
    @Override
    public PageInfo<QRManger> findQr(String shopName, String eqCode, String time, Integer isBan,Integer pageIndex ,Integer pageCount,Integer infoId) {
        if (pageIndex==null || pageIndex<=0){
            pageIndex = 1;
        }
        if (pageCount==null || pageCount<=0){
            pageCount = 15;
        }
        PageHelper.startPage(pageIndex, pageCount);
        List<QRManger> list = new ArrayList<>();
        try {
            //如果时间不为空，处理时间
            if (time!=null && !"".equals(time.trim())){
                String[] split = time.split("\\s-\\s");
                list = storeDao.findQrByShopNameAndEqCode(shopName ,eqCode ,split[0] ,split[1] ,isBan ,infoId);
            }else {
                list = storeDao.findQrByShopNameAndEqCode(shopName ,eqCode ,null ,null ,isBan ,infoId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new PageInfo<QRManger>(list);
    }

    /** 删除选中的二维码 */
    @Override
    public Map<String, Object> deleSelect(String id) {
        Map<String ,Object> result = new HashMap<>();
        int successNum = 0;//删除成功的个数
        int failNum = 0;//失败个数
        if (id==null || "".equals(id)){
            result.put("code" ,3);
            result.put("successNum" ,successNum);
            result.put("failNum" ,failNum);
            return result;
        }
        try {
            JSONArray jsonArray = JSON.parseArray(id);
            List<Integer> integers = jsonArray.toJavaList(Integer.class);
            //判断参数，从而执行不同的操作
            if (id!=null){
                //有选中二维码，删除选中的二维码，前提是该二维码未绑定
                for (Integer i:integers) {
                    QrcodeStore stores = storeDao.findQrcodeByID(i);
                    if(stores!=null && stores.getIsbind()==2){//未绑定的设备才能删除
                        storeDao.deleOne(stores);//执行删除操作
                        successNum++;
                    }else {
                        failNum++;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("code" ,3);
        result.put("successNum" ,successNum);
        result.put("failNum" ,failNum);
        return result;
    }

    /** 删除全二维码 */
    @Override
    public Map<String, Object> deleAll() {
        Map<String ,Object> result = new HashMap<>();
        int successNum = 0;//删除成功的个数
        int failNum = 0;//失败个数
        //执行删除全部未绑定的二维码
        List<QrcodeStore> qrcode = storeDao.findNotBindQrcode();
        if (qrcode!=null && qrcode.size()>0){
            for (QrcodeStore q:qrcode) {
                storeDao.deleOne(q);//执行删除操作
                successNum++;
            }
        }
        result.put("code" ,3);
        result.put("successNum" ,successNum);
        result.put("failNum" ,failNum);
        return result;
    }

    /** 查询所有的二维码信息 */
    @Override
    public List<QrcodeInfo> findAllInfo() {
        return storeDao.findAllQrInfo();
    }

    /** 根据设备编号查询二维码资料 */
    @Override
    public Map<String, Object> findCodeByEqCode(String code) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        result.put("msg" ,"fail");
        Map eqCode = storeDao.findCodeByEqCode(code);//调用dao
        //微信二维码的访问接口
        String wx = null;
        String ali = null;
        if (eqCode!=null && eqCode.containsKey("WECHAT_TICKET") && eqCode.containsKey("ALI_QRCODE")){
            wx = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+eqCode.get("WECHAT_TICKET").toString();
            ali = eqCode.get("ALI_QRCODE").toString();
            result.put("oneCode" ,eqCode.get("ONE_QRCODE"));
            result.put("msg" ,"success");
            result.put("code" ,3);
        }
        result.put("wx" ,wx);
        result.put("ali" ,ali);
        return result;
    }

    /** 验证key是否存在:判断key是否存在，存在返回true，并且把值设置未1(被解绑二维码使用过) */
    @Override
    public boolean keyUp(String key){
        if (key==null){
            return false;
        }
        Integer o = (Integer)redisTemplate.opsForValue().get(key);
        if (o==null || (o!=1 && o!=0)){
            return false;
        }
        if (o==0){
            //获取过期时间
            Long expire = redisTemplate.getExpire(key);
            redisTemplate.opsForValue().set(key, 1, expire, TimeUnit.SECONDS);
        }
        return true;
    }

    /** 【后台系统】根据设备号查询可以解绑的设备 */
    @Override
    public Map<String, Object> queryCanUntie(String data ,String key) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //调用key验证
        boolean flag = keyUp(key);
        if (!flag){
            result.put("msg","密钥错误，请重新输入");
            return result;
        }

        //解析data数据，以英文的,号分割
        if (data==null){
            result.put("msg","请输入要解绑的设备号");
            return result;
        }

        String[] split = data.split(",");
        if (split==null || split.length<=0){
            result.put("msg","请输入正确的解绑设备号");
            return result;
        }

        List<String> y = new ArrayList<>();//可以解绑设备
        Integer yNum = 0;//可以解绑设备数
        Map<String ,String> n = new HashMap<>();//无法解绑的设备
        Integer nNum = 0;//无法解绑设备数

        for (int i = 0; i < split.length; i++) {
            Map map = storeDao.findCodeByEqCode(split[i]);
            if (map==null || map.get("CODE")==null){
                n.put(split[i] ,"设备号不存在");
                nNum++;
            }
            if (map!=null && (map.get("WECHAT_TICKET")!=null || map.get("ALI_QRCODE")!=null)){
                y.add(split[i]);
                yNum++;
            }
            if (map!=null && map.get("WECHAT_TICKET")==null && map.get("ALI_QRCODE")==null){
                n.put(split[i] ,"该设备未绑定二维码");
                nNum++;
            }
        }

        result.put("code" ,3);
        result.put("y" ,y);
        result.put("n" ,n);
        result.put("yNum" ,yNum);
        result.put("nNum" ,nNum);
        result.put("allNum" ,split.length);
        result.put("msg" ,"成功");
        return result;
    }

    /** 【后台系统】根据设备编号解绑设备 */
    @Override
    public Map<String, Object> untieEq(String data ,String key) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //调用key验证
        boolean flag = keyUp(key);
        if (!flag){
            result.put("msg","密钥错误，请重新输入");
            return result;
        }
        if (data==null || "".equals(data.trim())){
            result.put("msg" ,"请输入设备号");
            return result;
        }
        JSONArray jsonArray = JSON.parseArray(data);
        List<String> strings = jsonArray.toJavaList(String.class);
        if (strings==null || strings.size()<=0){
            result.put("msg" ,"请输入设备号");
            return result;
        }

        Integer success = 0;
        Integer fail = 0;
        for (int i = 0; i < strings.size(); i++) {
            if (!"".equals(strings.get(i))){
                //查询出二维码id
                Long codeId = storeDao.findQrCodeIdByCode(strings.get(i));
                if (codeId!=null){
                    //解绑二位码
                    storeDao.untieEq(codeId);
                    //解绑店铺
                    storeDao.deleShopEquip(codeId);
                    success++;
                }else {
                    fail++;
                }
            }
        }
        result.put("code" ,3);
        result.put("msg" ,"解绑成功："+success+"。失败："+fail);
        return result;
    }

    /** 【后台系统】针对mqtt的视频机更新设备服务器的二维码 */
    @Override
    public Map<String, Object> updateMqEqQrCode(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        try {
            //判断参数
            if (eqCode==null || "".equals(eqCode)){
                result.put("msg" ,"请选择对应的设备更新");
                return result;
            }
            //查询该设备是否在线
            String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
            if (lin==null || "".equals(lin.trim())){
                result.put("msg" ,"该设备不在线，无法操作");
                return result;
            }
            IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
            String dt = inInfo.getDD().getDT();//设备编号
            if (!"MqBattery-Video".equals(dt)){
                result.put("msg" ,"该设备不能执行更新二维码操作");
                return result;
            }
            //查询该设备的二维码信息
            Map codeByEqCode = storeDao.findCodeByEqCode(eqCode);
            if (codeByEqCode==null || codeByEqCode.get("WECHAT_TICKET")==null){
                result.put("msg" ,"该设备没有绑定二维码，无法操作");
                return result;
            }
            //查询二维码模板消息的基础参数
            String movieLocal = basicsettingDao.findMovieLocal();
            //微信二维码的ticket
            String ticket = codeByEqCode.get("WECHAT_TICKET").toString();
            //支付宝二维码图片链接
            String ali_qrcode = codeByEqCode.get("ALI_QRCODE").toString();
            //二码合一
            String oneCode = codeByEqCode.get("ONE_QRCODE")==null?"":codeByEqCode.get("ONE_QRCODE").toString();
            //处理二码合一的图片问题
            if (!"".equals(oneCode)){
                QrcodeDown.createPic(yuming+"/one/ttOne?oneCode="+oneCode ,staticPath,oneCode);
            }
            //判断该模板消息是否设置，没有设置则按默认的来，设置了则按照设置的来
            if (movieLocal==null){
                //发布消息到机器上
                pubMsgl.publish("{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeBarcode\",\"S1\":\"https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket+"\",\"S2\":\""+ali_qrcode+"\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}", UUID.randomUUID().toString(), "SERVER/EQUIPMENT/"+eqCode);
                //发布消息到机器上
            /*    pubMsgl.publish("{\"MI\":\""+eqCode+"\",\"AT\":\"changeText\",\"T1\":\"Wechat\",\"T2\":\"Scan to Download       App\"}", UUID.randomUUID().toString(), "SERVER/EQUIPMENT/"+eqCode);
                pubMsgl.publish("{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeBarcode\",\"S1\":\""+yuming+":8090/"+oneCode+".jpg\",\"S2\":\""+yuming+":8090/app-download.jpg\"}", UUID.randomUUID().toString(), "SERVER/EQUIPMENT/"+eqCode);
                String str = "{\"MI\":\""+inInfo.getMI()+"\",\"AT\":\"defaulData\",\"DF\":\""+yuming+":8075/mid.jpg\"}";
                pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+inInfo.getMI());*/
            }else {
                MovieLocal local = null;
                try {
                    //解析模板消息
                    local = JSON.parseObject(movieLocal ,MovieLocal.class);
                }catch (Exception e){
                    result.put("msg" ,"模板消息解析错误，请重新设置公共模板");
                    return result;
                }
                if (local!=null){
                    //处理上下的图片显示
                    MovieLocalDetail up = local.getUp();
                    MovieLocalDetail down = local.getDown();
                    String s1 = null;
                    if (up!=null){
                        s1 = dealQrcode(ticket, ali_qrcode,oneCode, up, up.getType(), up.getUrl());
                    }
                    String s2 = null;
                    if (down!=null){
                        s2 = dealQrcode(ticket, ali_qrcode,oneCode, down, down.getType(), down.getUrl());
                    }
                    if (s1!=null || s2!=null){
                        pubMsgl.publish("{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeBarcode\",\"S1\":\""+s1+"\",\"S2\":\""+s2+"\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}", UUID.randomUUID().toString(), "SERVER/EQUIPMENT/"+eqCode);
                    }
                    //处理中间的图片显示
                    MovieLocalDetail center = local.getCenter();
                    if (center!=null){
                        String s3 = dealQrcode(ticket, ali_qrcode,oneCode, center, center.getType(), center.getUrl());
                        if (s3!=null){
                            String str = "{\"MI\":\""+eqCode+"\",\"AT\":\"defaulData\",\"DF\":\""+s3+"\"}";
                            pubMsgl.publish(str,UUID.randomUUID().toString(),"SERVER/EQUIPMENT/"+eqCode);
                        }
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            result.put("msg" ,"系统错误："+e.getMessage());
            return result;
        }
        result.put("code" ,3);
        result.put("msg" ,"操作成功！");
        return result;
    }

    /**
     * 更换二维码的时候提炼的方法
     * @param ticket
     * @param ali_qrcode
     * @param ml
     * @param type
     * @param url
     * @return
     */
    private String dealQrcode(String ticket, String ali_qrcode, String oneCode, MovieLocalDetail ml, Integer type, String url) {
        String str = null;
        if (ml!=null){
            //处理不同类型
            if (type ==1){//用上传的图片
                str = yuming+":8090/"+url;
            }else if (type ==2){//用logo
                //查找平台默认logo
                String logo = videoDao.findBasicTaltLogo();
                str = yuming+":8090/"+logo;
            }else if (type ==3){//微信二维码
                str = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
            }else if (type ==4){//支付宝二维码
                str = ali_qrcode;
            }else if (type ==5){//二码合一
                str = yuming+":8090/"+oneCode+".jpg";
            }
        }
        return str;
    }

    /*@Override
    public Map<String, Object> updateMqEqQrCode(String eqCode) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        //判断参数
        if (eqCode==null || "".equals(eqCode)){
            result.put("msg" ,"请选择对应的设备更新");
            return result;
        }
        //查询该设备是否在线
        String lin = (String) redisTemplate.opsForValue().get(eqCode + "-Info");
        if (lin==null || "".equals(lin.trim())){
            result.put("msg" ,"该设备不在线，无法操作");
            return result;
        }
        IN_Info inInfo = JSON.parseObject(lin, IN_Info.class);
        String dt = inInfo.getDD().getDT();//设备编号
        if (!"MqBattery-Video".equals(dt)){
            result.put("msg" ,"该设备不能执行更新二维码操作");
            return result;
        }
        //查询该设备的二维码信息
        Map codeByEqCode = storeDao.findCodeByEqCode(eqCode);
        if (codeByEqCode==null || codeByEqCode.get("WECHAT_TICKET")==null){
            result.put("msg" ,"该设备没有绑定二维码，无法操作");
            return result;
        }
        //微信二维码的ticket
        String ticket = codeByEqCode.get("WECHAT_TICKET").toString();
        //支付宝二维码图片链接
        Object ali_qrcode = codeByEqCode.get("ALI_QRCODE");
        //发布消息到机器上
        try {
            pubMsgl.publish("{\"MI\":\""+eqCode+"\",\"AT\":\"ChangeBarcode\",\"S1\":\"https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket+"\",\"S2\":\""+ali_qrcode+"\",\"TS\":\""+DateUtil.Date2TimeStamp(new Date())+"\"}", UUID.randomUUID().toString(), "SERVER/EQUIPMENT/"+eqCode);
        }catch (Exception e){
            System.out.println(e.getMessage());
            result.put("msg" ,"系统错误："+e.getMessage());
            return result;
        }
        result.put("code" ,3);
        result.put("msg" ,"操作成功！");
        return result;
    }*/

    /** 不重复的几位数字方法 */
    public static String generateRandomStr(int len) {
        //字符源，可以根据需要删减
        String generateSource = "123456789abcdefghigklmnpqrstuvwxyz";
        String rtnStr = "";
        for (int i = 0; i < len; i++) {
            //循环随机获得当次字符，并移走选出的字符
            String nowStr = String.valueOf(generateSource.charAt((int) Math.floor(Math.random() * generateSource.length())));
            rtnStr += nowStr;
            generateSource = generateSource.replaceAll(nowStr, "");
        }
        return rtnStr;
    }

    /** 针对只生成二码合一的二维码 */
    @Override
    public Map<String, Object> createQr(Integer num, Long qrInfoId) {
        Map<String ,Object> result = new HashMap<>();
        result.put("code" ,1);
        try {
            if (num!=null && num>0){
                List<QrcodeStore> list = new ArrayList<>();//构建类集合
                for (int i = 0; i < num; i++) {
                    //构建新的实体
                    QrcodeStore store = new QrcodeStore();
                    store.setCode("2018"+RandomUtil.getRandomInt(20));
                    store.setAliQrcode("1111");
                    store.setWechatQrcode("1111");
                    store.setWechatTicket("1111");
                    store.setIsonecode(2);
                    store.setQrcode(qrInfoId);
                    store.setIsbind(2);
                    store.setRemark("系统生成");
                    store.setRegistime(new Date());
                    store.setOperator(1L);
                    store.setOneQrcode(RandomUtil.getRandomInt(25));
                    list.add(store);
                }
                if (list!=null && list.size()>0){
                    //执行插入逻辑
                    storeDao.insertBat(list);
                    result.put("code" ,3);
                    result.put("msg" ,"插入成功");
                    return result;
                }
            }else{
                result.put("msg" ,"参数错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("msg" ,"调用生成二维码的接口错误");
        }
        return result;
    }



    @Override
    // type 为空 线充，type为1 干衣架
    public Map genLineChargerCode (Long num,Integer type){
        Map<String ,Object> result = new HashMap<>();
        String initEqCode = "";
        if(type != null && type == 1){//干衣
            //获取上次生成的最大id
            EquipInfo equipInfoDb = equipInfoDao.findHyjCode();
            initEqCode = "888000000";
            if( equipInfoDb != null){
                initEqCode = equipInfoDb.getCode();
            }
        }else{//xc
            //获取上次生成的最大id
            EquipInfo equipInfoDb = equipInfoDao.findLineChargerCode();
            initEqCode = "999000000";
            if( equipInfoDb != null){
                initEqCode = equipInfoDb.getCode();
            }
        }


        //查询未绑定的二维码
        List<QrcodeStore> list = storeDao.findNotBindQrcodeLimit(num);
        Long  code = Long.valueOf(initEqCode);
        Long  StratCode =  code +1;
        Long  endCode =  code + num;
        for(int i= 0 ; i < num ;i++){
            //设备编号
            code = code + 1;
            String s = "" + code;
            //插入一条设备信息
            EquipInfo equipInfo = new EquipInfo();
            equipInfo.setCode(s);
            equipInfo.setIsstock(1);
            equipInfo.setType(5L);
            if(type != null && type == 1) {//干衣
                equipInfo.setType(8L);
            }
            equipInfo.setState(2);
            equipInfo.setServer(1L);
            equipInfoDao.insert(equipInfo);
            //更新二维码绑定该设备

            storeDao.updateQrOneCodeIsBanByID(equipInfo.getId() ,list.get(i).getId() , code.toString());
            //oneCode = yuming+"/xc?oneCode="+list.get(i).getOneQrcode();
        }


        result.put("code" ,3);
        //result.put("eqCodeRange" ,null);
        result.put("msg" ,"生成线充的区间范围："+StratCode +"-"+ ""+endCode);
        if(type != null && type == 1) {//干衣
            result.put("msg" ,"生成干衣架的区间范围："+StratCode +"-"+ ""+endCode);
        }
        return result;

    }

/*    @Override
    public Map findCodeByEqCode (String eqCode){
        Map<String ,Object> result = new HashMap<>();
        return storeDao.findCodeByEqCode(eqCode);
    }*/


}
