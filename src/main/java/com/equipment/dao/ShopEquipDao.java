package com.equipment.dao;

import com.equipment.entity.ShopEquip;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

/**
 * @Author: JavaTansanlin
 * @Description: 店铺与设备之间的关系类，（以二维码作为媒介相关联） dao
 * @Date: Created in 16:40 2018/8/22
 * @Modified By:
 */
@Component
public interface ShopEquipDao {

    /** 插入一条数据 */
    @Insert("insert into SHOP_EQUIP(CODE,REGISTTIME,QRCODE_STORE,OPERATOR) values (#{code},#{registtime},#{qrcodeStore},#{operator})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertOne(ShopEquip shopEquip);

    /** 根据设备的id查询设备是否又店铺的绑定记录 */
    @Select("SELECT COUNT(ID) FROM shop_equip WHERE (SELECT ID FROM qrcode_store WHERE EQUIP = #{equipId}) = QRCODE_STORE")
    int findShopEquipByEquipId(@Param("equipId") Long equipId);

    /** 绑定设备时，判断该店铺是否存在 */
    @Select("SELECT COUNT(ID) FROM RANK_SHOP WHERE CODE=#{shopCode}")
    int findShopExistByShopCode(@Param("shopCode") String shopCode);

    /** 根据二维码仓库id删除相关的店铺与设备记录 */
    @Delete("DELETE FROM shop_equip WHERE QRCODE_STORE=#{qrcodeId}")
    int deleteByEquip(@Param("qrcodeId") Long qrcodeId);

    /** 根据设备的id查询设备是否又店铺的绑定记录 */
    @Select("SELECT shop_equip.code FROM shop_equip WHERE (SELECT ID FROM qrcode_store WHERE EQUIP = #{equipId}) = QRCODE_STORE")
    String findShopByEquipId(@Param("equipId") Long equipId);


}
