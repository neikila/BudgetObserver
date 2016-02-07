/**
    * Created by neikila.
    */
define(["backbone"], function(Backbone) {
    return Backbone.Router.extend({
        routes: {
            "": "start", // Пустой hash-тэг
            "!/": "start", // Начальная страница
            "!/login": "login",
            "!/signup": "signup",
            "!/purchases": "purchases" // Показ группы
        },

        initializeWithAppState: function(appStateExternal) {
            this.appState = appStateExternal;
        },

        start: function () {
            this.appState.set({state: "start"});
        },

        login: function() {
            this.appState.set({state: "login"})
        },

        signup: function() {
            this.appState.set({state: "signup"})
        },

        purchases: function() {
            this.appState.set({state: "purchases"})
        }
    });
});