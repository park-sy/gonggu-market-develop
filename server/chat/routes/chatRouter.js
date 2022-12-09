import express from "express";
import {
  getAllRooms,
  enterRoom,
  getAttachedRooms,
  checkUserInRoom,
  getChat,
  postChat,
  postSendNotification,
} from "../controllers/chat";

const chatRouter = express.Router();

// api starts with room
chatRouter.get("/", getAllRooms);
chatRouter.post("/enter", enterRoom);
chatRouter.get("/:roomId/nickname/:nickname", checkUserInRoom);
chatRouter.get("/:roomId/chat", getChat);
chatRouter.get("/:nickname", getAttachedRooms);
chatRouter.post("/chat", postChat);
chatRouter.post("/notification", postSendNotification);

export default chatRouter;
