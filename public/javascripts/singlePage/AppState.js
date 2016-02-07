/**
    * Created by neikila.
    */
define(["backbone"], function(Backbone) {
    return Backbone.Model.extend({
        defaults: {
            state: "start",
            username: "",
            groupToShow: ""
        },

        setListeners: function(user) {
            var self = this;
            this.listenTo(user, "change:isAuth", function() {
                if (user.get("isAuth")) {
                    self.set("groupToShow", user.get("defaultGroup"))
                } else {
                    self.set("groupToShow", "")
                }
            });
        }
    });
});