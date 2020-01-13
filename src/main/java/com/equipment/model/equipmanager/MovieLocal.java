package com.equipment.model.equipmanager;

import lombok.Data;

/**
 * @Author: JavaTansanlin
 * @Description: 视频机的公共模板消息
 * @Date: Created in 11:45 2019/4/2
 * @Modified By:
 */
@Data
public class MovieLocal {
    /** 上 */
    private MovieLocalDetail up;
    /** 中 */
    private MovieLocalDetail center;
    /** 下 */
    private MovieLocalDetail down;
}
