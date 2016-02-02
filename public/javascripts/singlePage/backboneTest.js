/**
 * Created by neikila on 01.02.16.
 */
$(function () {

    var AppState = Backbone.Model.extend({
        defaults: {
            state: "start",
            username: "",
            isAuth: false,
            user: "asd",
            email: "test",
            name: "testName",
            defaultGroup: "first",
            groups: ["first", "second"]
        }
    });

    var appState = new AppState();

    var User = Backbone.Model.extend({
        defaults: {
            isAuth: false,
            user: "asd",
            email: "test",
            name: "testName",
            defaultGroup: "first",
            groups: ["first", "second"]
        }
    });

    var user = new User();

    var Controller = Backbone.Router.extend({
        routes: {
            "": "start", // Пустой hash-тэг
            "!/": "start", // Начальная страница
            "!/success": "success", // Блок удачи
            "!/error": "error", // Блок ошибки
            "!/userInfo": "userInfo" // Блок ошибки
        },

        start: function () {
            appState.set({ state: "start" });
        },

        success: function () {
            appState.set({ state: "success" });
        },

        error: function () {
            appState.set({ state: "error" });
        },

        userInfo: function () {
            appState.set({ state: "userInfo" });
        }
    });

    var controller = new Controller(); // Создаём контроллер

    var UserNameModel = Backbone.Model.extend({ // Модель пользователя
        defaults: {
            "Name": ""
        }
    });

    var Family = Backbone.Collection.extend({ // Коллекция пользователей
        model: UserNameModel,

        checkUser: function (username) { // Проверка пользователя
            var findResult = this.find(function (user) { return user.get("Name") == username })
            return findResult != null;
        }

    });

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