// TREE

function buildTree() {
    $("#tree").empty();

    var userProjects = getUserProjects();
    sessionStorage.userProjects = JSON.stringify(userProjects);

    if (userProjects.length == 0) {
        //Write "no projects yet" and the welcome tab.
        $('#tree').append('<b>-No projects yet</b><br/><br/>');
    }

    for (var i = 0; i < userProjects.length; i++) {
        var projectStructure = getProjectStructure(userProjects[i]);
        sessionStorage.setItem(userProjects[i], JSON.stringify(projectStructure));
        $("#projectname").text(projectStructure.name);
        setProjectId(userProjects[i]);
        appendNodes(projectStructure, '#tree');
    }

    $('#tree').append('<li role="presentation"><a role="menuitem" tabindex="-1" href="#" onclick=\'$("#modal-newproj").modal("show");\'>Add New Project</a></li>');

    $(function () {
        $('#tree').liHarmonica({
            onlyOne: false,
            speed: 100
        });
    });
    openedNodesList().forEach(function(entry) {
        $("#" + entry).children('a').addClass('harOpen');
        $("#" + entry).children('ul').addClass('opened');
    });
}

function appendNodes(projectStructure, selector) {
    if(projectStructure.type === "root" || projectStructure.type === "source") {

        $(selector).append('<li id = "node_' + projectStructure.fileId + '" class = "open">\
                            <a href="#" class="' + projectStructure.type + '">' + projectStructure.name + '</a>\
                            <ul id ="node_' + projectStructure.fileId + '_' + projectStructure.type + '"/></li>');
    }
    else if(projectStructure.type === "package" && projectStructure.name !== "<default_package>") {

        $(selector).append('<li id = "node_' + projectStructure.fileId + '" class = "open">\
                            <a href="#" class="' + projectStructure.type + '" onclick="changeNodeState($(this));">' + projectStructure.name + '</a>\
                            <ul id ="node_' + projectStructure.fileId + '_' + projectStructure.type + '"/></li>');
    }else {

        $(selector).append('<li id = "node_' + projectStructure.fileId + '" class = "open">\
                            <a href="#" class="' + projectStructure.type + '" onclick="openTabFromTree($(this));">' + projectStructure.name + '</a>\
                            <ul id ="node_' + projectStructure.fileId + '_' + projectStructure.type + '"/></li>');
    }

    if(projectStructure.childFiles.length == 0) {
        return;
    }

    for(var i = 0; i < projectStructure.childFiles.length; i++){
        appendNodes(projectStructure.childFiles[i], '#node_'+ projectStructure.fileId + '_' + projectStructure.type);
    }

}

function getUserProjects() {
    var userProjects;
    $.ajax({
        url: PATH + "/fiddle/projects",
        type: "GET",
        dataType: "json",
        async: false,
        success: function(data)
        {
            userProjects = data;
        }
    });
    return userProjects;
}

function getProjectStructure(projectHash) {
    var projectStructure;
    $.ajax({
        url: PATH + "/fiddle/projects/" + projectHash,
        type: "GET",
        dataType: "json",
        async: false,
        success: function(data)
        {
            projectStructure = data;
        }
    });
    return projectStructure;
}