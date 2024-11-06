package org.psd.service.impl;

import org.psd.util.JacksonUtils;
import org.psd.util.JedisUtils;
import org.psd.common.PageInfo;
import org.psd.entity.Student;
import org.psd.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 学生业务层实现类
 *
 * @author pengshidun
 */
@Slf4j
public class StudentServiceImpl implements StudentService {

    /**
     * 存储学生按平均分排序的sorted set类型的key
     */
    private final String KEY_STUDENT_RANK = "student:rank";
    /**
     * 学生最低分数
     */
    private final Integer MIN_SCORE = 0;
    /**
     * 学生最高分数
     */
    private final Integer MAX_SCORE = 150;

    /**
     * 根据学生ID判断该学生是否已存在
     *
     * @param studentId 学生ID
     * @return 如果学生存在返回true，否则返回false
     */
    @Override
    public Boolean existStudent(String studentId) {
        // 获取Jedis实例
        Jedis jedis = JedisUtils.getJedis();
        // 判断Redis中是否存在指定的studentId键
        Boolean exists = jedis.exists(studentId);
        // 关闭Jedis连接
        JedisUtils.close(jedis);
        // 返回判断结果
        return exists;
    }

    /**
     * 根据提供的学生信息录入一个学生数据
     *
     * @param student 学生信息
     * @return 添加是否成功
     */
    @Override
    public Boolean saveStudent(Student student) {
        // 获取Jedis实例并开启事务
        Transaction multi = JedisUtils.getJedis().multi();
        // 检查学生ID是否已存在
        Response<Boolean> exists = multi.exists(student.getId());
        // 提交事务
        multi.exec();
        // 检查学生信息字段是否有效
        if (checkField(student)) {
            // 保存或更新学生信息
            return saveOrUpdate(student);
        }
        // 如果字段无效，返回false表示添加失败
        return false;
    }

    /**
     * 根据学生ID删除学生数据
     *
     * @param studentId 学生ID
     * @return 删除是否成功
     */
    @Override
    public Boolean removeStudent(String studentId) {
        // 获取Jedis实例
        Jedis jedis = JedisUtils.getJedis();
        // 开启事务
        Transaction multi = jedis.multi();
        try {
            // 删除指定学生ID的数据
            Response<Long> row = multi.del(studentId);
            // 从学生排名集合中移除该学生
            multi.zrem(KEY_STUDENT_RANK, studentId);
            // 提交事务
            multi.exec();
            // 返回删除成功
            return true;
        } catch (JedisException e) {
            // 记录警告日志
            log.warn(e.getMessage(), e);
            // 回滚事务
            multi.discard();
            // 返回删除失败
            return false;
        } finally {
            // 关闭Jedis连接
            JedisUtils.close(jedis);
        }
    }

    /**
     * 更新学生数据
     *
     * @param student 学生信息
     * @return 更新是否成功
     */
    @Override
    public Boolean updateStudent(Student student) {
        // 调用saveOrUpdate方法进行更新操作
        return saveOrUpdate(student);
    }

    /**
     * 以学生平均分倒序排序，分页获取学生数据
     * 其中页码(pageNum)和每页记录数(pageSize)必须大于0，否则设置为默认值1和10
     *
     * @param pageNum  页码
     * @param pageSize 每页记录数
     * @return 分页详细信息
     */
    @Override
    public PageInfo<Student> listStudentsPage(Integer pageNum, Integer pageSize) {
        // 获取Jedis实例
        Jedis jedis = JedisUtils.getJedis();
        // 获取学生总数
        Long count = jedis.zcount(KEY_STUDENT_RANK, MIN_SCORE, MAX_SCORE);
        // 初始化分页信息
        PageInfo<Student> page = new PageInfo<>(pageNum, pageSize, count);
        // 按平均分倒序排序后，获取该页的学生编号
        Set<String> keys = jedis.zrevrange(KEY_STUDENT_RANK, page.getStartIndex(), page.getEndIndex());
        // 根据编号获取学生详细信息
        List<Student> studentList = new ArrayList<>();
        keys.forEach(key -> {
            // 获取学生的所有字段
            Map<String, String> stringStringMap = jedis.hgetAll(key);
            // 将Map转换为Student对象
            Student student = JacksonUtils.mapToBean(stringStringMap, Student.class);
            // 添加到学生列表中
            studentList.add(student);
        });
        // 设置分页记录
        page.setRecords(studentList);
        // 关闭Jedis连接
        JedisUtils.close(jedis);
        // 返回分页详细信息
        return page;
    }

    /**
     * 添加或更新学生数据
     *
     * @param student 学生信息
     * @return 操作是否成功
     */
    private Boolean saveOrUpdate(Student student) {
        // 将学生对象转换为Map
        Map<String, String> map = JacksonUtils.beanToMap(student, String.class, String.class);
        // 获取Jedis实例
        Jedis jedis = JedisUtils.getJedis();
        // 开启事务
        Transaction multi = jedis.multi();
        try {
            // 将学生数据存入哈希中
            multi.hset(student.getId(), map);
            // 使用SortedSet按学生平均分排序存储学生的ID
            multi.zadd(KEY_STUDENT_RANK, student.getAvgScore(), student.getId());
            // 提交事务
            multi.exec();
            // 返回操作成功
            return true;
        } catch (JedisException e) {
            // 记录警告日志
            log.warn(e.getMessage(), e);
            // 回滚事务
            multi.discard();
            // 返回操作失败
            return false;
        } finally {
            // 关闭Jedis连接
            JedisUtils.close(jedis);
        }
    }

    /**
     * 检查学生信息各字段是否合法，如果字段为null则设置默认值。
     * 平均分必须在指定范围内，出生日期必须符合"yyyy-MM-dd"格式。
     *
     * @param student 学生信息对象
     * @return 如果所有字段都合法返回true，否则返回false
     */
    private boolean checkField(Student student) {
        // 检查姓名是否为null，如果是则设置为空字符串
        if (student.getName() == null) {
            student.setName("");
        }
        // 检查描述是否为null，如果是则设置为空字符串
        if (student.getDescription() == null) {
            student.setDescription("");
        }
        // 检查平均分是否为null，如果是则设置为0
        if (student.getAvgScore() == null) {
            student.setAvgScore(0);
        }
        // 检查出生日期是否为null，如果是则设置为空字符串
        if (student.getBirthday() == null) {
            student.setBirthday("");
        } else {
            // 检查平均分是否在有效范围内
            if (student.getAvgScore() < MIN_SCORE || student.getAvgScore() > MAX_SCORE) {
                return false;
            }
            // 检查出生日期是否符合"yyyy-MM-dd"格式
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            try {
                dtf.parse(student.getBirthday());
                return true;
            } catch (DateTimeParseException e) {
                // 记录警告日志并返回false
                log.warn(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }
}
