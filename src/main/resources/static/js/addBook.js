var book = {
    description: "",
    isbn: "",
    bookType: "经济金融类",
    sellPrice: 0,
    currentImg: "unknown",
    aim: 0,
    createTime: "unknown",
    title: "Title",
    author: "Author",
    oriPrice: "",
    publisher: "",
    oriImg: "",
    summary: "",
    rating: 8.0
};
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

    $('#type_select').selectpicker({});

    $('#aim_select').selectpicker({});

    getUserDetail();
    updateBookDetail();

    $('#add_isbn').blur(function(){
        var isbn =  $('#add_isbn').val();
        $.post("/book/queryIsbn", {
            isbn: isbn
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
                    bookInfo = data.data;
                    book.title = bookInfo.title;
                    if(book.author.length === 0){
                        book.author = "";
                    } else
                        book.author = bookInfo.author[0];
                    book.rating = bookInfo.rating.average;
                    book.publisher = bookInfo.publisher;
                    book.oriImg = bookInfo.image;
                    book.summary = bookInfo.summary;
                    book.isbn = isbn;
                    book.oriPrice = bookInfo.price;
                    updateBookDetail();
                }
            }
        );
    });

    $('#add_price').change(function(){
        book.sellPrice =  $('#add_price').val();
        updateBookDetail();
    });

    $('#type_select').change(function(){
        var array_type = ["", "经济金融类", "教育考试类", "计算机与网络类", "语言学习类", "管理类",
            "医学卫生类", "科技工程类", "少儿类", "文学小说类", "文化历史类",
            "法律类", "建筑类", "新闻传播类", "家庭育儿类", "艺术类",
            "生活时尚类", "旅游地理类", "心理类", "宗教哲学类",
            "社会科学类", "自然科学类", "政治军事类", "其他类"];
        var type = $('#type_select').val();
        var index = parseInt(type);
        book.bookType = array_type[index];
        updateBookDetail();
    });

    $('#add_book_description').change(function () {
       book.description = $('#add_book_description').val();
       updateBookDetail();
    });

    $('#add_image').blur(function(){
        var formData = new FormData();
        if($("#add_image")[0].files.length === 0){
            return;
        }
        formData.append("file",$("#add_image")[0].files[0]);
        $.ajax({
            url: "/image/upload",
            type: 'POST',
            data: formData,
            processData: false,
            contentType:false,
            success : function(data) {
                var res = JSON.parse(data);
                book.currentImg = res.data;
                updateBookDetail();
            }
        });
    });

    $('#add_submit').click(function(){
        $.post("/book/add", {
                isbn:  book.isbn,
                description: book.description,
                bookType: book.bookType,
                sellPrice: book.sellPrice,
                currentImg: book.currentImg,
                aim: book.aim
            },
            function(data, status){
                console.log(data, status);
                if(status !== "success"){
                    alert('服务器无法访问');
                    return;
                }
                if(data.code !== 0){
                    alert(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
                } else {
                    window.location.href = "/fe/main";
                }
            }
        );
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
                var userInfo = data.data;
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
}

function updateBookDetail() {
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
}