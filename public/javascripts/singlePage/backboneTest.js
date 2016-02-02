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
        jquery: '../utils/jquery-2.2.0',
        backbone: '../utils/backbone',
        underscore: '../utils/underscore',
        bp: '../../bootstrap/javascripts/bootstrap.min'
    },
    shim: {
        'bp': {
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

require(['jquery', "appState", "user", "controller", "family", "backbone", "bp"],
    function ($, AppState, User, Controller, Family, Backbone) {

        var appState = new AppState();

        var user = new User();

        var controller = new Controller(); // Создаём контроллер
        controller.initializeWithAppState(appState);

        var MyFamily = new Family([ // Моя семья
            {Name: "Саша" },
            {Name: "Юля" },
            {Name: "Елизар" }
        ]);

        var Block = Backbone.View.extend({
            el: $("#block"), // DOM элемент widget'а

            templates: { // Шаблоны на разное состояние
                "start": _.template($('#start').html()),
                "success": _.template($('#success').html()),
                "error": _.template($('#error').html()),
                "userInfo": _.template($('#userInfo').html())
            },

            events: {
                "click input:button": "check" // Обработчик клика на кнопке "Проверить"
            },

            initialize: function () { // Подписка на событие модели
                this.listenTo(this.model, 'change', this.render);
            },

            check: function () {
                var username = $(this.el).find("input:text").val();
                var find = MyFamily.checkUser(username); // Проверка имени пользователя
                appState.set({ // Сохранение имени пользователя и состояния
                    "state": find ? "success" : "error",
                    "username": username
                });
            },

            userInfo: undefined,

            render: function () {
                var state = this.model.get("state");
                $(this.el).html(this.templates[state](this.model.toJSON()));
                if (state == "userInfo") {
                    if (this.userInfo == undefined) {
                        this.userInfo = new UserInfo({el: this.$(".userInfo"), model: user});
                    } else {
                        this.userInfo.update(this.$(".userInfo"));
                        console.log("Now it's all OK")
                    }
                    user.trigger("change");
                }
                return this;
            }
        });

        var block = new Block({ model: appState });

        appState.trigger("change");

        appState.bind("change:state", function () { // подписка на смену состояния для контроллера
            var state = this.get("state");
            if (state == "start")
                controller.navigate("!/", false); // false потому, что нам не надо
                                                  // вызывать обработчик у Router
            else
                controller.navigate("!/" + state, false);
        });

        var UserInfo = Backbone.View.extend({
            templates: { // Шаблоны на разное состояние
                "userInfo": _.template($('#userInfoInner').html())
            },

            initialize: function () { // Подписка на событие модели
                this.model.bind('change', this.render, this);
            },

            render: function () {
                $(this.el).html(this.templates["userInfo"](this.model.toJSON()));
                return this;
            },

            update: function (newEl) {
                this.el = newEl;
            }
        });

        Backbone.history.start();  // Запускаем HTML5 History push
        console.log("start");
});