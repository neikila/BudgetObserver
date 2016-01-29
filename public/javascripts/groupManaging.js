/**
 * Created by neikila on 29.01.16.
 */

console.log("test");
navbarController.setPos(positionsEnum.ALL_GROUPS);

$.ajax({
    type: "GET",
    url: "/usersGroups",
    success: function(json){
        console.log(json);
    }
});
