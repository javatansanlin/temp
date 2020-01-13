package com.equipment.dao;

import com.equipment.entity.EquipInfo;
import com.equipment.entity.QrcodeInfo;
import com.equipment.entity.QrcodeStore;
import com.equipment.model.equipmanager.QRManger;
import com.equipment.model.querymodel.FindQrcodeStore;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 二维码仓库dao
 * @Date: Created in 15:22 2018/8/26
 * @Modified By:
 */
@Component
public interface QrcodeStoreDao {

    /** 根据设备id查询该二维码 */
    @Select("SELECT * FROM qrcode_store WHERE EQUIP=#{equipId}")
    QrcodeStore findCountQrcodeByEquipId(Long equipId);

    /** 根据二维码仓库的id更新绑定设备的id */
    @Update("UPDATE qrcode_store SET EQUIP=#{equip},ISBIND=1 WHERE ID=#{id}")
    int updateQrcodeByEquipId(QrcodeStore erQrcodeStore);

    /** 根据二维码链接查询记录二维码记录 */
    @Select("SELECT * FROM qrcode_store WHERE WECHAT_QRCODE = #{codeUrl} OR ALI_QRCODE=#{codeUrl} OR ONE_QRCODE=#{codeUrl};")
    List<QrcodeStore> fingCodeByUrl(String codeUrl);

    /** 根据设备id查询该设备的二维码 **/
    @Select("SELECT * FROM qrcode_store WHERE EQUIP = #{equipId};")
    QrcodeStore findQrcodeByEquipId(Long equipId);

    /** 根据设备编号查询该设备的二维码 **/
    @Select("SELECT * FROM qrcode_store AS qs LEFT JOIN equipinfo AS ei ON qs.EQUIP=ei.ID WHERE ei.CODE=#{eqcode}")
    QrcodeStore findQrcodeByEquipCode(String eqcode);

    /** 查询所有未被绑定的二维码 */
    @Select("SELECT * FROM qrcode_store WHERE ISBIND = 2 AND EQUIP IS NULL")
    List<QrcodeStore> findNotBindQrcode();

    /** 查询所有未被绑定的二维码 */
    @Select("SELECT * FROM qrcode_store WHERE ISBIND = 2 AND EQUIP IS NULL  ORDER BY id DESC limit #{num}  ")
    List<QrcodeStore> findNotBindQrcodeLimit(Long num);

    /** 根据二维码id查询二维码 */
    @Select("SELECT * FROM qrcode_store WHERE ID = #{id}")
    QrcodeStore findQrcodeByID(Integer id);

    /** 根据id删除二维码 */
    @Delete("DELETE FROM qrcode_store WHERE ID = #{id}")
    int deleOne(QrcodeStore store);

    /** 根据店铺名称，设备号，添加时间，是否绑定设备。查询二维码信息 */
    @Select({
            "<script>" ,
            "SELECT ",
            "qs.*, eq.CODE AS eqcode,rs.S_NAME AS shopname,qi.QR_URL as qrUrl,qi.QR_NAME AS qrName ",
            "FROM ",
            "qrcode_store AS qs ",
            "LEFT JOIN equipinfo AS eq ON qs.EQUIP = eq.ID ",
            "LEFT JOIN shop_equip AS se ON qs.ID = se.QRCODE_STORE ",
            "LEFT JOIN rank_shop AS rs ON se. CODE = rs.CODE ",
            "LEFT JOIN qrcode_info AS qi ON qs.QRCODE = qi.ID ",
            "WHERE 1=1",
            "<if test='code != null and code != &quot;&quot;'>AND eq.CODE = #{code}</if>",
            "<if test='shopName != null and shopName != &quot;&quot;'>AND rs.S_NAME like concat('%',#{shopName},'%')</if>",
            "<if test='starTime != null and starTime !=&quot;&quot;'>AND qs.REGISTIME &gt;= #{starTime}</if>" ,
            "<if test='endTime != null and endTime != &quot;&quot;'>AND qs.REGISTIME &lt;= #{endTime}</if>" ,
            "<if test='isBan != null'>AND qs.ISBIND = #{isBan}</if>",
            "<if test='infoId != null'>AND qs.QRCODE = #{infoId}</if>",
            "ORDER BY qs.ID DESC",
            "</script>"
    })
    List<QRManger> findQrByShopNameAndEqCode(@Param("shopName") String shopName , @Param("code") String code , @Param("starTime") String starTime ,
                                             @Param("endTime") String endTime , @Param("isBan") Integer isBan ,@Param("infoId") Integer infoId);
    /** 查询所有的二维码信息 */
    @Select("select * from qrcode_info")
    List<QrcodeInfo> findAllQrInfo();

    /** 根据二维码信息查询改二维码信息下的所有未绑定的二维码 */
    @Select("SELECT qs.* FROM qrcode_store AS qs LEFT JOIN qrcode_info AS qi ON qs.QRCODE=qi.ID WHERE qi.ID = #{id} AND qs.ISBIND=2")
    List<QrcodeStore> findNotBindQrcodeByInfoId(@Param("id") Integer id);

    /** 根据二维码id查询二维码 */
    @Select({
            "<script>" ,
                "SELECT qs.*,qi.pay_service_name,qi.QR_URL FROM qrcode_store AS qs LEFT JOIN qrcode_info AS qi ON qs.QRCODE=qi.ID where qs.ID in",
                "<foreach item='item' collection='list' open='(' close=')' separator=','>",
                    "#{item}",
                "</foreach>",
            "</script>"
    })
    List<FindQrcodeStore> findNotBindQrcodeByListId(List<Integer> id);

    /** 根据设备编号查询二维码 */
    @Select("SELECT eq.CODE,qs.WECHAT_TICKET,qs.ALI_QRCODE,qs.ONE_QRCODE FROM equipinfo AS eq LEFT JOIN qrcode_store AS qs ON qs.EQUIP=eq.ID WHERE eq.CODE = #{code}")
    Map findCodeByEqCode(String code);

    /** 根据设备号查询二维码id */
    @Select("select qs.ID FROM qrcode_store AS qs  LEFT JOIN equipinfo AS eq ON qs.EQUIP = eq.ID WHERE eq.CODE = #{code}")
    Long findQrCodeIdByCode(String code);

    /** 根据二维码id解绑设备 */
    @Update("update qrcode_store set EQUIP=null,ISBIND=2 where ID=#{id}")
    int untieEq(Long id);

    /** 根据二维码id删除店铺设备表信息 */
    @Delete("delete from SHOP_EQUIP where QRCODE_STORE=#{id}")
    int deleShopEquip(Long id);

    /** 根据支付宝的gxCode查询二维码信息 */
    @Select("SELECT * FROM qrcode_store WHERE GX_QRCODE = #{gxCode}")
    QrcodeStore findQrcodeByAliGxCode(String gxCode);

    /** 根据微信的ticket查询二维码信息 */
    @Select("SELECT * FROM qrcode_store WHERE WECHAT_TICKET=#{ticket}")
    QrcodeStore findQrcodeByWeChatTicket(String ticket);

    /** 根据大型设备的编号的查询下面的设备编号 */
    @Select({
            "<script>",
            "SELECT eq.CODE",
            "FROM",
            "qrcode_store AS qs",
            "LEFT JOIN equipinfo AS eq ON eq.ID = qs.EQUIP",
            "WHERE ONE_QRCODE = #{qrCode}",
            "AND eq.STATE=1 AND eq.ISSTOCK=1",
            "</script>"
    })
    List<String> findGCodeByCode(String qrCode);

    @Select("SELECT eq.* FROM qrcode_store AS qs LEFT JOIN equipinfo AS eq ON qs.EQUIP = eq.ID WHERE qs.ID = #{qsId}")
    EquipInfo findEqByQsCode(Long qsId);

    /** 根据二维码的二码合一的code查询二维码信息 */
    @Select("SELECT * FROM qrcode_store WHERE ONE_QRCODE=#{code}")
    QrcodeStore findQrcodeByOneCode(String code);

    /** 根据支付宝的ALI_QRCODE模糊查询二维码信息 */
    @Select("SELECT * FROM qrcode_store WHERE ALI_QRCODE LIKE concat('%',#{code},'%')")
    QrcodeStore findQrcodeByAliCode(String code);

    /** 根据微信的链接查询二维码信息 */
    @Select("SELECT * FROM qrcode_store WHERE WECHAT_QRCODE = #{code}")
    QrcodeStore findQrcodeByWeChatCode(String code);

    /** 根据二维码的id更新二维码的oneCode */
    @Update("UPDATE qrcode_store SET ONE_QRCODE = #{oneCode} WHERE ID=#{id}")
    int updateOneCodeByID(@Param("oneCode") String oneCode ,@Param("id") Long id);

    /** 根据二维码的id更新二维码未已绑定，并且绑定id */
    @Update("UPDATE qrcode_store SET ISBIND = 1,EQUIP=#{eqId} WHERE ID=#{id}")
    int updateQrCodeIsBanByID(@Param("eqId") Long eqId ,@Param("id") Long id);

    /** 根据二维码的id更新二维码未已绑定，并且绑定id */
    @Update("UPDATE qrcode_store SET ISBIND = 1,EQUIP=#{eqId} , ONE_QRCODE = #{oneCode} WHERE ID=#{id}")
    int updateQrOneCodeIsBanByID(@Param("eqId") Long eqId ,@Param("id") Long id,@Param("oneCode") String oneCode );

    /** 根据设备id查询改二维码的设备类型 */
    @Select("SELECT TYPE FROM equipinfo WHERE ID=#{id}")
    Long findEqTypeId(Long id);

    /** 根据设备id查询改二维码的设备类型 */
    @Select("SELECT CODE FROM equipinfo WHERE ID=#{id}")
    String findEqById(Long id);

    /** 创建二维码 */
    /** 创建 */
    @Insert({
            "<script>",
            "INSERT qrcode_store (CODE,ISONECODE,QRCODE,WECHAT_QRCODE,WECHAT_TICKET,ALI_QRCODE,ONE_QRCODE,ISBIND,REMARK,REGISTIME,OPERATOR)",
            "VALUES",
            "<foreach item='qr' index='index' collection='listQr' separator=','>",
            "(#{qr.code},#{qr.isonecode},#{qr.qrcode},#{qr.wechatQrcode},#{qr.wechatTicket},#{qr.aliQrcode},#{qr.oneQrcode},#{qr.isbind},#{qr.remark},#{qr.registime},#{qr.operator})",
            "</foreach>",
            "</script>"
    })
    int insertBat(@Param("listQr") List<QrcodeStore> listQr);

}
