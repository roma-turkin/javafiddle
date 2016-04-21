var PROJECTPARAM;
var outerLayout, innerLayout;
var $elClicked;

// EVENTS
//
$(document).ready(function(){
    getCurUser();
    openProjectByHash(PROJECTPARAM);
    runLayouts();
    loadLiHarmonica();
    buildTree();
    loadTabs();
    loadPopups();
    editorSettings();    
    resizeEditor();
    
    $("#main_menu .dropdown").on("mouseover", function() {
        if ($(document).find('.dropdown.open').index() !== -1) {
            $('.dropdown > ul').hide();
            $(document).find('.dropdown.open').removeClass("open");
            $(this).children("ul").show();
            $(this).addClass("open");
        }
        return false;
    });
    
    $("#main_menu .dropdown").on("click", function() {
        $('.dropdown > ul').hide();
        if ($(document).find('.dropdown.open').index() !== -1) {
            $(document).find('.dropdown.open').removeClass("open");
        } else {
            $(this).addClass("open");
            $(this).children("ul").show();
        }
        return false;
    });
});
                        
$(window).resize(function() {
    resizeEditor();
});

$(document).click(function () {
    $('.dropdown > ul').hide();
});

$(document).bind('keydown', function(e) {
    if(e.ctrlKey && (e.which === 83)) {
        e.preventDefault();
        if(e.shiftKey)
            saveAllFiles();
        else
            saveFile();
        return false;
    }
});

window.onbeforeunload = (function() {
    if (isEmpty('modified'))
        return;
    addCurrentFileText();
    return "WARNING! The project has unsaved files. When the session expires, any unsaved changes will be lost!";
});


function resizeEditor() { 
    $("#editor").height($("#block-editor").height());
    $("#console").height($("#console-wrapper").height());
    editor.resize();
    jfconsole.resize();
}


// LOADFUNCTIONS
// 
function runLayouts() {
    outerLayout = $('#container-outer').layout({
        center__paneSelector: ".outer-center", 
	west__paneSelector: ".outer-west",
        center__minWidth: 110,
        west__size: 250,
        west__minSize: 110,
        spacing_open: 5,		
	spacing_closed: 5,
        livePaneResizing: true,
        stateManagement__enabled: true,
        onresize: function() {
            resizeEditor();
            innerLayout.resizeAll;
        }
    });
    innerLayout = $('#container-inner').layout({
        center__paneSelector: ".inner-center", 
	south__paneSelector: ".inner-south",
        center__minHeight: 55,
        south__minSize: 55,
        south__initClosed: true,
        spacing_open: 5,		
	spacing_closed: 5,
        livePaneResizing: true,
        stateManagement__enabled: true,
        onresize: function() {
            resizeEditor();
        }
    });
}

function editorSettings() {
    editor.setTheme("ace/theme/idea");
    editor.getSession().setMode("ace/mode/java");
    editor.setReadOnly(true);
    
    editor.getSession().on("change", function() {
        pushModifiedTab();
    });

    jfconsole.setTheme("ace/theme/idea");
    jfconsole.getSession().setMode("ace/mode/text");
    jfconsole.renderer.setShowGutter(false); 
    jfconsole.renderer.setShowPrintMargin(false);
    jfconsole.setReadOnly(true); 
}

// TABS

function loadTabs() {    
    var opened = getCurrentFileID();
    openedTabs().forEach(function(entry) {
        var file = getFileDataById(entry);
        if (file !== false) {
            var cl = file["type"];
            var name = file["name"];
            var li = addTabToPanel(entry, name, cl);
            if (entry === opened)
                selectTab(li);
            if (isModified(entry))
                $("#" + entry).addClass("modified");
        } else {
            var cl = "notfound";
            var name = "not_found";
            var li = addTabToPanel(entry, name, cl);
            if (entry === opened)
                selectTab(li);
        }
    });
}

function openTabFromTree($el) {
    var id = $el.closest('li').attr('id') + "_tab";
    var name = $el.text();
    var cl = $el.attr('class');
    
    var li;
    if(!isOpened(id))
        li = addTabToPanel(id, name, cl);
    else {
        li = document.getElementById(id);
        li = $("#tabpanel").find(li);
    }
    selectTab(li);
}

function addTabToPanel(id, name, cl) {
    pushOpenedTab(id);
    var li = $('<li id="'+ id +'" class="'+ cl +'" onclick="selectTab($(this))">'+ name +'<div class="close" onclick="closeTab($(this).parent())"></div></li>');
    $("#tabpanel").append(li);
    return li;
}

function selectTab(li) {
    var id = li.attr("id");
    if (openedTabs().indexOf(id) === -1)
        return false;
    
    if (isCurrent(id) && $("#tabpanel").find(".active") === li)
        return false;

    if (openedTabs().indexOf($("#tabpanel").find(".active").attr("id")) > -1)
        addCurrentFileText();
    
    $("#tabpanel").find(".active").removeClass("active");
    li.addClass("active");
    setCurrentFileID(id);
    if (id !== undefined) setCurrentProjectId(id);
    getReadOnly(id);
    getCurrentFileText();
    changeLastUpdateLabel();
    editor.focus();
}

function selectEmptyTab() {
    editor.setValue("");
    editor.setReadOnly(true);  
    $('#latest_update').text("");
    
}

function closeTab(parent) {
    var id = parent.attr("id");
    closeTabInStorage(id);
    parent.remove();
    setCurrentFileID("");
    
    id = getLastOpenedTab();
    if (id !== null) {
        var li = document.getElementById(id);
        li = $("#tabpanel").find(li);
        selectTab(li);
    } else {
        selectEmptyTab();
    }
}

function changeLastUpdateLabel() {
    var time = getCurrentFileTimeStamp();
    
    if (getCurrentFileID() === "about_tab" || getCurrentFileID() === "shortcuts_tab") {
        $('#latest_update').text("Static tab.");
        return;
    }
        
    if (time === "")
        $('#latest_update').text("Last changes: not saved yet.");
    else {
        moment.lang('en');
        var earlier = moment(time);
        $('#latest_update').text("Last changes: " + earlier.from(moment()));
    }
}

function closeTabsFromPackage(id) {
    var $pack = $('#' + id);
    $pack.children("ul").children("li").each(function() {
        var subid = $(this).attr("id");
        if($(this).children("a").hasClass("package"))
            closeTabsFromPackage(subid);
        else 
            closeTab($('#' + subid + '_tab'));
    });
}

function closeAllTabs() {
    var $panel = $("#tabpanel");
    $panel.children("li").each(function() {
       closeTab($(this));
    });
}




function loadLiHarmonica() {
    (function ($) {
        $.fn.liHarmonica = function (params) {
            var p = $.extend({
                currentClass: 'cur',
                onlyOne: false,
                speed: 100
            }, params);
            return this.each(function () {

                var el = $(this).addClass('harmonica'), linkItem = $('ul', el).prev('a');

                el.children(':last').addClass('last');
                $('ul', el).each(function () {
                    $(this).children(':last').addClass('last');
                    el.find('.open').children('ul').addClass('opened');
                });

                $('ul', el).prev('a').addClass('harFull');

                el.find('.' + p.currentClass).parents('ul').show().prev('a').addClass(p.currentClass).addClass('harOpen');

                linkItem.on('click', function () {
                    if ($(this).next('ul').is(':hidden')) {
                        $(this).addClass('harOpen');
                    } else {
                        $(this).removeClass('harOpen');
                    }
                    if (p.onlyOne) {
                        $(this).closest('ul').closest('ul').find('ul').not($(this).next('ul')).slideUp(p.speed).prev('a').removeClass('harOpen');
                        $(this).next('ul').slideToggle(p.speed);
                    } else {
                        $(this).next('ul').stop(true).slideToggle(p.speed);
                    }
                    return false;
                });
            });
        };
    })(jQuery);
}

function loadPopups() {
    $(function () {
        $("#block-tree").on("contextmenu", "div", function(e) {
            $('.dropdown > ul').hide();
            var $contextMenu = $("#treeMenu");
            $("#treeMenu > ul").show();
            $elClicked = $(this);
            $contextMenu.css({
                display: "block",
                left: e.pageX,
                top: e.pageY
            });
           
            return false;           
        });
        $("#tree").on("contextmenu", "a", function(e) {
            $('.dropdown > ul').hide();
            var $contextMenu;
            var classname = $(this).attr("class").split(" ")[0];
            switch(classname) {
            case "root":    
                $contextMenu = $("#projectMenu");
                $("#projectMenu > ul").show();
                break;
            case "sources":
                $contextMenu = $("#srcMenu");
                $("#srcMenu > ul").show();
                break;
            case "package":
                $contextMenu = $("#packageMenu");
                $("#packageMenu > ul").show();
                break;
            case "class": 
            case "interface": 
            case "exception": 
            case "annotation": 
            case "runnable":
            case "enum":
                $contextMenu = $("#fileMenu");
                $("#fileMenu > ul").show();
                break;
            default:
                return;
            }
            
            $elClicked = $(this);
            $contextMenu.css({
                display: "block",
                left: e.pageX,
                top: e.pageY
            });
            return false;
        });
    });
}
 
// TOGGLE
// 

function toggleHeader() {
    var $header = $("#header-top");
    if($header.css("display") === "none")
        $header.css("display", "flex");
    else
        $header.css("display", "none");
    resizeEditor();
    outerLayout.resizeAll();
    innerLayout.resizeAll();
}

function toggleTree() {
    var state = outerLayout.state;
    if (state.west.isSliding)
        outerLayout.open('west');
    else {
        outerLayout.close('west');
        setTimeout(function () {
            outerLayout.slideOpen('west');
        }, 250);
        
    }  
}

function toggleConsole() {
    var state = innerLayout.state;
    if (state.south.isSliding)
        innerLayout.open('south');
    else {
        innerLayout.close('south');
        setTimeout(function () {
            innerLayout.slideOpen('south');
        }, 250);
        
    }
}

// FILE REVISIONS (SERVICES)
//!TODO working with dates
function saveFile(toBeSavedFileTabId) {

    var curFileTabId = (arguments.length === 0) ? getCurrentFileID() : toBeSavedFileTabId; //node_[number]_tab
    var curFileId = curFileTabId.match(/[\d]+/);//id extraction
    var toBeSavedFile = JSON.parse(sessionStorage.getItem("file_" + curFileId));

    //data extraction
    addCurrentFileText();
    var toBeSavedData = getOpenedFileText(curFileTabId);
    toBeSavedFile.data = toBeSavedData;

    console.log("Saving file " + toBeSavedFile.fileId);
    $('#latest_update').text("Saving...");

    $.ajax({
        type: "PUT",
        url: PATH + "/fiddle/files/" + toBeSavedFile.fileId,
        contentType: "application/json",
        'data': JSON.stringify(toBeSavedFile),
        success: function(data) {
            unModifiedTab(curFileTabId);
            sessionStorage.setItem("file_" + toBeSavedFile.fileId, JSON.stringify(toBeSavedFile));
            if (isCurrent(curFileTabId))
                $('#latest_update').text("All changes saved.");
        },
        error: function(jqXHR) {
            if (jqXHR.status === 406)
                $('#latest_update').text("Saving isn't acceptable.");
        }
    });
}


function saveAllFiles() {
    addCurrentFileText();
    
    $('#latest_update').text("Saving...");
    
    modifiedList().forEach(function(entry) {
        saveFile(entry);
    });
    
    $('#latest_update').text("All files saved");
}

function saveProject() {
    saveAllFiles();
    
    $.ajax({
        url: PATH + '/webapi/data/project',
        type:'POST', 
        async: false,
        contentType: "application/json",
        success: function(data) {
            $('#latest_update').text("Project successfully saved to the disk.");
        },
        error: function() {
            $('#latest_update').text("Project hasn't been saved.");
        }
    });
    console.log("saveProject() completed");
}

function openProjectByHash(projecthash) {
    if (!arguments.length === 1 || projecthash === null || projecthash === "")
        return false;
    
    $.ajax({
        url: PATH + '/webapi/data/changeproject',
        type: 'POST',
        async: false,
        data: {projecthash : projecthash},
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            invalidateSession();
            alert("Restored project with hash " + projecthash);
        },
        error: function() {
            alert("Can't restore project with hash " + projecthash);
        }
    });
}

function getFileContent(id) {
    if(arguments.length === 0) {
        id = getCurrentFileID();
    }
    var fileId = id.match(/[\d]+/);

    $.ajax({
        url: PATH + "/fiddle/files/" + fileId,
        type: "GET",
        dataType: "json",
        async: false,
        success: function(data)
        {
            editor.setValue(data.data);
            editor.clearSelection();
            editor.session.getUndoManager().reset();
            editor.setReadOnly(false);
            sessionStorage.setItem("file_" + data.fileId, JSON.stringify(data));
            //!TODO temporary 500 must be redone
            addCurrentFileTimeStamp(500);
            changeModifiedState(id, false);
        },
        error: function(jqXHR) {
            if (jqXHR.status === 406)
                $('#latest_update').text("File not found in project.");
        }
    });
}


// UTILS
//
String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

function getFileDataById(id) {
    var filedata;
    $.ajax({
        url: PATH + '/webapi/tree/filedata',
        type: 'GET',
        data: {id: id},
        async: false,
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            console.log(data);
            filedata = data;
        },
        error: function(jqXHR) {
            if (jqXHR.status === 410) {
                $('#latest_update').text("File not found in project.");
                filedata = false;
            }
        }
    }); 
    
    return filedata;
}


// COMPILATION AND EXECUTING
//
function compile() {
    innerLayout.open('south');
    saveProject();
    
    $.ajax({
        url: PATH + '/webapi/run/compile',
        type: 'POST',
        async: false,
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            poll();
        }
    });  
}

function execute() {
    innerLayout.open('south');
    saveProject();
    
    $.ajax({
        url: PATH + '/webapi/run/execute',
        type: 'POST',
        async: false,
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            poll();
        }
    });  
}

function compileAndRun() {
    innerLayout.open('south');
    compile();
    execute();
   /*
    $.ajax({
        url: PATH + '/webapi/run/compilerun',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: function() {
            poll();
        }
    });  
    */
}

function poll() {
    $.ajax({
        url: "webapi/run/output",
        contentType: "application/json",
        success: function(data){
            var result = 1;
            if (data !== null && $.isArray(data))
                data.forEach(function(entry) {
                    if(entry !== null) {
                        if(entry.localeCompare("#END_OF_STREAM#") === 0) {
                            result = 0;
                            return;
                        }
                        jfconsole.insert(entry + "\n");
                    }
                });
            if (result === 1)
                poll();
        }
    });
}    

function sendInput() {
    var input = $("#stdin").val();
    $.ajax({
        url: PATH + '/webapi/run/send',
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        data: {input: input},
        success: function() {
            $("#stdin").val("");
            jfconsole.insert("> " + entry + "\n");
        }
    }); 
    return false;
}

function openStaticTab(name) {
    var id;
    var nm;
    var cl;
    
    switch(name) {
        case "about":
            id = "about_tab";
            nm = "About";
            cl = "help";
            break;
        case "shortcuts":
            id = "shortcuts_tab";
            nm = "Shortcuts";
            cl = "help";
            break;       
        case "welcome": 
            id = "welcome_tab";
            nm = "Welcome!";
            cl = "help";
            break;       
        default:
            return;
    }

    setReadOnly(id);
    var li;
    if(!isOpened(id))
        li = addTabToPanel(id, nm, cl);
    else {
        li = document.getElementById(id);
        li = $("#tabpanel").find(li);
    }
    selectTab(li);
}

function hashIsCorrect(hash) {
    var result = false;
    $.ajax({
        url: PATH + '/webapi/tree/righthash',
        type: 'GET',
        data: {hash: hash},
        dataType: "json",
        async: false,
        success: function(data) {
            result = data;
        }
    });
    return result;
}

function getCurUser() {

    $.ajax({
        url: "https://localhost:8181/javaFiddle/fiddle/user",
        type: "GET",
        dataType: "json",
        async: false,
        success: function(data)
        {
            $("#header-top-right").prepend("Logged in as " + data.lastName + " " + data.firstName + "&nbsp;|&nbsp;");
            sessionStorage.curUser = JSON.stringify(data);
        }
    });
}
