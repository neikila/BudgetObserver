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

require(['jquery', "appState", "user", "controller", "family", "userInfo", "block", "todo", "navbarRight","backbone", "bs"],
    function ($, AppState, User, Controller, Family, UserInfo, Block, Todo, NavbarRight, Backbone) {

        var user = new User();
        user.getFromServer();

        var appState = new AppState();

        var controller = new Controller(); // Создаём контроллер
        controller.initializeWithAppState(appState);

        var MyFamily = new Family([ // Моя семья
            {Name: "Саша" },
            {Name: "Юля" },
            {Name: "Елизар" }
        ]);

        var block = new Block({ model: appState });
        block.init(user, MyFamily);

        var navbarRight = new NavbarRight({ model: user });
        navbarRight.render();

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