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
        userInfo: 'UserInfo',
        todo: 'Todo',
        block: 'Block',
        navbarRight: "NavbarRight",
        navbarLeft: "NavbarLeft",
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

require(['jquery', "appState", "user", "controller", "family", "userInfo", "todo", "navbarRight", "navbarLeft", "backbone", "bs"],
    function ($, AppState, User, Controller, Family, UserInfo, Todo, NavbarRight, NavbarLeft, Backbone) {

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
        navbarRight.render();
        var navbarLeft = new NavbarLeft({ model: user });
        navbarLeft.init(appState);
        navbarLeft.render();

        var todo = new Todo( {model: appState });

        appState.trigger("change");

        appState.bind("change:state", function () { // подписка на смену состояния для контроллера
            var state = this.get("state");
            if (state == "start")
                controller.navigate("!/", false); // false потому, что нам не надо
                                                  // вызывать обработчик у Router
            else
                controller.navigate("!/" + state, false);
        });

        Backbone.history.start();  // Запускаем HTML5 History push
        console.log("start");
});