let stompClient;
let accessToken = localStorage.getItem("accessToken");
let refreshToken = localStorage.getItem("refreshToken");

function connect() {
    const socket = new SockJS("/ws?token=" + accessToken);
    stompClient = Stomp.over(socket);

    stompClient.connect({}, frame => {
        console.log("Connected:", frame);
        stompClient.subscribe("/topic/messages", message => console.log(message.body));
    }, async error => {
        if (error.headers && error.headers.message?.includes("Unauthorized")) {
            console.log("Access token expired, refreshing...");
            const newTokens = await refreshJwt(refreshToken);
            accessToken = newTokens.accessToken;
            localStorage.setItem("accessToken", accessToken);
            setTimeout(connect, 1000); // reconnect
        }
    });
}

function isExpired(token) {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.exp * 1000 < Date.now();
}

async function refreshJwt(refreshToken) {
    const res = await fetch("/auth/refresh", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken })
    });
    return await res.json();
}

connect();