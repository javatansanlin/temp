package com.equipment.dao;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: JavaTansanlin
 * @Description: 公共类的dao
 * @Date: Created in 11:55 2019/4/2
 * @Modified By:
 */
@Component
public interface BasicsettingDao {

    /** 查询基础参数-视频机的公共模板 */
    @Select("SELECT movie_local FROM basic_setting WHERE ID=1")
    String findMovieLocal();

    /** 更新基础参数-视频机的公共模板 */
    @Update("UPDATE basic_setting SET movie_local=#{ml} WHERE ID=1")
    int updateSettingToMovieLocal(String ml);

    /** 查询基础设置--押金 **/
    @Select("SELECT DEFAULT_DEPOSIT FROM basic_setting")
    Double findDePosit();

    @Select("SELECT DEFAULT_EQUIPVIDEO AS vedio, DEFAULT_EQUIPVIDEO_MD5 AS vedioMd5 from basic_setting")
    Map findDeVedio();

    @Update("UPDATE basic_setting SET DEFAULT_EQUIPVIDEO_MD5=#{md5} WHERE ID=1")
    int updateDeVedioMd5(String md5);

}
