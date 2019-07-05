var book;
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

    args = GetRequest();
    var id = args.id;
    if(id === undefined){
        alert("参数不正确");
        self.location = document.referrer;
    }
    getUserDetail();
    updateBookDetail(id);

    $('#buy_book').click(function(){
        if(book.aim !== 0){
            alert("该书为求购书籍，不能购买");
            return;
        }
        window.location.href = "/fe/addOrder?bookId=" + book.id;
    });

    $('#chat_book').click(function(){
        window.location.href = "/fe/messageBox?id=" + user.id + "&uid=" + book.userId;
    });

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

function updateBookDetail(id) {
    $.post("/book/queryById", {
        id: id
        },
        function(data, status){
            console.log(data, status);
            if(status !== "success"){
                alert('服务器无法访问');
                self.location = document.referrer;
            }
            if(data.code !== 0){
                alert(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
            } else {
                book = data.data;
                // title
                if(book.aim === 0){
                    $('#book_name').text("出售：" + book.title);
                } else {
                    $('#book_name').text("求购：" + book.title);
                }

                $('#book_ori_img').attr("src", book.oriImg);
                $('#book_current_img').attr("src", "/image/" + book.currentImg);
                $('#author').text("作者：" + book.author);
                summary = book.summary;
                if(summary.length > 500){
                    summary = summary.slice(0, 499);
                }
                $('#summary').text(summary);

                var basic_info = "";
                basic_info = basic_info + "书籍类别：" + book.bookType + "<br>";
                basic_info = basic_info + "ISBN：" + book.isbn + "<br>";
                basic_info = basic_info + "出版社：" + book.publisher + "<br>";
                basic_info = basic_info + "豆瓣评分：" + book.rating + "<br>";
                basic_info = basic_info + "发布时间：" + book.createTime;
                $('#basic_info').html(basic_info);

                var price_info = "";
                if(book.aim === 0){
                    price_info = price_info + "售价：" + book.sellPrice + "<br>";
                } else {
                    price_info = price_info + "求购价：" + book.sellPrice + "<br>";
                }
                price_info = price_info + "新书参考价：" + book.oriPrice;
                $('#book_price').html(price_info);
                $('#owner_description').text(book.description);

                $.post("/user/queryById", {
                    id: book.userId
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
                            userInfo = data.data;
                            var user_info = "";
                            user_info = user_info + "昵称：" + userInfo.name + "<br>";
                            user_info = user_info + "邮箱：" + userInfo.email + "<br>";
                            user_info = user_info + "QQ：" + userInfo.qq + "<br>";
                            user_info = user_info + "微信：" + userInfo.wx + "<br>";
                            user_info = user_info + "手机：" + userInfo.phone + "<br>";
                            $('#owner_info').html(user_info);
                        }
                    }
                );

                if(book.aim === 1){
                    $('#buy_book').style.display = "none";
                }
            }
        }
    );
}