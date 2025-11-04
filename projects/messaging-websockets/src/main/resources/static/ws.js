const socket = new SockJS("http://localhost:8080/ws?token=" + jwtToken);
const stompClient = Stomp.over(socket);

stompClient.connect({}, frame => {
    console.log("Connected:", frame);
    stompClient.subscribe("/topic/messages", message => {
        console.log("Received:", message.body);
    }).then(r => console.log("Subscribed to messages"));
    stompClient.send("/app/chat", {}, "Hello from client!");
});
