package org.psd.controller;

import org.psd.util.JacksonUtils;
import org.psd.util.MapBeanUtils;
import org.psd.common.PageInfo;
import org.psd.entity.Student;
import org.psd.service.StudentService;
import org.psd.service.impl.StudentServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 该servlet主要用于处理与学生数据管理相关的请求
 *
 * @author pengshidun
 */
@WebServlet("/students")
public class StudentServlet extends HttpServlet {

    /**
     * 实例化StudentService接口的实现类
     */
    private StudentService studentService = new StudentServiceImpl();

    /**
     * 分页获取学生数据信息，接受页码(pageNum)和每页大小(pageSize)两个参数
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws IOException 如果发生输入输出异常
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求中的页码参数
        String pageNumberStr = request.getParameter("pageNum");
        // 获取请求中的每页大小参数
        String pageSizeStr = request.getParameter("pageSize");
        // 将页码参数转换为整数
        int pageNum = Integer.parseInt(pageNumberStr);
        // 默认每页大小为10
        int pageSize = 10;
        if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
            pageSize = Integer.parseInt(pageSizeStr);
        }
        // 调用studentService的listStudentsPage方法获取分页学生数据
        PageInfo<Student> pageInfoVO = studentService.listStudentsPage(pageNum, pageSize);
        // 将分页数据转换为JSON字符串并输出到响应中
        response.getWriter().print(JacksonUtils.objectToJsonStr(pageInfoVO));
    }

    /**
     * 添加一个学生
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws IOException 如果发生输入输出异常
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求参数映射
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 将参数映射转换为Student对象
        Student student = MapBeanUtils.mapToBean(parameterMap, Student.class);
        // 调用studentService的saveStudent方法保存学生数据
        boolean isSuccess = studentService.saveStudent(student);
        // 获取响应的PrintWriter对象，用于输出响应内容
        PrintWriter writer = response.getWriter();
        // 根据保存结果输出相应的消息
        if (isSuccess) {
            writer.print("添加成功");
        } else {
            writer.print("添加失败");
        }
        // 关闭PrintWriter对象
        writer.close();
    }

    /**
     * 删除学生
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws IOException 如果发生输入输出异常
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取响应的PrintWriter对象，用于输出响应内容
        PrintWriter writer = response.getWriter();
        // 获取请求中的学生ID参数
        String studentId = request.getParameter("id");
        // 调用studentService的removeStudent方法删除学生数据
        Boolean isSuccess = studentService.removeStudent(studentId);
        // 根据删除结果输出相应的消息
        if (isSuccess) {
            writer.print("删除成功");
        } else {
            writer.print("删除失败");
        }
        // 关闭PrintWriter对象
        writer.close();
    }

    /**
     * 更新学生信息
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @throws ServletException 如果发生Servlet异常
     * @throws IOException      如果发生输入输出异常
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求参数映射
        Map<String, String[]> parameterMap = request.getParameterMap();
        // 将参数映射转换为Student对象
        Student student = MapBeanUtils.mapToBean(parameterMap, Student.class);
        // 获取响应的PrintWriter对象，用于输出响应内容
        PrintWriter writer = response.getWriter();
        // 调用studentService的updateStudent方法更新学生数据
        Boolean isSuccess = studentService.updateStudent(student);
        // 根据更新结果输出相应的消息
        if (isSuccess) {
            writer.print("更新成功");
        } else {
            writer.print("更新失败");
        }
        // 关闭PrintWriter对象
        writer.close();
    }
}
