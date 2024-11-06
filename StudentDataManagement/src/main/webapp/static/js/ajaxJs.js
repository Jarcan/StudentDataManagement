// 获取学生分页信息，更新表格体(t-body)和分页信息条(bottom-bar)
function getPageStudent(pageNumber) {
    let url = "/students?pageNum=" + pageNumber;
    $.getJSON(url, function (res) {
        if (res === null) {
            return;
        }
        // 更新学生数据表格
        let students = res.records;
        let result = "";
        for (let i = 0; i < students.length; i++) {
            result += "<tr>";
            result += "<td>" + students[i].id + "</td>";
            result += "<td>" + students[i].name + "</td>";
            result += "<td>" + students[i].birthday + "</td>";
            result += "<td>" + students[i].description + "</td>";
            result += "<td>" + students[i].avgScore + "</td>";
            result += "<td>" +
                "<a class='link link-info' onclick='openModalStudent(" + i + ")'>修改</a>&nbsp;" +
                "<a class='link link-danger' onclick='delStudent(" + i + ", " + students[i].id + ")'>删除</a>&nbsp;";
            result += "</tr>";
        }
        $("#t-body").html(result);
        // 设置上一页按钮
        let $prev = $("#prev");
        if (res.pageNum === 1) {
            $prev.attr("disabled", true);
        } else {
            $prev.attr("disabled", false);
            $prev.click(function () {
                getPageStudent(res.pageNum - 1);
            });
        }
        // 设置跳转页按钮
        let html = "";
        for (let i = 1; i <= res.totalPage; i++) {
            if (i === res.pageNum) {
                html += "<button class='active'>" + i + "</button>";
            } else {
                html += "<button onclick='getPageStudent(" + i + ")'>" + i + "</button>";
            }
        }
        $("#page").html(html);
        // 设置下一页按钮
        let $next = $("#next");
        if (res.pageNum === res.totalPage) {
            $next.attr("disabled", true);
        } else {
            $next.attr("disabled", false);
            $next.click(function () {
                getPageStudent(res.pageNum + 1);
            });
        }
        // 设置总记录数
        $("#total").text("共" + res.totalCount + "条记录");
    })
}

// 添加或更新学生信息
function addOrUpdStudent(flag) {
    let id = $("#id").val();
    let name = $("#name").val();
    let birthday = $("#birthday").val();
    let description = $("#description").val();
    let avgScore = $("#avgScore").val();
    // 校验出生日期
    let currentDate = new Date();
    let inDate = new Date(birthday);
    if (birthday === "" || inDate > currentDate) {
        toast("出生日期有误");
        return;
    }
    // 校验平均分
    let reg = /^([0-9]|[1-9][0-9]|1[0-4][0-9]|150)$/;
    if (!reg.test(avgScore)) {
        toast("平均分有效范围0~150", 3);
        return;
    }

    console.log(flag);
    let url = "/students?id=" + id + "&name=" + name + "&birthday=" + birthday + "&description=" + description + "&avgScore=" + avgScore;
    if (flag === -1) {
        $.ajax({
            type: 'POST',
            url: url,
            dataType: 'html',
            success: function (data) {
                toast(data);
                closeModel();
                getPageStudent(1);
            },
            error: function () {
                toast("网络错误！")
            }
        });
    } else {
        $.ajax({
            type: 'PUT',
            url: url,
            dataType: 'html',
            success: function (data) {
                toast(data);
                closeModel();
                getPageStudent(1);
            },
            error: function () {
                toast("网络错误！")
            }
        });

    }
}

// 删除学生信息并移除相关dom
function delStudent(index, userId) {
    let url = '/students?id=' + userId;
    $.ajax({
        type: 'DELETE',
        url: url,
        success: function (data) {
            toast(data);
            getPageStudent(1);
            $('#t-body tr:eq(' + index + ')').remove();
        },
        error: function () {
            toast("网络错误！")
        }
    });
}

// 初始化模态框
$(document).ready(function () {
    let $addOrUpdUser = $("#addOrUpdUser");

    // 给添加或更新学生数据的提交按钮绑定点击事件
    $addOrUpdUser.click(function () {
        let index = $("#id").attr("data-index");
        if (index === "-1") {
            addOrUpdStudent(-1);
        } else {
            addOrUpdStudent(1);
        }
    });
});

// 打开学生信息模态框，index(-1表示添加学生信息)
function openModalStudent(index) {
    let $id = $("#id");
    let $name = $("#name");
    let $birthday = $("#birthday");
    let $description = $("#description");
    let $avgScore = $("#avgScore");

    if (index === -1) {
        $id.attr("disabled", false);
        $id.val("");
        $name.val("");
        $birthday.val("");
        $description.val("");
        $avgScore.val("");
        $id.attr("data-index", "-1");  // 设置标识
        modalCenter();
    } else {
        let $student = $("#t-body tr:eq(" + index + ") td");
        $id.val($student.eq(0).text());
        $id.attr("disabled", true);
        $name.val($student.eq(1).text());
        $birthday.val($student.eq(2).text());
        $description.val($student.eq(3).text());
        $avgScore.val($student.eq(4).text());
        $id.attr("data-index", index);  // 设置标识
    }
    $("#modal-student, .shade").removeClass("hide");
}