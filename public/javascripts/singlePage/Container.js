/**
 * Created by neikila on 05.02.16.
 */
define(["backbone", "login"], function(Backbone, Login) {
    return Backbone.View.extend({
        el: $(".container.content-bb"),

        templates: {
            "start": _.template($("script.container__todo").html()),
            "login": _.template("<div class='container__login'></div>")
        },

        init: function(user) {
            this.user = user;
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change:state', this.render);
            this.renderView = {
                "start": this.nothing,
                "login": this.login
            }
        },

        nothing: function() {},

        login: function(self) {
            new Login({ model: self.model, el: self.$("div.container__login") }).init(self.user).render();
        },

        signup: function(self) {

        },

        render: function() {
            var state = this.model.get("state");
            $(this.el).html(this.templates[state]());
            this.renderView[state](this);
        }
    })
});