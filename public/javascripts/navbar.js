/**
 * Created by neikila on 10.01.16.
 */
var userConstructor = function() {
    var user = {};

    user._isInitialised = false;
    user.login = undefined;
    user.email = undefined;
    user.name = undefined;
    user.surname = undefined;

    function init(login, email, name, surname) {
        user._isInitialised = true;
        if(arguments.length != 0) {
            user.login = login;
            user.email = email;
            user.name = name;
            user.surname = surname;
            user.auth();
        }
    }

    user._isAuthorised = false;
    user.isAuth = function() {
        return user._isAuthorised
    };

    user.auth = function () {
        user._isAuthorised = true;
        // TODO some action
        navbarController.invalidate();
    };

    user.deauth = function() {
        user._isAuthorised = false;
        // TODO some action
        navbarController.invalidate();
    };

    return user;
};

var user = userConstructor();

var navbarController = function() {
    function controller() {}

    controller.auth = function() {
        if(sessionStorage.getItem("isAuth") == undefined) {
            console.log("Request to get userData");
            $.ajax({
                type: "GET",
                url: "/userData",
                data: {},
                success: function(json){
                    console.log(json);
                    if (json.isAuth) {
                        sessionStorage.setItem("isAuth", true);
                        sessionStorage.setItem("login", json.user.login);
                        sessionStorage.setItem("name", json.user.name);
                        controller.updateRight();
                    } else {
                        sessionStorage.setItem("isAuth", false);
                    }
                }
            });
        }
    };

    controller.updateRight = function() {
        var navbarRight = $("ul.navbar-right");
        var isAuth = sessionStorage.getItem("isAuth") == "true";
        if (isAuth) {
            sessionStorage.getItem("login");
            navbarRight.empty();
            navbarRight.append('<p class="navbar-text">Signed in as <a href="#" class="navbar-link">' + sessionStorage.getItem("name") +
                '</a></p><li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>');
            $("ul.navbar-right li a").click(controller.invalidate());
        } else {
            navbarRight.empty();
            navbarRight.append('<li><a href="/login"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>' +
                '<li><a href="/signup"><span class="glyphicon glyphicon-user"></span> Signup</a></li>');
            controller.auth();
        }
    };

    controller.invalidate = function() {
        sessionStorage.removeItem("isAuth");
        sessionStorage.removeItem("login");
        sessionStorage.removeItem("name");
    };

    return controller
}();

navbarController.updateRight();
