
var userId = 7;
var currentId = 8;
var id_messages = {};
var idNames = {};

jQuery(document).ready(function( $ ) {
    var args = GetRequest();
    userId = args.id;
    currentId = args.uid;
    if(userId === undefined || currentId === undefined){
        alert("参数不正确");
        self.location = document.referrer;
    }
    currentId = parseInt(currentId);
    userId = parseInt(userId);
    id_messages[currentId.toString()] = [];
    $.post("/user/queryById", {
            id: currentId
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
                var name = data.data.name;
                idNames[currentId.toString()] = {
                    name: name,
                    time: "",
                    message: ""
                };
                updateMessage();
            }
        }
    );
    initWebSocket();
    $.post("/message/getMessage", {},
        function(data, status){
            console.log(data, status);
            if(status !== "success"){
                console.log('服务器无法访问');
                return;
            }
            if(data.code !== 0){
                console.log(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
            }
        }
    );

    $('#send').click(function(){
        $.post("/message/send",
            {
                to: currentId,
                msg: $('#text_input').val()
            },
            function(data, status){
                console.log(data, status);
                if(status !== "success"){
                    console.log('服务器无法访问');
                    return;
                }
                $('#text_input').html("");
                if(data.code !== 0){
                    console.log(data.msg.slice(data.msg.indexOf('(') + 1, data.msg.length - 1));
                }
            }
        );
    });
});

function initWebSocket() {
    var webSocket;
    if ("WebSocket" in window)
    {
        webSocket = new WebSocket("ws://localhost:3333/websocket/" + userId);

        webSocket.onopen = function()
        {
            console.log("已经连通了websocket");
        };

        webSocket.onmessage = function (evt)
        {
            var received_msg = evt.data;
            received_msg = JSON.parse(received_msg);
            console.log(received_msg);

            var another_id = parseInt(received_msg.fromId) + parseInt(received_msg.toId) - userId;
            if(id_messages[another_id.toString()] === undefined){
                id_messages[another_id.toString()] = [received_msg];
            } else {
                id_messages[another_id.toString()].push(received_msg);
            }
            if(idNames[another_id.toString()] === undefined){
                $.post("/user/queryById", {
                        id: another_id
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
                            var name = data.data.name;
                            idNames[another_id.toString()] = {
                                name: name,
                                time: received_msg.createTime,
                                message: received_msg.msg
                            };
                            updateMessage();
                        }
                    }
                );
            } else {
                idNames[another_id.toString()].time = received_msg.createTime;
                idNames[another_id.toString()].message = received_msg.msg;
                updateMessage();
            }
        };

        webSocket.onclose = function()
        {
            console.log("连接已关闭...");
        };
    }
    else{
        alert("您的浏览器不支持 WebSocket!");
        self.location = document.referrer;
    }
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

function updateMessage(){
    // update
    var recentMessages = "";
    for(var key in idNames){
        var userInfo = idNames[key];
        if(parseInt(key) === currentId){
            recentMessages = recentMessages + '<div class="chat_list active_chat" onclick="currentId='+ parseInt(key)+ '; updateMessage();">\n';
        } else {
            recentMessages = recentMessages + '<div class="chat_list" onclick="currentId='+ parseInt(key)+ '; updateMessage();">\n';
        }
        recentMessages = recentMessages +
            "            <div class=\"chat_people\">\n" +
            "            <div class=\"chat_ib\">\n" +
            "            <h5>";
        recentMessages = recentMessages + userInfo.name;
        recentMessages = recentMessages + "<span class=\"chat_date\">" + userInfo.time + "</span></h5><p>";
        recentMessages = recentMessages + userInfo.message + "</p>\n" +
            "        </div>\n" +
            "        </div>\n" +
            "        </div>";
    }
    $('#recentUsers').html(recentMessages);

    // draw messages
    var messages = "";
    var message = id_messages[currentId.toString()];
    for(var i = 0; i < message.length; i++){
        var item = message[i];
        if(item.toId === userId){
            messages = messages + "<div class=\"incoming_msg\">\n" +
                "                        <div class=\"received_msg\">\n" +
                "                            <div class=\"received_withd_msg\">\n" +
                "                                <p>";
        }
        else {
            messages = messages + "<div class=\"outgoing_msg\">\n" +
                "                        <div class=\"sent_msg\">\n" +
                "                            <p>";
        }
        messages = messages + item.msg + "</p>\n" +
            "                            <span class=\"time_date\">" + item.createTime;
        messages = messages + "</div>\n" +
            "                    </div>";
    }
    $('#chat_history').html(messages);
}