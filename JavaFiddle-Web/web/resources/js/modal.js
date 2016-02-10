var PATH;
var timer;
var $elClicked;

$(document).ready(function() {
    // NEW PACKAGE DIALOG
    //
    $('#modal-newpack').on('show.bs.modal', function () {
        $("#modal-newpack-name").val("");
        $("#modal-newpack-project").find('option').remove();

        var projects = getProjectsList();
        for (var i = 0; i < projects.length; i++)
            $("#modal-newpack-project").prepend("<option>" + projects[i] +"</option>");
        var projectName = getNameByID(getProjectId());
        $("#modal-newpack-project").val(projectName);
        
        var fullpath = "./" + projectName + "/";

        if ($elClicked.hasClass("package")) {
            var oldPackageName = getNameByID($elClicked.closest('li').attr('id'));
            $("#modal-newpack-name").val(oldPackageName + ".");
            fullpath += oldPackageName.replace(/\./g, '/');
        }
        $("#modal-newpack-fullpath").val(fullpath);
    });

    $("#modal-newpack-name").keyup(function(event) {
        if (event.keyCode === 13) {
            if (addPackage()) {
                $('#modal-newpack').modal('hide');
            }
        }
    });
    
    
    // NEW FILE DIALOG
    //
    $('#modal-newfile').on('show.bs.modal', function (e) {
        $("#modal-newfile-name").val(""); 
        $("#modal-newfile-package").find('option').remove();
        $("#modal-newfile-project").find('option').remove();
        $("#modal-newfile-fullpath").find('option').remove();

        var projects = getProjectsList();
        for (var i = 0; i < projects.length; i++)
            $("#modal-newfile-project").prepend("<option>" + projects[i] +"</option>");
        var currentProject = getNameByID(getProjectId());
        $("#modal-newfile-project").val(currentProject);

        var packages = getPackagesList($("#modal-newfile-project").val());
        for (var i = 0; i < packages.length; i++)
            $("#modal-newfile-package").append("<option>" + packages[i].replace("<", "&lt;").replace(">", "&gt;") +"</option>");

        var fullpath = "./" + currentProject + "/";

        if ($elClicked.hasClass("package")) {
            var packageName = getNameByID($elClicked.closest('li').attr('id'));
            fullpath += packageName.replace(/\./g, '/') + "/";
            $("#modal-newfile-package").val(packageName);
        }
        
        $("#modal-newfile-fullpath").val(fullpath);
    });
    
    $("#modal-newfile-name").keyup(function(event) {
        if (event.keyCode === 13) {
            if (addFile()) {
                $('#modal-newfile').modal('hide');
            }
        }
    });
    
    
    // RENAME DIALOG
    //
    $('#modal-rename').on('show.bs.modal', function (e) {
        var name = $elClicked.text();
        $("#modal-rename-header").text("Rename " + nodeType($elClicked) + " " + name);
        $("#modal-rename-name").val(name);
    });
    
    $("#modal-rename-name").keyup(function(event) {
        if (event.keyCode === 13) {
            if (renameElement()) {
                $('#modal-rename').modal('hide');
            }
        }
    });
    
    
    // DELETE DIALOG
    // 
    $('#modal-delete').on('show.bs.modal', function (e) {
        var id = $elClicked.closest('li').attr('id');
        var name = $(document.getElementById(id)).children("a").text();
        $("#modal-delete-name").text("Are you sure you want to delete " + nodeType($elClicked) + " " + name + "?");
    });
    
    
    // SHARE PROJECT DIALOG
    //
    $('#modal-share').on('show.bs.modal', function (e) {
        var latest = getLatestProjectHash();
        console.log("modal-share: hash is " + latest);
        var url = "http://localhost:38353/JavaFiddle-war/?h=";
        url = "";
        $("#modal-share-name").val(url + (!latest.localeCompare("null") ? "" : latest));
        $("#modal-share-verify").text(!latest.localeCompare("null") ? "There are no project revisions. Commit it!" : "Share this link with your friends!");
    });
    
    
//    // REVISIONS LIST DIALOG 
//    // 
//    $('#modal-revisions').on('show.bs.modal', function (e) {
//        $("#modal-revisions-name").find('option').remove();
//        var revisions;
//
//        $.ajax({
//            url: PATH + '/webapi/data/hierarchy',
//            type: 'GET',
//            async: false,
//            contentType: "application/json",
//            success: function(data) {
//                revisions = data;
//            }
//        }); 
//
//        for(var i = 0; i < revisions.length; ++i)
//            $("#modal-revisions-name").prepend("<option>" + revisions[i] +"</option>");
//    });
//});

    // COMMITS LIST DIALOG 
    $('#modal-commits').on('show.bs.modal', function (e) {
        $("#modal-commits-name").find('option').remove();
        var commits;
        console.log("Entered commits list dialog");
        $.ajax({
            url: PATH + '/webapi/git/commitsList',
            type: 'GET',
            async: false,
            success: function(data) {
                commits = data;
            }
        }); 

        for(var i = 0; i < commits.length; ++i)
            $("#modal-commits-name").prepend("<option>" + commits[i] +"</option>");
    });
    });

// NEW PROJECT DIALOG
//
function m_newproj_updname() {
    $("#modal-newproj-ok").prop('disabled', true);
    var localtimer = setTimeout(function () {
        if (localtimer === timer)
            m_newproj_update();
    }, 600);
    timer = localtimer;
}

function m_newproj_update() {
//    var projectName = $("#modal-newpack-project").val();
//    var fullpath = "./" + projectName + "/";
    var projectName = $("#modal-newproj-name").val();

    var irpn = isRightProjectName(projectName);

    if (irpn === "ok") {
        $("#modal-newproj-ok").prop('disabled', false);
        $("#modal-newproj-verify").text("");
    } else {
        $("#modal-newproj-ok").prop('disabled', true);
        switch(irpn) {
            case "wrongname":
                $("#modal-newproj-verify").text("Wrong project name. \n\
                    Only latin characters and numbers are allowed. \n\
                    The project name must not start with a digit.");
                break;
            case "used":
                $("#modal-newproj-verify").text("Project with this name already exists.");
                break;
            default:
                $("#modal-newproj-verify").text("Unknown error.");
                break;
        }
    }
    //fullpath += packageName.replace(/\./g, '/');
    //$("#modal-newpack-fullpath").val(fullpath); 
}

function m_newproj_updhash() {
    $("#modal-project-hash-ok").prop('disabled', false);
}

function addProject() {
    //var packageName = $('#modal-newpack-name').val();
    var projectName = $('#modal-newproj-name').val();
    //var projectName = $("#modal-newproj-project").val();
    
    if(isRightProjectName(projectName)) {
        $.ajax({
            url: PATH + '/webapi/tree/addProject',
            type: 'POST',
            data: {
                //packageName: packageName,
                projectName: projectName
            },
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                buildTree();
            }
        });  
    }
}
function addExampleProject() {
    $.ajax({
        url: PATH + '/webapi/tree/addExampleProject',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            buildTree();
        }
    });  
}
function addProjectByHash() {
    var projectHash = $('#modal-project-hash').val();
    console.log(projectHash);
    $.ajax({
       url: PATH + '/webapi/git/addProjectByHash' ,
       type: 'POST',
       data: {projectHash: projectHash},
       contentType: "application/x-www-form-urlencoded",
       success: function(data) {
           closeAllTabs();
           buildTree();
       }
    });
}

// TEST REGISTRATION DIALOG
function m_reg_updreg() {
    $("#modal-reg-ok").prop('disabled', true);
    var localtimer = setTimeout(function () {
        if (localtimer === timer)
            m_reg_updatereg();
    }, 600);
    timer = localtimer;
}
function m_reg_updatereg() {
    var name = $("#modal-reg-name").val();
    var email = $("#modal-reg-email").val();
    var password = $("#modal-reg-password").val();
    console.log($(name));
    var irr = isRightReg(name, email, password);
    console.log($(irr));
    if (irr === "ok") {
        $("#modal-reg-ok").prop('disabled', false);
        $("#modal-reg-verify").text("");
    } else {
        $("#modal-reg-ok").prop('disabled', true);
        switch(irr) {
            case "empty":
                $("#modal-reg-verify").text("");
                break;
            case "wrongname":
                $("#modal-reg-verify").text("Wrong user name. \n\
                    Only latin characters and numbers are allowed. \n\
                    Name must not start with a digit.");
                break;
            case "used":
                $("#modal-reg-verify").text("User with these name or email already exists.");
                break;
            default:
                $("#modal-reg-verify").text("Unknown error.");
                break;
        }
    }
}
function isRightReg(name, email, password) {
    var result;
    
    $.ajax({
        url: PATH + '/webapi/user/isRightRegData',
        type: 'GET',
        dataType: "json",
        async: false,
        data: {
            name: name,
            email: email,
            password: password
        },     
        success: function(data) {
            result = data;
        }
    });
    console.log($(result));
    return result;
}
function reg() {
    var name = $('#modal-reg-name').val();
    var email = $('#modal-reg-email').val();
    var password = $('#modal-reg-password').val();
    
    if (isRightReg(name, email, password) === "ok"){
        console.log($(password));
        $.ajax({
            url: PATH + '/webapi/user/register',
            type: 'POST',
            data: {
                name: name,
                email: email,
                password: password
            },
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                //buildTree();
                location.replace('fiddle/index.xhtml');
                window.reload();
            }
        });  
    }
}

// TEST LOG IN DIALOG
function m_log_updlog() {
    $("#modal-log-ok").prop('disabled', true);
    var localtimer = setTimeout(function () {
        if (localtimer === timer)
            m_log_updatelog();
    }, 600);
    timer = localtimer;
}
function m_log_updatelog() {
    var name = $("#modal-log-name").val();
    var password = $("#modal-log-password").val();
    var irl;
    irl = isRightLog(name, password);
    console.log($(irl));
    if (irl === "ok") {
        $("#modal-log-ok").prop('disabled', false);
        $("#modal-log-verify").text("");
    } else {
        $("#modal-log-ok").prop('disabled', true);
        switch(irl) {
            case "empty":
                $("#modal-log-verify").text("");
                break;
            case "wrong":
                $("#modal-log-verify").text("Pemission denied");
                break;
            case "used":
                $("#modal-log-verify").text("User with these name or email already exists.");
                break;
            default:
                $("#modal-log-verify").text("Unknown error.");
                break;
        }
    }
}
function isRightLog(name, password) {
    var result;
    
    $.ajax({
        url: PATH + '/webapi/user/isRightLogData',
        type: 'GET',
        dataType: "json",
        async: false,
        data: {
            name: name,
            password: password
        },     
        success: function(data) {
            result = data;
        }
    });
    return result;
}
function log() {
    var name = $('#modal-log-name').val();
    var password = $('#modal-log-password').val();
    var result;
    $.ajax({
        url: PATH + '/webapi/user/login',
        type: 'POST',
        data: {
            name: name,
            password: password
        },
        contentType: "application/x-www-form-urlencoded",
        success: function() {
//            buildTree();
            location.replace('fiddle/index.xhtml');
            closeAllTabs();
        }
    });
    
}

// NEW PACKAGE DIALOG
//
function m_newpack_updname() {
    $("#modal-newpack-ok").prop('disabled', true);
    var localtimer = setTimeout(function () {
        if (localtimer === timer)
            m_newpack_update();
    }, 600);
    timer = localtimer;
}

function m_newpack_update() {
    var projectName = $("#modal-newpack-project").val();
    var fullpath = "./" + projectName + "/";
    var packageName = $("#modal-newpack-name").val();

    var irpn = isRightPackageName(packageName, projectName);
    if (irpn === "ok") {
        $("#modal-newpack-ok").prop('disabled', false);
        $("#modal-newpack-verify").text("");
    } else {
        $("#modal-newpack-ok").prop('disabled', true);
        switch(irpn) {
            case "wrongname":
                $("#modal-newpack-verify").text("Wrong package name. \n\
                    Only latin characters and numbers are allowed. \n\
                    The package name must not start with a digit.");
                break;
            case "used":
                $("#modal-newpack-verify").text("This package name is already used in your project.");
                break;
            default:
                $("#modal-newpack-verify").text("Unknown error.");
                break;
        }
    }
    fullpath += packageName.replace(/\./g, '/');
    $("#modal-newpack-fullpath").val(fullpath); 
}

function addPackage() {
    var packageName = $('#modal-newpack-name').val();
    var projectName = $("#modal-newpack-project").val();
    
    if(isRightPackageName(packageName, projectName)) {
        $.ajax({
            url: PATH + '/webapi/tree/addPackage',
            type: 'POST',
            data: {
                packageName: packageName,
                projectName: projectName
            },
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                buildTree();
            }
        });  
    }
}


// NEW FILE DIALOG
//
function m_newfile_updname() {
    $("#modal-newfile-ok").prop('disabled', true);
    var localtimer = setTimeout(function () {
        if (localtimer === timer)
            m_newfile_update();
    }, 600);
    timer = localtimer;
}

function m_newfile_updproject() {
    var currentProject = $("#modal-newfile-project").val();
    $("#modal-newfile-package").find('option').remove();
    var packages = getPackagesList(currentProject);
    for (var i = 0; i < packages.length; i++)
        $("#modal-newfile-package").prepend("<option>" + packages[i] +"</option>");
    m_newfile_update();
}

function m_newfile_update() {
    var projectName = $("#modal-newfile-project").val();
    var fullpath = "./" + projectName + "/";
    var packageName = $("#modal-newfile-package").val();
    if (packageName !== "<default_package>")
        fullpath += packageName.replace(/\./g, '/') + "/";
    var className = $('#modal-newfile-name').val();

    var ircn = isRightClassName(className, packageName, projectName);
    if (ircn === "ok") {
        if (!className.endsWith(".java"))
            className += ".java";
        fullpath += className;
        $("#modal-newfile-ok").prop('disabled', false);
        $("#modal-newfile-verify").text("");
    } else {
        $("#modal-newfile-ok").prop('disabled', true);
        switch(ircn) {
            case "wrongname":
                $("#modal-newfile-verify").text("Wrong class name.\n\
                    Only latin characters and numbers are allowed.\n\
                    The class name must not start with a digit.");
                break;
            case "used":
                $("#modal-newfile-verify").text("This class name is already used in this package.");
                break;
            default:
                $("#modal-newfile-verify").text("Unknown error.");
                break;
        }
    }
    $("#modal-newfile-fullpath").val(fullpath); 
}

function addFile() {
    var className = $('#modal-newfile-name').val();
    var packName = $("#modal-newfile-package").val();
    var projectName = $("#modal-newfile-project").val();
    var classType = $("#modal-newfile-classtype").val();
    
    if(className.endsWith(".java")) {
        className = className.substring(0, className.length - 5);
    }
    
    if(isRightClassName(className, packName, projectName)) {
        var id;
        $.ajax({
            url: PATH + '/webapi/tree/addFile',
            type: 'POST',
            data: {
                className: className,
                packageName: packName,
                projectName: projectName,
                type: classType
            },
            contentType: "application/x-www-form-urlencoded",
            success: function(data) {
                var li = addTabToPanel("node_" + data + "_tab", (className.endsWith(".java") ? className  : className + ".java"), classType);
                id = "node_" + data + "_tab";
                selectTab(li);
                buildTree();
                $.ajax({
                    url: PATH + '/webapi/git/add',
                    type: 'POST',
                    data: {classId: id},
                    success: function(data) {
                        console.log("Successfully added " + id + " to git.");
                    },
                    error: function(data) {
                        console.log("Failed to add " + id + " to git.");
                    }
                });
                
            }
        });
        return true;
    }
    return false;
}


// RENAME DIALOG
//
function m_rename_updname() {
    $("#modal-rename-ok").prop('disabled', true);
    var localtimer = setTimeout(function () {
        if (localtimer === timer)
            m_rename_update($elClicked);
    }, 600);
    timer = localtimer;
}

function m_rename_update() {
    var name = $("#modal-rename-name").val();
    var result;
    
    switch (nodeType($elClicked)) {
        case "project":
            result = isRightProjectName(name);
            break;
        case "package":
            var projectName = getNameByID(getProjectId());
            result = isRightPackageName(name, projectName);
            break;
        default:
            var projectName = getNameByID(getProjectId());
            var packageName = $elClicked.closest('li').closest('ul').closest('li').children("a").text();
            result = isRightClassName(name, packageName, projectName);
            break;
    }
    
    $("#modal-rename-ok").prop('disabled', true);
    switch(result) {
        case "wrongname":
            $("#modal-rename-verify").text("Wrong name.\n\
                Only latin characters and numbers are allowed.\n\
                The name must not start with a digit.");
            break;
        case "used":
            $("#modal-rename-verify").text("This name is already used in this package.");
            break;
        case "ok":
            $("#modal-rename-verify").text("");
            $("#modal-rename-ok").prop('disabled', false);
            break;        
        default:
            $("#modal-rename-verify").text("Unknown error.");
            break;
    }
    
    return result;
}

function renameElement() {
    var name = $("#modal-rename-name").val();
    var result;
    
    var id = $elClicked.closest('li').attr('id');
    
    if (m_rename_update() === "ok") {
        saveProject();
        $.ajax({
            url: PATH + '/webapi/tree/rename',
            type: 'POST',
            data: {
                id: id,
                name: name
            },
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                buildTree();
            }
        }); 
    }
}


// DELETE DIALOG
// 

function deleteElement($elClicked) {
    var id = $elClicked.closest('li').attr('id');
    saveProject();
    
    $.ajax({
        url: PATH + '/webapi/tree/remove',
        type: 'POST',
        data: id,
        contentType: "application/json",
        success: function() {
            switch(nodeType($elClicked)) {
                case "project":
                    $('#' + id).remove();
                    closeAllTabs();
                    invalidateSession();
                    buildTree();
                    break;
                case "package":
                    closeTabsFromPackage(id);
                    $('#' + id).remove();
                    break;
                default:
                    $('#' + id).remove();
                    closeTab($('#' + id + '_tab'));
                    break;
            }
        }
    });     
}

// SHARE PROJECT DIALOG
//
function getLatestProjectHash() {
    var hash = "";
    
    $.ajax({
        url: PATH + '/webapi/git/lasthash',
        type:'GET',
        async: false,
        success: function(data) {
            hash = data;
            console.log("Received hash: " + hash);
        },
        error: function(data) {
            console.log("Failed to receive hash. Received from server: " + data);
        }
    });
    
    return hash;
}

// REVISIONS LIST DIALOG 
//
function revert() {
    var hash = $("#modal-revisions-name").val();
    
   $.ajax({
        url: PATH + '/webapi/data/changeproject',
        type: 'POST',
        async: false,
        data: {projecthash : hash},
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            closeAllTabs();
            invalidateSession();
            buildTree();
        }
    });
}
//Commit reversion dialog.
function revertToCommit() {
    var hash = $("#modal-commits-name").val();
    console.log("Sending hash " + hash);
   $.ajax({
        url: PATH + '/webapi/git/revert',
        type: 'POST',
        async: false,
        data: {projecthash : hash},
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            closeAllTabs();
            invalidateSession();
            buildTree();
        },
        error: function() {
            console.log("Failed to revert");
            $('#latest_update').text("Failed to revert");
        }
    });
}

// AJAX REQUESTS
//
function getNameByID(id) {
    var name;
    
    $.ajax({
        url: PATH + '/webapi/tree/namebyid',
        type: 'GET',
        dataType: "json",
        data: {id: id},
        async: false,
        success: function(data) {
            name = data;
        }
    }); 
    
    return name;
}

function getProjectsList() {
    var projectsList;
    
    $.ajax({
        url: PATH + '/webapi/tree/projectslist',
        type: 'GET',
        dataType: "json",
        async: false,
        success: function(data) {
            projectsList = data;
        }
    }); 
    
    return projectsList;
}

function getPackagesList(projectname) {
    var packagesList;
    
    $.ajax({
        url: PATH + '/webapi/tree/packageslist',
        type: 'GET',
        data: {
            projectname : projectname
        },
        dataType: "json",
        async: false,
        success: function(data) {
            console.log("Receied from server, list of packages: " + data);
            packagesList = data;
        }
    }); 
    
    return packagesList;
}

function isRightProjectName(name) {
    var result;

    result = "ok"
    
    //$.ajax({
    //    url: PATH + '/webapi/tree/rprojname',
    //    type: 'GET',
    //    data: {
    //        name: name
    //    },
    //    dataType: "json",
    //    async: false,
    //    success: function(data) {
    //        result = data;
    //    }
    //});
    
    return result;
}

function isRightPackageName(packageName, projectName) {
    var result;
    
    $.ajax({
        url: PATH + '/webapi/tree/rpackname',
        type: 'GET',
        dataType: "json",
        async: false,
        data: {
            packageName: packageName,
            projectName: projectName
        },
        success: function(data) {
            result = data;
        }
    }); 

    return result;
}

function isRightClassName(name, packageName, projectName){
    var result;
    
    $.ajax({
        url: PATH + '/webapi/tree/rclassname',
        type: 'GET',
        data: {
            name: name,
            packageName: packageName,
            projectName: projectName
        },
        dataType: "json",
        async: false,
        success: function(data) {
            result = data;
        }
    }); 

    return result;
}

function nodeType($elClicked) {
    var id = $elClicked.closest('li').attr('id');

    if ($(document.getElementById(id)).children("a").hasClass("root")) {
        return "project";
    } else {
        if ($(document.getElementById(id)).children("a").hasClass("package"))
            return "package";
        else {
            return "class";
        }
    }
}
