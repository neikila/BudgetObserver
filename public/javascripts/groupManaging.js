/**
 * Created by neikila on 29.01.16.
 */

console.log("test");
navbarController.setPos(positionsEnum.ALL_GROUPS);
var test;

groupManaging = function() {
    var manager = function() {};

    var groupsEl = $("tbody");

    var glyphs = "<td>" +
        "<span class='glyphicon glyphicon-info-sign'></span>" +
        "<span class='glyphicon glyphicon-pencil'></span>" +
        "<span class='glyphicon glyphicon-remove'></span>" +
        "</td>";


    manager.init = function () {
        $.ajax({
            type: "GET",
            url: "/usersGroups",
            success: function(json){
                console.log(json);
                test = json;
                var currentSize = $("tbody tr").size();
                json.forEach(function(group) {
                    ++currentSize;
                    var temp = group.dateOfCreating;
                    groupsEl.append("<tr>" +
                        "<th scope='row'>" + currentSize + "</th>" +
                        "<td>" + group.groupName + "</td>" +
                        "<td>" + group.author + "</td>" +
                        "<td>" + temp.substring(0, temp.length - 2)+ "</td>" +
                        glyphs + "</tr>")
                })
            }
        });
    };

    return manager;
}();

groupManaging.init();
