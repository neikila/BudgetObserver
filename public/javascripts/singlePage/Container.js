/**
 * Created by neikila on 05.02.16.
 */
define(["backbone", "login", "signup", "purchasesView", "groupModel"],
    function(Backbone, Login, Signup, PurchasesView, GroupModel) {
    return Backbone.View.extend({
        el: $(".container.content-bb"),

        templates: {
            "start": _.template($("script.container__todo").html()),
            "login": _.template("<div class='container__login'></div>"),
            "signup": _.template("<div class='container__signup'></div>"),
            "purchases": _.template("<div class='container__purchases'></div>")
        },

        init: function(user) {
            this.user = user;
        },

        initialize: function () { // Подписка на событие модели
            this.listenTo(this.model, 'change:state', this.render);
            this.renderView = {
                "start": this.nothing,
                "login": this.login,
                "signup": this.signup,
                "purchases": this.purchases
            }
        },

        nothing: function() {},

        login: function(self) {
            new Login({ model: self.model, el: self.$("div.container__login") }).init(self.user).render();
        },

        signup: function(self) {
            new Signup({ model: self.model, el: self.$("div.container__signup") }).init(self.user).render();
        },

        purchases: function(self) {
            this.groupModel = new GroupModel({ groupName: self.model.get("groupToShow") }).setListeners(self.model);
            new PurchasesView({ model: this.groupModel, el: self.$("div.container__purchases") }).init(self.user).render();
        },

        render: function() {
            var state = this.model.get("state");
            console.log("state: " + state);
            $(this.el).html(this.templates[state]());
            this.renderView[state](this);
        }
    })
});