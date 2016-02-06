/**
 * Created by neikila on 01.02.16.
 */
requirejs.config({
    baseUrl: "/assets/javascripts/singlePage/",
    paths: {
        appState: 'AppState',
        user: 'User',
        controller: 'Controller',
        userNameModel: 'UserNameModel',
        family: 'Family',
        container: 'Container',
        error: 'Error',
        navbarRight: "NavbarRight",
        navbarLeft: "NavbarLeft",
        utils: "Utils",
        jquery: '../utils/jquery-2.2.0.min',
        backbone: '../utils/backbone',
        underscore: '../utils/underscore',
        bs: '../../bootstrap/javascripts/bootstrap.min'
    },
    shim: {
        'bs': {
            deps: ['jquery']
        },
        'underscore': {
            exports: '_'
        },
        'backbone': {
            deps: ['jquery', 'underscore'],
            exports: 'Backbone'
        }
    }
});

require(['jquery', "appState", "user", "controller", "family", "container", "navbarRight", "navbarLeft", "backbone", "bs"],
    function ($, AppState, User, Controller, Family, Container, NavbarRight, NavbarLeft, Backbone) {

        var user = new User();

        var appState = new AppState();
        user.getFromServer(appState);

        var controller = new Controller(); // Создаём контроллер
        controller.initializeWithAppState(appState);

        var MyFamily = new Family([ // Моя семья
            {Name: "Саша" },
            {Name: "Юля" },
            {Name: "Елизар" }
        ]);

        var navbarRight = new NavbarRight({ model: user });
        navbarRight.init(appState);
        navbarRight.render();
        var navbarLeft = new NavbarLeft({ model: user });
        navbarLeft.init(appState);
        navbarLeft.render();

        var container = new Container( {model: appState });
        container.init(user);
        container.render();

        appState.bind("change:state", function () { // подписка на смену состояния для контроллера
            var state = appState.get("state");
            if (state == "start")
                controller.navigate("!/", true); // false потому, что нам не надо
                                                  // вызывать обработчик у Router
            else
                controller.navigate("!/" + state, true);
        });

        Backbone.history.start();  // Запускаем HTML5 History push
        console.log("start");
});