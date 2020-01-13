package com.equipment.dao;

import com.equipment.entity.Vedeo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: JavaTansanlin
 * @Description:
 * @Date: Created in 15:38 2019/5/5
 * @Modified By:
 */
@Component
public interface UtilDao {

    /** 获取md5字段为空的视频集 */
    @Select("SELECT * FROM vedeo WHERE FILE_MD5 IS NULL")
    List<Vedeo> findAllVideoByMd5IsNot();

    /** 更新指定视频的md5值 */
    @Update("UPDATE vedeo SET FILE_MD5=#{fileMd5} WHERE ID=#{id}")
    int updateFileMd5ById(@Param("fileMd5") String fileMd5 ,@Param("id") Long id);

}
