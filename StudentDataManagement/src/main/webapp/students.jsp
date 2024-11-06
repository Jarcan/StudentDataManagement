<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // 获取应用路径
    String path = request.getContextPath();
    // 构建基础路径
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">
    <title>学生数据管理</title>
    <link rel="stylesheet" href="static/css/custom.css">
    <script src="static/js/jquery.js"></script>
    <script src="static/js/ajaxJs.js"></script>
    <style>
        /* 表格样式 */
        table {
            margin: 0 auto;
            border-collapse: collapse;
        }

        table, tr th, tr td {
            border: 1px solid #dee2e6;
            padding: .75rem;
            text-align: center;
        }

        /* 标签内错误提示样式 */
        label span {
            color: rgba(225, 36, 30, 0.85);
            font-size: 0.6em;
        }

        /* 表格顶部栏样式 */
        .bar {
            border-color: transparent;
            border-bottom: #dee2e6;
        }

        /* 表格底部分页信息样式 */
        .bottom-bar {
            text-align: right;
            border-color: transparent;
            border-top: #dee2e6;
        }

        .bottom-bar button {
            min-width: 25px;
            height: 25px;
            line-height: 25px;
            margin: 0 5px;
            text-align: center;
            vertical-align: center;
            outline: none;
            border-style: none;
            border-radius: 3px;
        }

        .bottom-bar button:enabled:hover {
            cursor: pointer;
            color: #409eff;
        }

        #page button.active {
            background-color: #409eff;
            color: #fff;
            cursor: default;
        }

        .modal-body label {
            display: block;
            margin: 5px auto;
        }
    </style>
</head>
<body>
<h1 style="text-align: center">学生数据管理</h1>
<table>
    <thead>
    <tr>
        <td class="bar" colspan="5"></td>
        <td class="bar">
            <!-- 添加按钮 -->
            <button class="btn btn-info" style="float: right" onclick="openModalStudent(-1)">添加</button>
        </td>
    </tr>
    <tr>
        <th>学号</th>
        <th>姓名</th>
        <th>出生日期</th>
        <th>备注</th>
        <th>平均分</th>
        <th>操作</th>
    </tr>
    </thead>
    <!-- 学生表格数据展示 -->
    <tbody id="t-body"></tbody>
    <!-- 分页信息栏 -->
    <tr>
        <td class="bottom-bar" colspan="6">
            <button id="prev">&lt;</button>
            <span id="page"></span>
            <button id="next">&gt;</button>
            <span id="total"></span>
        </td>
    </tr>
</table>
<!-- 添加/更新学生信息的弹框 -->
<div class="modal hide" id="modal-student">
    <div class="modal-body">
        <label>
            学&emsp;&emsp;号：<input type="text" name="id" id="id"/>
        </label>
        <label>
            姓&emsp;&emsp;名：<input type="text" name="name" id="name"/>
        </label>
        <label>
            出生日期：<input type="date" name="birthday" id="birthday"/>
        </label>
        <label>
            备&emsp;&emsp;注：<textarea name="description" id="description"></textarea>
        </label>
        <label>
            平&ensp;均&ensp;分：<input type="number" name="avgScore" id="avgScore"/>
        </label>
    </div>
    <div class="modal-bottom">
        <button class="btn btn-outline-info" id="close-modal">取消</button>
        <button class="btn btn-info" id="addOrUpdUser">提交</button>
    </div>
</div>
<div class="shade hide"></div>
<script>
    $(function () {
        // 初始化加载第一页用户信息
        getPageStudent(1);
        // 监听学号输入框失去焦点事件，检查学号是否存在
        $("#id").blur(function () {
            let id = $("#id").val();
            $.get('/isExist?id=' + id, function (res) {
                if (res === "400") {
                    toast("学生id已存在");
                    $("#id").val("");
                }
            });
        });
        // 模态框水平居中显示
        modalCenter();
        window.onresize = modalCenter;
        // 关闭模态框按钮
        $("#close-modal").click(function () {
            closeModel();
        });
    });

    // 关闭模态框
    function closeModel() {
        $(".shade").addClass("hide");
        $("#modal-student").addClass("hide")
    }

    // 模态框水平居中
    function modalCenter() {
        let clientWidth = document.documentElement.clientWidth;
        let $modalStudent = $("#modal-student");
        let modeStudentWidth = (clientWidth - $modalStudent.width()) / 2;
        $modalStudent.css("right", modeStudentWidth + "px");
    }

    // 提示消息
    function toast(msg, second) {
        second = isNaN(second) ? 2.5 : second;
        let m = document.createElement('div');
        m.innerHTML = msg;
        m.style.cssText = "max-width:60%; min-width:150px; padding:0 14px; height:40px;" +
            " color: rgb(255, 255, 255); line-height:40px; text-align:center;" +
            "border-radius: 4px;position:fixed; top: 10%; left: 50%; " +
            "transform: translate(-50%, -50%);z-index: 999999;background: rgba(0, 0, 0,.7);font-size: 16px;";
        document.body.appendChild(m);
        setTimeout(function () {
            let d = 0.5;
            m.style.transition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
            m.style.opacity = '0';
            setTimeout(function () {
                document.body.removeChild(m)
            }, d * 1000);
        }, second * 1000);
    }
</script>
</body>
</html>
