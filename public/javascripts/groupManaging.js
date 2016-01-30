/**
 * Created by neikila on 29.01.16.
 */

console.log("test");
navbarController.setPos(positionsEnum.ALL_GROUPS);
var test;

groupManaging = function() {
    var manager = function() {};

    var groupsEl = $(".container-fluid");

    var glyphs = "\
    <div class='col-md-4 col-md-offset-1 button-area'>\
        <div class='btn-group' role='group' aria-label='...'>\
            <button type='button' class='btn btn-info'><span class='glyphicon glyphicon-info-sign'></span></button>\
            <button type='button' class='btn btn-default'><span class='glyphicon glyphicon-pencil'></span></button>\
            <button type='button' class='btn btn-danger'><span class='glyphicon glyphicon-remove'></span></button>\
        </div>\
    </div>";


    manager.init = function () {
        $.ajax({
            type: "GET",
            url: "/usersGroups",
            success: function(json){
                console.log(json);
                test = json;
                var currentSize = $(".row.panel").size();
                json.forEach(function(group) {
                    ++currentSize;
                    var temp = group.dateOfCreating;
                    groupsEl.append("\
                        <div class='row panel'>\
                            <div class='col-md-7 area'>\
                                <div class='col-sm-1 col-xs-1'><span>" + currentSize + "</span></div>\
                                <div class='col-sm-3 col-xs-6'><span>" + group.groupName + "</span></div>\
                                <div class='col-sm-2 col-xs-offset-1 col-xs-5'><span>" + group.author + "</span></div>\
                                <div class='col-sm-4 col-xs-offset-1 col-xs-6'><span>" + temp.substring(0, temp.length - 2)+ "</span></div>\
                            </div>" + glyphs + "\
                        </div>")
                })
            }
        });
    };

    return manager;
}();

//$(".container-fluid").append("\
//    <div class='row panel'>\
//        <div class='col-md-7 area'>\
//            <div class='col-sm-1 col-xs-1'><span>1</span></div>\
//            <div class='col-sm-3 col-xs-6'><span>test</span></div>\
//            <div class='col-sm-2 col-xs-offset-1 col-xs-5'><span>author</span></div>\
//            <div class='col-sm-4 col-xs-offset-1 col-xs-6'><span>2014:12:12 10:11:04</span></div>\
//        </div>\
//        <div class='col-md-4 col-md-offset-1 button-area'>\
//            <div class='btn-group' role='group' aria-label='...'>\
//                <button type='button' class='btn btn-info'><span class='glyphicon glyphicon-info-sign'></span></button>\
//                <button type='button' class='btn btn-default'><span class='glyphicon glyphicon-pencil'></span></button>\
//                <button type='button' class='btn btn-danger'><span class='glyphicon glyphicon-remove'></span></button>\
//            </div>\
//        </div>\
//    </div>\
//");

groupManaging.init();
