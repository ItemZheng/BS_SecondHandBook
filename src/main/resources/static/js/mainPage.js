var books;
var user;

jQuery(document).ready(function( $ ) {
    // Preloader
    $(window).on('load', function() {
        $('#preloader').delay(100).fadeOut('slow',function(){$(this).remove();});
    });

    // Hero rotating texts
    $("#hero .rotating").Morphext({
        animation: "flipInX",
        separator: ",",
        speed: 3000
    });

    // Initiate the wowjs
    new WOW().init();

    // Initiate superfish on nav menu
    $('.nav-menu').superfish({
        animation: {opacity:'show'},
        speed: 400
    });

    // Mobile Navigation
    if( $('#nav-menu-container').length ) {
        var $mobile_nav = $('#nav-menu-container').clone().prop({ id: 'mobile-nav'});
        $mobile_nav.find('> ul').attr({ 'class' : '', 'id' : '' });
        $('body').append( $mobile_nav );
        $('body').prepend( '<button type="button" id="mobile-nav-toggle"><i class="fa fa-bars"></i></button>' );
        $('body').append( '<div id="mobile-body-overly"></div>' );
        $('#mobile-nav').find('.menu-has-children').prepend('<i class="fa fa-chevron-down"></i>');

        $(document).on('click', '.menu-has-children i', function(e){
            $(this).next().toggleClass('menu-item-active');
            $(this).nextAll('ul').eq(0).slideToggle();
            $(this).toggleClass("fa-chevron-up fa-chevron-down");
        });

        $(document).on('click', '#mobile-nav-toggle', function(e){
            $('body').toggleClass('mobile-nav-active');
            $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
            $('#mobile-body-overly').toggle();
        });

        $(document).click(function (e) {
            var container = $("#mobile-nav, #mobile-nav-toggle");
            if (!container.is(e.target) && container.has(e.target).length === 0) {
                if ( $('body').hasClass('mobile-nav-active') ) {
                    $('body').removeClass('mobile-nav-active');
                    $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
                    $('#mobile-body-overly').fadeOut();
                }
            }
        });
    } else if ( $("#mobile-nav, #mobile-nav-toggle").length ) {
        $("#mobile-nav, #mobile-nav-toggle").hide();
    }

    // Stick the header at top on scroll
    $("#header").sticky({topSpacing:0, zIndex: '50'});

    // Smoth scroll on page hash links
    $('a[href*="#"]:not([href="#"])').on('click', function() {
        if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
            var target = $(this.hash);
            if (target.length) {

                var top_space = 0;

                if( $('#header').length ) {
                    top_space = $('#header').outerHeight();
                }

                $('html, body').animate({
                    scrollTop: target.offset().top - top_space
                }, 1500, 'easeInOutExpo');

                if ( $(this).parents('.nav-menu').length ) {
                    $('.nav-menu .menu-active').removeClass('menu-active');
                    $(this).closest('li').addClass('menu-active');
                }

                if ( $('body').hasClass('mobile-nav-active') ) {
                    $('body').removeClass('mobile-nav-active');
                    $('#mobile-nav-toggle i').toggleClass('fa-times fa-bars');
                    $('#mobile-body-overly').fadeOut();
                }

                return false;
            }
        }
    });

    // Back to top button
    $(window).scroll(function() {

        if ($(this).scrollTop() > 100) {
            $('.back-to-top').fadeIn('slow');
        } else {
            $('.back-to-top').fadeOut('slow');
        }

    });

    $('.back-to-top').click(function(){
        $('html, body').animate({scrollTop : 0},1500, 'easeInOutExpo');
        return false;
    });

    $('#type_select').selectpicker({});

    $('#aim_select').selectpicker({});

    $('#order_select').selectpicker({});

    $('#search_submit').click(function (){
        searchBooks();
    });

    getUserDetail();
    // search book
    searchBooks();
});

function getUserDetail() {
    $.post("/user/info", {},
        function(data, status){
            console.log(data, status);
            if(status !== "success"){
                console.log('服务器无法访问');
                return;
            }
            if(data.code !== 0){
                alert(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
            } else {
                user = data.data;
                $('#welcome_info').text('欢迎，' + user.name);
            }
        }
    );
}

function searchBooks() {
    var array_type = ["", "经济金融类", "教育考试类", "计算机与网络类", "语言学习类", "管理类",
        "医学卫生类", "科技工程类", "少儿类", "文学小说类", "文化历史类",
        "法律类", "建筑类", "新闻传播类", "家庭育儿类", "艺术类",
        "生活时尚类", "旅游地理类", "心理类", "宗教哲学类",
        "社会科学类", "自然科学类", "政治军事类", "其他类"];
    var key = $('#search_key').val();
    var type = $('#type_select').val();
    var index = parseInt(type);
    var aim = parseInt($('#aim_select').val());
    var choose = parseInt($('#order_select').val());

    var order = 0;
    var ase = false;
    if(choose === 0){
        order = 0;
        ase = false;
    } else if(choose === 1){
        order = 0;
        ase = true;
    } else if(choose === 2){
        order = 1;
        ase = false;
    } else if(choose === 3){
        order = 1;
        ase = true;
    }

    $.post("/book/queryBooks",
        {
            type: array_type[index],
            order: order,
            keyword: key,
            aim: aim,
            asc: ase
        },
        function(data, status){
            console.log(data, status);
            if(status !== "success"){
                $('#errormessage').text('服务器无法访问').show();
                return;
            }
            if(data.code !== 0){
                $('#errormessage').text(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1)).show();
            } else {
                $('#errormessage').display = false;
                books = data.data;
                console.log(books);
                updateBooks();
            }
        }
    );
}

function updateBooks() {
    var str = "";
    if(books !== undefined){
        for (i = 0; i < books.length; i++) {
            var title = books[i].title;
            if(title.length >= 15){
                title = title.slice(0, 14);
                title = title + '...';
            }

            str = str + ' <div class="col-md-3">\n' +
                '                <b style="position: center;">';
            str = str + title + '</b> <p> <b>RMB ';
            str = str + books[i].sellPrice + '</b> <s>' + books[i].oriPrice + '</s></p>';
            str = str + '<a class="portfolio-item" style="background-image: url(';
            str = str + books[i].oriImg + ');" href="/fe/bookDetail?id=' + books[i].id + '">';
            str = str + "<div class=\"details\">\n" +
                "                <h4>" + books[i].author +"</h4><span>" + books[i].publisher+ "</span>\n" +
                "            </div>\n" +
                "            </a>\n" +
                "\n" +
                "            </div>";
        }
    }

    $('#allBooks').html(str).show();
}