$(document).ready(function () {

    var logo = $(".logo");
    var load = $("#load");
    var home = $("#home");
    logo.click(function () {
        if (load.hasClass('betitle')) {
            home.animate({
                opacity: 1
            }, 600);
            load.animate({
                height: "100%"
            }, 600);
            $("#contain").fadeOut(200, function () {
                $(".show").fadeOut(600, faready);
            });
            load.removeClass('betitle');
        } else {

            $("#contain").fadeIn(200, function () {

                load.animate({
                    height: "13%"
                }, 600);
                $(".show").fadeIn(600, faready);
            });
            home.animate({
                opacity: 0
            }, 1200);
            load.addClass('betitle');
        }
    });



    function faready() {
        var cardH = $("#card").outerHeight();
        var cardW = $("#card").outerWidth();
        var cardh = $("#card").height();
        var cardw = $("#card").width();
        var page = new Boolean(false);

        // page2****************
        var show = $(".show");
        var singleProject = $(".show .single-project");
        var cdTitle = $(".cd-title");

        cdTitle.on('click', function () {
            var selectedProject = $(this).parent();
            if (show.hasClass('project-open')) {
                // $(".content-wrapper").fadeOut(300);
                selectedProject.removeClass('selected').addClass('no-selected');
                show.removeClass('project-open');
            } else {
                //open project
                // setTimeout(function() {
                //     $(".content-wrapper").fadeIn(400);
                // }, 400);
                selectedProject.addClass('selected').removeClass('no-selected');
                show.addClass('project-open');
            }
        });

    }


});