/**
 * Created by neikila on 03.02.16.
 */
define(["jquery", "backbone", "underscore", "userInfo"], function($, Backbone, _, UserInfo) {
    return Backbone.View.extend({
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
            var find = this.MyFamily.checkUser(username); // Проверка имени пользователя
            this.model.set({ // Сохранение имени пользователя и состояния
                "state": find ? "success" : "error",
                "username": username
            });
        },

        init: function(user, myFamily) {
            this.user = user;
            this.MyFamily = myFamily;
        },

        userInfo: undefined,

        render: function () {
            var state = this.model.get("state");
            $(this.el).html(this.templates[state](this.model.toJSON()));
            if (state == "userInfo") {
                if (this.userInfo == undefined) {
                    this.userInfo = new UserInfo({el: this.$(".userInfo"), model: this.user});
                } else {
                    this.userInfo.update(this.$(".userInfo"));
                    console.log("Now it's all OK")
                }
                this.user.trigger("change");
            }
            return this;
        }
    });
});