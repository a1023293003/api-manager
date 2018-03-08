
/** api信息表表头标题 */
var apiThead = new Array("编号", "请求路径", "请求方法", "请求参数", "响应编码类型", "响应内容类型");
/** api信息表行样式 */
var row_class = new Array("", "success", "error", "warning");
/** api信息表行样式下标 */
var row_class_index = 0;

/**
 * 初始化api信息表
 */
function initApiTable() {
    var theadHtml = "<tr>";
    for (var i = 0; i < apiThead.length; i ++) {
        theadHtml += "<th>" + apiThead[i] + "</th>";
    }
    theadHtml += "</tr>";
    $("#api-table-thead").html(theadHtml);
}

/**
 * 项目按钮操作事件
 */
function addProjectButtonEvents() {
    
    // 触发显示新建项目模态框事件
    $("#modal-container-725761").on("show.bs.modal", function() {
        alert("显示新建模态框");
    })
    
    // 点击提交项目新建模态框事件
    $("#submit-insert-project").on("click", function() {
        alert("点击提交新建项目按钮");
        // 隐藏模态框
        $("#modal-container-725761").modal("hide");
    })
    
    // 触发显示删除项目模态框事件
    $("#modal-container-725762").on("show.bs.modal", function() {
        alert("显示删除模态框");
    })
    
    // 点击提交项目删除模态框事件
    $("#submit-delete-project").on("click", function() {
        alert("点击确定删除项目按钮");
        // 隐藏模态框
        $("#modal-container-725762").modal("hide");
    })
    
    // 触发显示修改项目模态框事件
    $("#modal-container-725763").on("show.bs.modal", function() {
        alert("显示模态框");
    })
    
    // 点击提交项目更新模态框事件
    $("#submit-update-project").on("click", function() {
        alert("点击提交更新项目按钮");
        // 隐藏模态框
        $("#modal-container-725763").modal("hide");
    })
    
}

/**
 * 添加接口按钮事件
 */
function addApiButtonEvents() {
    
    // 触发显示新建接口模态框事件
    $("#modal-container-725771").on("show.bs.modal", function() {
        alert("显示新建模态框");
    })
    
    // 点击提交接口新建模态框事件
    $("#submit-insert-api").on("click", function() {
        alert("点击提交新建接口按钮");
        // 隐藏模态框
        $("#modal-container-725771").modal("hide");
    })
    
    // 触发显示删除接口模态框事件
    $("#modal-container-725772").on("show.bs.modal", function() {
        alert("显示删除模态框");
    })
    
    // 点击提交接口删除模态框事件
    $("#submit-delete-api").on("click", function() {
        alert("点击确定删除接口按钮");
        // 隐藏模态框
        $("#modal-container-725772").modal("hide");
    })
    
    // 触发显示修改接口模态框事件
    $("#modal-container-725773").on("show.bs.modal", function() {
        alert("显示模态框");
    })
    
    // 点击提交接口更新模态框事件
    $("#submit-update-api").on("click", function() {
        alert("点击提交更新接口按钮");
        // 隐藏模态框
        $("#modal-container-725773").modal("hide");
    })    
    
}

window.onload = function() {

    // 初始化表单
    initApiTable();

    // 添加按钮事件
    addProjectButtonEvents();
    addApiButtonEvents();    

    
    
}
