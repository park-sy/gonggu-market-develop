import "dotenv/config";
import express from "express";
import cors from "cors";
import morgan from "morgan";
import socket from "socket.io";
import chatRouter from "./routes/chatRouter";

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(morgan("dev"));
app.use(function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  // update to match the domain you will make the request from
  res.header(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );
  res.header("Access-Control-Max-Age", 600);
  next();
});

//app.use("/product", productRouter);
//app.use("/image", imageRouter);
//app.use("/users", userRouter);
app.use("/room", chatRouter);

const server = app.listen(process.env.SERVER_PORT, () => {
  console.log(`✅ Server running on ${process.env.SERVER_PORT}`);
});

const io = socket(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST", "PUT"],
  },
});

io.on("connection", (socket) => {
  socket.on("messageSent", (roomInfo) => {
    //console.log("socket On:", roomInfo);
    //roomInfo.time = new Date(roomInfo.time);
    try {
      console.log("SEND FROM SERVER - roomInfo:", roomInfo);
      io.to(roomInfo.roomId).emit("messageReceived", roomInfo);
    } catch (err) {
      console.log("[Error]", "message Send :", err);
    }
  });
  socket.on("leaveRoom", (roomInfo) => {
    try {
      socket.leave(roomInfo.roomId);
      io.to(roomInfo.roomId).emit("leaving", { joinedRoom: roomInfo.roomId });
      console.log("LEAVE ROOM", roomInfo);
    } catch (err) {
      console.log("[Error]", "leave room :", err);
    }
  });
  socket.on("joinRoom", (roomInfo) => {
    try {
      socket.join(roomInfo.roomId);
      io.to(roomInfo.roomId).emit("joined", { joinedRoom: roomInfo.roomId });
      console.log("JOIN ROOM", roomInfo);
    } catch (err) {
      console.log("[Error]", "join room :", err);
    }
  });
  socket.on("disconnect", () => {
    console.log("User disconnected", socket.id);
  });
});

export default io;
