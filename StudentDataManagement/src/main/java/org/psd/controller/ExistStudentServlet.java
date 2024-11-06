package org.psd.controller;

import org.psd.service.StudentService;
import org.psd.service.impl.StudentServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 该servlet根据请求中的id判断学生id是否已存在
 *
 * @author pengshidun
 */
@WebServlet("/isExist")
public class ExistStudentServlet extends HttpServlet {

    /**
     * 实例化StudentService接口的实现类
     */
    private StudentService studentService = new StudentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求中的学生id参数
        String studentId = request.getParameter("id");
        // 调用studentService的existStudent方法判断学生id是否存在
        Boolean isSuccess = studentService.existStudent(studentId);
        // 获取响应的PrintWriter对象，用于输出响应内容
        PrintWriter writer = response.getWriter();
        // 如果学生id存在，返回400状态码
        if (isSuccess) {
            writer.print("400");
        }
        // 关闭PrintWriter对象
        writer.close();
    }
}
