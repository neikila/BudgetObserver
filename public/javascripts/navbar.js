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
                        user.init(json.user.login, json.user.email, json.user.name, json.user.surname);
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
    };
    user.toDefault();

    user.init = function(loginPar, emailPar, namePar, surnamePar) {
        console.log("length = " + arguments.length);
        user._isInitialised = true;
        if(arguments.length != 0) {
            user.login = loginPar;
            user.email = emailPar;
            user.username = namePar;
            console.log("name: " + namePar + " " + user.username);
            user.surname = surnamePar;
            user.auth();
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
            navbarController.updateRight();
    };

    user.toJSON = function () {
        return JSON.stringify({
            "email": user.email,
            "login": user.login,
            "username": user.username,
            "surname": user.surname,
            "isAuthorised": user._isAuthorised,
            "isInit": user._isInitialised
        })
    };

    user.parse = function(str) {
        userJSON = JSON.parse(str);
        user.login = userJSON.login;
        user.email = userJSON.email;
        user.username = userJSON.username;
        user.surname = userJSON.surname;
        user._isAuthorised = userJSON.isAuthorised;
        user._isInitialised= userJSON.isInitialised;
    };

    user.deauth = function(shouldUpdate) {
        user.toDefault();
        sessionStorage.removeItem("user");
        // TODO some action
        if (!shouldUpdate)
            navbarController.updateRight();
    };

    return user;
};

var navbarController = function() {
    function controller() {}

    var isAuth = false;

    controller.updateRight = function() {
        console.log("updateRight + isAuth: " + user.isAuth());
        var navbarRight = $("ul.navbar-right");
        if (user.isAuth() != isAuth) {
            navbarRight.empty();
            console.log("empty");
            isAuth = user.isAuth();
            if (isAuth) {
                navbarRight.append('<p class="navbar-text">Signed in as <a href="#" class="navbar-link">' + user.username +
                    '</a></p><li><a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>');
                $("ul.navbar-right li a").click(user.deauth);
            } else {
                navbarRight.append('<li><a href="/login"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>' +
                    '<li><a href="/signup"><span class="glyphicon glyphicon-user"></span> Signup</a></li>');
            }
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
navbarController.updateRight();
