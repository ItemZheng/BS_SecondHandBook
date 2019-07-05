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

  $('#login_submit').click(function(){
      username = $('#name').val();
      key = $('#password').val();
      
      if(username.length === 0){
        $('#errormessage').text('用户名未填写').show();
        return;
      }

      if(key.length === 0){
        $('#errormessage').text('密码未填写').show();
        return;
      }

      $.post("/user/login",
      {
        key: key,
        username: username
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
            window.location.href = "/fe/main";
        }
      }
      );
      return false;
  });

  $('#register_submit').click(function(){
      username = $('#register_name').val();
      password = $('#register_password').val();
      email = $('#register_email').val();
      qq = $('#register_qq').val();
      wx = $('#register_wx').val();
      phone = $('#register_phone').val();

      if(username.length === 0){
          $('#error_register').text('用户名未填写').show();
          return;
      }
      if(password.length === 0){
          $('#error_register').text('密码未填写').show();
          return;
      }
      if(email.length === 0){
          $('#error_register').text('邮箱未填写').show();
          return;
      }

      $.post("/user/register",
          {
              name: username,
              password: password,
              email: email,
              qq: qq,
              wx: wx,
              phone: phone
          },
          function(data, status){
              console.log(data, status);
              if(status !== "success"){
                  $('#error_register').text('服务器无法访问').show();
                  return;
              }
              if(data.code !== 0){
                  $('#error_register').text(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1)).show();
              } else {
                  $('#code').show();
                  window.location.href = "#code";
              }
          }
      );

  });

    $('#code_submit').click(function(){
        code = $('#register_code').val();
        if(code.length === 0){
            $('#error_code').text('验证码未填写').show();
            return;
        }
        $.post("/user/verify",
            {
                code:code
            },
            function(data, status){
                console.log(data, status);
                if(status !== "success"){
                    $('#error_code').text('服务器无法访问').show();
                    return;
                }
                if(data.code !== 0){
                    $('#error_code').text(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1)).show();
                } else {
                    window.location.href = "#contact";
                }
            }
        );

    });

});
