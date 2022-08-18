package com.kaira.websocket.modules.chat;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatModule {

    private static final Logger log = LoggerFactory.getLogger(ChatModule.class);

    private final SocketIONamespace namespace;

    @Autowired
    public ChatModule(SocketIOServer server) {
        this.namespace = server.addNamespace("/chat");
        this.namespace.addConnectListener(onConnected());
        this.namespace.addDisconnectListener(onDisconnected());
        this.namespace.addEventListener("chat", ChatMessage.class, onChatReceived());
        this.namespace.addEventListener("dataReq",ChatMessage.class,onDataRequested());
    }
    private DataListener<ChatMessage> onDataRequested(){
        return (client, data, ackSender) -> {
            String driverClassName
                    = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/db_example?useUnicode=true&serverTimezone=UTC&useSSL=false";
            String username = "springuser";
            String password = "ThePassword";
            String query
                    = "select * from logs";
            Class.forName(driverClassName);
            Connection con = DriverManager.getConnection(
                    url, username, password);
            Statement st = con.createStatement();
            ResultSet logsData = st.executeQuery(query);
            while (logsData.next()){
                String userName = logsData.getString("userName");
                String content = logsData.getString("content");
                System.out.println(userName + content);
                ChatMessage log = new ChatMessage(userName,content);
                namespace.getBroadcastOperations().sendEvent("chat", log);
            }

            con.close();
        };
    }
    private DataListener<ChatMessage> onChatReceived() {
        return (client, data, ackSender) -> {
            log.debug("Client[{}] - Received chat message '{}'", client.getSessionId().toString(), data);
            System.out.println(data);
            String driverClassName
                    = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/db_example?useUnicode=true&serverTimezone=UTC&useSSL=false";
            String username = "springuser";
            String password = "ThePassword";
            String query
                    = "insert into logs(userName,content) values('"+data.getUserName()+"'"+",'"+data.getContent()+"');";
            Class.forName(driverClassName);

            // Obtain a connection
            Connection con = DriverManager.getConnection(
                    url, username, password);
            Statement st = con.createStatement();
            int count = st.executeUpdate(query);
            System.out.println(
                    "number of rows affected by this query= "
                            + count);

            // Closing the connection as per the
            // requirement with connection is completed
            con.close();
            namespace.getBroadcastOperations().sendEvent("logSent", data);
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            HandshakeData handshakeData = client.getHandshakeData();
            log.debug("Client[{}] - Connected to chat module through '{}'", client.getSessionId().toString(), handshakeData.getUrl());
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.debug("Client[{}] - Disconnected from chat module.", client.getSessionId().toString());
        };
    }

}
