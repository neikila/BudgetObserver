/**
    * Created by neikila.
    */
requirejs.config({
    baseUrl: "/assets/javascripts/singlePage/",
    paths: {
        appState: 'AppState',
        user: 'User',
        controller: 'Controller',
        container: 'Container',
        error: 'Error',
        errorView: 'ErrorView',
        login: 'Login',
        signup: 'Signup',
        navbarRight: "NavbarRight",
        navbarLeft: "NavbarLeft",
        groupModel: "purchasesPage/GroupModel",
        purchaseModel: "purchasesPage/PurchaseModel",
        purchasesCollection: "purchasesPage/PurchasesCollection",
        purchasesView: "purchasesPage/PurchasesView",
        pieChartView: "purchasesPage/PieChartView",
        utils: "Utils",
        jquery: '../utils/jquery-2.2.0.min',
        chart: '../utils/Chart',
        backbone: '../utils/backbone',
        underscore: '../utils/underscore',
        bs: '../../bootstrap/javascripts/bootstrap.min'
    },
    shim: {
        'bs': {
            deps: ['jquery']
        },
        'chart': {
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

require(['jquery', "appState", "user", "controller", "container", "navbarRight", "navbarLeft", "backbone", "bs"],
    function ($, AppState, User, Controller, Container, NavbarRight, NavbarLeft, Backbone) {

        var user = new User();

        var appState = new AppState();
        appState.setListeners(user);

        user.getFromServer(appState);

        var controller = new Controller(); // Создаём контроллер
        controller.initializeWithAppState(appState);

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