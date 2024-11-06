package org.psd.entity;

import lombok.Data;

/**
 * 学生实体类
 *
 * @author pengshidun
 */
@Data
public class Student {
    /**
     * 学生学号，长度40
     */
    private String id;
    /**
     * 学生姓名，长度40
     */
    private String name;
    /**
     * 出生日期
     */
    private String birthday;
    /**
     * 备注，长度255
     */
    private String description;
    /**
     * 平均分
     */
    private Integer avgScore;
}
