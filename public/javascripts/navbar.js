/**
 * Created by neikila on 10.01.16.
 */
var userConstructor = function() {
    function user(){
        if(!user.isInit()) {
            console.log("Request to get userData");
            $.ajax({
                type: "GET",
                url: "/userData",
                data: {},
                success: function(json){
                    console.log(json);
                    if (json.isAuth) {
                        user.init(json.user.login, json.user.email, json.user.name, json.user.surname, json.defaultGroup, json.otherGroups);
                    } else {
                        user.init();
                    }
                }
            });
        }
    }

    user._isInitialised = false;
    user.toDefault = function() {
        user._isAuthorised = false;
        user.login = undefined;
        user.email = undefined;
        user.username = undefined;
        user.surname = undefined;
        user.defaultGroup = undefined;
        user.otherGroups = undefined;
    };
    user.toDefault();

    user.init = function(loginPar, emailPar, namePar, surnamePar, defaultGroupPar, otherGroupsPar) {
        user._isInitialised = true;
        if(arguments.length != 0) {
            console.log("otherGroups: ");
            console.log(otherGroupsPar);
            console.log("defGroup: ");
            console.log(defaultGroupPar);
            user.login = loginPar;
            user.email = emailPar;
            user.username = namePar;
            user.surname = surnamePar;
            user.defaultGroup = defaultGroupPar;
            user.otherGroups = otherGroupsPar;
            user.auth(true);
        }
        user.save();
    };

    user.isInit = function() {
        return user._isInitialised;
    };

    user.isAuth = function() {
        return user._isAuthorised
    };

    user.save = function() {
        console.log("saved");
        sessionStorage.user = user.toJSON();
    };

    user.auth = function (shouldUpdate) {
        console.log("auth");
        user._isAuthorised = true;
        console.log(user.isAuth());
        // TODO some action
        if (shouldUpdate)
            navbarController.update();
    };

    user.toJSON = function () {
        return JSON.stringify({
            "email": user.email,
            "login": user.login,
            "username": user.username,
            "surname": user.surname,
            "isAuthorised": user._isAuthorised,
            "isInit": user._isInitialised,
            "defaultGroup": user.defaultGroup,
            "otherGroups": user.otherGroups
        })
    };

    user.parse = function(str) {
        userJSON = JSON.parse(str);
        user.login = userJSON.login;
        user.email = userJSON.email;
        user.username = userJSON.username;
        user.surname = userJSON.surname;
        user._isAuthorised = userJSON.isAuthorised;
        user._isInitialised = userJSON.isInitialised;
        user.defaultGroup = userJSON.defaultGroup;
        user.otherGroups = userJSON.otherGroups;
    };

    user.deauth = function(shouldUpdate) {
        user.toDefault();
        sessionStorage.removeItem("user");
        // TODO some action
        if (!shouldUpdate)
            navbarController.update();
    };

    return user;
};

var positionsEnum = {
    START_PAGE: -1,
    HOME: 0,
    ABOUT: 1,
    NEW_BUDGET: 2,
    ALL_GROUPS: 3
};

var navbarController = function() {
    function controller() {}

    var isAuth = false;
    var currentPos = positionsEnum.START_PAGE;
    var dropdownMenu = $("ul.nav ul.dropdown-menu");
    var pageChanged = true;

    controller.setPos = function(pos) {
        if (pos != currentPos) {
            currentPos = pos;
            pageChanged = true;
            controller.update();
        }
    };

    controller.update = function() {
        if (pageChanged) {
            $(".navbar-left li.active").removeClass("active");
            if (currentPos >= 0) {
                $(".navbar-left li").eq(currentPos).addClass("active");
                if (currentPos == positionsEnum.ALL_GROUPS) {
                    $(".navbar-left li").eq(currentPos + 1).addClass("active");
                }
            }
            pageChanged = false;
        }
        if (user.isAuth() != isAuth) {
            isAuth = user.isAuth();
            controller.updateRight();
            controller.reloadDropDown();
        }
    };

    function appendDropdownMenuItem(groupName) {
        dropdownMenu.append('<li><a href="/purchases">' + groupName +'</a></li>');
        dropdownMenu.children().last().click(function(groupNameIn) {
            var localGroupName = groupNameIn;
            return function() {
                sessionStorage.setItem("groupToShow", localGroupName);
            }
        }(groupName))
    }

    controller.reloadDropDown = function() {
        dropdownMenu.empty();
        if (isAuth) {
            appendDropdownMenuItem(user.defaultGroup);
            if (user.otherGroups.length > 0) {
                dropdownMenu.append('<li role="separator" class="divider"></li>');
                dropdownMenu.append('<li class="dropdown-header">Other budgets</li>');
                user.otherGroups.forEach(function(group) {
                    appendDropdownMenuItem(group);
                });
            }
        } else {
            dropdownMenu.append('<li><a href="#">Example budget</a></li>');
        }
    };

    controller.updateRight = function() {
        var navbarRight = $("ul.navbar-right");
        navbarRight.empty();
        if (isAuth) {
            navbarRight.append('<p class="navbar-text">Signed in as <a href="#" class="navbar-link">' + user.username +
                '</a></p><li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>');
            $("ul.navbar-right li a").click(user.deauth);
        } else {
            navbarRight.append('<li><a href="/login"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>' +
                '<li><a href="/signup"><span class="glyphicon glyphicon-user"></span> Signup</a></li>');
        }
    };

    return controller
}();

var user = userConstructor();
if (sessionStorage.user) {
    user.parse(sessionStorage.user);
    console.log("parsed");
    console.log("user: " + user.toJSON())
} else {
    user();
}
navbarController.update();
