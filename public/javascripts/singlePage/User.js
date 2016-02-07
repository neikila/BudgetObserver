/**
    * Created by neikila.
    */
define(["backbone"], function (Backbone) {
    return Backbone.Model.extend({
        defaults: {
            isAuth: false,
            login: "",
            email: "",
            name: "",
            surname: "",
            defaultGroup: "",
            otherGroups: []
        },

        getFromServer: function() {
            var self = this;
            $.ajax({
                type: "GET",
                url: "/userData",
                success: function(json){
                    if (json.isAuth) {
                        self.authWithJson(json);
                        self.set("isAuth", true);
                    }
                }
            });
        },

        authWithJson: function(json) {
            this.set("login", json.user.login);
            this.set("email",  json.user.email);
            this.set("name", json.user.name);
            this.set("surname", json.user.surname);
            this.set("defaultGroup", json.defaultGroup);
            this.set("otherGroups", json.otherGroups);
            this.set("isAuth", true)
        },

        deauth: function() {
            this.set("isAuth", false);
        }
    });
});