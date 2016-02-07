/**
 * Created by neikila.
 */
define(["backbone", "utils", "chart", "pieChartView"], function(Backbone, Utils, Chart, PieChartView) {
    return Backbone.View.extend({
        templates: {
            "purchases": _.template($("script.container__purchases").html())
        },

        init: function(user) {
            this.user = user;
            return this;
        },

        events: {
            "click #savePur": "savePurchase"
        },

        savePurchase: function() {
            var self = this;
            Utils.postJSON("/saveJSON", {
                "product": $("#product").val(),
                "amount": Number($("#amount").val()),
                "groupName": self.model.get("groupName")
            }, function(json){
                json["login"] = self.user.get("login");
                self.model.get("purchases").add(json);
                self.model.getGroupPieDataFromServer();
            });
            return false;
        },

        initialize: function () {
            this.listenTo(this.model.get("purchases"), "reset", this.render);
            this.listenTo(this.model.get("purchases"), "add", this.render);
            this.model.getFromServer();
            this.model.getGroupPieDataFromServer();
        },

        drawPieChart: function(pieData, num) {
            // TODO refactor.
            var graph = document.getElementById('graph').getContext('2d');
            var pieOptions = {
                animationSteps: num,
                animationEasing: 'easeInOutQuart'
            };
            if (this.pieChart != null) {
                this.pieChart.destroy();
            }
            this.pieChart = new Chart(graph).Pie(pieData, pieOptions);
        },

        render: function() {
            $(this.el).html(this.templates["purchases"]({
                "purchases": this.model.get("purchases").toJSON(),
                "purchasesGrouped": this.model.get("purchasesGrouped").toJSON(),
                "groupName": this.model.get("groupName")
            }));

            $(document).xpathEvaluate("//ul[@aria-labelledby='product']/li").click(function() {
                $("#product").val($(this).children().text())
            });

            // Закрытие списка при нажатии на таб
            this.$('div.dropdown').keydown(function(e) {
                if (e.keyCode == 9 || e.keyCode == 27)
                    $("div.dropdown-backdrop").click();
            });

            if (this.model.get("purchasesGrouped").length > 0) {
                this.$(".dropdown div").attr("data-toggle", "dropdown");
            }

            new PieChartView({ el: this.$("div.pieholder"), model: this.model.get("purchasesGrouped") }).render();

            //new ErrorView({ model: this.loginError, el: this.$("form div.alert") }).render()
        }
    })
});