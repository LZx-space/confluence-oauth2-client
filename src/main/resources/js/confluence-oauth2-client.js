AJS.$(document).ready(function() {
    var loginContainer = AJS.$('#login-container');
    if (AJS.$(loginContainer).length === 0) {
        return;
    }
    // 无账户的操作指引
    var authButtonName = 'Github登录';
    var signupMessage = AJS.$(loginContainer).find('#signupMessage');
    var paragraphContent = '<p>没有账户？使用<strong style="color:#0052cc"> ' + authButtonName + ' </strong>将自动为您创建账户</p>';
    if (AJS.$(signupMessage).length === 0) {
        AJS.$('<div id="signupMessage">' + paragraphContent + '</div>').insertAfter(AJS.$(loginContainer).find('fieldset'));
    } else {
        AJS.$(signupMessage).html(paragraphContent);
    }
    // 删除生产隐藏的注册表单
    AJS.$('div.signup-section').remove();
    // 添加授权登录按钮
    AJS.$('<a class="aui-button aui-style aui-button-primary" id="use_idp_button_js" href="plugins/servlet/oauth2/authorization/doap" style="align:center;">' + authButtonName + '</a>').insertAfter(AJS.$(loginContainer).find('#loginButton'));
    var url = location.href;
    // 耦合OAuth2ClientConfig#authenticationFailureHandler
    var idx = url.indexOf('oauth2_login_error=');
    if (idx > -1) {
        params = url.substr(idx);
        var error_message = params.split('&')[0].split('=')[1];
        var cn_error_message;
        switch(error_message) {
             case 'OAuth2_authorize_error_uri':
                cn_error_message = '授权请求地址错误';
                break;
             case 'OAuth2_login_error_uri':
                cn_error_message = '请求登录异常';
                break;
             case 'invalid_request':
                cn_error_message = '无效的请求';
                break;
             case 'with_no_state_param':
                cn_error_message = '参数state必须存在';
                break;
             case 'invalid_state_param':
                cn_error_message = '参数state错误';
                break;
             case 'no_username_attribute':
                cn_error_message = '未找到指定的用户名属性';
                break;
             case 'default_user_group_not_exist':
                cn_error_message = '默认用户组不存在';
                break;
             case 'cannot_find_any_admin':
                cn_error_message = '未找到任何管理员';
                break;
             default:
                cn_error_message = '授权登录失败';
        }
        AJS.$('<div class="aui-message aui-message-error closeable"><p class="title">发生如下错误：</p><ul><li>' + cn_error_message + '</li></ul><span id="oauth2-error-close" class="aui-icon icon-close" role="button"></span></div>').appendTo(AJS.$(loginContainer).find('#action-messages'));
        AJS.$('#oauth2-error-close').on('click', function(e) {
            AJS.$(e.target).closest('.aui-message').closeMessage();
        });
    }
})