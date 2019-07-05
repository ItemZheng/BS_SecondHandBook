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

    $('#type_way').selectpicker({});

    $('#search_submit').click(function (){
        searchBooks();
    });

    args = GetRequest();
    var id = args.bookId;
    if(id === undefined){
        alert("参数不正确");
        self.location = document.referrer;
    }

    setBookName(id);
    getUserDetail();

    $("#type_way").change(function(){
        var value = $("#type_way").val();
        if(parseInt(value) === 0){
            var divset = document.getElementsByClassName("mail_way_need");
            for (var i = 0; i<divset.length;i++) {
                divset[i].style.display="block";
            }
        } else {
            var divset = document.getElementsByClassName("mail_way_need");
            for (var i = 0; i<divset.length;i++) {
                divset[i].style.display = "none";
            }
        }
    });

    $("#buy_submit").click(function(){
        $.post("/order/create", {
            bookId: id,
            type: parseInt($("#type_way").val()),
            address: $("#order_address").val()
            },
            function(data, status){
                console.log(data, status);
                if(status !== "success"){
                    console.log('服务器无法访问');
                    return;
                }
                if(data.code !== 0){
                    alert(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
                } else {
                    alert("订单创建成功");
                    window.location.href = "/fe/main";
                }
            }
        );
    });
});

function GetRequest() {
    var url = location.search; //获取url中"?"符后的字串
    var theRequest = {};
    if (url.indexOf("?") !== -1) {
        var str = url.substr(1);
        strs = str.split("&");
        console.log(strs);
        for(var i = 0; i < strs.length; i ++) {
            theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}

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

function setBookName(id) {
    $.post("/book/queryById", {
        id: id
        },
        function(data, status){
            console.log(data, status);
            if(status !== "success"){
                console.log('服务器无法访问');
                return;
            }
            if(data.code !== 0){
                console.log(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
            } else {
                book = data.data;
                $('#buy_title').html("购买：" + book.title + " <br> 售价：" + book.sellPrice);
            }
        }
    );
}