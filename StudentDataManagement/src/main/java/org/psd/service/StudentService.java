package org.psd.service;

import org.psd.common.PageInfo;
import org.psd.entity.Student;

/**
 * 接口提供学生数据的管理功能
 *
 * @author pengshidun
 */
public interface StudentService {

    /**
     * 根据学生ID判断该学生是否已存在
     *
     * @param studentId 学生ID
     * @return 如果学生存在返回true，否则返回false
     */
    Boolean existStudent(String studentId);

    /**
     * 根据提供的学生信息录入一个学生数据
     *
     * @param student 学生信息
     * @return 添加是否成功
     */
    Boolean saveStudent(Student student);

    /**
     * 根据学生ID删除学生数据
     *
     * @param studentId 学生ID
     * @return 删除是否成功
     */
    Boolean removeStudent(String studentId);

    /**
     * 更新学生数据
     *
     * @param student 学生信息
     * @return 更新是否成功
     */
    Boolean updateStudent(Student student);

    /**
     * 以学生平均分倒序排序，分页获取学生数据
     * 其中页码(pageNum)和每页记录数(pageSize)必须大于0，否则设置为默认值1和10
     *
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return 分页详细信息
     */
    PageInfo<Student> listStudentsPage(Integer pageNum, Integer pageSize);
}
