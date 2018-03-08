/** 基础url前缀 */
var base_url_prefix = "http://localhost:8080/api-manager";
/** api信息表表头标题 */
var api_thead = {"aid" : "编号", "requestPath" : "请求路径", "requestMethod" : "请求方法", "param" : "请求参数", "encoding" : "响应编码类型", "contentType" : "响应内容类型"};
/** 响应内容面板标题 */
var api_response_content_configuration = ["responseContentConfiguration", "响应内容"];
/** api信息表行样式 */
var row_class = ["active", "success", "warning", "danger"];
/** api信息表行样式下标 */
var row_class_index = 0;

/** 项目信息 */
var project_list;
/** 当前用户点击的项目下标 */
var curr_project_index;
/** 当前用户点击的api下标 */
var curr_api_index;

<!--=====================================================================-->
<!-- 清空页面初始化
<!--=====================================================================-->

/**
 * 初始化api信息表
 */
function initApiDisplayPanel() {
    // 初始化表格
    var theadHtml = "<tr>";
    for (var i in api_thead) {
        theadHtml += "<th>" + api_thead[i] + "</th>";
    }
    theadHtml += "</tr>";
    $("#api-table-thead").html(theadHtml);
    $("#api-table-tbody").html("");
    // 初始化面板
    $("#api-response-content-configuration").html("");
    $("#api-response-content-title").html(api_response_content_configuration[1]);
}

/**
 * 初始化项目菜单
 */
function initProjectMenu() {
    $("#accordion").html("");
}

<!--=====================================================================-->
<!-- 获取指定下标的项目或api信息
<!--=====================================================================-->

/**
 * 获取指定下标的项目信息
 * 
 * @param {Object} index 指定下标
 */
function getProjectByIndex(index) {
    return project_list[index];
}

/**
 * 获取指定下标的api信息
 * 
 * @param {Object} projectIndex 项目下标
 * @param {Object} apiIndex     api下标
 */
function getApiByIndex(projectIndex, apiIndex) {
    if (projectIndex == undefined || apiIndex == undefined) {
        return undefined;
    } else {
        return project_list[projectIndex]["apiList"][apiIndex];
    }
}

<!--=====================================================================-->
<!-- 获取项目数据并显示在页面上
<!--=====================================================================-->

/**
 * 获取项目列表
 */
function listProject() {
    $.ajax({
        type: "get",
        url: base_url_prefix + "/project/listProject",
        dataType: "json",
        success: function(data) {
            project_list = data;
            // 把获取到的数据展示在页面上
            redraw(project_list);
        }
    });
}

/**
 * 获取表格行对应class的下标(不同class对应不同的颜色)
 */
function getRowClassIndex() {
    var rowClassIndex = row_class_index;
    row_class_index += 1;
    if (row_class_index >= row_class.length) row_class_index = 0;
    return rowClassIndex;
}

/**
 * 展示api信息
 * 
 * @param {Object} api
 */
function displayApi(api) {
    // 表格展示api信息
    var table = "<tr class='" + row_class[getRowClassIndex()] + "'>";
    for (var key in api_thead) {
        table += "<td>" + api[key] + "</td>";
    }
    table += "</tr>";
    $("#api-table-tbody").html(table);
    // 面板展示响应内容模板
    var content = api[api_response_content_configuration[0]];
    content = entityToString(content);
    try{
        content = eval("(" + content + ")");
        content = JSON.stringify(content, null, 4);
        $("#api-response-content-configuration").html("<pre>" + content + "</pre>");
    }catch(e){
        // 响应内容模板不是JSON格式
        $("#api-response-content-configuration").html("<pre>" + content + "</pre>");
    }
}

/**
 * 重新绘制页面
 * 
 * @param {Object} data 项目信息
 */
function redraw(data) {
    var menu = "";
    var apiList;
    for (var i = 0; i < data.length; i ++) {
        menu += "<div class='panel panel-primary leftMenu'>";
        menu += "<div class='panel-heading' data-toggle='collapse' data-target='#collapse" + i + "' id='panel" + i + "' project-index='" + i + "'>";
        menu += "<h4 class='panel-title'>" + data[i]["projectName"] + "<span class='glyphicon glyphicon-chevron-down right'></span></h4>";
        menu += "</div>";
        menu += "<div id='collapse" + i + "' class='panel-collapse collapse'><ul class='list-group'>";
        
        apiList = data[i]["apiList"];
        for (var k = 0; k < apiList.length; k ++) {
            menu += "<li class='list-group-item'>";
            menu += "<button class='menu-item-left' id='panel" + i + "-button" + k + "' project-index='" + i + "' api-index='" + k + "'>";
            menu += "<span class='glyphicon glyphicon-triangle-right'></span>" + apiList[k]["requestPath"];
            menu += "</button></li>";
        }
        
        menu += "</ul></div></div>";
    }
    $("#accordion").html(menu);
    
    // 项目菜单触发的点击事件
    $(".panel-heading").click(function(e) {
        /*切换折叠指示图标*/
        $(this).find("span").toggleClass("glyphicon-chevron-down");
        $(this).find("span").toggleClass("glyphicon-chevron-up");
    });
    
    for (var i = 0; i < data.length; i ++) {
        apiList = data[i]["apiList"];
        for (var k = 0; k < apiList.length; k ++) {
            $("#panel" + i + "-button" + k).click(function(e) {
                // 更新当前用户点击的项目下标和api下标
                curr_project_index = $(this).attr("project-index");
                curr_api_index = $(this).attr("api-index");
                // 获取api对象
                var api = getApiByIndex(curr_project_index, curr_api_index);
                // 把api信息战士到表格和面板中
                displayApi(api);
            });
        }
        $("#panel" + i).click(function(e) {
            // 更新当前用户点击的项目下标
            curr_project_index = $(this).attr("project-index");
            curr_api_index = undefined;
            var project = getProjectByIndex(curr_project_index);
        });
    }    
    
    
}

<!--=====================================================================-->
<!-- 初始化页面(清空页面并获取项目数据显示)
<!--=====================================================================-->

/**
 * 初始化页面
 */
function init() {
    // 初始化页面
    initApiDisplayPanel();
    initProjectMenu();
    
    // 从后台获取项目数据
    listProject();
    
    // 初始化下标参数
    curr_api_index = undefined;
    curr_project_index = undefined;
}

<!--=====================================================================-->
<!-- 项目操作事件
<!--=====================================================================-->

/**
 * 提示警告信息
 * 
 * @param {Object} id      警告框id
 * @param {Object} message 提示信息
 */
function alertWarning(id, message) {
    $(id).attr("class", "alert alert-warning");
    $(id).html("<strong>警告! </strong>" + message);
}

function clearAlert(id) {
    $(id).html("");
    $(id).attr("class", "alert alert-warning hide");
}

/**
 * 项目按钮操作事件
 */
function addProjectButtonEvents() {
    
    // 触发显示新建项目模态框事件
    $("#modal-container-725761").on("show.bs.modal", function() {
        clearAlert("#insertProjectAlert");
    })
    
    // 点击提交项目新建模态框事件
    $("#submit-insert-project").on("click", function() {
        var projectName = $("#projectName").val().trim();
        if (projectName.length == 0) {
            alertWarning("#insertProjectAlert", "项目名不能为空或全为空格");
        } else {
            $.ajax({
                type: "get",
                url: base_url_prefix + "/project/insertProject",
                dataType: "json",
                data: {
                    "projectName": projectName
                },
                success: function(data) {
                    // 初始化页面
                    init();
                    // 清空输入框
                    $("#projectName").val("");
                    // 隐藏模态框
                    $("#modal-container-725761").modal("hide");
                },
                error: function() {
                    alertWarning("#insertProjectAlert", "请求后台操作失败!");
                }
            });
        }
    })
    
    // 触发显示删除项目模态框事件
    $("#modal-container-725762").on("show.bs.modal", function() {
        clearAlert("#deleteProjectAlert");
    })
    
    // 触发显示删除项目模态框事件
    $("#modal-container-725762").on("shown.bs.modal", function() {
        var project = getProjectByIndex(curr_project_index);
        if (project == undefined) {
            alert("请在左侧菜单栏先单击选择一个需要删除的项目");
            // 隐藏模态框
            $("#modal-container-725762").modal("hide");
        } else {
            $("#delete-project-body").html("是否确定删除项目<strong>" + project["projectName"] + "</strong>?");
        }
    })
    
    // 点击提交项目删除模态框事件
    $("#submit-delete-project").on("click", function() {
        var project = getProjectByIndex(curr_project_index);
        if (project != undefined) {
            var pid = project["pid"];
            $.ajax({
                type: "get",
                url: base_url_prefix + "/project/deleteProject",
                dataType: "json",
                data: {
                    "pid": pid
                },
                success: function(data) {
                    // 初始化页面
                    init();
                    // 隐藏模态框
                    $("#modal-container-725762").modal("hide");
                },
                error: function() {
                    alertWarning("#deleteProjectAlert", "请求后台操作失败!");
                }
            });
        } else {
            // do nothing
        }
    })
    
    // 触发显示修改项目模态框事件
    $("#modal-container-725763").on("show.bs.modal", function() {
        clearAlert("#updateProjectAlert");
    })
    
    // 触发显示修改项目模态框事件
    $("#modal-container-725763").on("shown.bs.modal", function() {
        var project = getProjectByIndex(curr_project_index);
        if (project == undefined) {
            alert("请在左侧菜单栏先单击选择一个需要修改的项目");
            // 隐藏模态框
            $("#modal-container-725763").modal("hide");
        } else {
            $("#updateProjectName").val(project["projectName"]);
        }
    })
    
    // 点击提交项目更新模态框事件
    $("#submit-update-project").on("click", function() {
        var project = getProjectByIndex(curr_project_index);
        var projectName = $("#updateProjectName").val().trim();
        var pid = project["pid"];
        if (projectName.length == 0) {
            alertWarning("#insertProjectAlert", "项目名不能为空或全为空格");
        } else {
            $.ajax({
                type: "get",
                url: base_url_prefix + "/project/updateProject",
                dataType: "json",
                data: {
                    "projectName": projectName,
                    "pid": pid
                },
                success: function(data) {
                    // 初始化页面
                    init();
                    // 清空输入框
                    $("#updateProjectName").val("");
                    // 隐藏模态框
                    $("#modal-container-725763").modal("hide");
                },
                error: function() {
                    alertWarning("#updateProjectAlert", "请求后台操作失败!");
                }
            });
        }
    })
    
}

<!--=====================================================================-->
<!-- api操作事件
<!--=====================================================================-->

/**
 * 字符串转换成HTML实体
 * 
 * @param {Object} str 待转换字符串
 */
function stringToEnterty(str) {
    var div = document.createElement("div");
    div.innerText = str;
    div.textContent = str;
    var res = div.innerHTML;
    return res;
}

/**
 * HTML实体转换成字符
 * 
 * @param {Object} entity 待转换HTML实体
 */
function entityToString(entity) {
  var div = document.createElement('div');
  div.innerHTML = entity;
  var res = div.innerText || div.textContent;
  return res;
}

/**
 * 添加接口按钮事件
 */
function addApiButtonEvents() {
    
    // 触发显示新建接口模态框事件
    $("#modal-container-725771").on("show.bs.modal", function() {
        clearAlert("#insertApiAlert");
    })
    
    // 触发显示新建接口模态框事件
    $("#modal-container-725771").on("shown.bs.modal", function() {
        var project = getProjectByIndex(curr_project_index);
        if (project == undefined) {
            alert("请在左侧菜单栏先单击选择一个待创建api所属的项目");
            // 隐藏模态框
            $("#modal-container-725771").modal("hide");
        } else {
            $("#insertApiProjectName").val(project["projectName"]);
        }
    })
    
    // 点击提交接口新建模态框事件
    $("#submit-insert-api").on("click", function() {
        // 数据校验
        var requestPath = $("#requestPath").val().trim();
        var responseContentConfiguration = $("#responseContentConfiguration").val().trim();
        if (requestPath.length == 0) {
            alertWarning("#insertApiAlert", "请求路径不能为空或全为空格");
            
        } else if (responseContentConfiguration.length == 0) {
            alertWarning("#insertApiAlert", "响应内容模板不能为空或全为空格");
            
        } else {
            try{
                // 规格化JSON格式响应内容模板
            	responseContentConfiguration = eval("(" + responseContentConfiguration + ")");
            	responseContentConfiguration = JSON.stringify(responseContentConfiguration, null, 4);
            }catch(e){
                // 响应内容模板不是JSON格式数据
            }
            // 过滤掉换行
            responseContentConfiguration = responseContentConfiguration.replace("\n", "");
            var requestMethod = $("#requestMethod").val();
            var encoding = $("#encoding").val();
            var contentType = $("#contentType").val();
            var necessaryFormParam = $("#necessaryFormParam").val();
            var formParam = $("#formParam").val();
            var necessaryFileParam = $("#necessaryFileParam").val();
            var fileParam = $("#fileParam").val();
            var project = getProjectByIndex(curr_project_index);
            var pid = project["pid"];
            $.ajax({
                type: "post",
                url: base_url_prefix + "/api/insertApi",
                dataType: "json",
                data: {
                    "pid": pid,
                    "requestMethod": requestMethod,
                    "requestPath": requestPath,
                    "encoding": encoding,
                    "contentType": contentType,
                    "responseContentConfiguration": responseContentConfiguration,
                    "necessaryFormParam": necessaryFormParam,
                    "formParam": formParam,
                    "necessaryFileParam": necessaryFileParam,
                    "fileParam": fileParam
                },
                success: function(data) {
                    // 初始化页面
                    init();
                    // 清空输入框
                    $("#updateProjectName").val("");
                    // 隐藏模态框
                    $("#modal-container-725771").modal("hide");
                },
                error: function() {
                    alertWarning("#insertApiAlert", "请求后台操作失败!");
                }
            });          
        }
    })
    
    // 触发显示删除接口模态框事件
    $("#modal-container-725772").on("show.bs.modal", function() {
        clearAlert("#deleteApiAlert");
    })
    
    // 触发显示删除接口模态框事件
    $("#modal-container-725772").on("shown.bs.modal", function() {
        var project = getProjectByIndex(curr_project_index);
        var api = getApiByIndex(curr_project_index, curr_api_index);
        if (project == undefined || api == undefined) {
            alert("请在左侧菜单栏先单击选择一个待删除api");
            // 隐藏模态框
            $("#modal-container-725772").modal("hide");
        } else {
            $("#delete-api-body").html("是否确定删除<strong>" + project["projectName"] + 
                "</strong>项目下的<strong>" + api["requestPath"] + "</strong>接口?");
        }
    })
    
    // 点击提交接口删除模态框事件
    $("#submit-delete-api").on("click", function() {
        var api = getApiByIndex(curr_project_index, curr_api_index);
        if (api != undefined) {
            var aid = api["aid"];
            $.ajax({
                type: "get",
                url: base_url_prefix + "/api/deleteApi",
                dataType: "json",
                data: {
                    "aid": aid
                },
                success: function(data) {
                    // 初始化页面
                    init();
                    // 隐藏模态框
                    $("#modal-container-725772").modal("hide");
                },
                error: function() {
                    alertWarning("#deleteApi", "请求后台操作失败!");
                }
            }); 
        } else {
            // do nothing
        }
        // 隐藏模态框
        $("#modal-container-725772").modal("hide");
    })
    
    // 触发显示修改接口模态框事件
    $("#modal-container-725773").on("show.bs.modal", function() {
        clearAlert("#updateApiAlert");
    })
    
    // 触发显示修改接口模态框事件
    $("#modal-container-725773").on("shown.bs.modal", function() {
        var project = getProjectByIndex(curr_project_index);
        var api = getApiByIndex(curr_project_index, curr_api_index);
        if (project == undefined || api == undefined) {
            alert("请在左侧菜单栏先单击选择一个待更新api");
            // 隐藏模态框
            $("#modal-container-725773").modal("hide");
        } else {
            $("#updateApiProjectName").val(project["projectName"]);
            $("#updateRequestMethod").val(api["requestMethod"]);
            $("#updateRequestPath").val(api["requestPath"]);
            $("#updateContentType").val(api["contentType"]);
            $("#updateEncoding").val(api["encoding"]);
            // 响应内容模板
            var responseContentConfiguration = entityToString(api["responseContentConfiguration"]);
            try{
                // 规格化响应内容模板
                responseContentConfiguration = eval("(" + responseContentConfiguration + ")");
                responseContentConfiguration = JSON.stringify(responseContentConfiguration, null, 4);
            }catch(e){
                // 响应内容模板不是JSON格式的
            }
            $("#updateResponseContentConfiguration").val(responseContentConfiguration);
            
            // api参数
            var param = api["param"];
            if (param == null || param == undefined) {
                $("#updateNecessaryFormParam").val("");
                $("#updateFormParam").val("");
                $("#updateNecessaryFileParam").val("");
                $("#updateFileParam").val("");
            } else {
                // 普通属性
                var formParamList = param["formParamList"];
                if (formParamList == null || formParamList == undefined) {
                    $("#updateNecessaryFormParam").val("");
                    $("#updateFormParam").val("");
                } else {
                    var necessaryFormParam = "";
                    var formParam = "";
                    var necessary = "";
                    var primary = "";
                    for (var i = 0; i < formParamList.length; i ++) {
                        if (formParamList[i]["necessary"] == true) {
                            necessaryFormParam += necessary + formParamList[i]["fieldName"];
                            necessary = ";";
                        } else {
                            formParam += primary + formParamList[i]["fieldName"];
                            primary = ";";
                        }
                    }
                    $("#updateNecessaryFormParam").val(necessaryFormParam);
                    $("#updateFormParam").val(formParam);
                }
                // 文件属性
                var fileParamList = param["fileParamList"];
                if (fileParamList == null || fileParamList == undefined) {
                    $("#updateNecessaryFileParam").val("");
                    $("#updateFileParam").val("");
                } else {
                    var necessaryFileParam = "";
                    var fileParam = "";
                    var necessary = "";
                    var primary = "";
                    for (var i = 0; i < fileParamList.length; i ++) {
                        if (fileParamList[i]["necessary"] == true) {
                            necessaryFileParam += necessary + fileParamList[i]["fieldName"];
                            necessary = ";";
                        } else {
                            fileParam += primary + fileParamList[i]["fieldName"];
                            primary = ";";
                        }
                    }
                    $("#updateNecessaryFileParam").val(necessaryFileParam);
                    $("#updateFileParam").val(fileParam);
                }
                
            }
        }
    })
    
    // 点击提交接口更新模态框事件
    $("#submit-update-api").on("click", function() {
        var api = getApiByIndex(curr_project_index, curr_api_index);
        if(api != undefined) {
            var aid = api["aid"];
            var requestMethod = $("#updateRequestMethod").val();
            var requestPath = $("#updateRequestPath").val();
            var encoding = $("#updateEncoding").val();
            var contentType = $("#updateContentType").val();
            var necessaryFormParam = $("#updateNecessaryFormParam").val();
            var formParam = $("#updateFormParam").val();
            var necessaryFileParam = $("#updateNecessaryFileParam").val();
            var fileParam = $("#updateFileParam").val();
            var responseContentConfiguration = $("#updateResponseContentConfiguration").val();
            // 过滤掉换行符
            responseContentConfiguration = responseContentConfiguration.replace("\n", "");
            $.ajax({
                type: "post",
                url: base_url_prefix + "/api/updateApi",
                dataType: "json",
                data: {
                    "aid": aid,
                    "requestMethod": requestMethod,
                    "requestPath": requestPath,
                    "encoding": encoding,
                    "contentType": contentType,
                    "responseContentConfiguration": responseContentConfiguration,
                    "necessaryFormParam": necessaryFormParam,
                    "formParam": formParam,
                    "necessaryFileParam": necessaryFileParam,
                    "fileParam": fileParam
                },
                success: function(data) {
                    // 初始化页面
                    init();
                    // 隐藏模态框
                    $("#modal-container-725773").modal("hide");
                },
                error: function() {
                    alertWarning("#updateApi", "请求后台操作失败!");
                }
            });
        } else {
            // do nothing
        }
    })    
    
}

window.onload = function() {

    // 初始化
    init();

    // 添加按钮事件
    addProjectButtonEvents();
    addApiButtonEvents();    

}
