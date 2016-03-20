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
    //
    //$.ajax({
    //    url: PATH + '/webapi/tree/tree',
    //    type: 'GET',
    //    dataType: "json",
    //    async: false,
    //    success: function(data) {
    //        for (var i = 0; i < data.projects.length; i++) {
    //                var proj = data.projects[i];
    //                $('#tree').append('<li id = "node_' + proj.id + '" class="open"><a href="#" class="root">' + proj.name + '</a><ul id ="node_' + proj.id + '_src"\></li>');
    //                $('#node_' + proj.id + '_src').append('<li id = "node_' + proj.id + '_srcfolder" class="open"><a href="#" class="sources">src</a><ul id ="node_' + proj.id + '_list"\></li>');
    //                $("#projectname").text(proj.name);
    //                setProjectId(proj.id);
    //                for (var j = 0; j < proj.packages.length; j++) {
    //                    var pck = proj.packages[j];
    //                    if (!(pck.name === "<default_package>"))
    //                        $('#node_' + pck.parentId + '_list').append('<li id = "node_' + pck.id + '"><a href="#" class="package" onclick="changeNodeState($(this));">' + pck.name + '</a><ul id ="node_' + pck.id + '_list"\></li>');
    //                }
    //                for (var j = 0; j < proj.packages.length; j++) {
    //                    var pack = proj.packages[j];
    //                    for (var k = 0; k < pack.files.length; k++) {
    //                        var file = pack.files[k];
    //                        var parent = (pack.name === "<default_package>") ? proj.id : pack.id;
    //                        $('#node_' + parent + '_list').append('<li id = "node_' + file.id + '"><a href="#" class="' + file.type + '" onclick="openTabFromTree($(this));">' + file.name + '</a></li>');
    //                    }
    //                }
    //            }
    //        $('#tree').append('<li role="presentation"><a role="menuitem" tabindex="-1" href="#" onclick=\'$("#modal-newproj").modal("show");\'>Add New Project</a></li>');
    //
    //        $(function () {
    //            $('#tree').liHarmonica({
    //                onlyOne: false,
    //                speed: 100
    //            });
    //        });
    //        openedNodesList().forEach(function(entry) {
    //            $("#" + entry).children('a').addClass('harOpen');
    //            $("#" + entry).children('ul').addClass('opened');
    //        });
    //    }
    //});
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